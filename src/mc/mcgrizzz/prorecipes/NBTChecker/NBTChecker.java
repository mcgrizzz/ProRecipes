package mc.mcgrizzz.prorecipes.NBTChecker;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public interface NBTChecker {
	
	
	public List<String> getTags(String s);
	
	public String getPotionType(String s);
	
	public ItemStack addTag(ItemStack i, String key, String value);
	
}
