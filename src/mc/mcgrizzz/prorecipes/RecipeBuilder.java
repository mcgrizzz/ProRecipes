package mc.mcgrizzz.prorecipes;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;

import co.kepler.fastcraft.api.FastCraftApi;

public class RecipeBuilder implements Listener{
	
	//Used for backup
	public HashMap<String, ItemStack> storedItems = new HashMap<String, ItemStack>();
	public Messages m;
	
	public RecipeBuilder(){
		m = ProRecipes.getPlugin().ms;
	}
	
	public void openRecipe(final Player p, ItemStack lookup){
		
		ItemStack[] matrix = null;
		
		//ProRecipes.getPlugin().getTippedArrowRec(lookup);
		
		if(ProRecipes.getPlugin().isVanilla(lookup)){
			org.bukkit.inventory.Recipe r = ProRecipes.getPlugin().findRecipe(lookup);
			matrix = ProRecipes.getPlugin().getMatrix(r);
			if(r == null || matrix == null){
				ItemBuilder.sendMessage(p, m.getMessage("Recipe_Lookup_Title", ChatColor.GOLD + "Recipe Lookup"), 
						m.getMessage("Recipe_Lookup_Failed", ChatColor.DARK_RED + "No Recipe Found!"));
				return;
			}
		}else{
			//System.out.println("CUSTOM");
			RecipeContainer r = ProRecipes.getPlugin().getRecipes().getRecipe(lookup);
			if(r == null){
				org.bukkit.inventory.Recipe re = ProRecipes.getPlugin().findRecipe(lookup);
				matrix = ProRecipes.getPlugin().getMatrix(re);
				if(re == null || matrix == null){
					if(lookup.getType() == Material.TIPPED_ARROW){
						matrix = ProRecipes.getPlugin().getTippedArrowRec(lookup);
					}else{
						ItemBuilder.sendMessage(p, m.getMessage("Recipe_Lookup_Title", ChatColor.GOLD + "Recipe Lookup"), 
								m.getMessage("Recipe_Lookup_Failed", ChatColor.DARK_RED + "No Recipe Found!"));
						return;
					}
					
				}
			}else{
				matrix = r.getMatrixView();
			}
		}
		final ItemStack[] items = matrix;
		
		/*(for(ItemStack item : items){
			if(item == null){
				System.out.println("AIR");
			}else{
				System.out.println(item.toString());
			}
		}*/
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "itemLookup"));
				p.openWorkbench(null, true);
				for(int i = 0; i < items.length; i++){
					if(items[i] != null){
						p.getOpenInventory().setItem(i+1, items[i].clone());
					}
				}
			
			
	}
	
	
 	
	public void openRecipeBuilder(final Player p, final String type){
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		ItemBuilder.close(p);
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , m.getMessage("Recipe_Builder_Insert", ChatColor.DARK_GREEN + "Insert the desired result"));
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "itemRequest" + type));
				p.openWorkbench(null, true);
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, ProRecipes.getPlugin().wait);
		
	}
	
	private void openShapeless(final Player p) {
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		final ItemStack i = p.getOpenInventory().getItem(0).clone();
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") ,  m.getMessage("Recipe_Builder_Add", ChatColor.DARK_GREEN + "Add your ingredients! Close to save recipe."));
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){
			
			@Override
			public void run() {
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "craftRecipeShapeless"));
				p.openWorkbench(null, true);
				//p.removeMetadata("closed", RPGRecipes.getPlugin());
				p.getOpenInventory().setItem(0, i);
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, ProRecipes.getPlugin().wait);
		
	}

	private void openShaped(final Player p) {
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		final ItemStack i = p.getOpenInventory().getItem(0).clone();
		
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") ,  m.getMessage("Recipe_Builder_Add", ChatColor.DARK_GREEN + "Add your ingredients! Close to save recipe."));
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "craftRecipeShaped"));
				p.openWorkbench(null, true);
				//p.removeMetadata("closed", RPGRecipes.getPlugin());
				p.getOpenInventory().setItem(0, i);
				//Inventory i = RPGRecipes.getPlugin().getServer().createInventory(p, InventoryType.WORKBENCH, "ItemBuilder");
				//p.openInventory(i);
			}
			
		}, ProRecipes.getPlugin().wait);
		
	}
	
	private void openFurnace(final Player p) {
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		final ItemStack i = p.getOpenInventory().getItem(0).clone();
		
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") ,  m.getMessage("Recipe_Builder_Furnace", ChatColor.DARK_GREEN + "Add your source! Close to save recipe."));
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "craftRecipeFurnace"));
				p.openWorkbench(null, true);
				p.getOpenInventory().setItem(0, i);
			}
			
		}, ProRecipes.getPlugin().wait);
	}
	
	private void openMutliCraft(final Player p){
		
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder")  + ": " + m.getMessage("Recipe_Builder_MultiCraft_Title", "Multi-Craft"),  
				m.getMessage("Recipe_Builder_MultiCraft_Add", ChatColor.DARK_GREEN + "Add ingredients on left, results on right"));
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "craftRecipeChest"));
				p.openInventory(ProRecipes.getPlugin().getRecipes().createMultiTable(p, 0));
				
			}
			
		}, ProRecipes.getPlugin().wait);
	}
	
	
	public void openChoice(final Player p) {
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") ,  m.getMessage("Choose_Type", ChatColor.DARK_GREEN + "Choose the type of recipe"));
		
		ProRecipes.getPlugin().getServer().getScheduler().runTaskLater(ProRecipes.getPlugin(), new Runnable(){

			@Override
			public void run() {
				p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "chooseType"));
				p.openWorkbench(null, true);
				p.removeMetadata("closed", ProRecipes.getPlugin());
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
				p.getOpenInventory().setItem(8, takeLoreee);
				p.getOpenInventory().setItem(6, addLore);
				
			}
			
		}, ProRecipes.getPlugin().wait);
		
	}
	
	public void askPermission(final Player p){
		if(ProRecipes.getPlugin().fastCraft){FastCraftApi.allowCraftingInvToOpen(p);}
		
		
		ItemBuilder.close(p);
		p.setMetadata("closed", new FixedMetadataValue(ProRecipes.getPlugin(), ""));
		ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , m.getMessage("Choose_Permission", ChatColor.DARK_GREEN + "Type a permission. Type 'no' for no permission"));
		
		p.setMetadata("recipeBuilder", new FixedMetadataValue(ProRecipes.getPlugin(), "choosePermission"));
	
	}
	
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getInventory() == null)return;
		if(!event.getInventory().getType().equals(InventoryType.WORKBENCH))return;
		if(!event.getWhoClicked().hasMetadata("recipeBuilder"))return;
		
		if(((Player)event.getWhoClicked()).isSneaking()){
			event.setCancelled(true);
			return;
		}
		//////////////////////System.out.printlnevent.getAction());
		String step = event.getWhoClicked().getMetadata("recipeBuilder").get(0).asString();
		if(step.contains("itemRequest")){
			if(event.getRawSlot() > 9 || event.getRawSlot() == 0)return;
			String type = step.replace("itemRequest", "");
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
				storedItems.put(event.getWhoClicked().getName(), i);
				event.getInventory().setItem(0, i);
				if(type.equalsIgnoreCase("shapeless")){
					openShapeless((Player)event.getWhoClicked());
				}else if(type.equalsIgnoreCase("shaped")){
					openShaped((Player)event.getWhoClicked());
				}else if(type.equalsIgnoreCase("smelt")){
					openFurnace((Player)event.getWhoClicked());
				}/*else if(type.equalsIgnoreCase("multi")){
					openMutliCraft((Player)event.getWhoClicked());
				}*/
				return;
			case NOTHING:
				openMutliCraft((Player)event.getWhoClicked());
				//This is going to be mutlicraft
				
			}
		}else if(step.equalsIgnoreCase("chooseType")){
			if(event.getSlot() == 4){
				openRecipeBuilder((Player)event.getWhoClicked(), "shapeless");
			}else if(event.getSlot() == 6){
				openRecipeBuilder((Player)event.getWhoClicked(), "shaped");
			}else if(event.getSlot() == 5){
				openRecipeBuilder((Player)event.getWhoClicked(), "smelt");
			}else if(event.getSlot() == 8){
				openMutliCraft((Player)event.getWhoClicked());
			}
			
			event.setCancelled(true);
		}else if(step.contains("craft")){
			if(event.getRawSlot() == 0){
				event.setCancelled(true);
			}
			
			if(step.equalsIgnoreCase("craftRecipeFurnace")){
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
				saveFurnace(i, (Player)event.getWhoClicked());
				ItemBuilder.close((Player)event.getWhoClicked());
				}
			}
		}else if(step.equalsIgnoreCase("itemLookup")){
			event.setCancelled(true);
		}
			/*else if(step.equalsIgnoreCase("choosePermission")){
		}
			if(event.getRawSlot() == 0){
				event.setCancelled(true);
			}else if(event.getRawSlot() == 1){
				if(event.getInventory().getItem(2).hasItemMeta()){
					String name = event.getInventory().getItem(2).getItemMeta().getDisplayName();
					event.getWhoClicked().setMetadata("recPermission", new FixedMetadataValue(RPGRecipes.getPlugin(), name));
				}
				event.setCancelled(true);
				openChoice((Player)event.getWhoClicked());
			}
		}*/
		
	}
	
	@EventHandler
	public void onTalk(AsyncPlayerChatEvent event){
		if(event.getPlayer().hasMetadata("recipeBuilder")){
			String step = event.getPlayer().getMetadata("recipeBuilder").get(0).asString();
			if(step.equalsIgnoreCase("choosePermission")){
				if(event.getMessage().equalsIgnoreCase("no")){
					event.setCancelled(true);
					openChoice(event.getPlayer());
					return;
				}
				event.getPlayer().setMetadata("recPermission", new FixedMetadataValue(ProRecipes.getPlugin(), ChatColor.stripColor(event.getMessage())));
				event.setCancelled(true);
				openChoice(event.getPlayer());
			}
		}
	}
	
	

	@EventHandler
	public void inventoryDrag(InventoryDragEvent event){
		if(!event.getInventory().getType().equals(InventoryType.WORKBENCH))return;
		if(!event.getWhoClicked().hasMetadata("recipeBuilder"))return;
		String step = event.getWhoClicked().getMetadata("recipeBuilder").get(0).asString();
		if(step.contains("itemRequest")){
			
				event.setCancelled(true);
				
			}
	}
	
	protected static RecipeChest createChest(ItemStack[] result, ItemStack[] ingredients, boolean reg){
		RecipeChest rec = new RecipeChest(result);
		//System.out.println("Ingredients: ");
		//System.out.println(Arrays.deepToString(ingredients));
		Pair<String[][], HashMap<Character, ItemStack>> s = RecipeChest.getStructure
				(RecipeChest.convertToMinimizedStructure(RecipeChest.convertToArray(ingredients)));
		rec.setStructure(s.getA());
		rec.setIngredients(s.getB());
		if(reg){
			rec.register();
		}
		
		return rec;
		
	}
	
	protected static RecipeShaped createShaped(ItemStack result, ItemStack[] ingredients, boolean reg){
		RecipeShaped rec = new RecipeShaped(result);
		Pair<String[][], HashMap<Character, ItemStack>> s = RecipeShaped.getStructure
				(RecipeShaped.convertToMinimizedStructure(RecipeShaped.convertToArray(ingredients)));
		//////////////////////System.out.println"In create shaped : ");
		//////////////////////System.out.printlnArrays.deepToString(s.getA()));
		//////////////////////System.out.println(s.getB().toString()));
		rec.setStructure(s.getA());
		rec.setIngredients(s.getB());
		
		
		if(reg){
			rec.register();
		}
		
		return rec;
		//////////////////////System.out.println"result: " + result.getType());
	}
	
	protected static RecipeShapeless createShapeless(ItemStack result, ItemStack[] ingredients, boolean reg){
		RecipeShapeless rec = new RecipeShapeless(result);
		for(ItemStack i : ingredients){
			//////////////////////System.out.println"adding " + i);
			rec.addIngredient(i);
		}
		if(reg){
			rec.register();
		}
		return rec;
	}
	
	public void saveFurnace(ItemStack source, Player p){
		p.removeMetadata("recipeBuilder", ProRecipes.getPlugin());
		if(source.getType() != Material.AIR){
			ItemStack ib = null;
			ib = storedItems.get(p.getName()).clone();
			storedItems.remove(p.getName());
			RecipeFurnace rec = new RecipeFurnace(ib, source);
			if(p.hasMetadata("recPermission")){
				rec.setPermission(p.getMetadata("recPermission").get(0).asString());
				p.removeMetadata("recPermission", ProRecipes.getPlugin());
			}
			rec.register();
			ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") ,  m.getMessage("Furnace_Saved", ChatColor.DARK_GREEN + "Your furnace recipe has been saved!"));
			ProRecipes.getPlugin().rec.saveRecipes(false);
		}else{
			ItemBuilder.sendMessage(p, m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") ,  m.getMessage("Recipe_Builder_Empty", ChatColor.DARK_RED + "You cannot save an empty recipe"));
			//return;
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		if(event.getPlayer().hasMetadata("recipeBuilder")){
			if(!event.getPlayer().hasMetadata("closed")){
				event.getPlayer().removeMetadata("recipeBuilder", ProRecipes.getPlugin());
				event.getInventory().clear();
				return;
			}
			
			String step = event.getPlayer().getMetadata("recipeBuilder").get(0).asString();
			//System.out.println(step);
			if(step.contains("craftRecipe")){
				ItemStack ib = storedItems.containsKey(event.getPlayer().getName()) ? storedItems.get(event.getPlayer().getName()).clone() : null;
				
				
				ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
				
				for(int i = 1; i < 10; i++){
					//////////////////System.out.printlnevent.getInventory().getItem(i) + " " + i);
					arr.add(event.getInventory().getItem(i));
				}
				
				boolean empty = true;
				for(ItemStack it : arr){
					if(it != null && !it.getType().equals(Material.AIR)){
						empty = false;
					}
				}
				
				if(step.equalsIgnoreCase("craftRecipeShapeless")){
					if(ib.getType().equals(Material.AIR) || ib == null){
						//ib = storedItems.get(event.getPlayer().getName()).clone();
						storedItems.remove(event.getPlayer().getName());
					}
					event.getPlayer().removeMetadata("recipeBuilder", ProRecipes.getPlugin());
					if(empty){
						ItemBuilder.sendMessage((Player)event.getPlayer(), m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , 
								m.getMessage("Recipe_Builder_Empty", ChatColor.DARK_RED + "You cannot save an empty recipe"));
						//event.setCancelled(true);
						return;
					}
					ItemBuilder.sendMessage((Player)event.getPlayer(), m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , 
							 m.getMessage("Shapeless_Saved", ChatColor.DARK_GREEN + "Your shapeless recipe has been saved!"));
					RecipeShapeless c = createShapeless(ib, arr.toArray(new ItemStack[9]), true);
					if(event.getPlayer().hasMetadata("recPermission")){
						String permission = event.getPlayer().getMetadata("recPermission").get(0).asString();
						c.setPermission(permission);
						event.getPlayer().removeMetadata("recPermission", ProRecipes.getPlugin());
					}
					ProRecipes.getPlugin().rec.saveRecipes(false);
				}else if(step.equalsIgnoreCase("craftRecipeShaped")){
					if(ib.getType().equals(Material.AIR) || ib == null){
						ib = storedItems.get(event.getPlayer().getName()).clone();
						storedItems.remove(event.getPlayer().getName());
					}
					event.getPlayer().removeMetadata("recipeBuilder", ProRecipes.getPlugin());
					if(empty){
						ItemBuilder.sendMessage((Player)event.getPlayer(), m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , 
								m.getMessage("Recipe_Builder_Empty", ChatColor.DARK_RED + "You cannot save an empty recipe"));
						return;
					}
					ItemBuilder.sendMessage((Player)event.getPlayer(), m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , 
							 
							m.getMessage("Shaped_Saved", ChatColor.DARK_GREEN + "Your shaped recipe has been saved!"));
					RecipeShaped c = createShaped(ib, arr.toArray(new ItemStack[9]), true);
					if(event.getPlayer().hasMetadata("recPermission")){
						String permission = event.getPlayer().getMetadata("recPermission").get(0).asString();
						c.setPermission(permission);
						event.getPlayer().removeMetadata("recPermission", ProRecipes.getPlugin());
					}
					ProRecipes.getPlugin().rec.saveRecipes(false);
				}else if(step.equalsIgnoreCase("craftRecipeChest")){
					if(!event.getInventory().getName().contains(ProRecipes.getPlugin().ms.getMessage("Multi_Craft_Enter", "Enter Recipe"))){
						return;
					}
					ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
					ArrayList<ItemStack> results = new ArrayList<ItemStack>();
					for(int i = 9; i < 45; i++){
						
							if((i-1) % 9 == 0 || (i-2) % 9 == 0 || (i-3) % 9 == 0 || (i-4) % 9 == 0){
								ingredients.add(event.getInventory().getItem(i));
								if(event.getInventory().getItem(i) != null){
									//System.out.println(event.getInventory().getItem(i));
								}else{
									//System.out.println("null");
								}
							}else if((i-7) % 9 == 0){
								results.add(event.getInventory().getItem(i));
							}
						
						
					}
					
					
					boolean emptyIngredients = true;
					for(ItemStack i : ingredients){
						if(i != null){
							emptyIngredients = false;
							break;
							
						}
					}
					
					event.getPlayer().removeMetadata("recipeBuilder", ProRecipes.getPlugin());
					
					if(emptyIngredients){
						ItemBuilder.sendMessage((Player)event.getPlayer(), m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , m.getMessage("Recipe_Builder_Empty", ChatColor.DARK_RED + "You cannot save an empty recipe"));
						//createChest(results.toArray(new ItemStack[4]), ingredients.toArray(new ItemStack[16]));
					}else{
						ItemBuilder.sendMessage((Player)event.getPlayer(), m.getMessage("Recipe_Builder_Title", ChatColor.GOLD + "Recipe Builder") , 
								 m.getMessage("Multi-Craft_Saved", ChatColor.DARK_GREEN + "Your multi-craft recipe has been saved!"));
						RecipeChest c = createChest(results.toArray(new ItemStack[4]), ingredients.toArray(new ItemStack[16]), true);
						if(event.getPlayer().hasMetadata("recPermission")){
							String permission = event.getPlayer().getMetadata("recPermission").get(0).asString();
							c.setPermission(permission);
							event.getPlayer().removeMetadata("recPermission", ProRecipes.getPlugin());
						}
						ProRecipes.getPlugin().rec.saveRecipes(false);
					}
					
				}else if(step.equalsIgnoreCase("itemLookup")){
					event.getPlayer().removeMetadata("recipeBuilder", ProRecipes.getPlugin());
				}
			}
			
			if(step.equalsIgnoreCase("chooseType") || step.contains("craftRecipe")){
				event.getInventory().clear();
				//close((Player)event.getPlayer());
			}
		}
	}

	

	

}
