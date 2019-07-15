package com.oddengames.minecraft.dnrbukkit;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.oddengames.minecraft.dnrbukkit.CustomRecipe.EmptyChoice;

public class DnRBukkit extends JavaPlugin {
	public static final HashMap<UUID, Long> cooldown_time_crystal = new HashMap<>();

	public static final int ID_TIME_CRYSTAL = 3;
	public static final int ID_ORB = 2;
	public static final int ID_BELL = 1;
	public static final int ID_FLORYNITE_ORE = 1;
	public static final int ID_GIRTHY_DIAMOND = 1;
	public static final int ID_GIRTHY_IRON = 1;
	public static final int ID_GIRTHY_GOLD = 1;
	public static final int ID_RAW_SPIDER = 1;

	public static final int TIME_CRYSTAL_COOLDOWN = 60;

	public static final double GIRTHY_DAMAGE_MULT = 1.25;
	public static final String GIRTHY_LORE = "Deals +" + (GIRTHY_DAMAGE_MULT * 100 - 100) + "% damage";
	public static final double LIGHT_ORB_BONUS = 3.0;

	public static final String LIGHT_ORB_LORE = "Deals +" + LIGHT_ORB_BONUS + " damage to undead";
	public NamespacedKey girthyKey = new NamespacedKey(this, "girthyItem");
	public NamespacedKey orbKey = new NamespacedKey(this, "orbOfLight");

	ArrayList<CustomRecipe> smithingRecipies = new ArrayList<>();
	ArrayList<CustomRecipe> fletchingRecipies = new ArrayList<>();

	ArrayList<EntityType> etArr = new ArrayList<>();

	ItemStack bellVecna = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
	ItemStack florynite_ore = new ItemStack(Material.HEART_OF_THE_SEA, 1);
	ItemStack girthyDiamondSword = new ItemStack(Material.DIAMOND_SWORD, 1);
	ItemStack girthyIronSword = new ItemStack(Material.IRON_SWORD, 1);
	ItemStack girthyGoldSword = new ItemStack(Material.GOLDEN_SWORD, 1);
	ItemStack orbOfLight = new ItemStack(Material.HEART_OF_THE_SEA, 1);
	ItemStack timeCrystal = new ItemStack(Material.HEART_OF_THE_SEA, 1);
	ItemStack rawSpider = new ItemStack(Material.SPIDER_EYE, 1);

	DecimalFormat timeFormat = new DecimalFormat("0.0");

	public ItemStack getFletchingResult(CraftingInventory ci) {
		for (CustomRecipe sr : fletchingRecipies) {
			try {
				RecipeChoice[] rc = sr.grid;
				boolean fail = false;
				for (int i = 0; i < 9; i++) {
					if (!rc[i].test(ci.getMatrix()[i])) {
						fail = true;
						break;
					}
				}
				if (!fail) {
					return sr.getResult();
				}
			} catch (Exception ex) {
			}
		}
		return new ItemStack(Material.AIR);
	}

	public ItemStack getSmithingResult(CraftingInventory ci) {
		for (CustomRecipe sr : smithingRecipies) {
			try {
				RecipeChoice[] rc = sr.grid;
				boolean fail = false;
				for (int i = 0; i < 9; i++) {
					if (!rc[i].test(ci.getMatrix()[i])) {
						fail = true;
						break;
					}
				}
				if (!fail) {
					return sr.getResult();
				}
			} catch (Exception ex) {
			}
		}
		return new ItemStack(Material.AIR);
	}

	public boolean isUndead(EntityType entityType) {
		return etArr.contains(entityType);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		setupItems();
		setupRecipies();
		setupSchedulers();
		setupArrays();

		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new EventListener(this), this);
	}

	private void setupArrays() {
		etArr.add(EntityType.DROWNED);
		etArr.add(EntityType.HUSK);
		etArr.add(EntityType.PHANTOM);
		etArr.add(EntityType.PIG_ZOMBIE);
		etArr.add(EntityType.SKELETON);
		etArr.add(EntityType.SKELETON_HORSE);
		etArr.add(EntityType.STRAY);
		etArr.add(EntityType.WITHER_SKELETON);
		etArr.add(EntityType.ZOMBIE);
		etArr.add(EntityType.ZOMBIE_HORSE);
		etArr.add(EntityType.ZOMBIE_VILLAGER);
	}

	private void setupItems() {
		ItemMeta meta = bellVecna.getItemMeta();
		meta.setCustomModelData(ID_BELL);
		meta.setDisplayName("Bell of Vecna");
		bellVecna.setItemMeta(meta);

		meta = orbOfLight.getItemMeta();
		meta.setCustomModelData(ID_ORB);
		meta.setDisplayName("Orb of Light");
		List<String> lore = new ArrayList<>();
		meta.getPersistentDataContainer().set(orbKey, PersistentDataType.INTEGER, 1);
		lore.add(LIGHT_ORB_LORE);
		meta.setLore(lore);
		orbOfLight.setItemMeta(meta);

		meta = florynite_ore.getItemMeta();
		meta.setCustomModelData(ID_FLORYNITE_ORE);
		meta.setDisplayName("Florynite Ore");
		florynite_ore.setItemMeta(meta);

		meta = girthyDiamondSword.getItemMeta();
		meta.setCustomModelData(ID_GIRTHY_DIAMOND);
		meta.setDisplayName("Girthy Diamond Sword");
		lore = new ArrayList<>();
		lore.add(GIRTHY_LORE);
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(girthyKey, PersistentDataType.INTEGER, 1);
		girthyDiamondSword.setItemMeta(meta);

		meta = girthyIronSword.getItemMeta();
		meta.setCustomModelData(ID_GIRTHY_IRON);
		lore = new ArrayList<>();
		lore.add(GIRTHY_LORE);
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(girthyKey, PersistentDataType.INTEGER, 1);
		meta.setDisplayName("Girthy Iron Sword");
		girthyIronSword.setItemMeta(meta);

		meta = girthyGoldSword.getItemMeta();
		meta.setCustomModelData(ID_GIRTHY_GOLD);
		lore = new ArrayList<>();
		lore.add(GIRTHY_LORE);
		meta.setLore(lore);
		meta.getPersistentDataContainer().set(girthyKey, PersistentDataType.INTEGER, 1);
		meta.setDisplayName("Girthy Golden Sword");
		girthyGoldSword.setItemMeta(meta);

		meta = timeCrystal.getItemMeta();
		meta.setCustomModelData(ID_TIME_CRYSTAL);
		meta.setDisplayName("Time Displacement Crystal");
		timeCrystal.setItemMeta(meta);

		meta = rawSpider.getItemMeta();
		meta.setCustomModelData(ID_RAW_SPIDER);
		meta.setDisplayName("Raw Spider");
		rawSpider.setItemMeta(meta);
	}

	private void setupRecipies() {
		ShapedRecipe bell = new ShapedRecipe(new NamespacedKey(this, "bell_of_vecna"), bellVecna);
		bell.shape("fff", "fbf", " f ");
		bell.setIngredient('b', Material.BELL);
		bell.setIngredient('f', new ExactChoice(florynite_ore));
		Bukkit.addRecipe(bell);

		ShapedRecipe orbRecipe = new ShapedRecipe(new NamespacedKey(this, "orbLight"), orbOfLight);
		orbRecipe.shape("ggg", "ghg", "ggg");
		orbRecipe.setIngredient('g', Material.GLOWSTONE_DUST);
		orbRecipe.setIngredient('h', Material.BLUE_ICE);
		Bukkit.addRecipe(orbRecipe);

		ShapedRecipe timeCrystalRecipe = new ShapedRecipe(new NamespacedKey(this, "timeCrystal"), timeCrystal);
		timeCrystalRecipe.shape(" f ", "fcf", " f ");
		timeCrystalRecipe.setIngredient('f', new ExactChoice(florynite_ore));
		timeCrystalRecipe.setIngredient('c', Material.CLOCK);
		Bukkit.addRecipe(timeCrystalRecipe);

		CustomRecipe girthyDiamond = new CustomRecipe(girthyDiamondSword,
				new RecipeChoice[][] { { new EmptyChoice(), new MaterialChoice(Material.DIAMOND) },
						{ new MaterialChoice(Material.DIAMOND), new MaterialChoice(Material.DIAMOND_BLOCK),
								new MaterialChoice(Material.DIAMOND) },
						{ new EmptyChoice(), new MaterialChoice(Material.DIAMOND_SWORD) } });
		smithingRecipies.add(girthyDiamond);

		CustomRecipe girthyGold = new CustomRecipe(girthyGoldSword,
				new RecipeChoice[][] { { new EmptyChoice(), new MaterialChoice(Material.GOLD_INGOT) },
						{ new MaterialChoice(Material.GOLD_INGOT), new MaterialChoice(Material.GOLD_BLOCK),
								new MaterialChoice(Material.GOLD_INGOT) },
						{ new EmptyChoice(), new MaterialChoice(Material.GOLDEN_SWORD) } });
		smithingRecipies.add(girthyGold);

		CustomRecipe girthyIron = new CustomRecipe(girthyIronSword,
				new RecipeChoice[][] { { new EmptyChoice(), new MaterialChoice(Material.IRON_INGOT) },
						{ new MaterialChoice(Material.IRON_INGOT), new MaterialChoice(Material.IRON_BLOCK),
								new MaterialChoice(Material.IRON_INGOT) },
						{ new EmptyChoice(), new MaterialChoice(Material.IRON_SWORD) } });
		smithingRecipies.add(girthyIron);
	}

	private void setupSchedulers() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : getServer().getOnlinePlayers()) {
				if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta()
						.getPersistentDataContainer().get(girthyKey, PersistentDataType.INTEGER) != null) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 1, true));
				}
			}
		}, 1L, 20L);
	}

}
