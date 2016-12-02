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
 * Event is called when a shapeless recipe or shaped recipe is crafted
 *
 */

public class WorkbenchCraftEvent extends Event implements Cancellable{
	
	boolean cancelled;
	
	private static final HandlerList handlers = new HandlerList();
	
	RecipeContainer recipe;
	Inventory i;
	Player p;
	ItemStack result;
	
	public WorkbenchCraftEvent(RecipeContainer container, Player p, Inventory i, ItemStack result){
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
