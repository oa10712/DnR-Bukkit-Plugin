package com.oddengames.minecraft.dnrbukkit;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CustomRecipe {
	public static class EmptyChoice implements RecipeChoice {

		@Override
		public RecipeChoice clone() {
			return this;
		}

		@Override
		public ItemStack getItemStack() {
			// TODO Auto-generated method stub
			return new ItemStack(Material.AIR);
		}

		@Override
		public boolean test(ItemStack itemStack) {
			return (itemStack == null || itemStack.getType() == Material.AIR);
		}

	}

	ItemStack result;

	RecipeChoice[] grid = new RecipeChoice[9];

	public CustomRecipe(ItemStack itemStack, RecipeChoice[][] inputs) {
		result = itemStack;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				try {
					grid[i * 3 + j] = inputs[i][j];
				} catch (Exception ex) {
					grid[i * 3 + j] = new EmptyChoice();
				}
			}
		}
	}

	public ItemStack getResult() {
		return result.clone();
	}

	@Override
	public String toString() {
		return "[" + Arrays.toString(grid) + "=(" + result.toString() + ")]";
	}
}