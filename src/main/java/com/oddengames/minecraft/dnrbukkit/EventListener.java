package com.oddengames.minecraft.dnrbukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {
	DnRBukkit plugin;

	public EventListener(DnRBukkit dnRBukkit) {
		plugin = dnRBukkit;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(final BlockBreakEvent e) {
		Block block = e.getBlock();
		if (block.getType() == Material.IRON_ORE && (block.getBiome() == Biome.COLD_OCEAN
				|| block.getBiome() == Biome.DEEP_COLD_OCEAN || block.getBiome() == Biome.DEEP_FROZEN_OCEAN
				|| block.getBiome() == Biome.FROZEN_OCEAN || block.getBiome() == Biome.FROZEN_RIVER)) {
			e.setDropItems(false);
			block.getWorld().dropItem(block.getLocation(), plugin.florynite_ore);
		}
	}

	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent e) {
		if (e.getInventory().getLocation() != null
				&& e.getInventory().getLocation().getBlock().getType() == Material.SMITHING_TABLE) {
			e.getInventory().setResult(plugin.getSmithingResult(e.getInventory()));
			return;
		}
		if (e.getInventory().getLocation() != null
				&& e.getInventory().getLocation().getBlock().getType() == Material.FLETCHING_TABLE) {
			e.getInventory().setResult(plugin.getFletchingResult(e.getInventory()));
			return;
		}
		if (e.getRecipe() != null && e.getRecipe().getResult() != null) {
			Material itemType = e.getRecipe().getResult().getType();
			if (itemType == Material.CONDUIT && e.getInventory().getItem(5).hasItemMeta()
					&& e.getInventory().getItem(5).getItemMeta().hasCustomModelData()) {
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (plugin.isUndead(event.getEntityType()) && event.getDamager() instanceof Player) {
			if (((Player) event.getDamager()).getInventory().getItemInOffHand() != null && ((Player) event.getDamager())
					.getInventory().getItemInOffHand().getItemMeta().hasCustomModelData()) {
				event.setDamage(event.getDamage() + 3);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntityType() == EntityType.SPIDER || event.getEntityType() == EntityType.CAVE_SPIDER) {
			event.getDrops().add(plugin.rawSpider.clone());
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.SMITHING_TABLE) {
				event.getPlayer().openWorkbench(event.getClickedBlock().getLocation(), true);
				event.setCancelled(true);
			}
			if (event.getClickedBlock().getType() == Material.FLETCHING_TABLE) {
				event.getPlayer().openWorkbench(event.getClickedBlock().getLocation(), true);
				event.setCancelled(true);
			}
		}
		if (event.getItem() != null && event.getItem().getType() != null
				&& event.getItem().getType() == Material.HEART_OF_THE_SEA
				&& event.getItem().getItemMeta().getCustomModelData() == DnRBukkit.ID_TIME_CRYSTAL
				&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (DnRBukkit.cooldown_time_crystal.containsKey(p.getUniqueId())) {
				double l = DnRBukkit.cooldown_time_crystal.get(p.getUniqueId());
				double i = DnRBukkit.TIME_CRYSTAL_COOLDOWN * 1000;
				double c = System.currentTimeMillis();
				if (l + i > c) {
					event.setCancelled(true);
					double x = (l + i - c) / 1000;
					p.sendMessage(ChatColor
							.translateAlternateColorCodes('&',
									"&3You must wait &c<time> &3seconds before using that again!")
							.replace("<time>", plugin.timeFormat.format(x)));
					return;
				}
			}
			DnRBukkit.cooldown_time_crystal.put(p.getUniqueId(), System.currentTimeMillis());
			p.getNearbyEntities(20, 20, 20).forEach((e) -> {
				if (e instanceof LivingEntity) {
					LivingEntity le = (LivingEntity) e;
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 1));
				}
			});
			p.removePotionEffect(PotionEffectType.SLOW);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));
		}
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		event.getPlayer().sendMessage(
				"Welcome to the server! We strongly reccomend that you use the resource pack, as we have several custom items that depend on it.\nCheck out the online map at http://16colorgames.com:8124/");
	}

}
