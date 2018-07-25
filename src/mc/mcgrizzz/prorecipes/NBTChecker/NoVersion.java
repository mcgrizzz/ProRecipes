package mc.mcgrizzz.prorecipes.NBTChecker;

import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;


public class NoVersion implements NBTChecker{

	@Override
	public List<String> getTags(String s) {
		return null;
	}
	
	public String getPotionType(String s) {
		return null;
	}
	
	@Override
	public ItemStack addTag(ItemStack i, String key, String value) {
		return null;
	}

	@Override
	public void removeRecipe(Iterator<Recipe> it, Recipe recipe) {
	}

}
