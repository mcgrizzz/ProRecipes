package me.mcgrizzz.prorecipes.lib;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.mcgrizzz.prorecipes.ProRecipes;
import me.mcgrizzz.prorecipes.NBTChecker.MinecraftVersion;

public class ItemUtils {
	
	protected static List<String> getTags(String s){
		return ProRecipes.getPlugin().mv.getChecker().getTags(s);
	}
	
	public static String itemToStringBlob(ItemStack itemStack, boolean ignoreExtraData) {
		if(ignoreExtraData){
			return itemStack.toString();
		}
		boolean skullNoOwn = false;
		String textures = "";
		if(itemStack.getType() == Material.SKULL){
			SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
			if(!meta.hasOwner()){
				//meta.setOwner("***4239uivui3903j4vn");
				GameProfile f = null;
				try{
					f = (GameProfile) ReflectionUtils.getValue(meta, true, "profile");
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				Iterator<Property> it = f.getProperties().get("textures").iterator();
				while(it.hasNext()){
					Property p = it.next();
					if(p.getName().equalsIgnoreCase("textures")){
						textures = p.getValue();
						skullNoOwn = true;
						break;
					}
				}
				
			}
			itemStack.setItemMeta(meta);
		}
        YamlConfiguration config = new YamlConfiguration();
        //itemStack.setDurability((short)0);
        if(itemStack.getType() == Material.AIR){
        	itemStack.setDurability((short)-1);
        }
        config.set("i", itemStack);
        String ssa = config.saveToString();
       //ssa.replaceAll("damage: -1", "");
        Map<String, Object> map = itemStack.serialize();
       
        if(map.containsKey("meta")){
        	 String s = map.get("meta").toString();
             if(s.contains("internal")){
             	String internal = "";
             	List<String> arr = Arrays.asList(s.split(", "));
             	for(String t : arr){
             		if(t.contains("internal")){
             			internal = t.replace("internal=", "").replace("}", "");
             		}
             	}
             	//System.out.println("INDEX: " + ssa.indexOf("internal"));
             	ssa = ssa.substring(0, ssa.indexOf("internal"));
             	//System.out.println(ssa);
             	if(ProRecipes.getPlugin().mv != MinecraftVersion.NoVersion){
             		ssa+= "\n " + getTags(internal).toString();
             	}
             	 
             }
             
            // System.out.println(ssa);
        }
        if(skullNoOwn){
        	ssa+= "\n" + textures;
        }
       
        return ssa;
    }
	
	public static String itemToStringBlob(ItemStack itemStack) {
        return itemToStringBlob(itemStack, false);
    }
	
	public static GameProfile createGameProfile(String texture, UUID id)
	  {
	    GameProfile profile = new GameProfile(id, null);
	    PropertyMap propertyMap = profile.getProperties();
	    if (propertyMap == null)
	    {
	      Bukkit.getLogger().log(Level.INFO, "No property map found in GameProfile, can't continue.");
	      return null;
	    }
	    propertyMap.put("textures", new Property("textures", texture));
	    propertyMap.put("Signature", new Property("Signature", "1234"));
	    return profile;
	  }
	 
	 public static ItemStack createHead(UUID id, ItemStack c, String texture)
	  {
	    GameProfile profile = createGameProfile(texture, id);
	    ItemStack head = c.clone();
	    ItemMeta headMeta = head.getItemMeta();
	    try {
			ReflectionUtils.setValue(headMeta, headMeta.getClass(), true, "profile", profile);
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	    head.setItemMeta(headMeta);
	    SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
	    head.setItemMeta(skullMeta);
	    return head.clone();
	  }
	 
	 public static boolean isVanilla(ItemStack i){
		 for(ItemStack b : ProRecipes.getPlugin().creative.getContents()){
			 if(i.isSimilar(b))return true;
		 }
		 if(i.hasItemMeta())return false;
		 if(i.getEnchantments().size() > 0)return false;
		 return true;
	 }
	 
	 public static org.bukkit.inventory.Recipe findRecipe(ItemStack i){
		 Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().defaultRecipes.iterator();
			org.bukkit.inventory.Recipe recipe;
	        while(it.hasNext())
	        {
	            recipe = it.next();
	            if (recipe != null)
	            {
	            	if(recipe.getResult().isSimilar(i)){
	            		return recipe;
	            	}else if(recipe.getResult().getType() == i.getType()){
	            		return recipe;
	            	}
	            	
	            }
	        }
	        return null;
	 }
	 
	 public static ItemStack[] getMatrix(org.bukkit.inventory.Recipe rec){
		 if(rec instanceof ShapedRecipe){
			 ShapedRecipe r = (ShapedRecipe)rec;
			 ItemStack[] arr = new ItemStack[9];
			 String[] sh = r.getShape();
			 Map<Character, ItemStack> map = r.getIngredientMap();
			 for(int i = 0; i < sh.length; i++){
				 for(int b = 0; b < sh[i].length(); b++){
					 arr[i*3+b] = map.get(sh[i].toCharArray()[b]);
				 }
			 }
			 return arr;
		 }else if(rec instanceof ShapelessRecipe){
			 ShapelessRecipe r = (ShapelessRecipe)rec;
			 return ProRecipes.getAPI().toArray(r.getIngredientList());
		 }else if(rec instanceof FurnaceRecipe){
			 FurnaceRecipe r = (FurnaceRecipe)rec;
			 ItemStack[] arr = new ItemStack[9];
			 arr[4] = r.getInput();
			 return arr;
		 }
		 return null;
	 }
	 
	 public static ItemStack[] getTippedArrowRec(ItemStack b){
		 ItemStack[] arr = new ItemStack[9];
		 for(int i = 0; i < 9; i++){
			 if(i != 4){
				 arr[i] = new ItemStack(Material.ARROW);
			 }
		 }
		 //TippedArrow t = (TippedArrow)b;
		// for(String s : mv.getChecker().getTags(nbtString(b))){
			// System.out.println(s);
		// }
		 String type = ProRecipes.getPlugin().mv.getChecker().getPotionType(nbtString(b));
		 ItemStack i = new ItemStack(Material.LINGERING_POTION);
		 i = ProRecipes.getPlugin().mv.getChecker().addTag(i, "Potion", "minecraft:" + type);
		 arr[4] = i;
		 return arr;
	 }
	 
	 
	 protected static String nbtString(ItemStack itemStack) {
			
	        YamlConfiguration config = new YamlConfiguration();
	        //itemStack.setDurability((short)0);
	        if(itemStack.getType() == Material.AIR){
	        	itemStack.setDurability((short)-1);
	        }
	        config.set("i", itemStack);
	        String ssa = config.saveToString();
	        Map<String, Object> map = itemStack.serialize();
	       
	        if(map.containsKey("meta")){
	        	 String s = map.get("meta").toString();
	             if(s.contains("internal")){
	             	String internal = "";
	             	List<String> arr = Arrays.asList(s.split(", "));
	             	for(String t : arr){
	             		if(t.contains("internal")){
	             			internal = t.replace("internal=", "").replace("}", "");
	             			return internal;
	             		}
	             	}
	             	
	             	
	             	
	             }
	             
	        }
	       
	        return "";
	    }
	 
	 /*
	  * 
	  * For blacklist use. Not actual implementation
	  */
	 
	 public static String getRecipeKey(org.bukkit.inventory.Recipe rec, boolean dur){
			if(rec instanceof Keyed){
				return ((Keyed)rec).getKey().getKey();
			}else{
				return "nonkeyed:" + rec.getResult().getType().toString() + (dur ? rec.getResult().getDurability() : "");
			}
		}
	
	public static void createFakeRecipes(){
		
		
		/*for(int c = 1; c < 60; c++){
			ItemStack i = new ItemStack(Material.values()[c]);
			if(i.getType().equals(Material.AIR)){
				i.setType(Material.values()[c+1]);
			}
				RecipeShaped r = new RecipeShaped(i);
				String[][] s = {{"a", "a", "a",},{"a", "a", "a"},{"a", "a", "a"}};
				r.setStructure(s);
				r.setIngredient(i, 'a');
				r.register();
			
		}*/
		
		/*ItemStack i = new ItemStack(Material.APPLE);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("This is a test");
		i.setItemMeta(m);
		RecipeFurnace rec = new RecipeFurnace(i, i);
		rec.register();
		*/ 
		
	}
	

}
