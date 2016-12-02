package mc.mcgrizzz.prorecipes.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.mcgrizzz.prorecipes.RecipeAPI.RecipeContainer;

/**
 * 
 * Called when something is smelted into another
 *
 */

public class FurnaceCraftEvent extends Event implements Cancellable {
	
	boolean cancelled;
	
	private static final HandlerList handlers = new HandlerList();
	
	RecipeContainer recipe;
	Inventory i;
	ItemStack result;
	ItemStack source;
	
	public FurnaceCraftEvent(RecipeContainer container, Inventory i, ItemStack result, ItemStack source){
		this.recipe = container;
		this.i = i;
		this.result = result;
		this.source = source;
	}
	
	public ItemStack getSource(){
		return this.source;
	}
	
	/**
	 * 
	 * @param result The result of the craft
	 */
	public void setResult(ItemStack result){
		this.result = result;
	}
	
	/**
	 *@return The result of the craft event
	 */
	
	public ItemStack getResult(){
		return result;
	}
	
	/**
	 * 
	 * @return The original recipe of the event
	 */
	
	public RecipeContainer getRecipe(){
		return this.recipe;
	}
	
	
	/**
	 * 
	 * @return The inventory crafted in
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
