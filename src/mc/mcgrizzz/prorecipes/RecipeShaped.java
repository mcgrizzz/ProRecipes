package mc.mcgrizzz.prorecipes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeShaped extends RecipeContainer{
	
	String idCache = "";
	
	String[][] ids;
	
	HashMap<Character, ItemStack> ingredients = new HashMap<Character, ItemStack>();
	String[][] structure;
	ItemStack[][] itemsCache;
	
	//This is used to identify any special recipe (With armorstand as a result)
	ShapedRecipe registerer;
	ItemStack result;
	
	String permission;
	
	
	public RecipeShaped(ItemStack it){
		result = it;
		ItemStack i = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("recipedshapeditem");
		i.setItemMeta(m);
		registerer = new ShapedRecipe(i);
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
	
	public ItemStack[] subtractMatrix(ItemStack[] i){
		ArrayList<Integer> ints = new ArrayList<Integer>();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.addAll(Arrays.asList(getItems()));
		for(int b = 0; b < items.size(); b++){
			if(items.get(b)==null)continue;
			ItemStack t = items.get(b).clone();
			t.setAmount(t.getAmount()-1); //need to set to minus one. 
										  //The subtractMatric method is called after initial recipe is subtracted
			if(t.getAmount()==0){
				t.setType(Material.AIR);
				t.setAmount(1);
			}
			
			items.set(b, t);
		}
		
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
					if(items.get(c) == null || i[b] == null)continue;
					if(items.get(c).isSimilar(i[b])){
						checked = c;
						t = true;
						break;
					}
				}
			}
			if(t){
				ints.remove(new Integer(checked));
				test.setAmount(i[b].getAmount() - items.get(checked).getAmount());
				if(test.getAmount() == 0){
					test.setType(Material.AIR);
				}
				i[b] = test.clone();
			}
		}
		
		return i;
		
	}
	
	public boolean moreThanOne(){
		for(ItemStack i : getItems()){
			if(i == null)continue;
			if(i.getAmount() > 1){
				return true;
			}
		}
		return false;
	}
	
	public void setIngredients(HashMap<Character, ItemStack> in){
		this.ingredients = in;
		for(Character c : in.keySet()){
			if(c == ' ')continue;
			if(in.get(c).getType().equals(Material.AIR))continue;
			registerer.setIngredient(c, in.get(c).getType(),  (int)in.get(c).getDurability());
		}
	}
	
	public ItemStack[] getItems(){
		ItemStack[] items = new ItemStack[9];
		
		ItemStack[][] i = new ItemStack[structure.length][structure[0].length];
		for(int t = 0; t < i.length; t++){
			for(int z = 0; z <i[t].length; z++){
				
				if(structure[t][z] == null){
					i[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				if(structure[t][z].toCharArray().length <= 0 ||ingredients.get(structure[t][z].toCharArray()[0]) == null){
					i[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				i[t][z] = ingredients.get(structure[t][z].toCharArray()[0]);
			}
		}
		
		items = toOneD(i);
		
		return items;
	}
	
	
	public String getId(){
		if(!idCache.isEmpty()){
			return idCache;
		}
		String[][] i = new String[structure.length][structure[0].length];
		ItemStack[][] k = new ItemStack[structure.length][structure[0].length];
		for(int t = 0; t < i.length; t++){
			for(int z = 0; z <i[t].length; z++){
				if(structure.length < t || structure[t].length < z){
					continue;
				}
				
				if(structure[t][z] == null){
					i[t][z] = ProRecipes.airString;
					k[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				if(structure[t][z].toCharArray().length <= 0) {
					i[t][z] = ProRecipes.airString;
					k[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				
				if(ingredients.get(structure[t][z].toCharArray()[0]) == null){
					i[t][z] = ProRecipes.airString;
					k[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				ItemStack it = ingredients.get(structure[t][z].toCharArray()[0]);
				k[t][z] = it.clone();
				i[t][z] = ProRecipes.itemToStringBlob(it);
			}
		}
		ids = convertToMinimizedStructure(i, ProRecipes.airString);
		
		idCache = Arrays.deepToString(ids);
		itemsCache = convertToMinimizedStructure(k);
		return idCache;
 	}
	
	public int ingredientCount(){
		int i = 0;
		for(Character c : ingredients.keySet()){
			i+=ingredients.get(c) != null ? ingredients.get(c).getAmount() : 0;
		}
		return i;
		
	}
	
	public String getId(ItemStack[][] i){
		String k = "";
		String[][] b = new String[i.length][i[0].length];
		for(int t = 0; t < i.length; t++){
			for(int z = 0; z <i[t].length; z++){
				if(i.length < t || i[t].length < z){
					continue;
				}
				if(i[t][z] == null){
					b[t][z] = ProRecipes.airString;
					continue;
				}
				if(i[t][z].getType().equals(Material.AIR)){
					i[t][z] = new ItemStack(Material.AIR);
				}
				
				b[t][z] = ProRecipes.itemToStringBlob(i[t][z]);
			}
		}
		k = Arrays.deepToString(convertToMinimizedStructure(b, ProRecipes.airString));
		return k;
	}
	
	protected static ItemStack[][] convertToArray(ItemStack[] i){
		if(i.length / 3 == 3){
			ItemStack[][] arr = new ItemStack[3][3];
			
			for(int x = 0; x < 3; x++){
				for(int z = 0; z < 3; z++){
					if(i[x*3 + z] == null){
						arr[x][z] = new ItemStack(Material.AIR, 0);
						continue;
					}
					arr[x][z] = i[x*3 + z]; 
				}
			}
			return arr;
 		}else{
			return null;
		}
	}
	
 	
	protected static ItemStack[][] convertToMinimizedStructure(ItemStack[][] i){
		
		///GO THROUGH ROWS
		int x = i.length-1;
		//Check first row
		boolean clear = true;
		boolean fClear = true;
		for(int z = 0; z < i[0].length; z++){
			if(i[0][z] == null)continue;
			if(i[0][z] != null && !i[0][z].getType().equals(Material.AIR)){
				clear = false;
				fClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(ItemStack.class, i, 0, -1);
			x--;
			
		}
		
		
		//Check last row
		clear = true;
		boolean lClear = true;
		for(int z = 0; z < i[x].length; z++){
			if(i[x][z] == null)continue;
			if(i[x][z] != null && !i[x][z].getType().equals(Material.AIR)){
				clear = false;
				lClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(ItemStack.class, i, x, -1);
			x--;
		}
		
		//Only check middle row if one is empty
		if(fClear || lClear){
			if(fClear){
				clear = true;
				for(int z = 0; z < i[0].length; z++){
					if(i[0][z] == null)continue;
					if(i[0][z] != null && !i[0][z].getType().equals(Material.AIR)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(ItemStack.class, i, 0, -1);
					x--;
					
				}
			}else{
				clear = true;
				for(int z = 0; z < i[i.length-1].length; z++){
					if(i[i.length-1][z] == null)continue;
					if(i[i.length-1][z] != null && !i[i.length-1][z].getType().equals(Material.AIR)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(ItemStack.class, i, i.length-1, -1);
					x--;
					
				}
			}
			
		}
		
		
		//GO THROUGH COLUMNS
		
		int z = i[0].length-1;
		//Check first row
		clear = true;
		fClear = true;
		for(int xX = 0; xX < i.length; xX++){
			if(i[xX][0] == null)continue;
			if(i[xX][0] != null && !i[xX][0].getType().equals(Material.AIR)){
				clear = false;
				fClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(ItemStack.class, i, -1, 0);
			z--;
			
		}
		
		
		//Check last row
		clear = true;
		lClear = true;
		for(int xX = 0; xX < i.length; xX++){
			if(i[xX][z] == null)continue;
			if(i[xX][z] != null && !i[xX][z].getType().equals(Material.AIR)){
				clear = false;
				lClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(ItemStack.class, i, -1, z);
			z--;
		}
		
		//Only check middle row if one is empty
		if(fClear || lClear){
			if(fClear){
				clear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][0] == null)continue;
					if(i[xX][0] != null && !i[xX][0].getType().equals(Material.AIR)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(ItemStack.class, i, -1, 0);
					z--;
					
				}
			}else{
				clear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][z] == null)continue;
					if(i[xX][z] != null && !i[xX][z].getType().equals(Material.AIR)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(ItemStack.class, i, -1, z);
					z--;
				}
			}
			
		}
		
		return i;
	}
	
	protected static String[][] convertToMinimizedStructure(String[][] i, String filter){
		
		int x = i.length-1;
		//Check first row
		boolean clear = true;
		boolean fClear = true;
		for(int z = 0; z < i[0].length; z++){
			if(i[0][z] == null)continue;
			if(i[0][z] != null && !i[0][z].isEmpty() && !i[0][z].equals(filter)){
				clear = false;
				fClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(String.class, i, 0, -1);
			x--;
			
		}
		
		
		//Check last row
		clear = true;
		boolean lClear = true;
		for(int z = 0; z < i[x].length; z++){
			if(i[x][z] == null)continue;
			if(i[x][z] != null && !i[x][z].isEmpty() && !i[x][z].equals(filter)){
				clear = false;
				lClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(String.class, i, x, -1);
			x--;
		}
		
		//Only check middle row if one is empty
		if(fClear || lClear){
			if(fClear){
				clear = true;
				for(int z = 0; z < i[0].length; z++){
					if(i[0][z] == null)continue;
					if(i[0][z] != null && !i[0][z].isEmpty() && !i[0][z].equals(filter)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(String.class, i, 0, -1);
					x--;
					
				}
			}else{
				clear = true;
				for(int z = 0; z < i[i.length-1].length; z++){
					if(i[i.length-1][z] == null)continue;
					if(i[i.length-1][z] != null && !i[i.length-1][z].isEmpty() && !i[i.length-1][z].equals(filter)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(String.class, i, i.length-1, -1);
					x--;
					
				}
			}
			
		}
		
		
		//GO THROUGH COLUMNS
		
		
		int z = i[0].length-1;
		//Check first row
		clear = true;
		fClear = true;
		for(int xX = 0; xX < i.length; xX++){
			if(i[xX][0] == null)continue;
			if(i[xX][0] != null && !i[xX][0].isEmpty() && !i[xX][0].equals(filter)){
				clear = false;
				fClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(String.class, i, -1, 0);
			z--;
			
		}
		
		
		//Check last row
		clear = true;
		lClear = true;
		for(int xX = 0; xX < i.length; xX++){
			if(i[xX][z] == null)continue;
			if(i[xX][z] != null && !i[xX][z].isEmpty() && !i[xX][z].equals(filter)){
				clear = false;
				lClear = false;
				break;
			}
		}
		if(clear){
			i = copyIgnore(String.class, i, -1, z);
			z--;
		}
		
		//Only check middle row if one is empty
		if(fClear || lClear){
			if(fClear){
				clear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][0] == null)continue;
					if(i[xX][0] != null && !i[xX][0].isEmpty() && !i[xX][0].equals(filter)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(String.class, i, -1, 0);
					z--;
					
				}
			}else{
				clear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][z] == null)continue;
					if(i[xX][z] != null && !i[xX][z].isEmpty() && !i[xX][z].equals(filter)){
						clear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(String.class, i, -1, z);
					z--;
				}
			}
			
		}
		
		return i;
	}
	
	
	
	protected static Pair<String[][], HashMap<Character, ItemStack>> getStructure(ItemStack[][] i){
		Character[] chars = {'a','b','c','d','e','f','g','h','i'};
		ItemStack[][] min = convertToMinimizedStructure(i);
		String[][] st = new String[min.length][min[0].length];
		HashMap<Character, ItemStack> keys = new HashMap<Character, ItemStack>();
		for(int x = 0; x < min.length; x++){
			for(int z = 0; z < min[x].length; z++){
					Character b = chars[keys.keySet().size()];
					st[x][z] = b.toString();
					keys.put(b, min[x][z]);
				}
			}
		return new Pair<String[][], HashMap<Character, ItemStack>>(st, keys);
	}
	
	public String[] toOneD(String[][] s){
		String[] nS = new String[s.length];
		for(int x = 0; x < s.length; x++){
			String t = "";
			if(s[x]== null){
				nS[x] = " ";
			}
			for(int z = 0; z < s[x].length; z++){
					if(s[x][z] == null){
						t+=" ";
					}else{
						t += s[x][z];
					}
			}
			nS[x] = t;
		}
		return nS;
 	}
	
	public ItemStack[] toOneD(ItemStack[][] s){
		ItemStack[] items = new ItemStack[9];
		for(int x = 0; x < 3; x++){
			for(int z = 0; z < 3; z++){
				if(x >= s.length || z >= s[x].length){
					items[x*3 + z] = null;
				}else{
					items[x*3 + z] = s[x][z];
				}
			}
		}
		
		return items;
	}
	
	
	public void setStructure(String[][] s){
		this.structure = s;
		registerer.shape(toOneD(s));
	}
	
	public void setIngredient(ItemStack it, Character i){
		
		if(it.getType() != Material.AIR){
			ingredients.put(i, it);
			registerer.setIngredient(i, it.getData());
		}
		//Adds an ingredient(Itemstack) as well as how many of that ingredient
		
	}
	
	public boolean matchMaps(Map<Character, ItemStack> a, Map<Character, ItemStack> b){
		boolean match = true;
		
		//Go through first replace Material.AIR with null
		ArrayList<Character> setAir = new ArrayList<Character>();
		a.remove(" ");
		b.remove(" ");
		for(Character s : a.keySet()){
			if(a.get(s) == null){
				setAir.add(s);
			}
		}
		for(Character s : setAir){
			a.put(s, new ItemStack(Material.AIR));
		}
		
		setAir.clear();
		
		for(Character s : a.keySet()){
			if(b.get(s) == null){
				setAir.add(s);
			}
		}
		
		for(Character s : setAir){
			b.put(s, new ItemStack(Material.AIR));
		}
		
		ArrayList<String> aS = new ArrayList<String>();
		ArrayList<String> bS = new ArrayList<String>();
		
		for(java.util.Map.Entry<Character, ItemStack> e : a.entrySet()){
			
			String item = e.getValue() != null ? ProRecipes.itemToStringBlob(e.getValue(), true) : "null";
			aS.add(e.getKey().toString() + " " + item);
		}
		for(java.util.Map.Entry<Character, ItemStack> e : b.entrySet()){
			String item = e.getValue() != null ? ProRecipes.itemToStringBlob(e.getValue(), true) : "null";
			bS.add(e.getKey().toString() + " " + item);
		}
		
		for(String s : aS){
			if(!bS.contains(s)){
				match = false;
				break;
			}
		}
		for(String s : bS){
			if(!aS.contains(s)){
				match = false;
				break;
			}
		}
		
		
		return match;
	}
	
	public boolean register(){
		
		//Remove anything that will make a mess of things
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().defaultRecipes.iterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
         
            if (recipe != null && recipe instanceof ShapedRecipe)
            {
            	ShapedRecipe b = (ShapedRecipe)recipe;
            	
            	if(matchMaps(b.getIngredientMap(), registerer.getIngredientMap())){
            		if(Arrays.deepEquals(b.getShape(), registerer.getShape())){
            		
            			ProRecipes.getPlugin().getRecipes().addConflict(this, b);
            		}
            	}
            }
        }
		//You need your plugin here
		ProRecipes.getPlugin().getServer().addRecipe(registerer);
		return ProRecipes.getPlugin().getRecipes().addShaped(this);
	}
	
	public ItemStack getResult(){
		return this.result;
	}
	
	
	
	public boolean matchLowest(ItemStack[][] i){
		if(!match(i)){
			if(i.length != itemsCache.length){
				return false;
			}
			if(i[0].length != itemsCache[0].length){
		 		 return false;
		    }
			for(int x = 0; x < i.length; x++){
				for(int z = 0; z < i[x].length; z++){
					ItemStack compare = i[x][z].clone();
					ItemStack orig = itemsCache[x][z].clone();
					if(orig == null || orig.getType().equals(Material.AIR)){
						if(compare == null || compare.getType().equals(Material.AIR)){
							continue;
						}else{
							return false;
						}
					}
					if(compare.getAmount() < orig.getAmount()){
						return false;
					}
					compare.setAmount(orig.getAmount());
					if(!ProRecipes.itemToStringBlob(compare).equals(ids[x][z])){
						return false;
					}
				}
			}
			return true;
		}else{
			return true;
		}
		
		
	}
	
	public void unregister(){
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().getServer().recipeIterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
            recipe = it.next();
            if (recipe != null)
            {
                 if (recipe != null && recipe instanceof ShapedRecipe)
                 {
                 	ShapedRecipe b = (ShapedRecipe)recipe;
               
                 	if(matchMaps(b.getIngredientMap(), registerer.getIngredientMap())){
                 		if(Arrays.deepEquals(b.getShape(), registerer.getShape())){
                 			it.remove();
                 		}
                 	}
                 }
            	
            }
        }
	}
	
	//Is this the same recipe as the passed recipe
	public boolean match(ItemStack[][] i){
		String a = getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		
		String b = getId(i).replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");

		return a.equalsIgnoreCase(b);
	}
	
	public boolean match(RecipeShaped p){
		String a = getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		if(p == null){
			return false;
		}
		String b = p.getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		return a.equalsIgnoreCase(b);
	}
	
	/**
	 * Copies an array removing either a column or row or both. Uses reflection, oops.
	 * @param arr The array to copy data from. Must be rectangular!
	 * @param skRow The row to skip, or -1 to skip no rows
	 * @param skCol The col to skip, or -1 to skip no columns
	 * @return An array missing the specified columns and rows
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T[][] copyIgnore(Class<T> cl, T[][] arr, int skRow, int skCol) {
	        if (skRow == -1 && skCol == -1)
	                return arr;
	 
	        if (arr.length == 0 || (arr.length == 1 && skRow != -1) || arr[0].length == 0 || (arr[0].length == 1 && skCol != -1))
	                return (T[][]) Array.newInstance(cl, 0, 0);
	 
	        // Strip out the specified row
	        if (skRow != -1) {
	                T[][] newArr = (T[][]) Array.newInstance(cl, arr.length - 1, arr[0].length);
	 
	                for (int origRow = 0; origRow < arr.length; origRow++) {
	                        int newRow = origRow;
	                        if (newRow == skRow) continue; //  skip specified row
	                        if (newRow > skRow) newRow--; // fix index
	 
	                        for (int col = 0; col < arr[0].length; col++) {
	                                newArr[newRow][col] = arr[origRow][col];
	                        }
	                }
	 
	                // Now strip out the specified column
	                return copyIgnore(cl, newArr, -1, skCol);
	        }
	 
	        // Strip out the specified column
	        T[][] newArr = (T[][]) Array.newInstance(cl, arr.length, arr[0].length - 1);
	 
	        for (int row = 0; row < arr.length; row++) {
	                for (int origCol = 0; origCol < arr[0].length; origCol++) {
	                        int newCol = origCol;
	                        if (newCol == skCol) continue; // skip specified col
	                        if (newCol > skCol) newCol--; // fix index
	                       
	                        newArr[row][newCol] = arr[row][origCol];
	                }
	        }
	 
	        return newArr;
	}

	@Override
	public ItemStack[] getMatrixView() {
		return getItems();
	}
	

}
