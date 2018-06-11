package me.mcgrizzz.prorecipes.api;

import org.bukkit.inventory.ItemStack;

public abstract class RecipeContainer {
	
	public abstract ItemStack getResult();
	
	public abstract ItemStack[] getMatrixView();

}
