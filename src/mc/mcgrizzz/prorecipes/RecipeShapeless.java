package mc.mcgrizzz.prorecipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeShapeless extends RecipeContainer{
	
	/**
	 * Created by: AndrewEpifano
	 * CustomRecipe class.
	 */
	
	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	
	//This is used to identify any special recipe (With anvil as a result)
	ShapelessRecipe registerer = new ShapelessRecipe(new ItemStack(Material.ANVIL));
	ItemStack result;
	
	String permission;
	
	public RecipeShapeless(ItemStack it){
		result = it;
	}
	
	public boolean moreThanOne(){
		for(ItemStack i : items){
			if(i.getAmount() > 1){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPermission(){
		return permission != null && !permission.isEmpty();
	}
	
	public void setPermission(String s){
		this.permission = s;
	}
	
	public String getPermission(){
		return this.permission;
	}
	
	public int ingredientCount(){
		int i = 0;
		for(ItemStack t : items){
			i += (t != null ? t.getAmount() : 0);
			//System.out.println(t.toString());
		}
	//	System.out.println(i);
		//System.out.println(getId());
		return i;
		
	}
	
	public ArrayList<ItemStack> getItems(){
		
		return items;
	}
	
	public ArrayList<String> getId(){
		ArrayList<String> array = new ArrayList<String>();
		for(ItemStack i : items){
			array.add(ProRecipes.itemToStringBlob(i));
		}
		return array;
	}
	
	public void removeIngredient(ItemStack it){
		if(it == null){
			return;
		}
		
		for(int i = 0; i < items.size(); i++){
			if(items.get(i).equals(it)){
				items.remove(i);
				//System.out.println("REMOVEED IT");
				return;
			}
		}
	}
	
	public void addIngredient(ItemStack it){
		if(it == null){
			return;
		}
		if(items.size() == 9){
			//System.out.println("ERROR: RECIPE HAS NINE INGREDIENTS, NOT ADDING 10th!!!!");
			return;
		}
		//boolean contains = false;
		//int pairs = -1;
		
		//Go through to check if the ingredient is already present
		/*for(ItemStack i : items){
			
			ItemStack test = i.clone();
			ItemStack tt = it.clone();
			
			if(test.isSimilar(tt)){
				contains = true;
				pairs = items.indexOf(i);
			}
		}*/
		
		//Ingredient already present --> Just add one to the amount of ingredient
		/*if(contains){
			
				
				//registerer.removeIngredient(it.getData(), 1);
			ItemStack i = items.get(pairs).clone();
			i.setAmount(i.getAmount() + it.getAmount());
			items.remove(pairs);
			items.add(i);
		}else{
			//Otherwise add just one
		
			//registerer.addIngredient(it.getData());
			 **/
			items.add(it.clone());
		
	}
	
	
	/*public void addIngredient(ItemStack it, int i){
		//Adds an ingredient(Itemstack) as well as how many of that ingredient
		for(int n = 0; n < i; n++){
			addIngredient(it);
		}
	}*/

	
	public boolean register(){
		
		for(ItemStack i : items){
			//System.out.println(i.toString());
			registerer.addIngredient(i.getData());
			//registerer.addIngredient(i.getData());
		}
		
		for(RecipeShapeless rec : ProRecipes.getPlugin().rec.shapeless){
			if(ingredientCheckDuplicate(rec.getItems(), getItems())){
				//System.out.println("ERROR-SHAPELESS CREATION: Tried to add recipe with same ingredients of another recipe. "
					//	+ "You can't have multiple recipes for the same ingredients!");
				return false;
				
			}
		}
		
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().defaultRecipes.iterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            if (recipe != null && recipe instanceof ShapelessRecipe)
            {
            	////////////System.out.println"is shaped");
            	ShapelessRecipe b = (ShapelessRecipe)recipe;
            	if(ingredientCheck(b.getIngredientList(), registerer.getIngredientList())){
            		//System.out.println("Adding conflict.");
            		ProRecipes.getPlugin().getRecipes().addConflict(this, b);
            	}
            }else if(recipe != null && recipe instanceof ShapedRecipe){
            	ShapedRecipe b = (ShapedRecipe)recipe;
            	if(ingredientCheck(registerer.getIngredientList(), b.getIngredientMap())){
            		//System.out.println("Ingredients are the same, adding to doubles");
            		//System.out.println(b.getIngredientMap().toString());
            		ProRecipes.getPlugin().getRecipes().doubles.add(b);
            	}
            }
        }
		
		//You need your plugin here
		ProRecipes.getPlugin().getServer().addRecipe(registerer);
		
		//This is the Recipes.class(only have one instance of this class) 
		ProRecipes.getPlugin().getRecipes().addShapeless(this);
		return true;
	}
	
	public void unregister(){
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().getServer().recipeIterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            
            	 if (recipe != null && recipe instanceof ShapelessRecipe)
                 {
                 	////////////System.out.println"is shaped");
                 	ShapelessRecipe b = (ShapelessRecipe)recipe;
                 	if(ingredientCheck(b.getIngredientList(), registerer.getIngredientList())){
                 		it.remove();
                 	}
                 }
            	
            
        }
	}
	
	public boolean ingredientCheck(List<ItemStack> c, List<ItemStack> b){
		for(ItemStack t : c){
			if(!b.contains(t)){
				return false;
			}
		}
		
		for(ItemStack t : b){
			if(!c.contains(t)){
				return false;
			}
		}
		return true;
		
	}
	
	public boolean ingredientCheck(List<ItemStack> c, Map<Character, ItemStack> b){
		for(ItemStack t : c){
			//System.out.println("List of itemstacks : " + (t == null ? "air" : t.toString()));
			if(!b.containsValue(t)){
				return false;
			}
		}
		
		for(ItemStack t : b.values()){
			//System.out.println("List from shaped : " + (t == null ? "air" : t.toString()));
			if(!c.contains(t)){
				
				return false;
			}
		}
		//System.out.println("\n\n\n\n");
		return true;
	}
	
	public boolean ingredientCheckDuplicate(List<ItemStack> c, List<ItemStack> b){
		HashMap<ItemStack, Integer> ac = new HashMap<ItemStack, Integer>();
		HashMap<ItemStack, Integer> ab = new HashMap<ItemStack, Integer>();
		for(ItemStack i : c){
			ItemStack n = i.clone();
			int amount = n.getAmount();
			n.setAmount(1);
			if(ac.containsKey(n)){
				ac.put(n, ac.get(n) + amount);
			}else{
				ac.put(n, amount);
			}
		}
		
		for(ItemStack i : b){
			ItemStack n = i.clone();
			int amount = n.getAmount();
			n.setAmount(1);
			if(ab.containsKey(n)){
				ab.put(n, ab.get(n) + amount);
			}else{
				ab.put(n, amount);
			}
		}
		
		//for(ItemStack i : ac.keySet()){
		////	System.out.println("A" + i + ":" + ac.get(i));	
		//}
		
		//for(ItemStack i : ab.keySet()){
	//		System.out.println("B" + i + ":" + ab.get(i));	
	//	}
		
		
		for(ItemStack i : ac.keySet()){
			if(!ab.containsKey(i))return false;
			if(ab.get(i) != ac.get(i))return false;
		}
		
		for(ItemStack i : ab.keySet()){
			if(!ac.containsKey(i))return false;
			if(ac.get(i) != ab.get(i))return false;
		}
		
		return true;
		
	}
	
	public ItemStack getResult(){
		return this.result;
	}
	
	public ItemStack[] subtractMatrix(ItemStack[] i){
		ArrayList<Integer> ints = new ArrayList<Integer>();
		int x = 9;
		while(x-- > 0){
			ints.add(x);
		}
		for(int b = 0; b < i.length; b++){
			boolean t = false;
			if(i[b] == null)continue;
			ItemStack test = i[b].clone();
			int checked = 0;
			for(int c : ints){
				if(items.size() > c){
					if(items.get(c).isSimilar(i[b])){
						checked = c;
						t = true;
						break;
					}
				}
			}
			if(t){
				ints.remove(new Integer(checked));
				//System.out.println("Subtract 1");
				//System.out.println("Subtracting " + i[b].toString() +  " - " + items.get(checked).toString() );
				test.setAmount(i[b].getAmount() - items.get(checked).getAmount());
				i[b] = test.clone();
				//System.out.println("Test amount: " + test.getAmount());
			}
		}
		
		return i;
		
	}
	
	
	public boolean matchLowest(RecipeShapeless recipe){
		//System.out.println("Matchng a recipe");
		if(!match(recipe)){
			//System.out.println("Doesn't match checking lowest..");
			//if(recipe.items.size() != items.size()){
				
			//	return false;
			//}
			
			ArrayList<ItemStack> tt = new ArrayList<ItemStack>();
			tt.addAll(recipe.items);
			
			//System.out.println("Size: " + tt.size());
			
			boolean contains = false;
			for(ItemStack i : items){
				contains = false;
				ItemStack latest = new ItemStack(Material.AIR);
				
				for(ItemStack p : tt){
					ItemStack out = i.clone();
					ItemStack in = p.clone();
					out.setAmount(1);
					in.setAmount(1);
					//System.out.println("from in game: " + p.toString() + " \n");
					//System.out.println("recipe to be checked with: " + i.toString() + " \n" );
					if(in.isSimilar(out)){
						if(i.getAmount() <= p.getAmount()){
							contains = true;
							latest = p;
							break;
						}else{
							contains = false;
							//System.out.println("No contain");
							return false;
						}
					}else{
						//System.out.println("Not Similar");
					}
				}
				tt.remove(latest);
				
				if(!contains){
					//System.out.println("Does not contain");
					return false;
				}
			}
			//System.out.println(contains);
			return contains;
		}else{
			//System.out.println("Retrun true;");
			return true;
		}
	}
	
	
	//Is this the same recipe as the passed recipe
	public boolean match(RecipeShapeless recipe){
		Set<String> set1 = new HashSet<String>();
		set1.addAll(recipe.getId());
		Set<String> set2 = new HashSet<String>();
		set2.addAll(getId());
		return set1.equals(set2);
	}

	@Override
	public ItemStack[] getMatrixView() {
		return getItems().toArray(new ItemStack[9]);
	}
	
}
