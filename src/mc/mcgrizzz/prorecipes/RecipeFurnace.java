package mc.mcgrizzz.prorecipes;


import java.util.Iterator;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class RecipeFurnace extends RecipeContainer{
	
	ItemStack result;
	ItemStack toBurn;
	FurnaceRecipe register;
	FurnaceRecipe original;
	String id;
	boolean def;
	String permission;
	
	public RecipeFurnace(ItemStack r, ItemStack t){
		this.result = r;
		this.toBurn = t;
		id = "";
		def = false;
		register = new FurnaceRecipe(result, toBurn.getType(),(int)toBurn.getDurability());
	}
	
	public String getId(){
		if(!id.isEmpty()){
			return id;
		}
		
		id = ProRecipes.itemToStringBlob(result) + ProRecipes.itemToStringBlob(toBurn);
		return id;
	}
	
	public void setPermission(String s){
		this.permission = s;
	}
	
	public boolean hasPermission(){
		return permission != null && !permission.isEmpty();
	}
	
	public String getPermission(){
		return this.permission;
	}
	
	
	public boolean register(){
		
		
		
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().defaultRecipes.iterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            if (recipe != null && recipe instanceof FurnaceRecipe)
            {
            	FurnaceRecipe r = (FurnaceRecipe)recipe;
            	if(r.getInput().getType().equals(toBurn.getType())){
            		def = true;
            		original = r;
            	}
            	
            }
        }
		//This is the Recipes.class(only have one instance of this class) 
		boolean b = ProRecipes.getPlugin().getRecipes().addFurnace(this);
		ProRecipes.getPlugin().getServer().addRecipe(register);
		return b;
	}
	
	public boolean match(RecipeFurnace rec){
		return rec.getId().equals(getId());
	}
	
	public int getSubtractAmount(){
		return toBurn.getAmount();
	}
	
	
	public boolean match(ItemStack input){
		//System.out.println(input.toString());
		//System.out.println(toBurn.toString());
		if(input.getAmount() < getSubtractAmount()){
			//System.out.println("Less than");
			return false;
		}
		ItemStack i = input.clone();
		i.setAmount(1);
		ItemStack compare = toBurn.clone();
		compare.setAmount(1);
		//System.out.println("\n");
		//System.out.println(i.toString());
		//System.out.println(compare.toString());
		return ProRecipes.itemToStringBlob(i).equals(ProRecipes.itemToStringBlob(compare));
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public ItemStack[] getMatrixView() {
		ItemStack[] arr = new ItemStack[9];
		arr[4] = toBurn.clone();
		// TODO Auto-generated method stub
		return arr;
	}
	

}
