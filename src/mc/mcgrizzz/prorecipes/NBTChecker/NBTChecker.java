package mc.mcgrizzz.prorecipes.NBTChecker;

import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public interface NBTChecker {
	
	
	public List<String> getTags(String s);
	
	public String getPotionType(String s);
	
	public ItemStack addTag(ItemStack i, String key, String value);
	
	public void removeRecipe(Iterator<Recipe> it, Recipe recipe);
	
}
