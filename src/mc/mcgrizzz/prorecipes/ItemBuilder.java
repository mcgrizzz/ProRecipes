package mc.mcgrizzz.prorecipes;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;






import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;

public class ItemBuilder implements Listener{
	
	public HashMap<String, ItemStack> storedItems = new HashMap<String, ItemStack>();
	
	public ArrayList<ItemStack> createdItems = new ArrayList<ItemStack>();
	public HashMap<String, Material> materials = new HashMap<String, Material>();
	
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	public Messages m;
	
	public ItemBuilder(){
		m = ProRecipes.getPlugin().ms;
		loadItems();
	}
	
	public void loadItems(){
		
		
	    	ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					customConfigFile = ProRecipes.getPlugin().getRecipes().getFile();
			    	
				    customConfig = ProRecipes.getPlugin().getRecipes().getConfig();
				    ConfigurationSection items = customConfig.getConfigurationSection("createdItems");
				    if(!(customConfig == null || items == null)){
				    	Set<String> kk = items.getKeys(false);
					    for(String s : kk){
					    	ItemStack ri = items.getItemStack(s + ".item");
					    	createdItems.add(ri);
					    }
				    }
				    
					loadFromRecipes();
				}
				
			}, 10);
	    
	}
	
	public void loadFromRecipes(){
		for(RecipeShaped r : ProRecipes.getPlugin().getRecipes().shaped){
			for(ItemStack i : r.getItems()){
				if(i == null)continue;
				if(i.hasItemMeta()){
					ItemStack t = i.clone();
					t.setAmount(1);
					if(!createdItems.contains(t)){
						createdItems.add(t);
					}
				}
			}
			if(r.getResult() != null){
				ItemStack c = r.getResult().clone();
				c.setAmount(1);
				if(c.hasItemMeta()){
					if(!createdItems.contains(c)){
						createdItems.add(c);
					}
				}
			}
			
		}
		
		for(RecipeChest r : ProRecipes.getPlugin().getRecipes().chest){
			for(ItemStack i : r.getItems()){
				if(i == null)continue;
				if(i.hasItemMeta()){
					ItemStack t = i.clone();
					t.setAmount(1);
					if(!createdItems.contains(t)){
						createdItems.add(t);
					}
				}
			}
			for(ItemStack b : r.getResult()){
				if(b != null && !b.getType().equals(Material.AIR)){
					ItemStack c = b.clone();
					c.setAmount(1);
					if(c.hasItemMeta()){
						if(!createdItems.contains(c)){
							createdItems.add(c);
						}
					}
				}
				
			}
			
		}
		
		for(RecipeShapeless r : ProRecipes.getPlugin().getRecipes().shapeless){
			for(ItemStack i : r.getItems()){
				if(i == null)continue;
				if(i.hasItemMeta()){
					ItemStack t = i.clone();
					t.setAmount(1);
					if(!createdItems.contains(t)){
						createdItems.add(t);
					}
				}
			}
			if(r.getResult() != null){
				ItemStack c = r.getResult().clone();
				c.setAmount(1);
				if(c.hasItemMeta()){
					if(!createdItems.contains(c)){
						createdItems.add(c);
					}
				}
			}
			
		}
		
		for(RecipeFurnace r : ProRecipes.getPlugin().getRecipes().fur){
			if(r.toBurn != null){
				ItemStack burn = r.toBurn.clone();
				burn.setAmount(1);
				if(burn.hasItemMeta()){
					if(!createdItems.contains(burn)){
						createdItems.add(burn);
					}
				}
			}
			
			if(r.result != null){
				ItemStack result = r.result.clone();
				
				
				result.setAmount(1);
				if(result.hasItemMeta()){
					if(!createdItems.contains(result)){
						createdItems.add(result);
					}
				}
			}
			
		}
		
		
	}
	
	public void saveItems(){
		/*ConfigurationSection item = customConfig.createSection("createdItems");
		for(int i = 0; i < createdItems.size(); i++){
			item.set(i + ".item", createdItems.get(i));
		}
		
		//try {
		   // customConfig.save(customConfigFile);
		//} catch (IOException ex) {
	  //      RPGRecipes.getPlugin().getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	  //  }
		
		createdItems.clear();*/
	}
	
	
	public ArrayList<Material> getMaterials(){
		ArrayList<Material> m = new ArrayList<Material>();
		for(ItemStack i : createdItems){
			if(!m.contains(i.getType())){
				m.add(i.getType());
			}
		}
		
		return m;
	}
	
	public ArrayList<ItemStack> getItemsForMat(Material m){
		ArrayList<ItemStack> i = new ArrayList<ItemStack>();
		for(ItemStack c : createdItems){
			if(c.getType().equals(m)){
				i.add(c);
			}
		}
		
		return i;
	}
	
	public void openItems(final Player p, final int page, boolean delay){
		int wait = ProRecipes.getPlugin().wait;
		if(!delay){
			wait = 0;
		}else{
			sendMessage(p,  m.getMessage("Item_Viewer_Title", ChatColor.GOLD + "Item Viewer"),  m.getMessage("Item_Viewer_Prompt", ChatColor.DARK_GREEN + "Click a material to view its custom items"));
		}
		
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("itemViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "materialChoose"));
			
				boolean nextPage = false;
				boolean backPage = false;
				
				ArrayList<Material> items = getMaterials();
				int b = items.size();
				int pages = 0;
				while(b > 45){
					b -= 45;//everytime we have another page there will be a row missing on the bottom
					pages++;
				}
				if(b > 0){
					
					pages++;
				}
				
				
				if(pages >= page){
					//There is a next page
					if(pages > page){
						nextPage = true;
					}
					if(page - 1 > 0){
						//There is a page back
						backPage = true;
					}
				}
				
				if(nextPage || backPage){
					//There will be a whole row extra on the bottom
				int start = page*45 - 45;
				int end = page*45;
				if(!nextPage){
					//There's no next page, and  this page is not full
					end = (page-1)*45 + b; 
					
				}
				b+=9;
				
				if(nextPage){
					b = 54;
				}
				
				while(b % 9 != 0){
					b++;
				}
				
				if(b > 54){
					//////////////System.out.println("THIS ISN'T GOOD IT's BIGGER THAN IT SHOULD BE!");
				}
				
				Inventory ib = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Choose a material " + page + "/" + pages);
				
				for(int i = start; i < end; i++){
					int slot = i%45;
					ib.setItem(slot, new ItemStack(items.get(i)));
				}
				
				
				
					
				
				if(nextPage){
					ItemStack addLoree = new ItemStack(Material.ARROW);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.GREEN + "Next Page →");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-1, addLoree);
					
					//p.setMetadata("nextPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page+1));
				}
				if(backPage){
					ItemStack addLoree = new ItemStack(Material.BUCKET);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.YELLOW + "← Back Page");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-9, addLoree);
					//p.setMetadata("backPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page-1));
				}
					p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
					p.closeInventory();
					p.openInventory(ib);
					p.removeMetadata("closed", ProRecipes.getPlugin());
					
				}else{
					b = getMaterials().size() + 1;
					if(b > 54){
						b = 54;
					}
					if(b < 9){
						b = 9;
					}
					
					while(b % 9 != 0){
						b++;
					}
					
					Inventory i = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Choose a material");
					
					for(Material m : items){
						i.setItem(items.indexOf(m)%45, new ItemStack(m));
					}
					p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
					p.closeInventory();
					p.openInventory(i);
					p.removeMetadata("closed", ProRecipes.getPlugin());
				}
			}
			
		}, wait);
	}
	
	public void openItems(final Material m, final Player p, final int page){
		
				p.setMetadata("itemViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "pickUp"));
			
				boolean nextPage = false;
				boolean backPage = false;
				
				ArrayList<ItemStack> items = getItemsForMat(m);
				int b = items.size();
				int pages = 0;
				while(b > 45){
					b -= 45;//everytime we have another page there will be a row missing on the bottom
					pages++;
				}
				if(b > 0){
					
					pages++;
				}
				
				
				if(pages >= page){
					//There is a next page
					if(pages > page){
						nextPage = true;
					}
					if(page - 1 > 0){
						//There is a page back
						backPage = true;
					}
				}
				
				if(nextPage || backPage){
					//There will be a whole row extra on the bottom
				int start = page*45 - 45;
				int end = page*45;
				if(!nextPage){
					//There's no next page, and  this page is not full
					end = (page-1)*45 + b; 
					
				}
				b+=9;
				
				if(nextPage){
					b = 54;
				}
				
				while(b % 9 != 0){
					b++;
				}
				
				if(b > 54){
					//////////////System.out.println("THIS ISN'T GOOD IT's BIGGER THAN IT SHOULD BE!");
				}
				
				Inventory ib = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Click to pick up " + page + "/" + pages);
				
				for(int i = start; i < end; i++){
					int slot = i%45;
					ib.setItem(slot, items.get(i));
				}
				
				
				
					
				
				if(nextPage){
					ItemStack addLoree = new ItemStack(Material.ARROW);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.GREEN + "Next Page →");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-1, addLoree);
					
					//p.setMetadata("nextPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page+1));
				}
				if(backPage){
					ItemStack addLoree = new ItemStack(Material.BUCKET);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.YELLOW + "← Back Page");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-9, addLoree);
					//p.setMetadata("backPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page-1));
				}
					p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
					p.openInventory(ib);
					p.removeMetadata("closed", ProRecipes.getPlugin());
				}else{
					b = getItemsForMat(m).size() + 1;
					if(b > 54){
						b = 54;
					}
					if(b < 9){
						b = 9;
					}
					
					while(b % 9 != 0){
						b++;
					}
					String n = "Click to pick up";
					if(!p.hasPermission("procrecipes.takeitem")){
						n = "View Custom Items";
					}
					Inventory i = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + n);
					
					for(ItemStack t : items){
						i.setItem(items.indexOf(t)%45, t);
					}
					p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
					p.openInventory(i);
					p.removeMetadata("closed", ProRecipes.getPlugin());
				}
			
		
		
	}
	
	//The passed version is less than compared
	private static boolean compareVersion(String s, String compareTo){
		 String localVersion = s.replace(" ", "").replaceAll("\\.", " ");
		 String externalVersion = compareTo.replace(" ", "").replaceAll("[A-Za-z]", "").replaceAll("\\.", " ");
		 String[] externalDigits = externalVersion.split(" ");
		 String[] localDigits = localVersion.split(" ");
		 return (arrayToInt(localDigits) < arrayToInt(externalDigits));
			
	 }
	
	 private static int arrayToInt(String[] string){
		 int result = 0;
		 double multiplier = 100;
		 for(int i = 0; i < string.length; i++){
			 result += Integer.parseInt(string[i])*multiplier;
			 multiplier /= 10;
		 }
		 return result;
	 }
	
	
	
	public static void sendMessage(Player player, String title, String subtitle){
		String s = getVersion();
		//System.out.println(s);
		if(ProRecipes.getPlugin().spigot && !compareVersion(s, "1.8")){
			MessengerSpigot.sendMessage(player, title, subtitle);
		}else{
			Messenger.sendMessage(player, title, subtitle);
		}
			
	}
	
	public static String getVersion(){
		String s = Bukkit.getVersion();
		int index = s.indexOf("(MC: ");
		return s.substring(index + 4, s.length()-1);
	}
	
	
	
	
	
	
	
	public void removeLore(Player p){
		ItemStack i = p.getOpenInventory().getItem(0);
		i = removeLore(i);
		p.getOpenInventory().setItem(0, i);
	}
	
	public void openSetDisplayName(Player p){
		storedItems.put(p.getName(), p.getOpenInventory().getItem(0).clone());
		close(p);
		p.getInventory().remove(storedItems.get(p.getName()));
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		p.setMetadata("itemBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "setDisplay"));
		sendMessage(p,  m.getMessage("Item_Builder_Title", ChatColor.GOLD + "Item Builder"), m.getMessage("Item_Builder_Name", ChatColor.DARK_GREEN + "Type desired display name in chat"));
	}
	
	public void openAddLore(Player p){
		storedItems.put(p.getName(), p.getOpenInventory().getItem(0).clone());
		close(p);
		p.getInventory().remove(storedItems.get(p.getName()));
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		p.setMetadata("itemBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "addLore"));
		sendMessage(p, m.getMessage("Item_Builder_Title", ChatColor.GOLD + "Item Builder"),  m.getMessage("Item_Builder_Lore", ChatColor.DARK_GREEN + "Type desired lore to add in chat"));
	}
	
/*	public void openEnchantmentTypes(Player p){
		storedItems.put(p.getName(), p.getOpenInventory().getItem(0).clone());
		close(p);
		p.getInventory().remove(storedItems.get(p.getName()));
		p.setMetadata("closed", new FixedMetadataValue(RPGRecipes.getPlugin(), ""));
		p.setMetadata("itemBuilder", new FixedMetadataValue(RPGRecipes.getPlugin(), "addLore"));
		sendMessage(p, ChatColor.GOLD + m.getMessage("Item_Builder_Title"), ChatColor.DARK_GREEN + m.getMessage("Item_Builder_Lore"));
	}*/
	
	public void finish(Player p){
		ItemStack i = p.getOpenInventory().getItem(0).clone();
		if(!createdItems.contains(i)){
			createdItems.add(i.clone());
		}
		storedItems.remove(p.getName());
		close(p);
		p.removeMetadata("closed", ProRecipes.getPlugin());
		sendMessage(p, m.getMessage("Item_Builder_Title", ChatColor.GOLD + "Item Builder"),  m.getMessage("Item_Builder_Done", ChatColor.DARK_GREEN + "Your item is in your inventory!"));
		p.getInventory().addItem(i);
		p.removeMetadata("itemBuilder", ProRecipes.getPlugin());
		
	}
	
	
	public void openItemBuilder(final Player p){
		sendMessage(p,  m.getMessage("Item_Builder_Title", ChatColor.GOLD + "Item Builder"),  m.getMessage("Item_Builder_Insert", ChatColor.DARK_GREEN + "Insert a desired itemstack to modify."));
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.closeInventory();
				p.setMetadata("itemBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "itemRequest"));
				p.openWorkbench(null, true);
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, ProRecipes.getPlugin().wait);
		
	}
	
	public ItemStack removeLore(ItemStack i){
		ItemStack b = i.clone();
		ItemMeta m = b.getItemMeta();
		m.setLore(null);
		b.setItemMeta(m);
		return b;
	}
	
	public static void close(Player p ){
		clear(p);
		p.getOpenInventory().close();
	}
	
	public static void clear(Player p){
		for(int i = 0; i < p.getOpenInventory().getTopInventory().getSize(); i++){
			////////////////System.out.println(i);
			p.getOpenInventory().getTopInventory().setItem(i, null);
		}
	}
	
	public void openInterface(final Player p, boolean message){
		ItemStack i;
		if(storedItems.containsKey(p.getName())){
			i = storedItems.get(p.getName());
		}else{
			i = p.getOpenInventory().getItem(0).clone();
		}
		
		storedItems.remove(p.getName());
		
		final ItemStack b = i.clone();
		
		
		close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		int wait = 0;
		if(message){
			sendMessage(p,  m.getMessage("Item_Builder_Title", ChatColor.GOLD + "Item Builder"),  m.getMessage("Item_Builder_Edit", ChatColor.DARK_GREEN + "You may edit the itemstack."));
			wait = ProRecipes.getPlugin().wait;
		}
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("itemBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "itemInterface"));
				p.openWorkbench(null, true);
				p.removeMetadata("closed", ProRecipes.getPlugin());
				ItemStack addLore = new ItemStack(Material.ANVIL);
				ItemMeta m = addLore.getItemMeta();
				m.setDisplayName(ChatColor.GOLD + "Add lore");
				addLore.setItemMeta(m);
				p.getOpenInventory().setItem(1, addLore);
				
				ItemStack takeLore = new ItemStack(Material.ARROW);
				m = takeLore.getItemMeta();
				m.setDisplayName(ChatColor.RED + "Clear lore");
				takeLore.setItemMeta(m);
				
				ItemStack setDisplay = new ItemStack(Material.ARMOR_STAND);
				m = setDisplay.getItemMeta();
				m.setDisplayName(ChatColor.GOLD + "Set Displayname");
				setDisplay.setItemMeta(m);
				
				ItemStack finish = new ItemStack(Material.BLAZE_POWDER);
				m = finish.getItemMeta();
				m.setDisplayName(ChatColor.GREEN + "Finish");
				finish.setItemMeta(m);
				
				ItemStack preview = new ItemStack(Material.APPLE);
				m = preview.getItemMeta();
				m.setDisplayName(ChatColor.GREEN + "Preview");
				preview.setItemMeta(m);
				
				/*ItemStack enchantments = new ItemStack(Material.ENCHANTMENT_TABLE);
				m = preview.getItemMeta();
				m.setDisplayName(ChatColor.GREEN + "Enchant");
				enchantments.setItemMeta(m);*/
				
				
				p.getOpenInventory().setItem(9, finish);
				//p.getOpenInventory().setItem(8, enchantments);
				p.getOpenInventory().setItem(3, setDisplay);
				p.getOpenInventory().setItem(7, takeLore);
				p.getOpenInventory().setItem(1, addLore);
				p.getOpenInventory().setItem(5, preview);
				p.getOpenInventory().setItem(0, b);
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, wait);
	}
	
	public void closeItemBuilder(Player p){
		p.removeMetadata("itemBuilder", ProRecipes.getPlugin());
		close(p);
		if(p.hasMetadata("closed")){
			p.removeMetadata("closed", ProRecipes.getPlugin());
		}
		
	}
	
	public ItemStack setDisplayName(String s, ItemStack i){
		ItemStack t = i.clone();
		s = ChatColor.translateAlternateColorCodes('&', s);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(s);
		t.setItemMeta(m);
		return t;
		
	}
	
	public ItemStack addLore(String s, ItemStack i){
		ArrayList<String> curr = new ArrayList<String>();
		if(i.hasItemMeta()){
			if(i.getItemMeta().hasLore()){
				for(String ss : i.getItemMeta().getLore()){
					curr.add(ss);
				}
			}
		}
		curr.add(ChatColor.translateAlternateColorCodes('&', s));
		ItemStack t = i.clone();
		ItemMeta m = i.getItemMeta();
		m.setLore(curr);
		t.setItemMeta(m);
		return t;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getInventory() == null || event.getClickedInventory() == null)return;
		if(event.getSlot() == -999)return;
		if(!event.getInventory().getType().equals(InventoryType.WORKBENCH))return;
		if(!event.getWhoClicked().hasMetadata("itemBuilder"))return;
		//////////////////System.out.println("Called");
		//////////////////System.out.println(event.getInventory().getTitle());
		//////////////////System.out.println(event.getAction());
		if(((Player)event.getWhoClicked()).isSneaking()){
			event.setCancelled(true);
			return;
		}
		//////////////////System.out.println(event.getAction());
		String step = event.getWhoClicked().getMetadata("itemBuilder").get(0).asString();
		if(step.equalsIgnoreCase("itemRequest")){
			
			switch(event.getAction()){
			case PLACE_ONE:
			case PLACE_SOME:
			case PLACE_ALL:
			case DROP_ALL_CURSOR:
			case DROP_ALL_SLOT:
			case DROP_ONE_CURSOR:
			case DROP_ONE_SLOT:
			case MOVE_TO_OTHER_INVENTORY:
				event.setCancelled(true);
				ItemStack i = event.getCursor().clone();
				event.setCursor(null);
				if(i == null){
					i = event.getCurrentItem().clone();
					event.setCurrentItem(null);
				}
				event.getInventory().setItem(0, i);
				openInterface((Player)event.getWhoClicked(), true);
			}
		}else if(step.equalsIgnoreCase("itemInterface")){
			if(event.getSlot() == 0){
				
			}else if(event.getSlot() == 1){
				openAddLore((Player)event.getWhoClicked());
			}else if(event.getSlot() == 3){
				openSetDisplayName((Player)event.getWhoClicked());
			}else if(event.getSlot() == 7){
				removeLore((Player)event.getWhoClicked());
			}else if(event.getSlot() == 9){
				finish((Player)event.getWhoClicked());
			}else if(event.getSlot() == 5){
				
			}else if(event.getSlot() == 8){
				
			}
			
			event.setCancelled(true);
		}
		
	}
	
	
	@EventHandler
	public void onInventoryClick2(InventoryClickEvent event){
		if(event.getInventory() == null || event.getClickedInventory() == null)return;
		if(event.getSlot() == -999)return;
		if(!event.getWhoClicked().hasMetadata("itemViewer"))return;
		if(((Player)event.getWhoClicked()).isSneaking()){
			event.setCancelled(true);
			return;
		}
		String step = event.getWhoClicked().getMetadata("itemViewer").get(0).asString();
		////////System.out.println(step);
		if(step.equalsIgnoreCase("materialChoose")){
			////////System.out.println("chooseMaterial");
			event.setCancelled(true);
			
			if(event.getInventory().getTitle().contains("/")){
				////////System.out.println("Title contains");
				//This has more than one page
				String formatted = event.getInventory().getTitle().replaceAll(ChatColor.GOLD + "Choose a material ", "");
				String[] pagess = formatted.split("/");
				int page = Integer.parseInt(pagess[0]);
				int pages = Integer.parseInt(pagess[1]);
				
				if(event.getSlot() == event.getInventory().getSize()-9){
					//Wants to go back a page
					////////System.out.println("Go back a page");
					if(page-1 > 0){
						////////System.out.println("Going back");
						openItems((Player)event.getWhoClicked(), page-1, false);
					}
				}else if(event.getSlot() == event.getInventory().getSize()-1){
					////////System.out.println("go forward");
					ItemStack i = event.getCurrentItem();
					if(i != null && !i.getType().equals(Material.AIR)){
						////////System.out.println("not null or air");
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page →")){
							//next page
							////////////////System.out.println(i.getItemMeta().getDisplayName());
							if(page+1 <= pages){
								////////System.out.println("going forward def");
								openItems((Player)event.getWhoClicked(), page+1, false);
							}
							
						}
					}
					
				}else{
					if(event.getInventory().getItem(event.getSlot()) != null){
						////////System.out.println("Open items for that mat");
						materials.put(((Player)event.getWhoClicked()).getName(), event.getInventory().getItem(event.getSlot()).getType());
						openItems(event.getInventory().getItem(event.getSlot()).getType(), (Player)event.getWhoClicked(), 1);
					}
				}
				return;
			}
			
				if(event.getInventory().getItem(event.getSlot()) != null){
					////////System.out.println("Open items for that mat");
					materials.put(((Player)event.getWhoClicked()).getName(), event.getInventory().getItem(event.getSlot()).getType());
					openItems(event.getInventory().getItem(event.getSlot()).getType(), (Player)event.getWhoClicked(), 1);
				}
			
		}else if(step.equalsIgnoreCase("pickUp")){
			//ChatColor.GOLD + "Click to pick up "
			////////System.out.println("Pickup");
			event.setCancelled(true);
			if(event.getInventory().getTitle().contains("/")){
				////////System.out.println("Contains");
				Material mat = materials.get(((Player)event.getWhoClicked()));
				//This has more than one page
				String formatted = event.getInventory().getTitle().replaceAll(ChatColor.GOLD + "Click to pick up ", "");
				String[] pagess = formatted.split("/");
				int page = Integer.parseInt(pagess[0]);
				int pages = Integer.parseInt(pagess[1]);
				
				if(event.getSlot() == event.getInventory().getSize()-9){
					//Wants to go back a page
					////////System.out.println("go back at a page");
					if(page-1 > 0){
						////////System.out.println("can");
						openItems(mat, (Player)event.getWhoClicked(), page-1);
					}
				}else if(event.getSlot() == event.getInventory().getSize()-1){
					////////System.out.println("go forward at a page");
					ItemStack i = event.getCurrentItem();
					if(i != null && !i.getType().equals(Material.AIR)){
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page →")){
							//next page
							////////////////System.out.println(i.getItemMeta().getDisplayName());
							if(page+1 <= pages){
								////////System.out.println("Can");
								openItems(mat, (Player)event.getWhoClicked(), page+1);
							}
							
						}
					}
					
				}else{
					if(event.getInventory().getItem(event.getSlot()) != null){
						if(((Player)event.getWhoClicked()).hasPermission("procrecipes.takeitem")){
							((Player)event.getWhoClicked()).getInventory().addItem(event.getInventory().getItem(event.getSlot()));
						}
					}
				}
				return;
			}
			
				if(event.getInventory().getItem(event.getSlot()) != null){
					//////System.out.println("pick up");
					if(((Player)event.getWhoClicked()).hasPermission("procrecipes.takeitem")){
						((Player)event.getWhoClicked()).getInventory().addItem(event.getInventory().getItem(event.getSlot()));
					}
					
				}
			
			
		}
		
	}
	
	@EventHandler
	public void inventoryDrag(InventoryDragEvent event){
		if(!event.getInventory().getType().equals(InventoryType.WORKBENCH))return;
		if(!event.getWhoClicked().hasMetadata("itemBuilder"))return;
		String step = event.getWhoClicked().getMetadata("itemBuilder").get(0).asString();
		if(step.equalsIgnoreCase("itemRequest")){
			
				event.setCancelled(true);
				
			}
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event){
		ArrayList<Player> remove = new ArrayList<Player>();
		for(Player p : event.getRecipients()){
			if(p.hasMetadata("itemBuilder")){
				String step = p.getMetadata("itemBuilder").get(0).asString();
				if(step.equalsIgnoreCase("setDisplay") || step.equalsIgnoreCase("addLore")){
					remove.add(p);
				}
					
			}
		}
		event.getRecipients().removeAll(remove);
		if(event.getPlayer().hasMetadata("itemBuilder")){
			String step = event.getPlayer().getMetadata("itemBuilder").get(0).asString();
			if(step.equalsIgnoreCase("setDisplay")){
				ItemStack i = storedItems.get(event.getPlayer().getName()).clone();
				i = setDisplayName(event.getMessage(), i);
				storedItems.put(event.getPlayer().getName(), i);
				openInterface(event.getPlayer(), false);
				event.setCancelled(true);
			}else if(step.equalsIgnoreCase("addLore")){
				ItemStack i = storedItems.get(event.getPlayer().getName()).clone();
				i = addLore(event.getMessage(), i);
				storedItems.put(event.getPlayer().getName(), i);
				openInterface(event.getPlayer(), false);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		if(event.getPlayer().hasMetadata("itemBuilder")){
			if(!event.getPlayer().hasMetadata("closed")){
				event.getPlayer().removeMetadata("itemBuilder", ProRecipes.getPlugin());
				event.getInventory().clear();
				ItemStack i = storedItems.get(event.getPlayer().getName());
				if(i == null){
					i = event.getInventory().getItem(0);
					if(i == null){
						return;
					}
				}
				event.getPlayer().getInventory().addItem(i);
				return;
			}
			String step = event.getPlayer().getMetadata("itemBuilder").get(0).asString();
			if(step.equalsIgnoreCase("itemInterface")){
				event.getInventory().clear();
				//close((Player)event.getPlayer());
			}
		}else if(event.getPlayer().hasMetadata("itemViewer")){
			event.getInventory().clear();
			if(!event.getPlayer().hasMetadata("closed")){
				if(event.getPlayer().getMetadata("itemViewer").get(0).asString().equalsIgnoreCase("pickUp")){
					materials.remove(event.getPlayer().getName());
					openItems((Player)event.getPlayer(), 1, false);
				}else{
					event.getPlayer().removeMetadata("itemViewer", ProRecipes.getPlugin());
				}
				
			}
				
			
			
		}
	}
	
	

}
