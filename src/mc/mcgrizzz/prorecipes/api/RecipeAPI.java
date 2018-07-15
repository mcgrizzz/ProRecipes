package mc.mcgrizzz.prorecipes.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.mcgrizzz.prorecipes.ProRecipes;
import me.mcgrizzz.prorecipes.lib.Pair;
import me.mcgrizzz.prorecipes.recipes.RecipeChest;
import me.mcgrizzz.prorecipes.recipes.RecipeFurnace;
import me.mcgrizzz.prorecipes.recipes.RecipeShaped;
import me.mcgrizzz.prorecipes.recipes.RecipeShapeless;
import me.mcgrizzz.prorecipes.recipes.Recipes;

public class RecipeAPI{
	
	
	public enum RecipeType {
		SHAPED,
		SHAPELESS,
		FURNACE,
		MULTI;
	}
	
	
	/**
	 * <strong>Returns</strong> the id of the Shaped Recipe created. <br>
	 * <strong>Returns</strong> less than 0 when creation failed. <br>
	 * <strong>Can fail</strong> if original recipes and recipe packs have not loaded yet (-1). <br>
	 * <strong>Can fail</strong> if recipe exists already (-2).
	 * @param  ingredients All ingredients in array of length 9. Order affects shape
	 * @param  result The result of the recipe
	 * @return The id of the created Shaped Recipe
	 */
	
	public int createShapedRecipe(ItemStack[] ingredients, ItemStack result){
		
		if(!ProRecipes.getPlugin().loadedRecipes)return -1;
		
		RecipeShaped rec = new RecipeShaped(result);
		Pair<String[][], HashMap<Character, ItemStack>> s = RecipeShaped.getStructure
				(RecipeShaped.convertToMinimizedStructure(RecipeShaped.convertToArray(ingredients)));
		rec.setStructure(s.getA());
		rec.setIngredients(s.getB());
		if(!rec.register()){
			return -2;
		}
		return ProRecipes.getPlugin().getRecipes().shaped.size()-1;
	}
	
	/**
	 * <strong>Returns</strong> the id of the Shapeless Recipe created.<br>
	 * <strong>Returns</strong> less than 0 when creation failed.<br>
	 * <strong>Can fail</strong> if original recipes and recipe packs have not loaded yet (-1).<br>
	 * <strong>Can fail</strong> if recipe exists already (-2).
	 * @param  ingredients All ingredients in array. Order does not matter
	 * @param  result The result of the recipe
	 * @return The id of the created Shapeless Recipe
	 */
	
	public int createShapelessRecipe(ItemStack[] ingredients, ItemStack result){
		if(!ProRecipes.getPlugin().loadedRecipes)return -1;
		
		RecipeShapeless rec = new RecipeShapeless(result);
		for(ItemStack i : ingredients){
			rec.addIngredient(i);
		}
		if(!rec.register()){
			return -2;
		}
		
		return ProRecipes.getPlugin().getRecipes().shapeless.size()-1;
	}
	
	
	/**
	 *<strong>Returns</strong> the id of the Furnace Recipe created.<br>
	 *<strong>Returns</strong> less than 0 when creation failed.<br>
	 *<strong>Can fail</strong> if original recipes and recipe packs have not loaded yet (-1).<br>
	 *<strong>Can fail</strong> if recipe exists already (-2).
	 * @param  source Source itemstack to be burned
	 * @param  result The result of smelting
	 * @return The id of the created Furnace Recipe
	 */
	
	public int createFurnaceRecipe(ItemStack source, ItemStack result){
		if(!ProRecipes.getPlugin().loadedRecipes)return -1;
		
			RecipeFurnace rec = new RecipeFurnace(result, source);
			if(!rec.register()){
				return -2;
			}
			
			return ProRecipes.getPlugin().getRecipes().fur.size()-1;
	}
	
	/**
	 * <strong>Returns</strong> the id of the Multicraft Recipe created.<br>
	 * <strong>Returns</strong> less than 0 when creation failed.<br>
	 * <strong>Can fail</strong> if original recipes and recipe packs have not loaded yet (-1)<br>
	 * <strong>Can fail</strong> if recipe exists already (-2).
	 * @param  ingredients Array of length 16 of all ingredients. Order affects shape. OPTIONAL: Use <em>toArray</em> using ItemStack[4][4] 
	 * @param  results Array of length 4 of all results. Order will affect result order
	 * @return The id of the created Multicraft Recipe
	 */
	
	public int createMultiRecipe(ItemStack[] ingredients, ItemStack[] results){
		if(!ProRecipes.getPlugin().loadedRecipes)return -1;
		
		RecipeChest rec = new RecipeChest(results);
		//System.out.println("Ingredients: ");
		//System.out.println(Arrays.deepToString(ingredients));
		Pair<String[][], HashMap<Character, ItemStack>> s = RecipeChest.getStructure
				(RecipeChest.convertToMinimizedStructure(RecipeChest.convertToArray(ingredients)));
		rec.setStructure(s.getA());
		rec.setIngredients(s.getB());
		
		if(!rec.register()){
			return -2;
		}
		
		return ProRecipes.getPlugin().getRecipes().chest.size()-1;
		
	}
	
	
	/**
	 * <strong>Returns</strong> RecipeContainer of a Recipe.<br>
	 * <strong>Returns</strong> null if id is incorrect.
	 * @param type <em>RecipeType</em> enum value for type of Recipe
	 * @param id Recipe id
	 * @return The recipe container
	 */
	
	public RecipeContainer getRecipe(RecipeType type, int id){
		RecipeContainer r = null;
		switch(type){
		case SHAPED: 
			RecipeShaped shaped = ProRecipes.getPlugin().getRecipes().shaped.get(id);
			if(shaped == null){
				return null;
			}
			r = new RecipeContainer(toArray(shaped.getResult()), shaped.getItems(), type, shaped.hasPermission() ? shaped.getPermission() : null, id);
			return r;
		case SHAPELESS:
			RecipeShapeless shapeless = ProRecipes.getPlugin().getRecipes().shapeless.get(id);
			if(shapeless == null){
				return null;
			}
			r = new RecipeContainer(toArray(shapeless.getResult()), toArray(shapeless.getItems()), type, shapeless.hasPermission() ? shapeless.getPermission() : null, id);
			return r;
		case FURNACE:
			RecipeFurnace furnace = ProRecipes.getPlugin().getRecipes().fur.get(id);
			if(furnace == null){
				return null;
			}
			r = new RecipeContainer(toArray(furnace.getResult()), toArray(furnace.getToBurn()), type, furnace.hasPermission() ? furnace.getPermission() : null, id);
			return r;
		case MULTI:
			RecipeChest multi = ProRecipes.getPlugin().getRecipes().chest.get(id);
			if(multi == null){
				return null;
			}
			r = new RecipeContainer(multi.getResult(), multi.getItems(), type, multi.hasPermission() ? multi.getPermission() : null, id);
			return r;
		}
		return null;
	}
	
	
	/**
	 * <strong>Returns</strong> a recipe of specified type and id.
	 * @param  type <em>RecipeType</em> enum value for type of Recipe to delete
	 * @param  id  an integer value representing the recipe id
	 */
	
	public void removeRecipe(RecipeType type, int id){
		Recipes rec = ProRecipes.getPlugin().getRecipes();
		switch(type){
		case SHAPED:
			rec.shaped.get(id).unregister();
			rec.removeConflict(rec.shaped.get(id));
			rec.shaped.remove(id);
			return;
		case SHAPELESS:
			rec.shapeless.get(id).unregister();
			rec.removeConflict(rec.shapeless.get(id));
			rec.shapeless.remove(id);
			return;
		case FURNACE:
			rec.fur.remove(id);
			return;
		case MULTI:
			rec.chest.remove(id);
			return;
		}
	}
	
	
	/**
	 * 
	 * Container class for recipes
	 * 
	 */
	
	public class RecipeContainer{
		
		ItemStack[] result;
		ItemStack[] ingredients;
		RecipeType type;
		int id;
		
		String permission = null;
		
		public RecipeContainer(ItemStack[] result, ItemStack[] ingredients, RecipeType type, String permission, int id){
			this.result = result;
			this.ingredients = ingredients;
			this.type = type;
			this.permission = permission;
			this.id = id;
		}
		
		public int getId(){
			return id;
		}
		
		public boolean hasPermission(){
			return permission != null;
		}
		
		public String getPermission(){
			return this.permission;
		}
		
		public ItemStack[] getResult(){
			return this.result;
		}
		
		public ItemStack[] getIngredients(){
			return this.ingredients;
		}
	
		public RecipeType getType(){
			return this.type;
		}
	}
	
	/**
	 * <strong>Returns</strong> size of recipe list of given type.<br>
	 * Use with <em>getRecipe</em> method if going through all recipes
	 * 
	 *@param type Type of recipe to search 
	 */
	
	public int recipeCount(RecipeType type){
		switch(type){
		case SHAPED:
			return ProRecipes.getPlugin().getRecipes().shaped.size();
		case SHAPELESS:
			return ProRecipes.getPlugin().getRecipes().shapeless.size();
		case FURNACE:
			return ProRecipes.getPlugin().getRecipes().fur.size();
		case MULTI:
			return ProRecipes.getPlugin().getRecipes().chest.size();
		}
		return 0;
	}
	
	/**
	 * <strong>Returns</strong> a list of all recipe of a given type
	 * @param type Type of recipe
	 * @return recipes;
	 */
	
	public List<RecipeContainer> getRecipes(RecipeType type){
		
		ArrayList<RecipeContainer> recipes = new ArrayList<RecipeContainer>();
		
		for(int i = 0; i < recipeCount(type); i++){
			recipes.add(getRecipe(type, i));
		}
		
		return recipes;
	}
	
	
	/**
	 * 
	 * Lazy toArray method
	 * 
	 */
	
	public ItemStack[] toArray(ItemStack... i){
		return i;
	}
	
	
	/**
	 * 
	 * Lazy toArray method
	 * 
	 */
	
	
	public ItemStack[] toArray(List<ItemStack> i){
		return i.toArray(new ItemStack[i.size()]);
	}
	
	/**
	 * 
	 * Lazy toArray method
	 * Only works with "square" arrays
	 * 
	 */
	
	public ItemStack[] toArray(ItemStack[][] arr){
		
		int i = arr.length;
		
		ItemStack[] items = new ItemStack[i*i];
		
		for(int x = 0; x < i; x++){
			for(int z = 0; z < i; z++){
				if(x >= i || z >= arr[x].length){
					items[x*i + z] = null;
				}else{
					items[x*i + z] = arr[x][z];
				}
			}
		}
		
		return items;
		
	}
	

}
