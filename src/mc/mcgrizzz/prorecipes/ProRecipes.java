package mc.mcgrizzz.prorecipes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mc.mcgrizzz.prorecipes.NBTChecker.MinecraftVersion;
import mc.mcgrizzz.prorecipes.api.RecipeAPI;
import mc.mcgrizzz.prorecipes.api.RecipeAPI.RecipeType;
import mc.mcgrizzz.prorecipes.command.BuildItem;
import mc.mcgrizzz.prorecipes.command.Items;
import mc.mcgrizzz.prorecipes.command.Recipe;
import mc.mcgrizzz.prorecipes.command.RecipesCmd;
import mc.mcgrizzz.prorecipes.gui.ItemBuilder;
import mc.mcgrizzz.prorecipes.gui.RecipeBuilder;
import mc.mcgrizzz.prorecipes.lib.ItemUtils;
import mc.mcgrizzz.prorecipes.lib.Messages;
import mc.mcgrizzz.prorecipes.lib.Metrics;
import mc.mcgrizzz.prorecipes.lib.ResourceInfo;
import mc.mcgrizzz.prorecipes.recipes.EventListener;
import mc.mcgrizzz.prorecipes.recipes.RecipeManager;
import mc.mcgrizzz.prorecipes.recipes.Recipes;

public class ProRecipes extends JavaPlugin implements Listener{
	
	
	public ArrayList<org.bukkit.inventory.Recipe> defaultRecipes = new ArrayList<org.bukkit.inventory.Recipe>();
	
	
	protected static ProRecipes me;
	protected static RecipeAPI api;
	
	public boolean loadedRecipes = false;
	
	Recipes rec;
	ItemBuilder b;
	RecipeBuilder rb;
	RecipeManager rm;
	ItemStack i;
	
	public boolean title;
	public boolean prompts;
	public int wait;
	public boolean spigot;


	boolean checkUpdate;
	
	public Messages ms;
	
	String id;
	String version;
	
	
	boolean updateNeeded;
	
	
	
	
	public static MinecraftVersion mv;
	
	HashMap<String, List<Short>> blacklistItems = new HashMap<String, List<Short>>();
	public HashSet<String> blacklisted = new HashSet<String>();
	
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
	 		console.sendMessage(ChatColor.DARK_RED +"[ProRecipes] SERVER VERSION NOT SUPPORTED. SOME FEATURES MAY NOT WORK.");
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
		
		ms = new Messages();
		rec = new Recipes();
		
		rb = new RecipeBuilder();
		rm = new RecipeManager();
		listener = new EventListener();
	    	
		console.sendMessage(ChatColor.DARK_GREEN + "Thank you for purchasing ProRecipes! \n" +  "Any comments or concerns message me on spigot!" + ChatColor.RESET);
		 
		title = getServer().getPluginManager().isPluginEnabled("TitleManager");
		
		
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
		
		removeBlacklist();
		
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(!prompts){
			wait = 0;
		}else{
			wait = 35;
		}
		
		setupRecipes();
		
		setupMetrics();
		
		
		/*if(info.notAuthentic){
			console.sendMessage(ChatColor.DARK_RED + "Unable to authenticate plugin. It will disable...");
			//this.getServer().getPluginManager().disablePlugin(this);
			return;
		}*/
		
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
            			blacklisted.add(ItemUtils.getRecipeKey(recipe, false));
            		}else if(blacklistItems.get(id).contains(recipe.getResult().getDurability())){
            			blacklisted.add(ItemUtils.getRecipeKey(recipe, true));
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
	
	
	public boolean updateNeeded(boolean startup){
		
		System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString() + "[" +id+ "] Checking for updates..." + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
		try {
			
			
			String link = "https://api.github.com/repos/mcgrizzz/ProRecipes/releases/latest";
			
			String jsonRaw = getHTML(link);
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(jsonRaw);
			JsonObject obj = element.getAsJsonObject();
			
			version = obj.get("tag_name").getAsString();
	
			
			if(compareVersion(getDescription().getVersion())){
				if(startup)System.out.println(Ansi.ansi().fg(Ansi.Color.RED).bold().toString() + "[" + id + "] Plugin outdated. New version: " + this.version + Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString());
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public String getHTML(String urlToRead) throws Exception {
	      StringBuilder result = new StringBuilder();
	      URL url = new URL(urlToRead);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
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
	
	public static ProRecipes getPlugin(){
		return me;
	}
	
	public Recipes getRecipes(){
		return this.rec;
	}
	
	public ItemBuilder getItemBuilder(){
		return this.b;
	}
	
	public RecipeBuilder getRecipeBuilder(){
		return this.rb;
	}
	
	public RecipeManager getRecipeManager(){
		return this.rm;
	}
	
	public static RecipeAPI getAPI(){
	    return api;
	}
	
	
	
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		if(checkUpdate && updateNeeded){
			if(event.getPlayer().hasPermission("prorecipes.notifyupdate")){
				event.getPlayer().sendMessage(ChatColor.GOLD + "{o------»  " + ChatColor.YELLOW +  "ProRecipes" + ChatColor.GOLD + "  «------o}");
				event.getPlayer().sendMessage(ChatColor.GOLD + "-> There is an update available (" + ChatColor.DARK_GREEN + this.version + ChatColor.GOLD + ") ! <-");
				event.getPlayer().sendMessage(ChatColor.GOLD + "-> Download " + ChatColor.GREEN + "@ https://goo.gl/mzT8BH" + ChatColor.GOLD + " <-");
			} 
		}
		
	}
	
	 
	 
	
	 
	 
	 public boolean hashCompare(ItemStack a, ItemStack b){
		 return a.hashCode() == b.hashCode();
	 }
	 
	 
	 public void setupRecipes(){
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().getServer().recipeIterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            defaultRecipes.add(recipe);
            
        }
		
        airString = ItemUtils.itemToStringBlob(new ItemStack(Material.AIR, 0));
        
		rec.loadRecipes();
		
		b = new ItemBuilder();
		getServer().getPluginManager().registerEvents(b, this);
		
		ItemUtils.createFakeRecipes();
		loadedRecipes = true;
	 }
	 
	 
	 public void setupMetrics(){
			
			Metrics metrics = new Metrics(this);
			
			metrics.addCustomChart(new Metrics.AdvancedPie("recipes_created", new Callable<Map<String, Integer>>() {

				@Override
				public Map<String, Integer> call() throws Exception {
					Map<String, Integer> valueMap = new HashMap<>();
		            valueMap.put("Shapeless", rec.shapeless.size());
		            valueMap.put("Shaped", rec.shaped.size());
		            valueMap.put("Furnace", rec.fur.size());
		            valueMap.put("Multicraft", rec.chest.size());
		            return valueMap;
				}
		    }));
			
			metrics.addCustomChart(new Metrics.AdvancedPie("recipes_crafted", new Callable<Map<String, Integer>>() {

				@Override
				public Map<String, Integer> call() throws Exception {
					Map<String, Integer> valueMap = new HashMap<>();
		            valueMap.put("Shapeless", craftedRecipes.containsKey(RecipeType.SHAPELESS) ? 
							craftedRecipes.get(RecipeType.SHAPELESS) : 0);
		            valueMap.put("Shaped", craftedRecipes.containsKey(RecipeType.SHAPED) ? 
							craftedRecipes.get(RecipeType.SHAPED) : 0);
		            valueMap.put("Furnace", craftedRecipes.containsKey(RecipeType.FURNACE) ? 
							craftedRecipes.get(RecipeType.FURNACE) : 0);
		            valueMap.put("Multicraft", craftedRecipes.containsKey(RecipeType.MULTI) ? 
							craftedRecipes.get(RecipeType.MULTI) : 0);
		            return valueMap;
				}
				
		    }));
		}
	 
	 
	 
	 public void incrementRecipesCrafted(RecipeType rec){
		 if(craftedRecipes.containsKey(rec)){
			 craftedRecipes.put(rec, craftedRecipes.get(rec) + 1);
		 }else{
			 craftedRecipes.put(rec, 1);
		 }
	 }
	 
	 
	 
	
	

}
