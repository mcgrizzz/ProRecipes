package mc.mcgrizzz.prorecipes.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.mcgrizzz.prorecipes.RecipeAPI.RecipeContainer;

/**
 * 
 * Event is called when a multicraft recipe is crafted
 *
 */

public class MulticraftEvent extends Event implements Cancellable {

	boolean cancelled;
	
	private static final HandlerList handlers = new HandlerList();
	
	RecipeContainer recipe;
	Inventory i;
	Player p;
	ItemStack[] result;
	
	public MulticraftEvent(RecipeContainer container, Player p, Inventory i, ItemStack[] result){
		this.recipe = container;
		this.p = p;
		this.i = i;
		this.result = result;
	}
	/**
	 * 
	 * @return The player who crafted 
	 */
	public Player getPlayer(){
		return this.p;
	}
	/**Set a result slot
	 * 
	 * @param item The result item you want to set
	 * @param slot The location of the result <strong>(0-3)</strong>
	 */
	public void setResult(ItemStack item, int slot){
		if(slot > 3 || slot < 0)return;
		result[slot] = item;
	}
	/**
	 * 
	 * @param slot The slot to check for results
	 * @return The result in the given slot
	 */
	public ItemStack getResult(int slot){
		if(slot > 3 || slot < 0)return null;
		return result[slot];
	}
	
	/**
	 * 
	 * @param result Set all the results at once
	 */
	public void setResults(ItemStack[] result){
		this.result = result;
	}
	
	/**
	 * 
	 * @return An array of all of the results
	 */
	public ItemStack[] getResults(){
		return result;
	}
	
	/**
	 * 
	 * @return The original recipe
	 */
	
	public RecipeContainer getRecipe(){
		return this.recipe;
	}
	
	/**
	 * 
	 * @return The inventory crafted in. Not recommended to modify.
	 */
	
	public Inventory getInventory(){
		return this.i;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

	public HandlerList getHandlers() {
		
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
