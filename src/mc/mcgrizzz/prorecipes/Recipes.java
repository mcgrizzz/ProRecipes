package mc.mcgrizzz.prorecipes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import mc.mcgrizzz.prorecipes.RecipeAPI.RecipeType;
import mc.mcgrizzz.prorecipes.events.FurnaceCraftEvent;
import mc.mcgrizzz.prorecipes.events.MulticraftEvent;
import mc.mcgrizzz.prorecipes.events.WorkbenchCraftEvent;
import net.md_5.bungee.api.ChatColor;


public class Recipes implements Listener{
	
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	
	boolean changed = false;
	
		
		
		
		/**
		 * Created by: AndrewEpifano
		 * Create recipes with custom ItemStacks as opposed to Materials 
		 */
		
		ArrayList<RecipeShapeless> shapeless = new ArrayList<RecipeShapeless>();
		ArrayList<RecipeShaped> shaped = new ArrayList<RecipeShaped>();
		ArrayList<RecipeFurnace> fur = new ArrayList<RecipeFurnace>();
		ArrayList<RecipeChest> chest = new ArrayList<RecipeChest>();
		
		ArrayList<ShapedRecipe> doubles = new ArrayList<ShapedRecipe>(); //if you set a shapeless recipe and there's a shaped recipe it needs to override
		
		HashMap<RecipeShaped, ArrayList<ShapedRecipe>> conflictsShaped = new HashMap<RecipeShaped, ArrayList<ShapedRecipe>>();
		
		HashMap<RecipeShapeless, ShapelessRecipe> conflictsShapeless = new HashMap<RecipeShapeless, ShapelessRecipe>();
		
		//These are for saving only correct recipes
		ArrayList<RecipeShapeless> shapelessPack = new ArrayList<RecipeShapeless>();
		ArrayList<RecipeShaped> shapedPack = new ArrayList<RecipeShaped>();
		ArrayList<RecipeFurnace> furPack = new ArrayList<RecipeFurnace>();
		ArrayList<RecipeChest> chestPack = new ArrayList<RecipeChest>();
		
		
		public FileConfiguration getConfig(){
			return customConfig;
		}
		
		public File getFile(){
			return customConfigFile;
		}
		
		public boolean addChest(RecipeChest recipeChest) {
			
			for(RecipeChest r : chest){
				if(r.match(recipeChest)){
					return false;
				}
			}
			changed = true;
			chest.add(recipeChest);
			return true;
		}
		
		//HashMap<RecipeFurnace, FurnaceRecipe> conflictsFur = new HashMap<RecipeFurnace, FurnaceRecipe>();
		
		public boolean addFurnace(RecipeFurnace rec){
			
			for(RecipeFurnace r : fur){
				if(r.match(rec)){
					return false;
				}
			}
			changed = true;
			fur.add(rec);
			return true;
		}
		
		//public void addConflict(RecipeFurnace rec, FurnaceRecipe re){
		//	conflictsFur.put(rec, re);
		//}
		
		//public void removeConflict(RecipeFurnace rec){
		//	if(conflictsFur.containsKey(rec)){
		//		conflictsFur.remove(rec);
			//}
		//}
		
		/*public void runAuthentication(){
			new BukkitRunnable(){
				
				@Override
				public void run() {
					if(!Authentication.Host.is.isHosting()){
						if(Authentication.Host.can.canHost()){
							Authentication.Host.add.addHost();
						}else{
							System.out.println("Unable to authenticate plugin...");
							//this.cancel();
							//ProRecipes.getPlugin().getServer().getPluginManager().disablePlugin(ProRecipes.getPlugin());
						}
					}
				}
				
			}.runTaskTimer(ProRecipes.getPlugin(), 0, 20*60*10);
		}
		*/
		public void addConflict(RecipeShaped rec, ShapedRecipe re){
			if(conflictsShaped.containsKey(rec)){
				ArrayList<ShapedRecipe> list = conflictsShaped.get(rec);
				list.add(re);
				conflictsShaped.put(rec, list);
			}else{
			
				ArrayList<ShapedRecipe> list = new ArrayList<ShapedRecipe>();
				list.add(re);
				conflictsShaped.put(rec, list);
			}
		}
		
		public void removeConflict(RecipeShaped rec){
			if(conflictsShaped.containsKey(rec)){
				conflictsShaped.remove(rec);
			}
		}
		
		public void removeConflict(RecipeShapeless rec){
			if(conflictsShapeless.containsKey(rec)){
				conflictsShapeless.remove(rec);
			}
		}
		
		public void addConflict(RecipeShapeless rec, ShapelessRecipe re){
			conflictsShapeless.put(rec, re);
		}
		
		
		public boolean addShapeless(RecipeShapeless recipe){
			//Add recipe if it's not already added.
			
			for(RecipeShapeless r : shapeless){
				if(r.match(recipe))return false;
			}
			changed = true;
			shapeless.add(recipe);
			return true;
		}
		
		public boolean addShaped(RecipeShaped recipe){
			
			for(RecipeShaped r : shaped){
				if(r.match(recipe))return false;
			}
			changed = true;
			shaped.add(recipe);
			return true;
		}
		
		public void onDisable(){
			//Remove recipes
			shapeless.clear();
			shaped.clear();
			fur.clear();
			chest.clear();
			
			shapelessPack.clear();
			shapedPack.clear();
			furPack.clear();
			chestPack.clear();
			
			doubles.clear();
			
			conflictsShaped.clear();
			conflictsShapeless.clear();
			
		}
		
		
		public void saveRecipes(boolean onDisable){
			
			if(onDisable && changed){
				moveBackups();
			}
			
			changed = true;
			
			ConfigurationSection shapedd = customConfig.createSection("shaped");
			ConfigurationSection shapelesss = customConfig.createSection("shapeless");
			ConfigurationSection furr = customConfig.createSection("furnaces");
			ConfigurationSection multi = customConfig.createSection("multi");
			ConfigurationSection textures = customConfig.createSection("textures");
			
			for(RecipeFurnace rec : fur){
				if(furPack.contains(rec))continue;
				//////////System.out.println("Furnace recipe: " + fur.indexOf(rec));
				int i = fur.indexOf(rec);
				furr.set(i + ".result", rec.result);
				setTexture(rec.result, textures, "furnaces." + i + ".result");
				furr.set(i + ".source", rec.toBurn);
				setTexture(rec.toBurn, textures, "furnaces." + i + ".source");
				if(rec.getPermission() != null && !rec.getPermission().isEmpty()){
					furr.set(i + ".permission", rec.getPermission());
				}
			}
			
			for(RecipeChest rec : chest){
				if(chestPack.contains(rec))continue;
				int i = chest.indexOf(rec);
				
				//////////System.out.println("Shaped recipe: " + i);
				String structure = "";
				
				ArrayList<String> arr = new ArrayList<String>();
				
				for(String[] s : rec.structure){
					arr.add(join("_", s));
				}
				String[] a = arr.toArray(new String[4]);
				structure = join("|", a);
				multi.set(i + ".structure", structure);
				for(Character c : rec.ingredients.keySet()){
					multi.set(i + ".characters." + c, rec.ingredients.get(c));
					setTexture(rec.ingredients.get(c), textures, "multi." +  i + ".characters." + c);
				}
				
				for(int b = 0; b < 4; b++){
					multi.set(i + ".result." + b, rec.getResult()[b]);
					setTexture(rec.getResult()[b], textures, "multi." +  i + ".result." + b);
				}
				if(rec.getPermission() != null && !rec.getPermission().isEmpty()){
					multi.set(i + ".permission", rec.getPermission());
				}
				
			}
			
			for(RecipeShaped rec : shaped){
				if(shapedPack.contains(rec))continue;
				
				int i = shaped.indexOf(rec);
				
				//////////System.out.println("Shaped recipe: " + i);
				String structure = "";
				
				ArrayList<String> arr = new ArrayList<String>();
				for(int t = 0; t < 9; t++){
					
				}
				for(String[] s : rec.structure){
					arr.add(join("_", s));
				}
				String[] a = arr.toArray(new String[3]);
				structure = join("|", a);
				shapedd.set(i + ".structure", structure);
				for(Character c : rec.ingredients.keySet()){
					shapedd.set(i + ".characters." + c, rec.ingredients.get(c));
					setTexture(rec.ingredients.get(c), textures, "shaped." +  i + ".characters." + c);
				}
				shapedd.set(i + ".result", rec.getResult());
				setTexture(rec.getResult(), textures, "shaped." +  i + ".result");
				if(rec.getPermission() != null && !rec.getPermission().isEmpty()){
					shapedd.set(i + ".permission", rec.getPermission());
				}
			}
			
			for(RecipeShapeless rec : shapeless){
				if(shapelessPack.contains(rec))continue;
				int i = shapeless.indexOf(rec);
				//////////System.out.println("Shapeless recipe: " + i);
				int previous = 0;
				for(ItemStack t : rec.items){
					previous++;
					ItemStack it = t.clone();
					shapelesss.set(i + ".items." + previous, it);
					setTexture(it, textures, "shapeless." + i + ".items." + previous);
				}
				shapelesss.set(i + ".result", rec.getResult());
				setTexture(rec.getResult(), textures, "shapeless." + i + ".result");
				
				
				if(rec.getPermission() != null && !rec.getPermission().isEmpty()){
					shapelesss.set(i + ".permission", rec.getPermission());
				}
			}
			
			
			
			 	try {
				    customConfig.save(customConfigFile);
				} catch (Exception ex) {
			        ProRecipes.getPlugin().getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
			    }
			 	
			 		ProRecipes.getPlugin().getLogger().log(Level.INFO, "\u001B[32mSaved " + (shapeless.size()-shapelessPack.size()) + 
				 			" shapeless recipes, " + (shaped.size()-shapedPack.size()) + " shaped recipes, " + (fur.size()-furPack.size()) + " furnace recipes, and "  + (chest.size()-chestPack.size())  + " multi-craft recipes!\u001B[0m");
			 	
			 	
			 	if(onDisable){
			 		onDisable();
			 	}
		}
		
		public void setTexture(ItemStack i, ConfigurationSection s, String path){
			if(i != null && i.getType() == Material.SKULL_ITEM){
				SkullMeta meta = (SkullMeta)i.getItemMeta();
				if(!meta.hasOwner()){
					GameProfile f = null;
					try{
						f = (GameProfile) ReflectionUtils.getValue(meta, true, "profile");
					}catch(Exception e){
						e.printStackTrace();
					}
					if(f == null){
						return;
					}
					String textures = "";
					Iterator<Property> it = f.getProperties().get("textures").iterator();
					while(it.hasNext()){
						Property p = it.next();
						if(p.getName().equalsIgnoreCase("textures")){
							textures = p.getValue();
							break;
						}
					}
					s.set(path + ".texture", textures);
					s.set(path + ".uuid", f.getId().toString());
					
				}
			}
		}
		
		public ItemStack checkTexture(ItemStack i, ConfigurationSection s, String path){
			if(i == null || i.getType() != Material.SKULL_ITEM){
				return i;
			}
			if(s == null){
				return i;
			}
			SkullMeta meta = (SkullMeta) i.getItemMeta();
			if(meta.hasOwner()){
				return i;
			}
			if(s.contains(path)){
				String texture = s.getString(path + ".texture");
				String id = s.getString(path + ".uuid");
				return ProRecipes.getPlugin().CreateHead(UUID.fromString(id), i, texture);
			}else{
				return i;
			}
		}
		
		public void moveBackups(){
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			File f = new File(ProRecipes.getPlugin().getDataFolder(), "/backups/" + "Year " + 
			cal.get(Calendar.YEAR) + "/" +"Month " + cal.get(Calendar.MONTH)  + "/" + "Day " + cal.get(Calendar.DAY_OF_MONTH));
			f.mkdirs();
			File b1 = new File(f, "" + dateFormat.format(cal.getTime()).replaceAll("\\:", " ") + ".yml");
			try{
				Files.move(customConfigFile.toPath(), b1.toPath(), StandardCopyOption.REPLACE_EXISTING);
				customConfigFile = new File(ProRecipes.getPlugin().getDataFolder(), "recipes.yml");
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		public void loadRecipes(){ 
			
			 //runAuthentication();
			
			 if (customConfigFile == null) {
			    	customConfigFile = new File(ProRecipes.getPlugin().getDataFolder(), "recipes.yml");
			    }
			    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
			    ConfigurationSection textures = customConfig.getConfigurationSection("textures");
			    ConfigurationSection furnace = customConfig.getConfigurationSection("furnaces");
			    if(!(customConfig == null || furnace == null)){
			    	 Set<String> kk = furnace.getKeys(false);
					    for(String s : kk){
					    	ItemStack result = checkTexture(furnace.getItemStack(s + ".result"), textures, "furnace." + s + ".result");
					    	ItemStack source = checkTexture(furnace.getItemStack(s + ".source"), textures, "furnace." + s + ".source");
					    	RecipeFurnace rec = new RecipeFurnace(result, source);
					    	if(furnace.contains(s + ".permission")){
					    		rec.setPermission(furnace.getString(s + ".permission"));
					    	}
					    	rec.register();
					    }
					    
			    }
			   
			    ConfigurationSection shaped = customConfig.getConfigurationSection("shaped");	    
			    if (!(customConfig == null || shaped == null)){
			    	 Set<String> keys =  shaped.getKeys(false);
					    for(String s : keys){
					    	String beforeFormat = shaped.getString(s + ".structure");
					    	String[] firstFormat = beforeFormat.split("\\|");
					    	String[][] format = new String[3][3];
					    	ArrayList<String[]> strns = new ArrayList<String[]>();
					    	for(String ss : firstFormat){
					    		////////////////System.out.printlnArrays.deepToString(ss.split("\\_")));
					    		strns.add(ss.split("\\_"));
					    	}
					    	//////////////////System.out.printlnstrns.size());
					    	//////////////////System.out.printlnstrns.get(0).length);
					    	for(int x = 0; x < strns.size(); x++){
					    		for(int z = 0; z < strns.get(x).length; z++){
					    			if(strns.get(x)[0].equalsIgnoreCase("null") || strns.get(x)[0] == null){
					    				for(int b = 0; b < 3; b++){
					    					format[x][b] = " ";
					    				}
					    				break;
					    			}
					    			
					    			if(strns.get(x)[z] == "null" || strns.get(x)[z].isEmpty() || strns.get(x)[z] == null){
					    				format[x][z] = " ";
					    			}else{
					    				format[x][z] = strns.get(x)[z];
					    			}
					    			////////////////System.out.printlnArrays.deepToString(format));
					    			
					    			
					    		}
					    	}
					    	
					    	////////////////System.out.printlnArrays.deepToString(format));
					    	
					    	format = RecipeShaped.convertToMinimizedStructure(format, " ");
					    	
					    	////////////////System.out.printlnArrays.deepToString(format));
					    	
					    	//////////////////System.out.printlnArrays.deepToString(format));
					    	ConfigurationSection characters = customConfig.getConfigurationSection("shaped." + s + "." + "characters");	    
						    if (customConfig == null || characters == null) continue;
						    
						    Set<String> keysss =  characters.getKeys(false);
						    
						    HashMap<Character, ItemStack> items = new HashMap<Character, ItemStack>();
						    for(String ss : keysss){
						    	
						    	ItemStack i = characters.getItemStack(ss);
						    	i = checkTexture(i, textures, "shaped." + s + "." + "characters." + ss);
						    	//////////////////System.out.printlnss + " : " + i.getType());  
						    	items.put(ss.toCharArray()[0], i);
						    }
						    
						    ItemStack result = shaped.getItemStack(s + ".result");
						    result = checkTexture(result, textures, "shaped." + s + ".result");
						    RecipeShaped rec = new RecipeShaped(result);
						   // ////System.out.println(Arrays.deepToString(format));
						    rec.setStructure(format);
						    rec.setIngredients(items);
						    if(shaped.contains(s + ".permission")){
						    	rec.setPermission(shaped.getString(s + ".permission"));
						    }
						    rec.register();
					    }
					    
			    }
			    
			    ConfigurationSection multi = customConfig.getConfigurationSection("multi");	    
			    if (!(customConfig == null || multi == null)){
			    	 Set<String> keys =  multi.getKeys(false);
					    for(String s : keys){
					    	String beforeFormat = multi.getString(s + ".structure");
					    	String[] firstFormat = beforeFormat.split("\\|");
					    	String[][] format = new String[4][4];
					    	ArrayList<String[]> strns = new ArrayList<String[]>();
					    	for(String ss : firstFormat){
					    		////////////////System.out.printlnArrays.deepToString(ss.split("\\_")));
					    		strns.add(ss.split("\\_"));
					    	}
					    	//////////////////System.out.printlnstrns.size());
					    	//////////////////System.out.printlnstrns.get(0).length);
					    	for(int x = 0; x < strns.size(); x++){
					    		for(int z = 0; z < strns.get(x).length; z++){
					    			if(strns.get(x)[0].equalsIgnoreCase("null") || strns.get(x)[0] == null){
					    				for(int b = 0; b < 4; b++){
					    					format[x][b] = " ";
					    				}
					    				break;
					    			}
					    			
					    			if(strns.get(x)[z] == "null" || strns.get(x)[z].isEmpty() || strns.get(x)[z] == null){
					    				format[x][z] = " ";
					    			}else{
					    				format[x][z] = strns.get(x)[z];
					    			}
					    			////////////////System.out.printlnArrays.deepToString(format));
					    			
					    			
					    		}
					    	}
					    	
					    	////////////////System.out.printlnArrays.deepToString(format));
					    	
					    	format = RecipeChest.convertToMinimizedStructure(format, " ");
					    	
					    	////////////////System.out.printlnArrays.deepToString(format));
					    	
					    	//////////////////System.out.printlnArrays.deepToString(format));
					    	ConfigurationSection characters = customConfig.getConfigurationSection("multi." + s + "." + "characters");	    
						    if (customConfig == null || characters == null) continue;
						    
						    Set<String> keysss =  characters.getKeys(false);
						    
						    HashMap<Character, ItemStack> items = new HashMap<Character, ItemStack>();
						    for(String ss : keysss){
						    	
						    	ItemStack i = characters.getItemStack(ss);
						    	i = checkTexture(i, textures, "multi." + s + "." + "characters" + ss);
						    	//////////////////System.out.printlnss + " : " + i.getType());  
						    	items.put(ss.toCharArray()[0], i);
						    }
						    
						   // ItemStack result = shaped.getItemStack(s + ".result");
						    ItemStack[] results = new ItemStack[4];
						    for(int b = 0; b < 4; b++){
						    	results[b] = checkTexture(multi.getItemStack(s + ".result." + b), textures, "multi." + s + ".result." + b);
						    	
						    }
						    RecipeChest rec = new RecipeChest(results);
						   // ////System.out.println(Arrays.deepToString(format));
						    rec.setStructure(format);
						    rec.setIngredients(items);
						    
						    if(multi.contains(s + ".permission")){
						    	rec.setPermission(multi.getString(s + ".permission"));
						    }
						    
						    rec.register();
					    }
					    
			    }
			    
			   
			    
			    ConfigurationSection shapeless = customConfig.getConfigurationSection("shapeless");	    
			    if (!(customConfig == null || shapeless == null)){
			    	Set<String> keyss =  shapeless.getKeys(false);
				    
				    for(String s : keyss){
				    	 ConfigurationSection items = shapeless.getConfigurationSection(s + ".items");	    
						 if (customConfig == null || items == null) continue;
						 
						    Set<String> keysss =  items.getKeys(false);
						    
						    ItemStack result = checkTexture(shapeless.getItemStack(s + ".result"), textures, "shapeless." +  s + ".result");
						    RecipeShapeless rec = new RecipeShapeless(result);
						    
						    for(String ss : keysss){
						    	ItemStack i = checkTexture(items.getItemStack(ss), textures, "shapeless." + s + ".items." + ss);
						    	rec.addIngredient(i);
						    } 
						    if(shapeless.contains(s + ".permission")){
						    	rec.setPermission(shapeless.getString(s + ".permission"));
						    }
						    
						    rec.register();
				    }
				    
			    }
			    
			    ProRecipes.getPlugin().getLogger().log(Level.INFO, "\u001B[32mLoaded " + this.shapeless.size() + 
			 			" shapeless recipes, " + this.shaped.size() + " shaped recipes, " + fur.size() + " furnace recipes, and "  + chest.size()  + " multi-craft recipes!\u001B[0m");  
			   
			    loadPacks();
			    changed = false;
			   // customConfigFile.delete();
			    // = null;
		}
		
		
		
		public void loadPacks(){
			
			ProRecipes.getPlugin().getLogger().log(Level.INFO, "\u001B[32mLoading recipe packs...\u001B[0m");
			(new File(ProRecipes.getPlugin().getDataFolder(), "Recipe Packs")).mkdir();
			for(File f : (new File(ProRecipes.getPlugin().getDataFolder(), "Recipe Packs")).listFiles()){
				if(f.getName().contains("yml")){
					try{
						 customConfig = YamlConfiguration.loadConfiguration(f);
						    ConfigurationSection textures = customConfig.getConfigurationSection("textures");
						    ConfigurationSection furnace = customConfig.getConfigurationSection("furnaces");
						    if(!(customConfig == null || furnace == null)){
						    	 Set<String> kk = furnace.getKeys(false);
								    for(String s : kk){
								    	ItemStack result = checkTexture(furnace.getItemStack(s + ".result"), textures, "furnace." + s + ".result");
								    	ItemStack source = checkTexture(furnace.getItemStack(s + ".source"), textures, "furnace." + s + ".source");
								    	RecipeFurnace rec = new RecipeFurnace(result, source);
								    	if(furnace.contains(s + ".permission")){
								    		rec.setPermission(furnace.getString(s + ".permission"));
								    	}
								    	furPack.add(rec);
								    	rec.register();
								    }
								    
						    }
						   
						    ConfigurationSection shaped = customConfig.getConfigurationSection("shaped");	    
						    if (!(customConfig == null || shaped == null)){
						    	 Set<String> keys =  shaped.getKeys(false);
								    for(String s : keys){
								    	String beforeFormat = shaped.getString(s + ".structure");
								    	String[] firstFormat = beforeFormat.split("\\|");
								    	String[][] format = new String[3][3];
								    	ArrayList<String[]> strns = new ArrayList<String[]>();
								    	for(String ss : firstFormat){
								    		////////////////System.out.printlnArrays.deepToString(ss.split("\\_")));
								    		strns.add(ss.split("\\_"));
								    	}
								    	//////////////////System.out.printlnstrns.size());
								    	//////////////////System.out.printlnstrns.get(0).length);
								    	for(int x = 0; x < strns.size(); x++){
								    		for(int z = 0; z < strns.get(x).length; z++){
								    			if(strns.get(x)[0].equalsIgnoreCase("null") || strns.get(x)[0] == null){
								    				for(int b = 0; b < 3; b++){
								    					format[x][b] = " ";
								    				}
								    				break;
								    			}
								    			
								    			if(strns.get(x)[z] == "null" || strns.get(x)[z].isEmpty() || strns.get(x)[z] == null){
								    				format[x][z] = " ";
								    			}else{
								    				format[x][z] = strns.get(x)[z];
								    			}
								    			////////////////System.out.printlnArrays.deepToString(format));
								    			
								    			
								    		}
								    	}
								    	
								    	////////////////System.out.printlnArrays.deepToString(format));
								    	
								    	format = RecipeShaped.convertToMinimizedStructure(format, " ");
								    	
								    	////////////////System.out.printlnArrays.deepToString(format));
								    	
								    	//////////////////System.out.printlnArrays.deepToString(format));
								    	ConfigurationSection characters = customConfig.getConfigurationSection("shaped." + s + "." + "characters");	    
									    if (customConfig == null || characters == null) continue;
									    
									    Set<String> keysss =  characters.getKeys(false);
									    
									    HashMap<Character, ItemStack> items = new HashMap<Character, ItemStack>();
									    for(String ss : keysss){
									    	
									    	ItemStack i = characters.getItemStack(ss);
									    	i = checkTexture(i, textures, "shaped." + s + "." + "characters." + ss);
									    	//////////////////System.out.printlnss + " : " + i.getType());  
									    	items.put(ss.toCharArray()[0], i);
									    }
									    
									    ItemStack result = shaped.getItemStack(s + ".result");
									    result = checkTexture(result, textures, "shaped." + s + ".result");
									    RecipeShaped rec = new RecipeShaped(result);
									   // ////System.out.println(Arrays.deepToString(format));
									    rec.setStructure(format);
									    rec.setIngredients(items);
									    if(shaped.contains(s + ".permission")){
									    	rec.setPermission(shaped.getString(s + ".permission"));
									    }
									    shapedPack.add(rec);
									    rec.register();
								    }
								    
						    }
						    
						    ConfigurationSection multi = customConfig.getConfigurationSection("multi");	    
						    if (!(customConfig == null || multi == null)){
						    	 Set<String> keys =  multi.getKeys(false);
								    for(String s : keys){
								    	String beforeFormat = multi.getString(s + ".structure");
								    	String[] firstFormat = beforeFormat.split("\\|");
								    	String[][] format = new String[4][4];
								    	ArrayList<String[]> strns = new ArrayList<String[]>();
								    	for(String ss : firstFormat){
								    		////////////////System.out.printlnArrays.deepToString(ss.split("\\_")));
								    		strns.add(ss.split("\\_"));
								    	}
								    	//////////////////System.out.printlnstrns.size());
								    	//////////////////System.out.printlnstrns.get(0).length);
								    	for(int x = 0; x < strns.size(); x++){
								    		for(int z = 0; z < strns.get(x).length; z++){
								    			if(strns.get(x)[0].equalsIgnoreCase("null") || strns.get(x)[0] == null){
								    				for(int b = 0; b < 4; b++){
								    					format[x][b] = " ";
								    				}
								    				break;
								    			}
								    			
								    			if(strns.get(x)[z] == "null" || strns.get(x)[z].isEmpty() || strns.get(x)[z] == null){
								    				format[x][z] = " ";
								    			}else{
								    				format[x][z] = strns.get(x)[z];
								    			}
								    			////////////////System.out.printlnArrays.deepToString(format));
								    			
								    			
								    		}
								    	}
								    	
								    	////////////////System.out.printlnArrays.deepToString(format));
								    	
								    	format = RecipeChest.convertToMinimizedStructure(format, " ");
								    	
								    	////////////////System.out.printlnArrays.deepToString(format));
								    	
								    	//////////////////System.out.printlnArrays.deepToString(format));
								    	ConfigurationSection characters = customConfig.getConfigurationSection("multi." + s + "." + "characters");	    
									    if (customConfig == null || characters == null) continue;
									    
									    Set<String> keysss =  characters.getKeys(false);
									    
									    HashMap<Character, ItemStack> items = new HashMap<Character, ItemStack>();
									    for(String ss : keysss){
									    	
									    	ItemStack i = characters.getItemStack(ss);
									    	i = checkTexture(i, textures, "multi." + s + "." + "characters" + ss);
									    	//////////////////System.out.printlnss + " : " + i.getType());  
									    	items.put(ss.toCharArray()[0], i);
									    }
									    
									   // ItemStack result = shaped.getItemStack(s + ".result");
									    ItemStack[] results = new ItemStack[4];
									    for(int b = 0; b < 4; b++){
									    	results[b] = checkTexture(multi.getItemStack(s + ".result." + b), textures, "multi." + s + ".result." + b);
									    	
									    }
									    RecipeChest rec = new RecipeChest(results);
									   // ////System.out.println(Arrays.deepToString(format));
									    rec.setStructure(format);
									    rec.setIngredients(items);
									    
									    if(multi.contains(s + ".permission")){
									    	rec.setPermission(multi.getString(s + ".permission"));
									    }
									    chestPack.add(rec);
									    rec.register();
								    }
								    
						    }
						    
						   
						    
						    ConfigurationSection shapeless = customConfig.getConfigurationSection("shapeless");	    
						    if (!(customConfig == null || shapeless == null)){
						    	Set<String> keyss =  shapeless.getKeys(false);
							    
							    for(String s : keyss){
							    	 ConfigurationSection items = shapeless.getConfigurationSection(s + ".items");	    
									 if (customConfig == null || items == null) continue;
									 
									    Set<String> keysss =  items.getKeys(false);
									    
									    ItemStack result = checkTexture(shapeless.getItemStack(s + ".result"), textures, "shapeless." +  s + ".result");
									    RecipeShapeless rec = new RecipeShapeless(result);
									    
									    for(String ss : keysss){
									    	ItemStack i = checkTexture(items.getItemStack(ss), textures, "shapeless." + s + ".items." + ss);
									    	rec.addIngredient(i);
									    } 
									    if(shapeless.contains(s + ".permission")){
									    	rec.setPermission(shapeless.getString(s + ".permission"));
									    }
									    shapelessPack.add(rec);
									    rec.register();
							    }
							    
						    }
						ProRecipes.getPlugin().getLogger().log(Level.INFO, "\u001B[32mLoaded '" + f.getName() + "' !\u001B[0m");
					}catch(Exception e){
						ProRecipes.getPlugin().getLogger().log(Level.INFO, "ERROR LOADING '" + f.getName() + "' !");
					}
					
				}
			}
		}
		
		/*@EventHandler
		public void blackList(PrepareItemCraftEvent event){
			String itemType = event.getRecipe().getResult().getType().toString().toLowerCase();
			if(RPGRecipes.getPlugin().blacklistItems.containsKey(itemType)){
				List<Short> shorts = RPGRecipes.getPlugin().blacklistItems.get(itemType);
				if(shorts.contains((short)-1)){
					event.getInventory().setResult(null);
				}else if(shorts.contains(event.getRecipe().getResult().getDurability())){
					event.getInventory().setResult(null);
				}
			}
		}*/
		
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent event){
			////System.out.println("Called");
			////System.out.println(event.getBlock().getType());
			
			if(event.getBlock().getType().equals(Material.SIGN) || 
					event.getBlock().getType().equals(Material.SIGN_POST) || event.getBlock().getType().equals(Material.WALL_SIGN)){
				
				////System.out.println("Sign");
				org.bukkit.block.Sign s = (org.bukkit.block.Sign) event.getBlock().getState();
				if(s.getLine(1).equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft", ChatColor.GOLD + "Multi-Craft"))){
					////System.out.println("line");
					if(event.getPlayer().hasPermission("prorecipes.createtable")){
						event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully removed Mutli-Craft Table!");
					}else{
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to delete an multi-craft table");
					}
				}
			}
		}
		
		@EventHandler
		public void onSignChangeEvent(SignChangeEvent event){
			
			if(event.getLine(0).equalsIgnoreCase("mTable")){
				Sign s = (Sign) event.getBlock().getState().getData();
				if(event.getBlock().getRelative(s.getFacing().getOppositeFace()).getRelative(BlockFace.UP).getType().equals(Material.AIR)){
					event.getPlayer().sendMessage("There must be a block above the sign!");
					event.setCancelled(true);
					return;
				}
				if(event.getPlayer().hasPermission("prorecipes.createtable")){
					event.setLine(1, ProRecipes.getPlugin().ms.getMessage("Multi_Craft", ChatColor.GOLD + "Multi-Craft"));
					event.setLine(0, "");
					event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully created Mutli-Craft Table!");
				}else{
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to create an multi-craft table");
				}
			}
		}
		
		
		@EventHandler
		public void onClose(InventoryCloseEvent event){
			//System.out.println("Inventory closed");
			
			if(event.getInventory().getName() != null){
				////System.out.println("name not null");
				////System.out.println(event.getInventory().getName());
				////System.out.println(ChatColor.GOLD + "Mutli-Craft");
				if(event.getInventory().getName().equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_GUI", ChatColor.GOLD + "Multi-Craft Table"))){
					////System.out.println("Name equals");
					boolean ing = false;
					boolean res = false;
					ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
					ArrayList<ItemStack> results = new ArrayList<ItemStack>();
					for(int i = 9; i < 45; i++){
						if((i-7) % 9 == 0){
							//Results
							if(event.getInventory().getItem(i) != null && !event.getInventory().getItem(i).getType().equals(Material.AIR)){
								results.add(event.getInventory().getItem(i).clone());
								res = true;
							}
						}else if(((i-1) % 9 == 0 || (i-2) % 9 == 0 || (i-3) % 9 == 0 || (i-4) % 9 == 0)){
							//Ingredients
							if(event.getInventory().getItem(i) != null && !event.getInventory().getItem(i).getType().equals(Material.AIR)){
								ing = true;
								ingredients.add(event.getInventory().getItem(i).clone());
							}
						}
					}
					
					if(res){
						//System.out.println("results");
						if(ing){
							//System.out.println("Ingredients");
							//Just drop ingredients
							for(ItemStack i : ingredients){
								event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), i);
							}
						}else{
							//System.out.println("just results");
							//just drop results
							for(ItemStack i : results){
								event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), i);
							}
						}
					}else{
						//System.out.println("just ingredients");
						//just drop ingredients
						for(ItemStack i : ingredients){
							event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), i);
						}
					}
				}
			}
		}
		
		public boolean multiTable(Block b){
			if(b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR))return false;
			
			for(BlockFace f : new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST}){
				
				Block signP = b.getRelative(BlockFace.DOWN).getRelative(f);
				if(signP.getType().equals(Material.SIGN) || 
						signP.getType().equals(Material.SIGN_POST) || signP.getType().equals(Material.WALL_SIGN)){
					org.bukkit.block.Sign s = (org.bukkit.block.Sign) b.getRelative(BlockFace.DOWN).getRelative(f).getState();
					if(s.getLine(1).equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft", ChatColor.GOLD + "Multi-Craft"))){
						return true;
					}
				}
			}
			return false;
		}
		
		public Inventory createMultiTable(Player p, int type){
			Inventory it;
			if(type == 0){
				//This was true
				it = Bukkit.createInventory(p, 54, ProRecipes.getPlugin().ms.getMessage("Multi_Craft_Enter", ChatColor.GOLD + "Enter Recipe"));
			}else if(type == 1){
				//This was false
				it = Bukkit.createInventory(p, 54, ProRecipes.getPlugin().ms.getMessage("Multi_Craft_GUI", ChatColor.GOLD + "Multi-Craft Table"));
			}else{
				it = Bukkit.createInventory(p, 54, ProRecipes.getPlugin().ms.getMessage("Multi_Craft_View", ChatColor.GOLD + "View Recipe"));
			}
			
			
			ItemStack gPane = new ItemStack(Material.STAINED_GLASS_PANE);
			gPane.setDurability((short) 13);
			ItemMeta m = gPane.getItemMeta();
			m.setDisplayName(ChatColor.BLACK + "#");
			gPane.setItemMeta(m);
			ItemStack greyPane = new ItemStack(Material.STAINED_GLASS_PANE);
			greyPane.setDurability((short)7);
			m = greyPane.getItemMeta();
			m.setDisplayName(ChatColor.BLACK + "#");
			greyPane.setItemMeta(m);
			
				for(int i = 0; i < 54; i++){
					//it.setItem(i, greyPane);
					
					//Top row
					if(i > 0 && i < 9){
						it.setItem(i, greyPane);
					//side rows	
					}else if(i % 9 == 0 || i % 9 == 8){
						it.setItem(i, greyPane);
					//bottom rows	
					}else if(i > 45 && i < 54){
						it.setItem(i, greyPane);
					//green columns	(x = 5,6), (y = 1,2,3,4)
					}else if(i < 45 && ((i-5) % 9 == 0) || ((i-6) % 9 == 0) && i > 9){
						it.setItem(i, gPane);
					}
				}
				//it.setItem(53, greyPane);
				return it;
		}
		
		public void resetBorders(final Inventory it){
			
			ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					ItemStack gPane = new ItemStack(Material.STAINED_GLASS_PANE);
					gPane.setDurability((short) 13);
					ItemMeta m = gPane.getItemMeta();
					m.setDisplayName(ChatColor.BLACK + "#");
					gPane.setItemMeta(m);
					ItemStack greyPane = new ItemStack(Material.STAINED_GLASS_PANE);
					greyPane.setDurability((short)7);
					m = greyPane.getItemMeta();
					m.setDisplayName(ChatColor.BLACK + "#");
					greyPane.setItemMeta(m);
						for(int i = 0; i < 54; i++){
							//it.setItem(i, greyPane);
							
							//Top row
							if(i > 0 && i < 9){
								it.setItem(i, greyPane);
							//side rows	
							}else if(i % 9 == 0 || i % 9 == 8){
								it.setItem(i, greyPane);
							//bottom rows	
							}else if(i > 45 && i < 54){
								it.setItem(i, greyPane);
							//green columns	(x = 5,6), (y = 1,2,3,4)
							}else if(i < 45 && ((i-5) % 9 == 0) || ((i-6) % 9 == 0) && i > 9){
								it.setItem(i, gPane);
							}
						}
				}
				
			}, 1);
			
		}
		
		@EventHandler
		public void onLick(final InventoryClickEvent event){
			//LOL TO THE METHOD NAME
			if(event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.WORKBENCH)){
				//System.out.println(event.getAction());
				switch(event.getAction()){
				case PLACE_ONE:
				case PLACE_SOME:
				case PLACE_ALL:
					ItemStack to = event.getInventory().getItem(event.getSlot()) != null ? event.getInventory().getItem(event.getSlot()).clone() : null;
					if(to != null){
						
						final CraftingInventory i = (CraftingInventory) event.getInventory();
						ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

							@Override
							public void run() {
								//System.out.println("Calling prepare!");
								callPrepare(i, event.getView());
								
							}
							
						}, 0);
						
					}
				}
			}
			
		}
		
		
		//#fakecrafting handling
		@EventHandler(priority=EventPriority.MONITOR)
		public void onClick(final InventoryClickEvent event){
			//System.out.println(event.getAction());
			
			if(event.getClickedInventory() != null && event.getClickedInventory().getName() != null){
				if(event.getClickedInventory().getName().equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_GUI", ChatColor.GOLD + "Multi-Craft Table")) || 
						event.getClickedInventory().getName().equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_Enter", ChatColor.GOLD + "Enter Recipe")) || 
						event.getClickedInventory().getName().equals(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_View", ChatColor.GOLD + "View Recipe"))){
					//System.out.println(event.getAction());
					if(event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || 
							event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction().toString().contains("DROP") 
							|| event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)){
						
						event.setCancelled(true);
						return;
					}
					ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

						@Override
						public void run() {
							int i = event.getSlot();
							if(i > 0 && i < 9){
								event.setCancelled(true);
								event.setResult(Result.DENY);
								//System.out.println("FIX CANCEL 1");
								fixCancel(event);
								return;
							}else if(i % 9 == 0 || i % 9 == 8){
								event.setCancelled(true);
								event.setResult(Result.DENY);
								//System.out.println("FIX CANCEL 2");
								fixCancel(event);
								return;
							}else if(i > 45 && i < 54){
								event.setCancelled(true);
								event.setResult(Result.DENY);
								//System.out.println("FIX CANCEL 3");
								fixCancel(event);
								return;
							}else if(i < 45 && ((i-5) % 9 == 0) || ((i-6) % 9 == 0) && i > 9){
								event.setCancelled(true);
								event.setResult(Result.DENY);
								//System.out.println("FIX CANCEL 4");
								fixCancel(event);
								return;
							}
							//System.out.println("Edges check: passed");
							
							if(event.getClickedInventory().getName().equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_GUI", ChatColor.GOLD + "Multi-Craft Table"))){
								//if they click into results and they're all empty
								//System.out.println("It's a crafting table");
								boolean results = false;
								
								
									for(int b = 9; b < 45; b++){
										if((b-7) % 9 == 0){
											//System.out.println(b);
											if(event.getClickedInventory().getItem(b) != null || 
													(event.getSlot() == b && (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)))){
												//System.out.println("There are results");
												results = true;
												break;
											}
										}
									}
								
								if((i-7) % 9 == 0){
									//System.out.println("Clicked inside the results");
									/*if(event.getCursor() != null){
										if(results){
										//System.out.println("The clicked item wasn't null");
										clearIngredients(event.getClickedInventory());
										updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
										results = true;
										}
										
									}*/
									switch(event.getAction()){
									//Can't add anything into them 
									case PLACE_ONE:
									case PLACE_SOME:
									case PLACE_ALL:
									case UNKNOWN:
									case SWAP_WITH_CURSOR:
									case DROP_ALL_CURSOR:
									case DROP_ALL_SLOT:
									case DROP_ONE_CURSOR:
									case DROP_ONE_SLOT:
									case MOVE_TO_OTHER_INVENTORY:
										//System.out.println("Place/drop");
										event.setCancelled(true);
										//System.out.println("FIX CANCEL 5");
										fixCancel(event);
									default:
										//System.out.println("trying to remove, if there are results");
										if(results){
											//System.out.println("Removing ingredients");
											clearIngredients(event.getClickedInventory());
											updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
											results = true;
											}
										return;
									}
									
									
								}else if((i-1) % 9 == 0 || (i-2) % 9 == 0 || (i-3) % 9 == 0 || (i-4) % 9 == 0){
									//System.out.println("clicked inside the ingredients");
									//Cannot place items if there are results, remove items and results dissappear
									if(results){
										boolean retur = false;
										//System.out.println("there are results");
										//System.out.println(event.getAction());
										switch(event.getAction()){
										case PLACE_ONE:
										case PLACE_SOME:
										case PLACE_ALL:
										case SWAP_WITH_CURSOR:
										case UNKNOWN:
										case DROP_ALL_CURSOR:
										case DROP_ALL_SLOT:
										case DROP_ONE_CURSOR:
										case DROP_ONE_SLOT:
										case MOVE_TO_OTHER_INVENTORY:
											//System.out.println("trying to drop into ingredients");
										//	if(!empty(event.getInventory(), event.getInventory().getItem(event.getSlot()))){
												//event.setCancelled(true);
												//event.setResult(Result.DENY);
												//fixCancel(event);
												//retur = true;
												//return;
												clearResults(event.getClickedInventory());
											//}else{
											//	System.out.println("FIX CANCEL 6");
											//	fixCancel(event);
											//	return;
											//}
											
											break;
										case COLLECT_TO_CURSOR:
										case PICKUP_ALL:
										case PICKUP_HALF:
										case PICKUP_ONE:
										case PICKUP_SOME:
										case HOTBAR_SWAP:
										case HOTBAR_MOVE_AND_READD:
											//System.out.println("trying to remove from ingredients");
											//if(!empty(event.getInventory(), null)){
											//	System.out.println("not empty");
												clearResults(event.getClickedInventory());
												//System.out.println("cleared results");
												updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
												//System.out.println("updateInventoy");
												//retur = true;
												//return;
											//}
											
										}
										
										if(retur){
											return;
										}
										
									}
									
									
									
								}
								
								//Now check for recipes
								//System.out.println("Adding ingredients (for checking)");
								ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
								boolean empty = true;
								updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
								event.getClickedInventory().setItem(event.getSlot(), event.getCurrentItem());
								for(int b = 9; b < 45; b++){
										if((b-1) % 9 == 0 || (b-2) % 9 == 0 || (b-3) % 9 == 0 || (b-4) % 9 == 0){
											ingredients.add(event.getClickedInventory().getItem(b));
											if(event.getInventory().getItem(b) != null){
												//System.out.println(event.getInventory().getItem(b));
											}else{
												//System.out.println("null");
											}
											if(event.getClickedInventory().getItem(b) != null){
												empty = false;
											}
											////System.out.println(event.getInventory().getItem(b));
										}
									
								}
								
								
								ArrayList<ItemStack> fakeResults = new ArrayList<ItemStack>();
								fakeResults.add(new ItemStack(Material.ANVIL));
								fakeResults.add(new ItemStack(Material.ANVIL));
								fakeResults.add(new ItemStack(Material.ANVIL));
								fakeResults.add(new ItemStack(Material.ANVIL));
								//System.out.println("create fake recipe");
								updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
								if(!empty){
									RecipeChest rec = RecipeBuilder.createChest(new ItemStack[4], ingredients.toArray(new ItemStack[16]), false);
									boolean match = false;
									RecipeChest ma = null;
									for(RecipeChest r : chest){
										if(rec.match(r)){
											//they're a match
											//System.out.println("There's a match");
											ma = r;
											match = true;
											break;
										}
									}
									if(match){
										//System.out.println("It matches");
										/*if(results){
											
											return;
										}else{*/
											//System.out.println("no results, continue to set results");
											clearResults(event.getClickedInventory());
											ItemStack[] resultss = ma.getResult();
											MulticraftEvent multiEvent = new MulticraftEvent(ProRecipes.getAPI().getRecipe(RecipeType.MULTI, chest.indexOf(ma)), (Player)event.getWhoClicked(), event.getClickedInventory(), resultss);
											ProRecipes.getPlugin().getServer().getPluginManager().callEvent(multiEvent);
											
											updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
										//}
									}else{
										clearResults(event.getClickedInventory());
									}
								}else{
									//System.out.println("empty");
								}
								ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

									@Override
									public void run() {
										updateInventory((Player)event.getWhoClicked(), event.getClickedInventory());
									}
									
								}, 2);
								
								//System.out.println("\n");
							}
						}
						
					}, 1);
					
					
					//System.out.println("\n");
				}
				//System.out.println("\n");
			}
			//System.out.println("\n");
		}
		
		
		public boolean empty(Inventory i, ItemStack it){
			
			boolean safe = true;
			for(int b = 9; b < 45; b++){
				if((b-1) % 9 == 0 || (b-2) % 9 == 0 || (b-3) % 9 == 0 || (b-4) % 9 == 0){
					if(i.getItem(b) != null && !i.getItem(b).getType().equals(Material.AIR)){
						if(safe && it != null){
							if(ProRecipes.itemToStringBlob(i.getItem(b)).equalsIgnoreCase(ProRecipes.itemToStringBlob(it))){
								safe = false;
								continue;
							}
						}
						return false;
					}
				}
			}
			return true;
			
		}
		
		public boolean emptyResults(Inventory i){
			for(int b = 9; b < 45; b++){
				if((b-7) % 9 == 0){
					if(i.getItem(b) != null && !i.getItem(b).getType().equals(Material.AIR)){
						return false;
					}
				}
			}
			return true;
		}
		
		public void fixCancel(final InventoryClickEvent event){
			//First try right here
			event.setCancelled(true);
			//System.out.println("BEING CANCELLED");
			//Need to know if picking up item
			switch(event.getAction()){
			case COLLECT_TO_CURSOR:
			case PICKUP_ALL:
			case PICKUP_HALF:
			case PICKUP_ONE:
			case PICKUP_SOME:
			case HOTBAR_SWAP:
			case HOTBAR_MOVE_AND_READD:
				final ItemStack pickedup = event.getCursor().clone();
				final int slot = event.getSlot();
				//Need to know what item picked up, where it was picked up from
				//Reset item, take off of cursor
				
				
					ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

						@Override
						public void run() {
							event.getWhoClicked().setItemOnCursor(null);
							event.getInventory().setItem(slot, pickedup);
							resetBorders(event.getInventory());
							
						}
						
					}, 0);
				return;
			case PLACE_ONE:
			case PLACE_SOME:
			case PLACE_ALL:
			case UNKNOWN:
			case DROP_ALL_CURSOR:
			case DROP_ALL_SLOT:
			case DROP_ONE_CURSOR:
			case DROP_ONE_SLOT:
			case MOVE_TO_OTHER_INVENTORY:
			case SWAP_WITH_CURSOR:
				//System.out.println("SOMETHING DROPPED");
				final ItemStack dropped = event.getCurrentItem().clone();
				//System.out.println(dropped.getType());
				
					final ItemStack swapped = event.getCursor().clone();
					final int s = event.getSlot();
					
				
				
				
				ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

					@Override
					public void run() {
						if(swapped != null && event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)){
							//there was a swap
							event.getWhoClicked().setItemOnCursor(dropped);
							event.getInventory().setItem(s, swapped);
						}else if(swapped != null && dropped != null && !swapped.getType().equals(Material.AIR)){
							if(swapped.isSimilar(dropped)){
							   // System.out.println("Is similar");
								event.getInventory().setItem(s, null);
								ItemStack t = dropped.clone();
								t.setAmount(dropped.getAmount() + swapped.getAmount());
								event.getWhoClicked().setItemOnCursor(t);
							}
						}else{
							//System.out.println("else");
							event.getInventory().setItem(s, null);
							event.getWhoClicked().setItemOnCursor(dropped);
						}
						
					}
					
				}, 0);
				
				
				
				return;
				
			
			}
				
			
			
			//Need to know if dropping item(s)
				//This isn't as much of a problem (Come back later if it is)
			
		}
		
		//Used for drag clicks
		public void callClick(int slot, InventoryView i){
			InventoryClickEvent event = new InventoryClickEvent(i, InventoryType.SlotType.CONTAINER, slot, ClickType.RIGHT, InventoryAction.UNKNOWN);
			ProRecipes.getPlugin().getServer().getPluginManager().callEvent(event);
		}
		
		public void callPrepare(CraftingInventory inv, InventoryView v){
			PrepareItemCraftEvent event = new PrepareItemCraftEvent(inv, v, false);
			ProRecipes.getPlugin().getServer().getPluginManager().callEvent(event);
		}
		
		@EventHandler
		public void onDrag(InventoryDragEvent event){
		//	int counter = 0;
			if(event.getInventory().getName() != null && event.getInventory().getName().equalsIgnoreCase(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_GUI", ChatColor.GOLD + "Multi-Craft Table"))){
				for(int b : event.getInventorySlots()){
					if(((b-1) % 9 == 0 || (b-2) % 9 == 0 || (b-3) % 9 == 0 || (b-4) % 9 == 0) && event.getInventorySlots().size() > 1){
						if(empty(event.getInventory(), null) && !emptyResults(event.getInventory())){
							event.setCancelled(true);
							return;
						}else{
							callClick(b, event.getView());
							
						}
						
						continue;
					}else{
						event.setCancelled(true);
						break;
					}
				}
		
				
			}
		}
		
		public void clearIngredients(Inventory it){
			
		
					for(int i = 8; i< 45; i++){
						if((i-1) % 9 == 0 || (i-2) % 9 == 0 || (i-3) % 9 == 0 || (i-4) % 9 == 0){
							it.setItem(i, null);
						}
					}
				
			
		}
		
		public void clearResults(Inventory it){
		//	System.out.println("Clearing results...");
					for(int i = 9; i < 45; i++){
						if((i-7) % 9 == 0){
							it.setItem(i, null);
						}
					}
				
			
		}
		
		public void updateInventory(Player p, Inventory i){
			ItemStack[] contents = i.getContents();
			i.clear();
			//p.closeInventory();
			i.setContents(contents);
			//p.openInventory(i);
		}
		
		@EventHandler
		public void onInteract(PlayerInteractEvent event){
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				if(multiTable(event.getClickedBlock())){
					
						//c.setContents(items);
						event.setCancelled(true);
					event.getPlayer().openInventory(createMultiTable(event.getPlayer(), 1));
					
				}
			}
		}
		
		
		@EventHandler
		public void afterCraftShaped(final CraftItemEvent event){
			//System.out.println("Calledddd");
			
			////////////////System.out.println"Craft called");
			if(!(event.getRecipe() instanceof ShapedRecipe))return;
			if(event.getInventory().getResult() == null)return;
			ShapedRecipe recip = (ShapedRecipe)event.getRecipe();
			boolean check = false;
			////////////System.out.println("Crafted called");
			CraftingInventory inv = event.getInventory();
			////////////////System.out.println(event.getViewers().size());
			
			Player p = (Player) event.getViewers().get(0);
			if(p == null){
				p =  (Player)event.getView().getPlayer();
			}
			
			ItemStack[] items = inv.getContents();
			
			//boolean value used to denote whether it is actually a "special" recipe or not
			boolean anvils = false;
			
			//Check to see if special
			ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
			for(ItemStack i : items){
			// System.out.println(i.getType());
				arr.add(i);
			}
			arr.remove(event.getInventory().getResult());
			////System.out.println(event.getRecipe().getResult());
			
			if(event.getRecipe().getResult().hasItemMeta()){
				////System.out.println("has meta");
				if(event.getRecipe().getResult().getItemMeta().hasDisplayName()){
					////System.out.println("has display name");
					if(event.getRecipe().getResult().getItemMeta().getDisplayName().equalsIgnoreCase("recipedshapeditem")){
						////System.out.println("Is shapeddd");
						////System.out.println("Anvils");
						ItemStack i = new ItemStack(Material.TRIPWIRE_HOOK);
						ItemMeta m = i.getItemMeta();
						m.setDisplayName("recipedshapeditem");
						i.setItemMeta(m);
						arr.remove(i);
						anvils = true;
						check = true;
						//System.out.println("It's shaped");
						////////////////////System.out.println"It is shaped");
					}
				}
			}
			if(!anvils){
				for(RecipeShaped rec : conflictsShaped.keySet()){
					////System.out.println("Checking conflict");
					if(matchConflict(rec, recip)){
						////System.out.println("Is a conflict");
						check = true;
						arr.remove(recip.getResult());
						break;
					}
				}
			}
			
						//Check match
			if(check){
				ItemStack[][] converted = RecipeShaped.convertToMinimizedStructure(RecipeShaped.convertToArray(arr.toArray(new ItemStack[9])));
				for(RecipeShaped recipe : shaped){
					//System.out.println(recipe.idCache);
					final RecipeShaped re = recipe;
					if(recipe.matchLowest(converted)){
						if(!recipe.moreThanOne())return;
						// System.out.println("THEY MATCH BOY");
						////System.out.println("Matches \n");
						//event.getInventory().setResult(recipe.getResult());
						new BukkitRunnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								 event.getInventory().setMatrix(re.subtractMatrix(event.getInventory().getMatrix()));
							}
							
						}.runTaskLater(ProRecipes.getPlugin(), 0);
						
						return;
					}else{
						//////////System.out.println("Doesn't match \n");
					}
				}
			}
			
			
			//It is a "special" recipe but the item information on the ingredients is incorrect. The result must be air.
			//if(anvils){
				//event.getInventory().setResult(new ItemStack(Material.AIR));
			//}
		}
		@EventHandler
		public void afterCraft(final CraftItemEvent event){
			
			CraftingInventory inv = event.getInventory();
			if(!(event.getRecipe() instanceof ShapelessRecipe))return;
			ItemStack[] items = inv.getContents();
			if(event.getInventory().getResult() == null)return;
			//////////////System.out.println(event.getViewers().size());
			if(event.getViewers().size() <= 0){
				////////////////System.out.println("setting to air");
				event.getInventory().setItem(0, null);
				event.getInventory().setResult(new ItemStack(Material.AIR));
				return;
			}
			Player p = (Player) event.getViewers().get(0);
			if(p == null){
				p =  (Player)event.getView().getPlayer();
			}
			final Player c = p;
			
			if(p.hasMetadata("itemBuilder") || p.hasMetadata("recipeBuilder") || p.hasMetadata("recipeViewer")){
				
				if(p.hasMetadata("recipeViewer")){
					String step = p.getMetadata("recipeViewer").get(0).asString();
					if(p.getMetadata("recipeViewer").get(0).asString().contains("display")){
						int id = p.getMetadata("recipeId").get(0).asInt();
						if(step.equalsIgnoreCase("displayShaped")){
							RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(id);
							ItemStack result = rec.getResult().clone();
							if(p.hasPermission("prorecipes.modifyrecipes")){
								ItemMeta m = result.getItemMeta();
								List<String> lore = m.hasLore() ? m.getLore() : new ArrayList<String>();
								if(rec.hasPermission()){
									lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
								}else{
									lore.add(ChatColor.RED + "No Permission set");
								}
								lore.add(ChatColor.GREEN + "Click to edit permissions");
								m.setLore(lore);
								result.setItemMeta(m);
							}
							event.getInventory().setItem(0, result);
							event.getInventory().setResult(result);
						}else if(step.equalsIgnoreCase("displayShapeless")){
							RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
							ItemStack result = rec.getResult().clone();
							if(p.hasPermission("prorecipes.modifyrecipes")){
								ItemMeta m = result.getItemMeta();
								List<String> lore = m.hasLore() ? m.getLore() : new ArrayList<String>();
								if(rec.hasPermission()){
									lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
								}else{
									lore.add(ChatColor.RED + "No Permission set");
								}
								lore.add(ChatColor.GREEN + "Click to edit permissions");
								m.setLore(lore);
								result.setItemMeta(m);
							}
							event.getInventory().setItem(0, result);
							event.getInventory().setResult(result);
						}else if(step.equalsIgnoreCase("displayFurnace")){
							RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(id);
							ItemStack result = rec.result.clone();
							if(p.hasPermission("prorecipes.modifyrecipes")){
								ItemMeta m = result.getItemMeta();
								List<String> lore = m.hasLore() ? m.getLore() : new ArrayList<String>();
								if(rec.hasPermission()){
									lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
								}else{
									lore.add(ChatColor.RED + "No Permission set");
								}
								lore.add(ChatColor.GREEN + "Click to edit permissions");
								m.setLore(lore);
								result.setItemMeta(m);
							}
							event.getInventory().setItem(0, result);
							event.getInventory().setResult(result);
						}
						return;
					}else{
						event.getInventory().setItem(0, null);
						event.getInventory().setResult(new ItemStack(Material.AIR));
						//p.updateInventory();
						return;
					}
				}else{
					//event.getInventory().setItem(0, null);
					//event.getInventory().setResult(new ItemStack(Material.AIR));
					//p.updateInventory();
					return;
				}
			}
			RecipeShapeless r = new RecipeShapeless(new ItemStack(Material.ANVIL));
			//System.out.println(event.getRecipe().getResult());
			//ShapelessRecipe recc = (ShapelessRecipe)event.getRecipe();
			//for(ItemStack i : recc.getIngredientList()){
				//System.out.println(i);
			//}
			
			int anvil = 0;
			
			//boolean value used to denote whether it is actually a "special" recipe or not
			boolean anvils = false;
			
			//Add ingredients to mock recipe
			for(ItemStack i : items){
				if(!i.getType().equals(Material.AIR)){
					if(i.equals(new ItemStack(Material.ANVIL)) && anvil == 0){
						anvil = 1;
						continue;
					}
					if(anvil == 1){
						anvils = true;
					}
					if(!event.getInventory().getResult().equals(i)){
						r.addIngredient(i);
					}
					
				}
			}
			//Check match
			for(RecipeShapeless recipe : shapeless){
				if(recipe.matchLowest(r)){
					if(!recipe.moreThanOne())return;
					final RecipeShapeless re = recipe;
					
							final ItemStack[] arrr = re.subtractMatrix(event.getInventory().getMatrix());
							
							new BukkitRunnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									event.getInventory().setMatrix(arrr);
								}
								
							}.runTaskLater(ProRecipes.getPlugin(), 0);
						
					
					return;
				}
			}
			
			
			
		}
		
		public boolean handleDoubles(ItemStack[] items, ItemStack result, Player p, Inventory inv){
			//long mil = System.currentTimeMillis();
			RecipeShapeless r = new RecipeShapeless(new ItemStack(Material.ANVIL));
			
			for(ItemStack i : items){
				if(i != null && i.getType() != Material.AIR){
					r.addIngredient(i);
				}
			}
			
			r.removeIngredient(result);
			int ing = 0;
			
			RecipeShapeless fina = null;
			for(RecipeShapeless recipe : shapeless){
				if(recipe.matchLowest(r)){
					if(recipe.ingredientCount() >= ing){
						ing = recipe.ingredientCount();
					}else{
						continue;
					}
			
					if(recipe.hasPermission()){
						if(!p.hasPermission(recipe.getPermission())){
							continue;
						}
					}
					
					fina = recipe;
					
				}
			
			}
			
			if(fina != null){
				
				ItemStack old = result;
				
				WorkbenchCraftEvent workbenchEvent = new WorkbenchCraftEvent(ProRecipes.getAPI().getRecipe(RecipeType.SHAPELESS, shapeless.indexOf(fina)), p, inv, fina.getResult());
				ProRecipes.getPlugin().getServer().getPluginManager().callEvent(workbenchEvent);
						
				if(old != null){
					if(!old.isSimilar(fina.getResult())){
						p.updateInventory();
					}
				}
				//System.out.println("Milliseconds spent (handleDoubles): " + (mil - System.currentTimeMillis()));
				return true;
			}
			//System.out.println("Milliseconds spent (handleDoubles): " + (mil - System.currentTimeMillis()));
			return false;
			
		}
		

		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onCraft(PrepareItemCraftEvent event){
			//System.out.println("Calledddd");
			
			CraftingInventory inv = event.getInventory();
			
			if(!(event.getRecipe() instanceof ShapelessRecipe))return;
			ItemStack[] items = inv.getContents();
			//System.out.println(event.getViewers().size());
			
			//System.out.println("ITEMS SIZE: " + items.length);
			
			if(event.getViewers().size() <= 0){
			//	System.out.println("setting to air");
				event.getInventory().setItem(0, null);
				event.getInventory().setResult(new ItemStack(Material.AIR));
				return;
			}
			
			
			
			Player p = (Player) event.getViewers().get(0);
			if(p == null){
				p =  (Player)event.getView().getPlayer();
			}
			final Player c = p;
			/*if(p.hasMetadata("noprecraft")){
				int t  = p.getMetadata("noprecraft").get(0).asInt();
				if(t == 2){
					
				}else{
					t++;
					p.setMetadata("noprecraft", new FixedMetadataValue(RPGRecipes.getPlugin(), t));
					return;
				}
			
			}*/
			if(p.hasMetadata("itemBuilder") || p.hasMetadata("recipeBuilder") || p.hasMetadata("recipeViewer")){
				
				
				if(p.hasMetadata("recipeViewer")){
					String step = p.getMetadata("recipeViewer").get(0).asString();
					if(p.getMetadata("recipeViewer").get(0).asString().contains("display")){
						int id = p.getMetadata("recipeId").get(0).asInt();
						if(step.equalsIgnoreCase("displayShaped")){
							RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(id);
							ItemStack result = rec.getResult().clone();
							if(p.hasPermission("prorecipes.modifyrecipes")){
								ItemMeta m = result.getItemMeta();
								List<String> lore = m.hasLore() ? m.getLore() : new ArrayList<String>();
								if(rec.hasPermission()){
									lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
								}else{
									lore.add(ChatColor.RED + "No Permission set");
								}
								lore.add(ChatColor.GREEN + "Click to edit permissions");
								m.setLore(lore);
								result.setItemMeta(m);
							}
							event.getInventory().setItem(0, result);
							event.getInventory().setResult(result);
						}else if(step.equalsIgnoreCase("displayShapeless")){
							RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
							ItemStack result = rec.getResult().clone();
							if(p.hasPermission("prorecipes.modifyrecipes")){
								ItemMeta m = result.getItemMeta();
								List<String> lore = m.hasLore() ? m.getLore() : new ArrayList<String>();
								if(rec.hasPermission()){
									lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
								}else{
									lore.add(ChatColor.RED + "No Permission set");
								}
								lore.add(ChatColor.GREEN + "Click to edit permissions");
								m.setLore(lore);
								result.setItemMeta(m);
							}
							event.getInventory().setItem(0, result);
							event.getInventory().setResult(result);
						}else if(step.equalsIgnoreCase("displayFurnace")){
							RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(id);
							ItemStack result = rec.result.clone();
							if(p.hasPermission("prorecipes.modifyrecipes")){
								ItemMeta m = result.getItemMeta();
								List<String> lore = m.hasLore() ? m.getLore() : new ArrayList<String>();
								if(rec.hasPermission()){
									lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
								}else{
									lore.add(ChatColor.RED + "No Permission set");
								}
								lore.add(ChatColor.GREEN + "Click to edit permissions");
								m.setLore(lore);
								result.setItemMeta(m);
							}
							event.getInventory().setItem(0, result);
							event.getInventory().setResult(result);
						}
						return;
					}else{
						event.getInventory().setItem(0, null);
						event.getInventory().setResult(new ItemStack(Material.AIR));
						//p.updateInventory();
						return;
					}
				}else{
					event.getInventory().setItem(0, null);
					event.getInventory().setResult(new ItemStack(Material.AIR));
					//p.updateInventory();
					return;
				}
			}
			
			
			
			//Mock recipe to match with any possible recipes
			RecipeShapeless r = new RecipeShapeless(new ItemStack(Material.ANVIL));
			//System.out.println(event.getRecipe().getResult());
	  // ShapelessRecipe recc = (ShapelessRecipe)event.getRecipe();
		///for(ItemStack i : recc.getIngredientList()){
		//	System.out.println(i);
	   // }
			
			int anvil = 0;
			
			//boolean value used to denote whether it is actually a "special" recipe or not
			boolean anvils = false;
			
			if(event.getRecipe() != null && event.getRecipe().getResult() != null && event.getRecipe().getResult().getType().equals(Material.ANVIL)){
				anvils = true;
			}
			//Add ingredients to mock recipe
			for(ItemStack i : items){
				if(i != null && i.getType() != Material.AIR){
				//	System.out.println("\n" + i.toString() + "\n");
					r.addIngredient(i);
				}
			}
			r.removeIngredient(event.getInventory().getResult());
			//Check match
			int ing = 0;
			//System.out.println(shapeless.size());
			boolean set = false;
			//System.out.println(r.items.toString());
			RecipeShapeless fina = null;
			for(RecipeShapeless recipe : shapeless){
				if(recipe.matchLowest(r)){
					if(recipe.ingredientCount() >= ing){
						ing = recipe.ingredientCount();
					}else{
						continue;
					}
				//	System.out.println("MATCHED");
				//	System.out.println("EVENT RECIPE: " + event.getRecipe().getResult());
				//	System.out.println("INV RECIPE " + event.getInventory().getResult());
					//System.out.println("CHECKED RECIPE " + recipe.getResult());
					//event.getInventory().setResult(null);
					
					
					if(recipe.hasPermission()){
						if(!p.hasPermission(recipe.getPermission())){
							//event.getInventory().setResult(null);
							continue;
						}
					}
					fina = recipe;
					
					
					//event.getInventory().setMatrix(recipe.subtractMatrix(event.getInventory().getMatrix()));
					//return;
				}
			
				
				}
			
			if(fina != null){
				
				ItemStack old = event.getInventory().getResult() != null ? event.getInventory().getResult() : null;
				
				//event.getInventory().setResult(fina.getResult());
				//event.getInventory().setItem(0, fina.getResult());
				//event.getInventory().setResult(new ItemStack(Material.AIR));
				
				WorkbenchCraftEvent workbenchEvent = new WorkbenchCraftEvent(ProRecipes.getAPI().getRecipe(RecipeType.SHAPELESS, shapeless.indexOf(fina)), p, event.getInventory(), fina.getResult());
				ProRecipes.getPlugin().getServer().getPluginManager().callEvent(workbenchEvent);
						
				set = true;
				if(old != null){
					if(!old.isSimilar(fina.getResult())){
						//System.out.println("CALLING UPDATE!");
						((Player)event.getView().getPlayer()).updateInventory();
					}
				}
				
				}
			
			//It is a "special" recipe but the item information on the ingredients is incorrect. The result must be air.
			if(anvils && !set){
				event.getInventory().setResult(new ItemStack(Material.AIR));
			}
			
		}
		
		@EventHandler(priority=EventPriority.MONITOR)
		public void onSmelt(final FurnaceSmeltEvent event){
			
			for(RecipeFurnace rec : fur){
				if(rec.toBurn.getType().equals(event.getSource().getType())){
					if(rec.match(event.getSource())){
						////System.out.println("Matched");
						////System.out.println("1");
						Furnace f = ((Furnace)event.getBlock().getState());
						Inventory inv = f.getInventory();
						
						
						//f.update();
						////System.out.println(rec.result.toString());
						FurnaceCraftEvent eventFurnace = new FurnaceCraftEvent(ProRecipes.getAPI().getRecipe(RecipeType.FURNACE, fur.indexOf(rec)), inv, rec.getResult(), event.getSource());
						ProRecipes.getPlugin().getServer().getPluginManager().callEvent(eventFurnace);
						
						//System.out.println("1");
						event.setCancelled(true);
						break;
					}else if(!rec.def){
						////System.out.println("2");
						////System.out.println("It doesnt match and it doesn't have an original");
						////////////System.out.println("Doesn't");
						event.setResult(null);
						event.setCancelled(true);
						///return;
						//return;
						//System.out.println("2");
					}else{
						////System.out.println("3");
						//////System.out.println("It has an original");
						event.setResult(rec.original.getResult());
						event.setCancelled(false);
						//System.out.println("3");
					//	return;
					}
				}
				
			}
			
			if(event.getSource().getAmount()-1 > 0){
				//Check if there's source, then set a timer for the amount of time for new one to finish, then add the old one on after that. 
				ProRecipes.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(ProRecipes.getPlugin(), new Runnable(){

					@Override
					public void run() {
						//refreshCraft(((Furnace)event.getBlock().getState()).getInventory());
					}
					
				}, 1);
			}
			
			////System.out.println("Cancelled: " + event.isCancelled());
			////System.out.println("Done");
			
		}
		
		@EventHandler
		public void furnaceShiftClickFix(InventoryClickEvent event){
			final Player p = (Player)event.getWhoClicked();
			if(p != null){
				//p.sendMessage("Clicked");
				if(event.getInventory().getType().equals(InventoryType.FURNACE)){
				//	p.sendMessage("It's a furnace");
					if(event.isShiftClick()){
					//	p.sendMessage("Update ivnentory");
						new BukkitRunnable(){

							@Override
							public void run() {
								p.updateInventory();
								
							}
							
						}.runTaskLater(ProRecipes.getPlugin(), 0);
						
					}
				}
			}
			
		}
			
			
		
		
		@EventHandler
		public void noClickFurnace(InventoryClickEvent event){
			if(event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.FURNACE)){
				//System.out.println(event.getAction());
				if(event.getRawSlot() == 0){
					//System.out.println(event.getRawSlot());

					//System.out.println(event.getAction());
					switch(event.getAction()){
						case PLACE_ONE:
						case PLACE_SOME:
						case PLACE_ALL:
						case UNKNOWN:
						case DROP_ALL_CURSOR:
						case DROP_ALL_SLOT:
						case DROP_ONE_CURSOR:
						case DROP_ONE_SLOT:
						case MOVE_TO_OTHER_INVENTORY:
						case SWAP_WITH_CURSOR:
							boolean match = false;
							RecipeFurnace rec = null;
							if(event.getCursor() == null || event.getCursor().getType() == Material.AIR){
								
								return;
							}
							ItemStack c = event.getCursor().clone();
							for(RecipeFurnace r : fur){
								if(r.match(c)){
									if(r.hasPermission()){
										if(!((Player)event.getWhoClicked()).hasPermission(r.getPermission())){
											event.setCancelled(true);
											event.getWhoClicked().sendMessage(ProRecipes.getPlugin().ms.getMessage("no_permission_smelt", ChatColor.RED + "You do not have permission to smelt this item."));
											return;
										}
									}
								}
							}
					}
				}
				
			}
		}
		
		@EventHandler
		public void onDraggy(InventoryDragEvent event){
			if(event.getInventory().getType().equals(InventoryType.FURNACE)){
				event.setCancelled(true);
			}
		}
		
		
		public void refreshCraft(final FurnaceInventory inv){
			final ItemStack i = inv.getResult().clone();
			
			inv.setResult(null);
					
				
			
			ProRecipes.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					if(inv.getResult() != null && inv.getResult().isSimilar(i)){
						ItemStack ne = i.clone();
						i.setAmount(i.getAmount() + inv.getResult().getAmount());
						inv.setResult(ne);
					}else{
						inv.getHolder().getLocation().getWorld().dropItem(inv.getHolder().getLocation(), i);
					}
					
				}
				
				
				}, 20*10 - 1);
			
			
			
			}
		
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onCraftShaped(PrepareItemCraftEvent event){
			
			//System.out.println("Called shaped recipe");
			
			if(!(event.getRecipe() instanceof ShapedRecipe))return;
			ShapedRecipe recip = (ShapedRecipe)event.getRecipe();
			boolean check = false;
			CraftingInventory inv = event.getInventory();
			if(event.getViewers().size() <= 0){
				
				event.getInventory().setItem(0, null);
				event.getInventory().setResult(new ItemStack(Material.AIR));
				return;
			}
			
			Player p = (Player) event.getViewers().get(0);
			
			if(p == null){
				
				p =  (Player)event.getView().getPlayer();
				
			}
			
			if(p.hasMetadata("itemBuilder") || p.hasMetadata("recipeBuilder") || p.hasMetadata("recipeViewer")){
				////////////System.out.println("setting to air");
				
				if(p.hasMetadata("recipeViewer")){
					String step = p.getMetadata("recipeViewer").get(0).asString();
					if(p.getMetadata("recipeViewer").get(0).asString().contains("display")){
						int id = p.getMetadata("recipeId").get(0).asInt();
						//////System.out.println(id);
						if(step.equalsIgnoreCase("displayShaped")){
							RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(id);
							event.getInventory().setItem(0, rec.getResult());
							event.getInventory().setResult(rec.getResult());
						}else if(step.equalsIgnoreCase("displayShapeless")){
							RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
							event.getInventory().setItem(0, rec.getResult());
							event.getInventory().setResult(rec.getResult());
						}else if(step.equalsIgnoreCase("displayFurnace")){
							RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(id);
							event.getInventory().setResult(rec.result);
							event.getInventory().setItem(0, rec.result);
						}else{
							return;
						}
						return;
					}else{
						event.getInventory().setItem(0, null);
						event.getInventory().setResult(new ItemStack(Material.AIR));
						//p.updateInventory();
						return;
					}
				}else{
					event.getInventory().setItem(0, null);
					event.getInventory().setResult(new ItemStack(Material.AIR));
					//p.updateInventory();
					return;
				}
				
				
			}
			
			ShapedRecipe reccc = (ShapedRecipe)event.getRecipe();
			
			//System.out.println("Ingredient map: \n\n" +  reccc.getIngredientMap().toString());
			
			//for(ShapedRecipe rec : doubles){
			//	//System.out.println("DOUBLES: " + rec.getIngredientMap().toString() + "\n");
			///}
			
			if(isDouble(reccc)){
			//	System.out.println("Doubles Contains");
				if(handleDoubles(event.getInventory().getContents(), event.getInventory().getResult(), p, event.getInventory())){
					//System.out.println("Handled..");
					return;
				}else{
					//System.out.println("Not handled...");
				}
			}
			
			ItemStack[] items = inv.getContents();
			
			//boolean value used to denote whether it is actually a "special" recipe or not
			boolean anvils = false;
			
			//Check to see if special
			ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
			for(ItemStack i : items){
			// System.out.println(i.getType());
				arr.add(i);
			}
			//System.out.println(event.getRecipe().getResult());
			
			if(event.getRecipe().getResult().hasItemMeta()){
				////System.out.println("has meta");
				if(event.getRecipe().getResult().getItemMeta().hasDisplayName()){
					////System.out.println("has display name");
					if(event.getRecipe().getResult().getItemMeta().getDisplayName().equalsIgnoreCase("recipedshapeditem")){
					
						arr.remove(event.getRecipe().getResult());
						anvils = true;
						check = true;
						//System.out.println("It's shaped");
						////////////////////System.out.println"It is shaped");
					}
				}
			}
			if(!anvils){
			// System.out.println("Checking conflicts");
				for(RecipeShaped rec : conflictsShaped.keySet()){
				 //System.out.println("Checking conflict");
					if(matchConflict(rec, recip)){
					//	 System.out.println("Is a conflict");
						check = true;
						arr.remove(recip.getResult());
						break;
					}
				}
			}
			
			
						//Check match
			if(check){
				ItemStack[][] converted = RecipeShaped.convertToMinimizedStructure(RecipeShaped.convertToArray(arr.toArray(new ItemStack[9])));
				for(RecipeShaped recipe : shaped){
					//////System.out.println(recipe.idCache);
					if(recipe.matchLowest(converted)){
						//System.out.println("THEY MATCH BOY");
						////System.out.println("Matches \n");
						ItemStack i = recipe.getResult().clone();
						if(recipe.hasPermission()){
							if(!p.hasPermission(recipe.getPermission())){
								continue;
							}
						}
						WorkbenchCraftEvent workbenchEvent = new WorkbenchCraftEvent(ProRecipes.getAPI().getRecipe(RecipeType.SHAPED, shaped.indexOf(recipe)), p, event.getInventory(), recipe.getResult());
						ProRecipes.getPlugin().getServer().getPluginManager().callEvent(workbenchEvent);
						//event.getInventory().setResult(recipe.getResult());
						return;
					}else{
						//////////System.out.println("Doesn't match \n");
					}
				}
			}
			
			
			//It is a "special" recipe but the item information on the ingredients is incorrect. The result must be air.
			if(anvils){
				event.getInventory().setResult(new ItemStack(Material.AIR));
			}
			
		}
		
		
		public boolean isDouble(ShapedRecipe rec){
			//long mil = System.currentTimeMillis();
			for(ShapedRecipe shaped : doubles){
				if(shaped.getIngredientMap().equals(rec.getIngredientMap())){
					//System.out.println("Milliseconds spent (isDouble): " + (mil - System.currentTimeMillis()));
					return true;
				}
			}
			//System.out.println("Milliseconds spent (isDouble): " + (mil - System.currentTimeMillis()));
			return false;
			
		}
		
		public boolean matchConflict(RecipeShaped rec, ShapedRecipe r){
			if(conflictsShaped.containsKey(rec)){
				 //System.out.println("Contains");
				for(ShapedRecipe rr : conflictsShaped.get(rec)){
					if(rec.matchMaps(rr.getIngredientMap(), r.getIngredientMap())){
						// System.out.println("Maps match");
						if(Arrays.deepEquals(rr.getShape(),r.getShape())){
							// System.out.println("Shape is the same");
							return true;
						}else{
							// System.out.println("Shape is not the same");
						}
						
					}else{
						// System.out.println("Maps don't match");
					}
				}
				
				
			}
			//////System.out.println("Doesn't contain");
			return false;
		}
		
		/*public boolean matchMaps(Map<Character, ItemStack> a, Map<Character, ItemStack> b){
			boolean match = true;
			for(java.util.Map.Entry<Character, ItemStack> e : a.entrySet()){
				if(e.getValue() != null){
					////System.out.println("A : " + e.getKey() + " : " + e.getValue().toString());
				}else{
					////System.out.println("A : " + e.getKey() + " : null");
				}
				
				if(!b.entrySet().contains(e)){
					match = false;
					break;
				}
			}
			
			for(java.util.Map.Entry<Character, ItemStack> e : b.entrySet()){
				if(e.getValue() != null){
					////System.out.println("B : " + e.getKey() + " : " + e.getValue().toString());
				}else{
					////System.out.println("B : " + e.getKey() + " : null");
				}
				if(!a.entrySet().contains(e)){
					match = false;
					break;
				}
			}
			
			return match;
		}*/
		
		/*public boolean matchMaps(Map<Character, ItemStack> a, Map<Character, ItemStack> b){
			boolean match = true;
			
			//Go through first replace Material.AIR with null
			ArrayList<Character> setAir = new ArrayList<Character>();
			a.remove(" ");
			b.remove(" ");
			for(Character s : a.keySet()){
				if(a.get(s) == null){
					setAir.add(s);
				}
			}
			for(Character s : setAir){
				a.put(s, new ItemStack(Material.AIR));
			}
			
			setAir.clear();
			
			for(Character s : a.keySet()){
				if(b.get(s) == null){
					setAir.add(s);
				}
			}
			
			for(Character s : setAir){
				b.put(s, new ItemStack(Material.AIR));
			}
			
			ArrayList<String> aS = new ArrayList<String>();
			ArrayList<String> bS = new ArrayList<String>();
			
			
			
			//////System.out.println("AAAAAAAAAAAA");
			for(java.util.Map.Entry<Character, ItemStack> e : a.entrySet()){
				String item = e.getValue() != null ? e.getValue().toString() : "null";
				//////System.out.println(e.getKey().toString() + " " + item);
				aS.add(e.getKey().toString() + " " + item);
			}
			//////System.out.println("\nBBBBBBBBBBBBBBBBBBBBB");
			for(java.util.Map.Entry<Character, ItemStack> e : b.entrySet()){
				String item = e.getValue() != null ? e.getValue().toString() : "null";
				//////System.out.println(e.getKey().toString() + " " + item);
				bS.add(e.getKey().toString() + " " + item);
			}
			
			for(String s : aS){
				if(!bS.contains(s)){
					match = false;
					break;
				}
			}
			for(String s : bS){
				if(!aS.contains(s)){
					match = false;
					break;
				}
			}
			
			
			return match;
		}*/
		
		public String join(String delimiter, String[] s){
			StringBuilder sb = new StringBuilder();
			String separator = "";
			for(String item : s){
			    sb.append(separator);
			    separator=delimiter;
			    sb.append(item);
			}
			String joined = sb.toString();
			return joined;
		}
		
		public RecipeContainer getRecipe(ItemStack l){
			RecipeContainer rec = null;
			ArrayList<RecipeContainer> recs = new ArrayList<RecipeContainer>();
			recs.addAll(fur);
			recs.addAll(shaped);
			recs.addAll(shapeless);
			for(RecipeContainer r : recs){
				if(r.getResult().isSimilar(l)){
					rec = r;
					break;
				}
			}
			return rec;
		}

		
}
