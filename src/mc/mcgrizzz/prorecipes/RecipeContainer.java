package mc.mcgrizzz.prorecipes;

import org.bukkit.inventory.ItemStack;

abstract class RecipeContainer {
	
	public abstract ItemStack getResult();
	
	public abstract ItemStack[] getMatrixView();

}
