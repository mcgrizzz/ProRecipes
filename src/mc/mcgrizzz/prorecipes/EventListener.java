package mc.mcgrizzz.prorecipes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.mcgrizzz.prorecipes.RecipeAPI.RecipeType;
import mc.mcgrizzz.prorecipes.events.FurnaceCraftEvent;
import mc.mcgrizzz.prorecipes.events.MulticraftEvent;
import mc.mcgrizzz.prorecipes.events.WorkbenchCraftEvent;

public class EventListener implements Listener{
	
	
	/**
	 * 
	 * 
	 * Will only be called for shaped and shapeless
	 * @param event
	 */
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void workbenchCraft(WorkbenchCraftEvent event){
		CraftingInventory inventory = (CraftingInventory)event.getInventory();
		
		if(event.isCancelled()){
			inventory.setResult((new ItemStack(Material.AIR)));
			return;
		}
		
		inventory.setResult(event.getResult());
		inventory.setItem(0, event.getResult());
		
		ProRecipes.getPlugin().incrementRecipesCrafted(event.getRecipe().getType());
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void multicraftEvent(MulticraftEvent event){
		Inventory inv = event.getInventory();
		if(event.isCancelled()){
			for(int i = 0; i < 3; i++){
				inv.setItem(16 + i*9, null);
			}
			return;
		}
		
		int c = 0;
		for(ItemStack ite : event.getResults()){
			if(c > 3)return;
			//System.out.println(ite);
			inv.setItem(16 + c*9, ite);
			c++;
		}
		
		ProRecipes.getPlugin().incrementRecipesCrafted(RecipeType.MULTI);
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void furnaceCraftEvent(FurnaceCraftEvent event){
		FurnaceInventory inv = (FurnaceInventory)event.getInventory();
		if(event.isCancelled()){
			inv.setResult(new ItemStack(Material.AIR));
			return;
		}else{
			int amount = 0;
			
			ItemStack b = new ItemStack(inv.getItem(0));
			
			amount = b.getAmount();
			
			int newAmount = amount - ProRecipes.getPlugin().getRecipes().fur.get(event.getRecipe().getId()).getSubtractAmount();
			ItemStack i = event.getSource().clone();
			i.setAmount(newAmount);
			inv.setSmelting(i);
			if(inv.getResult() != null && inv.getResult().isSimilar(event.getResult())){
				ItemStack re = event.getResult().clone();
				re.setAmount(re.getAmount() + inv.getResult().getAmount());
				inv.setResult(re);
			}else{
				inv.setResult(event.getResult());
			}
			
			ProRecipes.getPlugin().incrementRecipesCrafted(RecipeType.FURNACE);
			
		}
	}

}
