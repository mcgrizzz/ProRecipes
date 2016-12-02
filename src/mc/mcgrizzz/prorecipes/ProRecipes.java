package mc.mcgrizzz.prorecipes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import mc.mcgrizzz.prorecipes.Metrics.Graph;
import mc.mcgrizzz.prorecipes.Metrics.Plotter;
import mc.mcgrizzz.prorecipes.RecipeAPI.RecipeType;
import mc.mcgrizzz.prorecipes.NBTChecker.MinecraftVersion;

public class ProRecipes extends JavaPlugin implements Listener{
	
	/**
	 * 
	 * WELCOME to the source of my eternal pain. I know it's not pretty, some of it's just plain lazy. I know you'll eventually forgive me. 
	 * 
	 * I have never had time to officially clean up the mess that is this shitty waste of my useless life. That's why I decided to
	 * release it open source, for free. I don't have nearly as much time as I used to. This is for the best. Please submit code 
	 * changes + optimizations etc
	 * 
	 * Thank you to everyone who purchased ProRecipes and left kind comments in regards to it. 
	 * 
	 * I know now that I will burn eternally in H.E.L.L (hairy ecstasy of lucrative lubricant) for this. 
	 * 
	 * 
	 * 
	 * P.S. I know A LOT needs improvement. Going back I would have done many things differently. This project is a result of a bet 
	 * that I could make a plugin with more functionality and usability than the other recipe plugins out there. Then it turned into 
	 * a passion. Then life hit me. Thus, it's messy and ugly and disgusting and I hate my baby.  This is where you guys come in. 
	 * Fix my baby. 
	 *  
	 * Feel free to make whatever changes you like (without removing functionality). 
	 */
	
	ArrayList<org.bukkit.inventory.Recipe> defaultRecipes = new ArrayList<org.bukkit.inventory.Recipe>();
	
	
	protected static ProRecipes me;
	protected static RecipeAPI api;
	
	boolean loadedRecipes = false;
	
	Inventory creative;
	
	Recipes rec;
	ItemBuilder b;
	RecipeBuilder rb;
	RecipeManager rm;
	ItemStack i;
	
	public boolean title;
	public boolean prompts;
	public int wait;
	boolean fastCraft, spigot, checkUpdate;
	
	public Messages ms;
	
	String id;
	String version;
	
	
	boolean updateNeeded;
	
	
	
	
	protected static MinecraftVersion mv;
	
	HashMap<String, List<Short>> blacklistItems = new HashMap<String, List<Short>>();
	
	EventListener listener;
	
	HashMap<RecipeType, Integer> craftedRecipes = new HashMap<RecipeType, Integer>();
	
	ResourceInfo info;
	
	public static String airString;
	
	
	
	boolean notAuthentic = false;
	
	@Override
	public void onEnable(){
		
		
		me = this;
		
		info = new ResourceInfo();
		
		api = new RecipeAPI();
		
		
		
		ConsoleCommandSender console = getServer().getConsoleSender();
		String version;
		
		
		
	 	try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            version = "NOPE";
        }
	 	
	 	if(version != "NOPE"){
	 		
	 	}else{
	 		console.sendMessage(ChatColor.DARK_RED +"[ProRecipes] UNABLE TO GET SERVER VERSION! NBTTAGS WILL NOT BE SUPPORTED!");
	 	}
	 	///System.out.println(version);
	 	mv = MinecraftVersion.fromId(version);
	 	if(mv != MinecraftVersion.NoVersion){
	 		console.sendMessage(ChatColor.GREEN +"[ProRecipes] Version detected for NBTTag support!: " + ChatColor.DARK_GREEN + mv.name());
	 	}
		
		this.id = "ProRecipes";
		
		this.getConfig().addDefault("checkUpdate", true);
		checkUpdate = getConfig().getBoolean("checkUpdate");
		
		if(checkUpdate){
			updateNeeded = updateNeeded(true);
			if(!updateNeeded){
				System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString() + "[ProRecipes] Up to date!"+ Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
			}
		}
		
		
		fastCraft = false;
		
		ms = new Messages();
		rec = new Recipes();
		
		rb = new RecipeBuilder();
		rm = new RecipeManager();
		listener = new EventListener();
	    	
		console.sendMessage(ChatColor.DARK_GREEN + "Thank you for purchasing ProRecipes! \n" +  "Any comments or concerns message me on spigot!" + ChatColor.RESET);
		 
		title = getServer().getPluginManager().isPluginEnabled("TitleManager");
		
		
		fastCraft = getServer().getPluginManager().isPluginEnabled("FastCraft");
		
		
		
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(rec, this);
		
		getServer().getPluginManager().registerEvents(rb, this);
		getServer().getPluginManager().registerEvents(rm, this);
		getServer().getPluginManager().registerEvents(listener, this);
		
		getCommand("recipes").setExecutor(new RecipesCmd());
		getCommand("recipe").setExecutor(new Recipe());
		getCommand("builditem").setExecutor(new BuildItem());
		getCommand("items").setExecutor(new Items());
		
		this.getConfig().addDefault("spigot", true);
		spigot = getConfig().getBoolean("spigot");
		
		this.getConfig().addDefault("showPrompts", true);
		prompts = getConfig().getBoolean("showPrompts");
		
		
		this.getConfig().addDefault("recipeBlacklist", new ArrayList<String>());
		List<String> blacklist = getConfig().getStringList("recipeBlacklist");
		
		
		
		List<String> lowerCase = new ArrayList<String>();
		for(String s : blacklist){
			lowerCase.add(s.toLowerCase());
		}
		
		blacklist = lowerCase;
		
		generateBlacklist(blacklist);
		//removeBlacklist();
	/*	for(String s : blacklistItems.keySet()){
			System.out.println(s);
			System.out.println(blacklistItems.get(s));
		}*/
		
		removeBlacklist();
		
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(!prompts){
			wait = 0;
		}else{
			wait = 35;
		}
		
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().getServer().recipeIterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            defaultRecipes.add(recipe);
            
        }
		
        airString = itemToStringBlob(new ItemStack(Material.AIR, 0));
        
		rec.loadRecipes();
		
		b = new ItemBuilder();
		getServer().getPluginManager().registerEvents(b, this);
		
		createFakeRecipes();
		loadedRecipes = true;
		
		creative = getServer().createInventory(null, InventoryType.CREATIVE);
		
		Metrics metrics = null;
		
		try {
			metrics = new Metrics(this);
			metrics.start();
			
			Graph recipesCreated = metrics.createGraph("Recipes Created");
			
			Graph recipesCrafted = metrics.createGraph("Recipes Crafted");
			
			recipesCreated.addPlotter(new Plotter("Shapeless"){

				@Override
				public int getValue() {
					return rec.shapeless.size();
				}
				
			});
			
			recipesCreated.addPlotter(new Plotter("Shaped"){

				@Override
				public int getValue() {
					return rec.shaped.size();
				}
				
			});
			
			recipesCreated.addPlotter(new Plotter("Furnace"){

				@Override
				public int getValue() {
					return rec.fur.size();
				}
				
			});
			
			recipesCreated.addPlotter(new Plotter("Multicraft"){

				@Override
				public int getValue() {
					return rec.chest.size();
				}
				
			});
			
			recipesCrafted.addPlotter(new Plotter("Shapeless"){

				@Override
				public int getValue() {
					return craftedRecipes.containsKey(RecipeType.SHAPELESS) ? 
							craftedRecipes.get(RecipeType.SHAPELESS) : 0;
				}
				
			});
			
			recipesCrafted.addPlotter(new Plotter("Shaped"){

				@Override
				public int getValue() {
					return craftedRecipes.containsKey(RecipeType.SHAPED) ? 
							craftedRecipes.get(RecipeType.SHAPED) : 0;
				}
				
			});
			
			recipesCrafted.addPlotter(new Plotter("Furnace"){

				@Override
				public int getValue() {
					return craftedRecipes.containsKey(RecipeType.FURNACE) ? 
							craftedRecipes.get(RecipeType.FURNACE) : 0;
				}
				
			});
			
			recipesCrafted.addPlotter(new Plotter("Multicraft"){

				@Override
				public int getValue() {
					return craftedRecipes.containsKey(RecipeType.MULTI) ? 
							craftedRecipes.get(RecipeType.MULTI) : 0;
				}
				
			});
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(info.notAuthentic){
			console.sendMessage(ChatColor.DARK_RED + "Unable to authenticate plugin. It will disable...");
			//this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		
	}
	
	public void loadFile(String fileName){
		try{
			FileInputStream saveFile = new FileInputStream(fileName);
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			Object obj = restore.readObject();
			String name = (String) restore.readObject();
			restore.close();
			System.out.println(name);
			System.out.println(obj);
		}catch(Exception e){
			
		}
		
		
	}

	public void removeBlacklist(){
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().getServer().recipeIterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            if (recipe != null)
            {
            	String id = recipe.getResult().getType().toString().toLowerCase();
            	if(blacklistItems.containsKey(id)){
            		if(blacklistItems.get(id).contains((short)-1)){
            			it.remove();
            		}else if(blacklistItems.get(id).contains(recipe.getResult().getDurability())){
            			it.remove();
            		}
            	}
            	
            }
        }
	}
	
	public void removeMeta(Player p){
		String[] s = {"recipeBuilder","recipeViewer","itemViewer","itemBuilder"};
		for(String t : s){
			if(p.hasMetadata(t)){
				p.removeMetadata(t, this);
			}
		}
	}
	
	public void generateBlacklist(List<String> list){
		for(String s : list){
			List<Short> shorts = new ArrayList<Short>();
			String id = s.contains("!") ? s.substring(0, s.indexOf("!")) : s;
			if(blacklistItems.containsKey(id)){
				shorts = blacklistItems.get(id);
			}
			
			if(s.contains("!")){
				String[] t = s.split("!");
				short b = (short)0;
				try{
					b = Short.parseShort(t[1]);
				}catch(Exception e){
					System.out.println("ERROR PARSING BLACKLISTED ITEM: MUST BE IN FORMAT 'Material:data");
				}
				if(!shorts.contains(b)){
					shorts.add(b);
				}
				blacklistItems.put(id, shorts);
			}else{
				shorts.add((short)-1);
				blacklistItems.put(id, shorts);
			}
		}
		
		
	}
	
	
	public void createFakeRecipes(){
		
		
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
	
	public boolean updateNeeded(boolean startup){
		
		System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString() + "[" +id+ "] Checking for updates..." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
		try {
			
			
			String link = "https://raw.githubusercontent.com/mcgrizzz/PluginVersions/master/versions.txt";
			
			ArrayList<String> input = new ArrayList<String>();
			try {
				URL url = new URL(link);
				Scanner s = new Scanner(url.openStream());
				while(s.hasNext()){
					input.add(s.next());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//for(String s : input){
				//System.out.println(s);
			//}
			int start = 0;
			for(String s : input){
				if(s.contains(id)){
					start = input.indexOf(s);
					break;
				}
			}
			
			version = input.get(start + 1);
	
			
			
			if(compareVersion(getDescription().getVersion())){
				if(startup)System.out.println(Ansi.ansi().fg(Ansi.Color.RED).bold().toString() + "[" + id + "] Plugin outdated. New version: " + this.version + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	private boolean compareVersion(String s){
		 String localVersion = s.replace(" ", "").replaceAll("\\.", " ");
		 String externalVersion = this.version.replace(" ", "").replaceAll("[A-Za-z]", "").replaceAll("\\.", " ");
		 String[] externalDigits = externalVersion.split(" ");
		 String[] localDigits = localVersion.split(" ");
		 return (arrayToInt(localDigits) < arrayToInt(externalDigits));
			
	 }
	 
	 private int arrayToInt(String[] string){
		 int result = 0;
		 double multiplier = 100;
		 for(int i = 0; i < string.length; i++){
			 result += Integer.parseInt(string[i])*multiplier;
			 multiplier /= 10;
		 }
		 return result;
	 }
	 
	
	@Override
	public void onDisable(){
		//getServer().getScheduler().cancelTask(info.disableTask);
		//getServer().getScheduler().cancelTask(info.hostTask);
		//if(Authentication.Host.is.isHosting()){
			//Authentication.Host.remove.removeHost();
		//}
		rec.saveRecipes(true);
		b.saveItems();
		ms.saveMessages();
	}
	
	protected static ProRecipes getPlugin(){
		return me;
	}
	
	public Recipes getRecipes(){
		return this.rec;
	}
	
	protected static List<String> getTags(String s){
		return mv.getChecker().getTags(s);
	}
	
	protected static String itemToStringBlob(ItemStack itemStack, boolean ignoreExtraData) {
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
             	if(mv != MinecraftVersion.NoVersion){
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
	
	protected static String itemToStringBlob(ItemStack itemStack) {
        return itemToStringBlob(itemStack, false);
    }
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		if(checkUpdate && updateNeeded){
			if(event.getPlayer().hasPermission("prorecipes.notifyupdate")){
				event.getPlayer().sendMessage(ChatColor.GOLD + "{o------»  " + ChatColor.YELLOW +  "ProRecipes" + ChatColor.GOLD + "  «------o}");
				event.getPlayer().sendMessage(ChatColor.GOLD + "-> There is an update available (v." + ChatColor.DARK_GREEN + this.version + ChatColor.GOLD + ") ! <-");
				event.getPlayer().sendMessage(ChatColor.GOLD + "-> Download " + ChatColor.GREEN + "@ https://goo.gl/orffPA" + ChatColor.GOLD + " <-");
			} 
		}
		
	}
	
	 public GameProfile createGameProfile(String texture, UUID id)
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
	 
	 public ItemStack CreateHead(UUID id, ItemStack c, String texture)
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
	 
	 public boolean isVanilla(ItemStack i){
		 for(ItemStack b : creative.getContents()){
			 if(i.isSimilar(b))return true;
		 }
		 if(i.hasItemMeta())return false;
		 if(i.getEnchantments().size() > 0)return false;
		 return true;
	 }
	 
	 public org.bukkit.inventory.Recipe findRecipe(ItemStack i){
		 Iterator<org.bukkit.inventory.Recipe> it = defaultRecipes.iterator();
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
	 
	 public ItemStack[] getMatrix(org.bukkit.inventory.Recipe rec){
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
			 return getAPI().toArray(r.getIngredientList());
		 }else if(rec instanceof FurnaceRecipe){
			 FurnaceRecipe r = (FurnaceRecipe)rec;
			 ItemStack[] arr = new ItemStack[9];
			 arr[4] = r.getInput();
			 return arr;
		 }
		 return null;
	 }
	 
	 public ItemStack[] getTippedArrowRec(ItemStack b){
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
		 String type = mv.getChecker().getPotionType(nbtString(b));
		 ItemStack i = new ItemStack(Material.LINGERING_POTION);
		 i = mv.getChecker().addTag(i, "Potion", "minecraft:" + type);
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
	 
	 
	 public static RecipeAPI getAPI(){
		 return api;
	 }
	 
	 public void incrementRecipesCrafted(RecipeType rec){
		 if(craftedRecipes.containsKey(rec)){
			 craftedRecipes.put(rec, craftedRecipes.get(rec) + 1);
		 }else{
			 craftedRecipes.put(rec, 1);
		 }
	 }
	 
	 public boolean hashCompare(ItemStack a, ItemStack b){
		 return a.hashCode() == b.hashCode();
	 }
	 
	 
	 
	
	

}
