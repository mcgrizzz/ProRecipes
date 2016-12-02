package mc.mcgrizzz.prorecipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;

import co.kepler.fastcraft.api.FastCraftApi;

public class RecipeManager implements Listener {
	
	public HashMap<String, RecipeShaped> shaped = new HashMap<String, RecipeShaped>();
	public HashMap<String, RecipeShapeless> shapeless = new HashMap<String, RecipeShapeless>();
	public HashMap<String, RecipeFurnace> fur = new HashMap<String, RecipeFurnace>();
	public HashMap<String, RecipeChest> chest = new HashMap<String, RecipeChest>();
	public HashMap<String, ItemStack> storedItem = new HashMap<String, ItemStack>();
	public HashMap<String, ItemStack[]> storedResults = new HashMap<String, ItemStack[]>();
	public Messages m;
	
	public RecipeManager(){
		m = ProRecipes.getPlugin().ms;
	}
	
	public void openRecipeManager(final Player p){
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		if(p.hasPermission("prorecipes.modifyrecipes")){
			ItemBuilder.sendMessage(p, m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), 
					m.getMessage("Recipe_Viewer_Prompt", ChatColor.DARK_GREEN + "View and manage recipes"));
		}else{
			ItemBuilder.sendMessage(p, m.getMessage("Recipe_Viewer_Title2", ChatColor.GOLD  + "Recipe Viewer"),  m.getMessage("Recipe_Viewer_Prompt2", ChatColor.DARK_GREEN + "View recipes"));
		}
		
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.closeInventory();
				p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
				p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "chooseType"));
				p.openWorkbench(null, true);
				ItemStack addLore = new ItemStack(Material.BRICK);
				ItemMeta m = addLore.getItemMeta();
				
				m.setDisplayName(ChatColor.GOLD + "Shaped");
				addLore.setItemMeta(m);
				
				ItemStack takeLore = new ItemStack(Material.GLOWSTONE_DUST);
				m = takeLore.getItemMeta();
				m.setDisplayName(ChatColor.RED + "Shapeless");
				takeLore.setItemMeta(m);
				
				
				ItemStack takeLoree = new ItemStack(Material.COAL);
				m = takeLoree.getItemMeta();
				m.setDisplayName(ChatColor.DARK_PURPLE + "Furnace");
				takeLoree.setItemMeta(m);
				
				ItemStack takeLoreee = new ItemStack(Material.WORKBENCH);
				m = takeLoree.getItemMeta();
				m.setDisplayName(ChatColor.DARK_GREEN + "Multi-Craft");
				takeLoreee.setItemMeta(m);
				
				p.getOpenInventory().setItem(4, takeLore);
				p.getOpenInventory().setItem(5, takeLoree);
				p.getOpenInventory().setItem(6, addLore);
				p.getOpenInventory().setItem(8, takeLoreee);
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, ProRecipes.getPlugin().wait);
		
	}
	
	private void openShaped(final Player p, final int page) {
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		//ItemBuilder.sendMessage(p, ChatColor.GOLD + "Recipe Manager", ChatColor.DARK_GREEN + "Click on a recipe to view and manage it");
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
				
				
				p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
				p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "viewShaped"));
				
				if(page > 1){
					ItemBuilder.clear(p);
				}else{
					ItemBuilder.close(p);
				}
				
				
				
				
				
				boolean nextPage = false;
				boolean backPage = false;
				
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				int b = ProRecipes.getPlugin().getRecipes().shaped.size();
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
				
				Inventory ib = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Shaped recipes " + page + "/" + pages);
				
				for(int i = start; i < end; i++){
					int slot = i%45;
					ib.setItem(slot, ProRecipes.getPlugin().getRecipes().shaped.get(i).getResult());
				}
				
				ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
				ItemMeta m = addLore.getItemMeta();
				
				m.setDisplayName(ChatColor.GOLD + "View Multi-Craft recipes");
				addLore.setItemMeta(m);
				
					
				
				if(nextPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.GREEN + "Next Page →");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-1, addLoree);
					
					//p.setMetadata("nextPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page+1));
				}
				if(backPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.YELLOW + "← Back Page");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-9, addLoree);
					//p.setMetadata("backPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page-1));
				}
				ib.setItem(b-5, addLore);
					p.openInventory(ib);
					
				}else{
					b = ProRecipes.getPlugin().getRecipes().shaped.size() + 1;
					if(b > 54){
						b = 54;
					}
					if(b < 9){
						b = 9;
					}
					
					while(b % 9 != 0){
						b++;
					}
					
					Inventory i = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Shaped recipes");
					
					for(RecipeShaped rec : ProRecipes.getPlugin().getRecipes().shaped){
						i.setItem(ProRecipes.getPlugin().getRecipes().shaped.indexOf(rec)%45, rec.getResult());
					}
					
					ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
					ItemMeta m = addLore.getItemMeta();
					
					m.setDisplayName(ChatColor.GOLD + "View Multi-Craft recipes");
					addLore.setItemMeta(m);
					
					i.setItem(b-1, addLore);
					
					p.openInventory(i);
				}
				
			
				
				
				
				
				
				p.removeMetadata("closed", ProRecipes.getPlugin());
				
				
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, 0);
	}
	
	private void openFurnace(final Player p, final int page) {
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		//ItemBuilder.sendMessage(p, ChatColor.GOLD + "Recipe Manager", ChatColor.DARK_GREEN + "Click on a recipe to view and manage it");
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
				
				p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
				p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "viewFurnace"));
				
				if(page > 1){
					ItemBuilder.clear(p);
				}else{
					ItemBuilder.close(p);
				}
				
				
				
				
				
				boolean nextPage = false;
				boolean backPage = false;
				
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				int b = ProRecipes.getPlugin().getRecipes().fur.size();
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
				
				Inventory ib = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Furnace recipes " + page + "/" + pages);
				
				for(int i = start; i < end; i++){
					int slot = i%45;
					ib.setItem(slot, ProRecipes.getPlugin().getRecipes().fur.get(i).result);
				}
				
				ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
				ItemMeta m = addLore.getItemMeta();
				
				m.setDisplayName(ChatColor.GOLD + "View Shapeless recipes");
				addLore.setItemMeta(m);
				
					
				
				if(nextPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.GREEN + "Next Page →");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-1, addLoree);
					
					//p.setMetadata("nextPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page+1));
				}
				if(backPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.YELLOW + "← Back Page");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-9, addLoree);
					//p.setMetadata("backPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page-1));
				}
				ib.setItem(b-5, addLore);
					p.openInventory(ib);
					
				}else{
					b = ProRecipes.getPlugin().getRecipes().fur.size() + 1;
					if(b > 54){
						b = 54;
					}
					if(b < 9){
						b = 9;
					}
					
					while(b % 9 != 0){
						b++;
					}
					
					Inventory i = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Furnace recipes");
					
					for(RecipeFurnace rec : ProRecipes.getPlugin().getRecipes().fur){
						i.setItem(ProRecipes.getPlugin().getRecipes().fur.indexOf(rec)%45, rec.result);
					}
					
					ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
					ItemMeta m = addLore.getItemMeta();
					
					m.setDisplayName(ChatColor.GOLD + "View Shapeless recipes");
					addLore.setItemMeta(m);
					
					i.setItem(b-1, addLore);
					
					p.openInventory(i);
				}
				
			
				
				
				
				
				
				p.removeMetadata("closed", ProRecipes.getPlugin());
				
				
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, 0);
	}


	private void openShapeless(final Player p, final int page) {
		
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		
		//ItemBuilder.sendMessage(p, ChatColor.GOLD + "Recipe Manager", ChatColor.DARK_GREEN + "Click on a recipe to view and manage it");
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
				
				p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
				p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "viewShapeless"));
				
				if(page > 1){
					ItemBuilder.clear(p);
				}else{
					ItemBuilder.close(p);
				} 
				
				boolean nextPage = false;
				boolean backPage = false;
				
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				int b = ProRecipes.getPlugin().getRecipes().shapeless.size();
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
				
				Inventory ib = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Shapeless recipes " + page + "/" + pages);
				
				for(int i = start; i < end; i++){
					int slot = i%45;
					ib.setItem(slot, ProRecipes.getPlugin().getRecipes().shapeless.get(i).getResult());
				}
				
				ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
				ItemMeta m = addLore.getItemMeta();
				
				m.setDisplayName(ChatColor.GOLD + "View Shaped recipes");
				addLore.setItemMeta(m);
				
					
				
				if(nextPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.GREEN + "Next Page →");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-1, addLoree);
					
					//p.setMetadata("nextPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page+1));
				}
				if(backPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.YELLOW + "← Back Page");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-9, addLoree);
					//p.setMetadata("backPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page-1));
				}
				ib.setItem(b-5, addLore);
					
					p.openInventory(ib);
					
				}else{
					b = ProRecipes.getPlugin().getRecipes().shapeless.size() + 1;
					if(b > 54){
						b = 54;
					}
					if(b < 9){
						b = 9;
					}
					
					while(b % 9 != 0){
						b++;
					}
					
					Inventory i = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Shapeless recipes");
					
					for(RecipeShapeless rec : ProRecipes.getPlugin().getRecipes().shapeless){
						i.setItem(ProRecipes.getPlugin().getRecipes().shapeless.indexOf(rec)%45, rec.getResult());
					}
					
					ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
					ItemMeta m = addLore.getItemMeta();
					
					m.setDisplayName(ChatColor.GOLD + "View Shaped recipes");
					addLore.setItemMeta(m);
					
					i.setItem(b-1, addLore);
					
					p.openInventory(i);
				}
				
			
				
				
				
				
				
				p.removeMetadata("closed", ProRecipes.getPlugin());
				
				
				
			}
			
		}, 0);
	}
	
	private void openMulti(final Player p, final int page) {
		
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		
		//ItemBuilder.sendMessage(p, ChatColor.GOLD + "Recipe Manager", ChatColor.DARK_GREEN + "Click on a recipe to view and manage it");
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				
				p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
				
				p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
				p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "viewMulti"));
				
				if(page > 1){
					ItemBuilder.clear(p);
				}else{
					ItemBuilder.close(p);
				}
				
				
				
				
				
				
				boolean nextPage = false;
				boolean backPage = false;
				
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				int b = ProRecipes.getPlugin().getRecipes().chest.size();
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
				
				Inventory ib = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Multi-Craft recipes " + page + "/" + pages);
				
				for(int i = start; i < end; i++){
					int slot = i%45;
					ib.setItem(slot, ProRecipes.getPlugin().getRecipes().chest.get(i).getDisplayResult());
				}
				
				ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
				ItemMeta m = addLore.getItemMeta();
				
				m.setDisplayName(ChatColor.GOLD + "View Furnace recipes");
				addLore.setItemMeta(m);
				
					
				
				if(nextPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.GREEN + "Next Page →");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-1, addLoree);
					
					//p.setMetadata("nextPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page+1));
				}
				if(backPage){
					ItemStack addLoree = new ItemStack(Material.PAPER);
					ItemMeta mm = addLoree.getItemMeta();
					
					mm.setDisplayName(ChatColor.YELLOW + "← Back Page");
					addLoree.setItemMeta(mm);
					
					ib.setItem(b-9, addLoree);
					//p.setMetadata("backPage", new FixedMetadataValue(RPGRecipes.getPlugin(), page-1));
				}
				ib.setItem(b-5, addLore);
					
					p.openInventory(ib);
					
				}else{
					b = ProRecipes.getPlugin().getRecipes().chest.size() + 1;
					if(b > 54){
						b = 54;
					}
					if(b < 9){
						b = 9;
					}
					
					while(b % 9 != 0){
						b++;
					}
					
					Inventory i = ProRecipes.getPlugin().getServer().createInventory(p, b, ChatColor.GOLD + "Multi-Craft recipes");
					
					for(RecipeChest rec : ProRecipes.getPlugin().getRecipes().chest){
						i.setItem(ProRecipes.getPlugin().getRecipes().chest.indexOf(rec)%45, rec.getDisplayResult());
					}
					
					ItemStack addLore = new ItemStack(Material.GLOWSTONE_DUST);
					ItemMeta m = addLore.getItemMeta();
					
					m.setDisplayName(ChatColor.GOLD + "View Furnace recipes");
					addLore.setItemMeta(m);
					
					i.setItem(b-1, addLore);
					
					p.openInventory(i);
				}
				
			
				
				
				
				
				
				p.removeMetadata("closed", ProRecipes.getPlugin());
				
				
				
			}
			
		}, 0);
	}
	
	
	private void confirmModify(final Player p, String title, final int type){
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		//0 = shapeless
		//1 = shaped
		//2 == furnace
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
			
				
				p.openWorkbench(null, true);
				
				ItemStack addLore = new ItemStack(Material.GLOWSTONE);
				ItemMeta m = addLore.getItemMeta();
				
				m.setDisplayName(ChatColor.GOLD + "Accept");
				addLore.setItemMeta(m);
				
				ItemStack addLore2 = new ItemStack(Material.REDSTONE_BLOCK);
				ItemMeta m2 = addLore2.getItemMeta();
				
				m2.setDisplayName(ChatColor.GOLD + "Cancel");
				addLore2.setItemMeta(m2);
				
				p.getOpenInventory().setItem(1, addLore);
				p.getOpenInventory().setItem(4, addLore);
				p.getOpenInventory().setItem(7, addLore);
				
				p.getOpenInventory().setItem(3, addLore2);
				p.getOpenInventory().setItem(6, addLore2);
				p.getOpenInventory().setItem(9, addLore2);
				
				if(type == 1){
					p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
					p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "confirmModifyShaped"));
				}else if(type == 0){
					p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
					p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "confirmModifyShapeless"));
				}else if(type == 2){
					p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
					p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "confirmModifyFurnace"));
				}else{
					p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
					p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "confirmModifyMulti"));
				}
				
			}
			
		}, (int)(ProRecipes.getPlugin().wait/2.333));
		//ItemBuilder.close(p);
		
	}
	
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event){
		if(event.getClickedInventory() == null)return;
		//if(event.getSlot() == -999)
		if(!event.getWhoClicked().hasMetadata("recipeViewer"))return;
		//////////////////////System.out.println"Called");
		//////////////////////System.out.printlnevent.getInventory().getTitle());
		//////////////////////System.out.printlnevent.getAction());
		if(((Player)event.getWhoClicked()).isSneaking()){
			event.setCancelled(true);
			return;
		}
		//////////////////////System.out.printlnevent.getAction());
		String step = event.getWhoClicked().getMetadata("recipeViewer").get(0).asString();
		////////////////////System.out.printlnstep);
		//if(event.getSlotType().equals(SlotType.RESULT))event.setCancelled(true);
		if(step.equalsIgnoreCase("chooseType")){
			if(event.getSlot() == 4){
				openShapeless((Player)event.getWhoClicked(), 1);
			}else if(event.getSlot() == 6){
				openShaped((Player)event.getWhoClicked(), 1);
			}else if(event.getSlot() == 5){
				openFurnace((Player)event.getWhoClicked(), 1);
			}else if(event.getSlot() == 8){
				openMulti((Player)event.getWhoClicked(), 1);
			}
			event.setCancelled(true);
		}else if(step.equals("viewShaped")){
			//Open which recipe they select
			event.setCancelled(true);
			
			if(event.getInventory().getTitle().contains("/")){
				
				//This has more than one page
				String formatted = event.getInventory().getTitle().replaceAll(ChatColor.GOLD + "Shaped recipes ", "");
				String[] pagess = formatted.split("/");
				int page = Integer.parseInt(pagess[0]);
				int pages = Integer.parseInt(pagess[1]);
				
				if(event.getSlot() == event.getInventory().getSize()-9){
					//Wants to go back a page
					
					if(page-1 > 0){
						openShaped((Player)event.getWhoClicked(), page-1);
					}
				}else if(event.getSlot() == event.getInventory().getSize()-5){
					//View shapeless
					openMulti((Player)event.getWhoClicked(), 1);
				}else if(event.getSlot() == event.getInventory().getSize()-1){
					ItemStack i = event.getCurrentItem();
					if(i != null && !i.getType().equals(Material.AIR)){
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page →")){
							//next page
							////////////////System.out.println(i.getItemMeta().getDisplayName());
							if(page+1 <= pages){
								openShaped((Player)event.getWhoClicked(), page+1);
							}
							
						}else{
							//View shapeless
							openMulti((Player)event.getWhoClicked(), 1);
						}
					}
					
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked(), event.getSlot()+ ((page-1)*45), 1);
					}
				}
				return;
			}
			if(event.getSlot() == event.getInventory().getSize()-1){
				//View the other one
				//////////////System.out.println("Viewing other one");
				openMulti((Player)event.getWhoClicked(), 1);
			}else{
				
				if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
					openRecipeInfo((Player)event.getWhoClicked(), event.getSlot(), 1);
				}
				
			}
		}else if(step.equals("viewShapeless")){
			event.setCancelled(true);
			if(event.getInventory().getTitle().contains("/")){
				
				//This has more than one page
				String formatted = event.getInventory().getTitle().replaceAll(ChatColor.GOLD + "Shapeless recipes ", "");
				String[] pagess = formatted.split("/");
				int page = Integer.parseInt(pagess[0]);
				int pages = Integer.parseInt(pagess[1]);
				
				if(event.getSlot() == event.getInventory().getSize()-9){
					//Wants to go back a page
					
					if(page-1 > 0){
						openShapeless((Player)event.getWhoClicked(), page-1);
					}
				}else if(event.getSlot() == event.getInventory().getSize()-5){
					//View shaped
					openShaped((Player)event.getWhoClicked(), 1);
				}else if(event.getSlot() == event.getInventory().getSize()-1){
					ItemStack i = event.getCurrentItem();
					if(i != null && !i.getType().equals(Material.AIR)){
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page →")){
							//next page
							////////////////System.out.println(i.getItemMeta().getDisplayName());
							if(page+1 <= pages){
								openShapeless((Player)event.getWhoClicked(), page+1);
							}
							
						}else{
							//View shaped
							openShaped((Player)event.getWhoClicked(), 1);
						}
					}
					
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked(), event.getSlot()+ ((page-1)*45), 0);
					}
				}
				return;
			}
				if(event.getSlot() == event.getInventory().getSize()-1){
					//View the other one
					
					openShaped((Player)event.getWhoClicked(), 1);
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked(), event.getSlot(), 0);
					}
					
				}
			
			
		
		}else if(step.equals("viewFurnace")){
			event.setCancelled(true);
			if(event.getInventory().getTitle().contains("/")){
				
				//This has more than one page
				String formatted = event.getInventory().getTitle().replaceAll(ChatColor.GOLD + "Furnace recipes ", "");
				String[] pagess = formatted.split("/");
				int page = Integer.parseInt(pagess[0]);
				int pages = Integer.parseInt(pagess[1]);
				
				if(event.getSlot() == event.getInventory().getSize()-9){
					//Wants to go back a page
					
					if(page-1 > 0){
						openFurnace((Player)event.getWhoClicked(), page-1);
					}
				}else if(event.getSlot() == event.getInventory().getSize()-5){
					//View shaped
					openFurnace((Player)event.getWhoClicked(), 1);
				}else if(event.getSlot() == event.getInventory().getSize()-1){
					ItemStack i = event.getCurrentItem();
					if(i != null && !i.getType().equals(Material.AIR)){
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page →")){
							//next page
							////////////////System.out.println(i.getItemMeta().getDisplayName());
							if(page+1 <= pages){
								openFurnace((Player)event.getWhoClicked(), page+1);
							}
							
						}else{
							//View shaped
							openShapeless((Player)event.getWhoClicked(), 1);
						}
					}
					
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked() , event.getSlot()+ ((page-1)*45), 2);
					}
				}
				return;
			}
				if(event.getSlot() == event.getInventory().getSize()-1){
					//View the other one
					
					openShapeless((Player)event.getWhoClicked(), 1);
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked(), event.getSlot(), 2);
					}
					
				}
			
			
		}else if(step.equals("viewMulti")){
			event.setCancelled(true);
			if(event.getInventory().getTitle().contains("/")){
				
				//This has more than one page
				String formatted = event.getInventory().getTitle().replaceAll(ChatColor.GOLD + "Multi-Craft recipes ", "");
				String[] pagess = formatted.split("/");
				int page = Integer.parseInt(pagess[0]);
				int pages = Integer.parseInt(pagess[1]);
				
				if(event.getSlot() == event.getInventory().getSize()-9){
					//Wants to go back a page
					
					if(page-1 > 0){
						openMulti((Player)event.getWhoClicked(), page-1);
					}
				}else if(event.getSlot() == event.getInventory().getSize()-5){
					//View shaped
					openMulti((Player)event.getWhoClicked(), 1);
				}else if(event.getSlot() == event.getInventory().getSize()-1){
					ItemStack i = event.getCurrentItem();
					if(i != null && !i.getType().equals(Material.AIR)){
						if(i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page →")){
							//next page
							////////////////System.out.println(i.getItemMeta().getDisplayName());
							if(page+1 <= pages){
								openMulti((Player)event.getWhoClicked(), page+1);
							}
							
						}else{
							//View shaped
							openFurnace((Player)event.getWhoClicked(), 1);
						}
					}
					
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked() , event.getSlot()+ ((page-1)*45), 3);
					}
				}
				return;
			}
				if(event.getSlot() == event.getInventory().getSize()-1){
					//View the other one
					
					openFurnace((Player)event.getWhoClicked(), 1);
				}else{
					if(event.getInventory().getSize() > event.getSlot() && event.getInventory().getItem(event.getSlot()) != null){
						openRecipeInfo((Player)event.getWhoClicked(), event.getSlot(), 3);
					}
					
				}
			
			
			
		}else if(step.equals("confirmModifyShaped")){
		
			//these are set when player closes their inventory
			//Check other metadata to see if it is delete, or modify recipe
			if(event.getSlot() == 4 || event.getSlot() == 1 || event.getSlot() == 7){
				//Accept
				int id = event.getWhoClicked().getMetadata("recipeId").get(0).asInt();
				ProRecipes.getPlugin().rec.shaped.get(id).unregister();
				ProRecipes.getPlugin().rec.removeConflict(ProRecipes.getPlugin().rec.shaped.get(id));
				ProRecipes.getPlugin().rec.shaped.remove(id);
				if(shaped.get(event.getWhoClicked().getName()) != null){
					shaped.get(event.getWhoClicked().getName()).register();
					shaped.remove(event.getWhoClicked().getName());
					
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Edited", ChatColor.DARK_GREEN + "Recipe edited!"));
				}else{
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Deleted", ChatColor.DARK_RED + "Recipe deleted!"));
				}
				
				ProRecipes.getPlugin().rec.saveRecipes(false);
				
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
			}else if(event.getSlot() == 6 || event.getSlot() == 3 || event.getSlot() == 9){
				//Cancel
				event.setCancelled(true);
				ItemBuilder.close((Player)event.getWhoClicked());
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
				
			}
			openShaped((Player)event.getWhoClicked(), 1);
		}else if(step.equalsIgnoreCase("confirmModifyShapeless")){
			if(event.getSlot() == 4 || event.getSlot() == 1 || event.getSlot() == 7){
				int id = event.getWhoClicked().getMetadata("recipeId").get(0).asInt();
				ProRecipes.getPlugin().rec.removeConflict(ProRecipes.getPlugin().rec.shapeless.get(id));
				ProRecipes.getPlugin().rec.shapeless.get(id).unregister();
				ProRecipes.getPlugin().rec.shapeless.remove(id);
				if(shapeless.get(event.getWhoClicked().getName()) != null){
					shapeless.get(event.getWhoClicked().getName()).register();
					shapeless.remove(event.getWhoClicked().getName());
					
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Edited", ChatColor.DARK_GREEN + "Recipe edited!"));
				}else{
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Deleted", ChatColor.DARK_RED + "Recipe deleted!"));
				}
				
				ProRecipes.getPlugin().rec.saveRecipes(false);
				
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
			}else if(event.getSlot() == 6 || event.getSlot() == 3 || event.getSlot() == 9){
				event.setCancelled(true);
				ItemBuilder.close((Player)event.getWhoClicked());
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
				
			}
			openShapeless((Player)event.getWhoClicked(), 1);
		}else if(step.equalsIgnoreCase("confirmModifyFurnace")){
			if(event.getSlot() == 4 || event.getSlot() == 1 || event.getSlot() == 7){
				int id = event.getWhoClicked().getMetadata("recipeId").get(0).asInt();
				//RPGRecipes.getPlugin().rec.removeConflict(RPGRecipes.getPlugin().rec.shapeless.get(id));
				////////System.out.println(RPGRecipes.getPlugin().rec.fur.size());
				//if(RPGRecipes.getPlugin().rec.fur.size() > id){
					////////System.out.println("Can exist");
				///}
				
				ProRecipes.getPlugin().rec.fur.remove(id);
				////////System.out.println(RPGRecipes.getPlugin().rec.fur.size());
				if(fur.get(event.getWhoClicked().getName()) != null){
					fur.get(event.getWhoClicked().getName()).register();
					fur.remove(event.getWhoClicked().getName());
					
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Edited", ChatColor.DARK_GREEN + "Recipe edited!"));
				}else{
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Deleted", ChatColor.DARK_RED + "Recipe deleted!"));
				}
				
				ProRecipes.getPlugin().rec.saveRecipes(false);
				
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
			}else if(event.getSlot() == 6 || event.getSlot() == 3 || event.getSlot() == 9){
				event.setCancelled(true);
				ItemBuilder.close((Player)event.getWhoClicked());
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
				
			}
			
			openFurnace((Player)event.getWhoClicked(), 1);
			
		}else if(step.equalsIgnoreCase("confirmModifyMulti")){
			if(event.getSlot() == 4 || event.getSlot() == 1 || event.getSlot() == 7){
				int id = event.getWhoClicked().getMetadata("recipeId").get(0).asInt();
				//RPGRecipes.getPlugin().rec.removeConflict(RPGRecipes.getPlugin().rec.shapeless.get(id));
				////////System.out.println(RPGRecipes.getPlugin().rec.fur.size());
				//if(RPGRecipes.getPlugin().rec.fur.size() > id){
					////////System.out.println("Can exist");
				///}
				
				ProRecipes.getPlugin().rec.chest.remove(id);
				////////System.out.println(RPGRecipes.getPlugin().rec.fur.size());
				if(chest.get(event.getWhoClicked().getName()) != null){
					chest.get(event.getWhoClicked().getName()).register();
					chest.remove(event.getWhoClicked().getName());
					
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Edited", ChatColor.DARK_GREEN + "Recipe edited!"));
				}else{
					event.setCancelled(true);
					ItemBuilder.close((Player)event.getWhoClicked());
					ItemBuilder.sendMessage((Player)event.getWhoClicked(), m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"), m.getMessage("Recipe_Viewer_Deleted", ChatColor.DARK_RED + "Recipe deleted!"));
				}
				
				ProRecipes.getPlugin().rec.saveRecipes(false);
				
				storedResults.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
			}else if(event.getSlot() == 6 || event.getSlot() == 3 || event.getSlot() == 9){
				event.setCancelled(true);
				ItemBuilder.close((Player)event.getWhoClicked());
				storedItem.remove(event.getWhoClicked().getName());
				event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getWhoClicked().removeMetadata("recipeId", ProRecipes.getPlugin());
				
			}
			
			openMulti((Player)event.getWhoClicked(), 1);
			
		}else if(step.contains("display")){
			if(!event.getWhoClicked().hasPermission("prorecipes.modifyrecipes")){
				event.setCancelled(true);
			}
			if(!step.contains("Multi")){
				CraftingInventory inv = (CraftingInventory) event.getView().getTopInventory();
				int id = event.getWhoClicked().getMetadata("recipeId").get(0).asInt();
				if(step.equalsIgnoreCase("displayShaped")){
					//////System.out.println("displayShaped");
					RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(id);
					
					ItemStack result = rec.getResult().clone();
					if(event.getWhoClicked().hasPermission("prorecipes.modifyrecipes")){
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
					inv.setItem(0, result);
					inv.setResult(result);
					//System.out.println(result.toString());
				}else if(step.equalsIgnoreCase("displayShapeless")){
					RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
					ItemStack result = rec.getResult().clone();
					if(event.getWhoClicked().hasPermission("prorecipes.modifyrecipes")){
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
					inv.setItem(0, result);
					inv.setResult(result);
					//System.out.println(result.toString());
				}else if(step.equalsIgnoreCase("displayFurnace")){
					RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(id);
					ItemStack result = rec.result.clone();
					if(event.getWhoClicked().hasPermission("prorecipes.modifyrecipes")){
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
					inv.setItem(0, result);
					
					inv.setResult(result);
					//System.out.println(result.toString());
				}
				new BukkitRunnable() {
					  @Override
					  public void run() {
						  for(int t = 0; t < 2; t++){
							  ((Player)event.getWhoClicked()).updateInventory();
						  }
					    
					  }
					}.runTaskLater(ProRecipes.getPlugin(), 2);
				//////System.out.println("Being called");
				//PrepareItemCraftEvent e = new PrepareItemCraftEvent(null, null, false);
				//RPGRecipes.getPlugin().getServer().getPluginManager().callEvent(e);
					if(event.getWhoClicked().hasPermission("prorecipes.modifyrecipes")){
						if(event.getRawSlot() == 0){
							event.setCancelled(true);
							event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
							askPermission((Player)event.getWhoClicked(), step.replace("display", ""));
						}
					}
					//event.setCancelled(true);
				
			}else{
				if(event.getWhoClicked().hasPermission("prorecipes.modifyrecipes")){
					if((event.getRawSlot() - 7) % 9 == 0){
						event.setCancelled(true);
						event.getWhoClicked().removeMetadata("recipeViewer", ProRecipes.getPlugin());
						askPermission((Player)event.getWhoClicked(), step.replace("display", ""));
					}
				}
				//event.setCancelled(true);
				
			}
			
			
			
		}
		
		
	}
	
	
	@EventHandler
	public void onTalk(AsyncPlayerChatEvent event){
		if(event.getPlayer().hasMetadata("recipeViewer")){
			String step = event.getPlayer().getMetadata("recipeViewer").get(0).asString();
			if(step.contains("choosePermission")){
				String recType = step.replace("choosePermission", "");
				if(event.getMessage().equalsIgnoreCase("no")){
					event.setCancelled(true);
					ItemBuilder.sendMessage(event.getPlayer(), m.getMessage("Permission_Set_Title", ChatColor.GOLD + "Permission Change"), m.getMessage("Permission_No", ChatColor.DARK_GREEN + "There is now no permission"));
					
					int id = event.getPlayer().getMetadata("recipeId").get(0).asInt();
					String perm = "";
					if(recType.equalsIgnoreCase("Shapeless")){
						RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
						rec.setPermission(perm);
					}else if(recType.equalsIgnoreCase("Shaped")){
						RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(id);
						rec.setPermission(perm);
					}else if(recType.equalsIgnoreCase("Furnace")){
						RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(id);
						rec.setPermission(perm);
					}else{
						RecipeChest rec = ProRecipes.getPlugin().getRecipes().chest.get(id);
						rec.setPermission(perm);
					}
					event.getPlayer().removeMetadata("recipeViewer", ProRecipes.getPlugin());
					return;
				}
				//event.getPlayer().setMetadata("recPermission", new FixedMetadataValue(RPGRecipes.getPlugin(), event.getMessage()));
				ItemBuilder.sendMessage(event.getPlayer(), m.getMessage("Permission_Set_Title", ChatColor.GOLD + "Permission Change"),  m.getMessage("Permission_Set", ChatColor.DARK_GREEN + "Permission has been set to") + 
						": '" + event.getMessage() + "'");
				event.setCancelled(true);
				
				int id = event.getPlayer().getMetadata("recipeId").get(0).asInt();
				String perm = event.getMessage();
				
				perm = ChatColor.stripColor(perm);
				
				if(recType.equalsIgnoreCase("Shapeless")){
					RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
					rec.setPermission(perm);
				}else if(recType.equalsIgnoreCase("Shaped")){
					RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(id);
					rec.setPermission(perm);
				}else if(recType.equalsIgnoreCase("Furnace")){
					RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(id);
					rec.setPermission(perm);
				}else{
					RecipeChest rec = ProRecipes.getPlugin().getRecipes().chest.get(id);
					rec.setPermission(perm);
				}
				event.getPlayer().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				return;
			}
		}
	}
	
	public void askPermission(final Player p, String type){
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Viewer_Title", ChatColor.GOLD + "Recipe Manager"),  m.getMessage("Choose_Permission", ChatColor.DARK_GREEN + "Type a permission. Type 'no' for no permission"));
		
		p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "choosePermission" + type));
	
	}
	
	public void openRecipeInfo(final Player p, int slot, int type){
		
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		ItemBuilder.close(p);
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		p.openWorkbench(null, true);
		if(type == 3){
			p.openInventory(ProRecipes.getPlugin().getRecipes().createMultiTable(p, 3));
		}
		//////////////System.out.println(p.getOpenInventory().getTopInventory().getName());
		if(type == 1){
			p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
			p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "displayShaped"));
			p.setMetadata("recipeId", new FixedMetadataValue(ProRecipes.getPlugin(), slot));
			RecipeShaped rec = ProRecipes.getPlugin().getRecipes().shaped.get(slot);
			ItemStack[] i = rec.getItems();
			
			for(int z = 0; z < i.length; z++){
				////////////////////System.out.println(z + 1)+ "" + i[z].getType());
				if(i[z] != null){
					p.getOpenInventory().setItem(z+1, i[z].clone());
				}
				
			}
			final ItemStack result = rec.getResult().clone();
			
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
			
			ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					p.getOpenInventory().setItem(0, result);
				}
				
			}, 1);
			
			//p.getOpenInventory().setItem(0, rec.getResult().clone());
			////////////////////System.out.printlnrec.getResult().getType());
			//p.updateInventory();
			storedItem.put(p.getName(), rec.getResult());
			ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					p.updateInventory();
					
				}
				
			}, 2);
		}else if (type == 0){
			p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
			p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "displayShapeless"));
			p.setMetadata("recipeId", new FixedMetadataValue(ProRecipes.getPlugin(), slot));
			RecipeShapeless rec = ProRecipes.getPlugin().getRecipes().shapeless.get(slot);
			
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.addAll(rec.items);
			for(int z = 0; z < items.size(); z++){
				////////////////////System.out.println(z + 1)+ "" + items.get(z).getType());
				if(items.get(z) != null){
					p.getOpenInventory().setItem(z+1, items.get(z).clone());
				}
				
			}
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
			
			p.getOpenInventory().setItem(0, result);
			//p.getOpenInventory().setItem(0, rec.getResult().clone());
			
			//p.updateInventory();
			storedItem.put(p.getName(), rec.getResult());
			
			ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					p.updateInventory();
					
				}
				
			}, 2);
			
			
		}else if(type == 2){
			p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
			p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "displayFurnace"));
			p.setMetadata("recipeId", new FixedMetadataValue(ProRecipes.getPlugin(), slot));
			RecipeFurnace rec = ProRecipes.getPlugin().getRecipes().fur.get(slot);
			
			
			//p.getOpenInventory().setItem(0, rec.getResult().clone());
			
			//p.updateInventory();
			p.getOpenInventory().setItem(5, rec.toBurn);
			storedItem.put(p.getName(), rec.result);
			
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
			
			
			p.getOpenInventory().setItem(0, result);
			
			ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

				@Override
				public void run() {
					p.updateInventory();
					
				}
				
			}, 2);
		}else if(type == 3){
			p.removeMetadata("recipeViewer", ProRecipes.getPlugin());
			p.setMetadata("recipeViewer", new FixedMetadataValue(ProRecipes.getPlugin(), "displayMulti"));
			p.setMetadata("recipeId", new FixedMetadataValue(ProRecipes.getPlugin(), slot));
			RecipeChest rec = ProRecipes.getPlugin().getRecipes().chest.get(slot);
			
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for(ItemStack i : rec.getItems()){
				if(i == null){
					items.add(new ItemStack(Material.AIR));
						continue;
					
				}
				items.add(i.clone());
			}
			
			ArrayList<ItemStack> results = new ArrayList<ItemStack>();
			for(ItemStack i : rec.getResult()){
				if(i == null){
					results.add(new ItemStack(Material.AIR));
						continue;
					
				}
				
				ItemStack result = i.clone();
				if(p.hasPermission("prorecipes.modifyrecipes")){
					ItemMeta m = result.getItemMeta();
					List<String> lore = (m != null && m.hasLore()) ? m.getLore() : new ArrayList<String>();
					if(rec.hasPermission()){
						lore.add(ChatColor.RED + "Permission: '" + rec.getPermission() + "'");
					}else{
						lore.add(ChatColor.RED + "No Permission set");
					}
					lore.add(ChatColor.GREEN + "Click to edit permissions");
					m.setLore(lore);
					result.setItemMeta(m);
				}
				results.add(result.clone());
			}
			int inCount = 0;
			int resCount = 0;
			for(int i = 9; i < 45; i++){
				
				if((i-1) % 9 == 0 || (i-2) % 9 == 0 || (i-3) % 9 == 0 || (i-4) % 9 == 0){
					p.getOpenInventory().setItem(i, items.get(inCount));
					inCount++;
				}else if((i-7) % 9 == 0){
					p.getOpenInventory().setItem(i, results.get(resCount));
					resCount++;
				}
			}
			
			//p.getOpenInventory().setItem(0, rec.getResult().clone());
			
			//p.updateInventory();
			storedResults.put(p.getName(), rec.getResult());
		}
				
			
			
		
		
	}
	
	public ItemStack removePerm(ItemStack i){
		if(i == null)return i;
		ItemStack b = i.clone();
		//System.out.println(b.getDurability());
		//if(b.getDurability() == (short)-1){
		//	System.out.println("SETTING DURABILITY");
			//b.setDurability((short)0);
		//}
		if(b.hasItemMeta()){
			if(b.getItemMeta().hasLore()){
				List<String> lore = b.getItemMeta().getLore();
				lore.remove(ChatColor.GREEN + "Click to edit permissions");
				lore.remove(ChatColor.RED + "No Permission set");
				String re = null;
				for(String s : lore){
					if(s.contains("Permission: '")){
						re = s;
						break;
					}
				}
				if(re != null){
					lore.remove(re);
				}
				ItemMeta m = b.getItemMeta();
				m.setLore(lore);
				b.setItemMeta(m);
			}
		}
		return b;
	}
	
	
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		if(event.getPlayer().hasMetadata("recipeViewer")){
			String step = event.getPlayer().getMetadata("recipeViewer").get(0).asString();
			ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
			((Player)event.getPlayer()).updateInventory();
			
			//////////////////System.out.printlnevent.getPlayer().getOpenInventory().getTopInventory().getType());
			boolean empty = true;
			boolean one = false;
			ItemStack z = null;
			Inventory v = event.getPlayer().getOpenInventory().getTopInventory();
			if(step.contains("display")){
				for(int i = 1; i < 10; i++){
					ItemStack it = event.getPlayer().getOpenInventory().getTopInventory().getItem(i);
					if(it != null && !it.getType().equals(Material.AIR)){
						
						//////////////////System.out.printlnit.getType() + " " + i);
					}else{
						//////////////////System.out.println"null " + i);
					}
					//////////////////System.out.println);
					arr.add(it);
				}
				
				for(ItemStack it : arr){
					
					if(it != null && !it.getType().equals(Material.AIR)){
						if(!empty){
							////////System.out.println("Not one");
							one = false;
						}
						if(empty){
							z = it.clone();
							one = true;
							empty = false;
						}
						////////////////////System.out.printlnit.getType());
					}else{
						////////////////////System.out.println"null");
					}
				}
			}
			
			if(event.getPlayer().hasPermission("prorecipes.modifyrecipes")){
				if(step.equals("displayShaped")){
					int id = event.getPlayer().getMetadata("recipeId").get(0).asInt();
					//event.getPlayer().removeMetadata("recipeId", RPGRecipes.getPlugin());
					RecipeShaped recC = ProRecipes.getPlugin().getRecipes().shaped.get(id);
					//////////////////System.out.printlnempty);
					RecipeShaped rec = null;
					if(!empty){
						//////////////////System.out.println"Is not empty");
						rec = RecipeBuilder.createShaped(storedItem.get(event.getPlayer().getName()), arr.toArray(new ItemStack[9]), false);
					}
					
					
					if(rec != null && recC.match(rec) && 
							ProRecipes.itemToStringBlob(recC.getResult()).equalsIgnoreCase(ProRecipes.itemToStringBlob(removePerm(rec.getResult())))){
						//////////////////System.out.println"They're the same");
						openShaped((Player)event.getPlayer(), 1);
					}else{
						//////////////////System.out.println"Not the same");
						if(empty){
							shaped.put(event.getPlayer().getName(), null);
							confirmModify((Player)event.getPlayer(), "Confirm Delete", 1);
							//They want to remove
						}else{
							shaped.put(event.getPlayer().getName(), rec);
							confirmModify((Player)event.getPlayer(), "Confirm Modify", 1);
						}
						//They changed it.
					}
				}else if(step.equals("displayShapeless")){
					int id = event.getPlayer().getMetadata("recipeId").get(0).asInt();
					//event.getPlayer().removeMetadata("recipeId", RPGRecipes.getPlugin());
					RecipeShapeless recC = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
					RecipeShapeless rec = null;
					if(!empty){
						rec = RecipeBuilder.createShapeless(storedItem.get(event.getPlayer().getName()), arr.toArray(new ItemStack[9]), false);
					}
					
					if(rec !=null && recC.match(rec) &&
							ProRecipes.itemToStringBlob(recC.getResult()).equalsIgnoreCase(ProRecipes.itemToStringBlob(removePerm(rec.getResult())))){
						openShapeless((Player)event.getPlayer(), 1);
					}else{
						if(empty){
							shapeless.put(event.getPlayer().getName(), null);
							confirmModify((Player)event.getPlayer(), "Confirm Delete", 0);
							//They want to remove
						}else{
							shapeless.put(event.getPlayer().getName(), rec);
							confirmModify((Player)event.getPlayer(), "Confirm Modify", 0);
							//Just changed the recipe
						}
					}
					
				}else if(step.endsWith("displayFurnace")){
					int id = event.getPlayer().getMetadata("recipeId").get(0).asInt();
					//////////System.out.println(id);
					////////System.out.println(z.toString());
					////////System.out.println(storedItem.get(event.getPlayer().getName()).toString());
					RecipeFurnace recC = ProRecipes.getPlugin().getRecipes().fur.get(id);
					////////System.out.println("Display Furnace");
					//boolean nono = false;
					if(!one && !empty){
						////////System.out.println("Not one not empty");
						//nono = true;
						event.getInventory().clear();
						ItemBuilder.clear((Player)event.getPlayer());
					}
					
					if(empty){
						////////System.out.println("Empty");
						fur.put(event.getPlayer().getName(), null);
						confirmModify((Player)event.getPlayer(), "Confirm Delete", 2);
					}else if(one){
						////////System.out.println("nono and not empty");
						RecipeFurnace rec = new RecipeFurnace(storedItem.get(event.getPlayer().getName()), z);
						if(recC.match(rec)){
							////////System.out.println("Does match");
							ItemBuilder.clear((Player)event.getPlayer());
							openFurnace((Player)event.getPlayer(), 1);
						}else{
							////////System.out.println("doesn't match");
							fur.put(event.getPlayer().getName(), rec);
							confirmModify((Player)event.getPlayer(), "Confirm Modify", 2);
						}
						
					}
					
				}else if(step.equals("displayMulti")){
					
					ArrayList<ItemStack> items = new ArrayList<ItemStack>();
					ArrayList<ItemStack> resu = new ArrayList<ItemStack>();
					
					for(int i = 9; i < 45; i++){
						if((i-1) % 9 == 0 || (i-2) % 9 == 0 || (i-3) % 9 == 0 || (i-4) % 9 == 0){
							items.add(removePerm(event.getPlayer().getOpenInventory().getItem(i)));
						}else if((i-7) % 9 == 0){
							resu.add(removePerm(event.getPlayer().getOpenInventory().getItem(i)));
						}
					}
					
					boolean emptyIngredients = true;
					for(ItemStack i : items){
						if(i != null && !i.getType().equals(Material.AIR)){
							emptyIngredients = false;
							break;
							
						}
					}
					
					boolean emptyResults = true;
					for(ItemStack i : resu){
						if(i != null && !i.getType().equals(Material.AIR)){
							emptyResults = false;
							break;
							
						}
					}
					
					int id = event.getPlayer().getMetadata("recipeId").get(0).asInt();
					//event.getPlayer().removeMetadata("recipeId", RPGRecipes.getPlugin());
					RecipeChest recC = ProRecipes.getPlugin().getRecipes().chest.get(id);
					RecipeChest rec = null;
					if(!emptyIngredients && !emptyResults){
						//System.out.println("Creating recipe");
						rec = RecipeBuilder.createChest(resu.toArray(new ItemStack[4]), items.toArray(new ItemStack[16]), false);
					}
					
					//System.out.println("Match: " + recC.match(rec));
				//	System.out.println("Match results: " + matchResults(recC.getResult(), rec.getResult()));
					if(rec !=null && recC.match(rec) /*&& matchResults(recC.getResult(), rec.getResult())*/){
					//s	System.out.println("Matched, recipe wasn't null, and results were same");
						openMulti((Player)event.getPlayer(), 1);
					}else{
						//System.out.println("Didn't match");
						if(emptyIngredients || emptyResults){
							//System.out.println("no ingredients");
							chest.put(event.getPlayer().getName(), null);
							confirmModify((Player)event.getPlayer(), "Confirm Delete", 3);
							//They want to remove
						}else{
							//System.out.println("were ingredients");
							chest.put(event.getPlayer().getName(), rec);
							confirmModify((Player)event.getPlayer(), "Confirm Modify", 3);
							//Just changed the recipe
						}
					}
					
				}
			}else{
				if(step.equals("displayFurnace")){
					openFurnace((Player)event.getPlayer(), 1);
				}else if(step.equals("displayShapeless")){
					openShapeless((Player)event.getPlayer(), 1);
				}else if(step.equals("displayShaped")){
					openShaped((Player)event.getPlayer(), 1);
				}else if(step.equals("displayMulti")){
					openMulti((Player)event.getPlayer(), 1);
				}
			}
			//////////////////System.out.printlnstep);
			
			if(!event.getPlayer().hasMetadata("closed") || step.equalsIgnoreCase("chooseType")){
				event.getPlayer().removeMetadata("recipeViewer", ProRecipes.getPlugin());
				event.getInventory().clear();
				return;
			}
			
		}
	}
	
	public boolean matchResults(ItemStack[] t, ItemStack[] a){
		//i is original
		for(int i = 0; i < t.length; i++){
			if((t[i] == null || t[i].getType().equals(Material.AIR)) && (a[i] != null && !a[i].getType().equals(Material.AIR))){
				//System.out.println("oringinal is null, otheri s not");
				return false;
			}
			if((a[i] == null || a[i].getType().equals(Material.AIR)) && (t[i] != null && !t[i].getType().equals(Material.AIR))){
				//System.out.println("oringinal is not null, other is");
				return false;
			}
			if(t[i] == null || a[i] == null)continue;
			
			if(!ProRecipes.itemToStringBlob(t[i]).equalsIgnoreCase(ProRecipes.itemToStringBlob(a[i]))){
				//System.out.println("do not equal");
				return false;
			}
		}
		return true;
	}


	

}
