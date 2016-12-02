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

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;

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
				System.out.println("Subtract 1");
				System.out.println("Subtracting " + i[b].toString() +  " - " + items.get(checked).toString());
				test.setAmount(i[b].getAmount() - items.get(checked).getAmount());
				if(test.getAmount() == 0){
					test.setType(Material.AIR);
				}
				i[b] = test.clone();
				System.out.println("Test amount: " + test.getAmount());
			}
		}
		
		
		System.out.println(Arrays.deepToString(i));
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
	
	/**public ArrayList<String> getId(){
		ArrayList<String> array = new ArrayList<String>();
		
		return array;
	}*/
	
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
				/*if(structure.length < t || structure[t].length < z){
					continue;
				}*/
				
				if(structure[t][z] == null){
					i[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				if(structure[t][z].toCharArray().length <= 0 ||ingredients.get(structure[t][z].toCharArray()[0]) == null){
					i[t][z] = new ItemStack(Material.AIR, 0);
					continue;
				}
				//////////////////System.out.println"adding something ");
				//////////////////System.out.printlningredients.get(structure[t][z].toCharArray()[0]).getType());
				i[t][z] = ingredients.get(structure[t][z].toCharArray()[0]);
			}
		}
		
		//////////////System.out.println"GetItems: ");
		//////////////System.out.printlnArrays.deepToString(i));
		
		items = toOneD(i);
		
		//////////////System.out.println"After toOneD: ");
		//////////////System.out.printlnArrays.deepToString(items));
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
				//////////////////System.out.println"adding something ");
				//////////////////System.out.printlningredients.get(structure[t][z].toCharArray()[0]).getType());
				ItemStack it = ingredients.get(structure[t][z].toCharArray()[0]);
				k[t][z] = it.clone();
				i[t][z] = ProRecipes.itemToStringBlob(it);
			}
		}
		//////////////////System.out.println"Getting id...");
		ids = convertToMinimizedStructure(i, ProRecipes.airString);
		
		idCache = Arrays.deepToString(ids);
		itemsCache = convertToMinimizedStructure(k);
		////////System.out.println(idCache);
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
			//////////////////System.out.printlni.length);
			//("Before it's converted");
			//(Arrays.deepToString(i));
			ItemStack[][] arr = new ItemStack[3][3];
			//////////////////System.out.printlnarr.length);
			//////////////////System.out.printlnarr[0].length);
			for(int x = 0; x < 3; x++){
				//////////////////System.out.println"x" + x);
				for(int z = 0; z < 3; z++){
				//	//("z" + z);
					if(i[x*3 + z] == null){
						arr[x][z] = new ItemStack(Material.AIR, 0);
						continue;
					}
					arr[x][z] = i[x*3 + z]; 
				}
			}
			//////////////System.out.println"Converted array: ");
			//////////////System.out.printlnArrays.deepToString(arr));
			return arr;
 		}else{
 			//////////////////System.out.println"Not nine");
			return null;
		}
	}
	
 	
	protected static ItemStack[][] convertToMinimizedStructure(ItemStack[][] i){
		
		///GO THROUGH ROWS
		//////////System.out.println("Going through rows");
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
			//////////System.out.println("First row is clear");
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
			//////////System.out.println("Last row is clear");
			i = copyIgnore(ItemStack.class, i, x, -1);
			x--;
		}
		
		//Only check middle row if one is empty
		if(fClear || lClear){
			//////////System.out.println("One is clear");
			if(fClear){
				//////////System.out.println("First is clear");
				clear = true;
				for(int z = 0; z < i[0].length; z++){
					if(i[0][z] == null)continue;
					if(i[0][z] != null && !i[0][z].getType().equals(Material.AIR)){
						clear = false;
						break;
					}
				}
				if(clear){
					//////////System.out.println("Middle(now first) is clear");
					i = copyIgnore(ItemStack.class, i, 0, -1);
					x--;
					
				}
			}else{
				//////////System.out.println("Last is clear");
				clear = true;
				for(int z = 0; z < i[i.length-1].length; z++){
					if(i[i.length-1][z] == null)continue;
					if(i[i.length-1][z] != null && !i[i.length-1][z].getType().equals(Material.AIR)){
						clear = false;
						break;
					}
				}
				if(clear){
					//////////System.out.println("Middle(Now last) is clear");
					i = copyIgnore(ItemStack.class, i, i.length-1, -1);
					x--;
					
				}
			}
			
		}
		
		
		//GO THROUGH COLUMNS
		//////////System.out.println("Going through columns");
		
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
			//////////System.out.println("First is clear");
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
			//////////System.out.println("Last is clear");
			i = copyIgnore(ItemStack.class, i, -1, z);
			z--;
		}
		
		//Only check middle row if one is empty
		if(fClear || lClear){
			//////////System.out.println("One was clear");
			if(fClear){
				//////////System.out.println("First was clear");
				clear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][0] == null)continue;
					if(i[xX][0] != null && !i[xX][0].getType().equals(Material.AIR)){
						clear = false;
						//fClear = false;
						break;
					}
				}
				if(clear){
					//////////System.out.println("Middle now clear");
					i = copyIgnore(ItemStack.class, i, -1, 0);
					z--;
					
				}
			}else{
				clear = true;
				//////////System.out.println("last was clear");
				//lClear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][z] == null)continue;
					if(i[xX][z] != null && !i[xX][z].getType().equals(Material.AIR)){
						clear = false;
						//lClear = false;
						break;
					}
				}
				if(clear){
					//////////System.out.println("MIddle now clear");
					i = copyIgnore(ItemStack.class, i, -1, z);
					z--;
				}
			}
			
		}
		
		
		
		/*for(int x = 0; x < i.length; x++){
			
			for(int z = 0; z < i[x].length; z++){
				//("x:" + x + " z:" + z);
				if(i[x][z] == null){
					
					//("Null");
				}else{
					//(i[x][z].getType());
				}
				
			}
			
		}*/
		
		//////////////////System.out.println"Before anything: ");
		//////////////////System.out.printlnArrays.deepToString(i));
		/*******for(int x = 0; x < i.length; x++){
			boolean clear = true;
			for(int z = 0; z < i[x].length; z++){
				if(i[x][z] == null)continue;
				if(i[x][z] != null && !i[x][z].getType().equals(Material.AIR)){
					clear = false;
					break;
				}
			}
			if(clear){
				i = copyIgnore(ItemStack.class, i, x, -1);
				x--;
			}
		}
		
		
		
		//////////////////System.out.println"After rows: ");
		//////////////////System.out.printlnArrays.deepToString(i));
		
		if(i.length == 0)return i;
		for(int z = 0; z < i[0].length; z++){
			boolean clear = true;
			for(int x = 0; x < i.length; x++){
				if(i[x][z] == null)continue;
				if(i[x][z] != null && !i[x][z].getType().equals(Material.AIR)){
					clear = false;
					break;
				}
			}
			if(clear){
				i = copyIgnore(ItemStack.class, i, -1, z);
				z--;
			}
		}
		
		
		/*for(int x = 0; x < i.length; x++){
			
			for(int z = 0; z < i[x].length; z++){
				if(i[x][z] == null){
					//("Null");
				}else{
					//(i[x][z].getType());
				}
				
			}
			
		}*////////
		//////////////System.out.println"After cols: ");
		//////////////System.out.printlnArrays.deepToString(i));
		//
		return i;
	}
	
	protected static String[][] convertToMinimizedStructure(String[][] i, String filter){
		
	
		/**for(int x = 0; x < i.length; x++){
			boolean clear = true;
			for(int z = 0; z < i[x].length; z++){
				if(i[x][z] != null && !i[x][z].equalsIgnoreCase(" ")){
					clear = false;
					break;
				}
			}
			if(clear){
				i = copyIgnore(String.class, i, x, -1);
				x--;
			}
		}
		if(i.length == 0)return i;
		for(int z = 0; z < i[0].length; z++){
			boolean clear = true;
			for(int x = 0; x < i.length; x++){
				if(i[x][z] != null && !i[x][z].equalsIgnoreCase(" ")){
					clear = false;
					break;
				}
			}
			if(clear){
				i = copyIgnore(String.class, i, -1, z);
				z--;
			}
		}
		return i;*/
		
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
						//fClear = false;
						break;
					}
				}
				if(clear){
					i = copyIgnore(String.class, i, -1, 0);
					z--;
					
				}
			}else{
				clear = true;
				//lClear = true;
				for(int xX = 0; xX < i.length; xX++){
					if(i[xX][z] == null)continue;
					if(i[xX][z] != null && !i[xX][z].isEmpty() && !i[xX][z].equals(filter)){
						clear = false;
						//lClear = false;
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
		//("Before 'getStructure':");
		//(Arrays.deepToString(i));
		Character[] chars = {'a','b','c','d','e','f','g','h','i'};
		ItemStack[][] min = convertToMinimizedStructure(i);
		//(Arrays.deepToString(min));
		//if(min.length == 0)return null;
		String[][] st = new String[min.length][min[0].length];
		HashMap<Character, ItemStack> keys = new HashMap<Character, ItemStack>();
		for(int x = 0; x < min.length; x++){
			for(int z = 0; z < min[x].length; z++){
					Character b = chars[keys.keySet().size()];
					
					/**if(i[x][z].getType().equals(Material.AIR) || i[x][z] == null){
						st[x][z] = " ";
						
					}*/
					
					st[x][z] = b.toString();
					keys.put(b, min[x][z]);
				}
			}
		
		//////////////System.out.println"After 'getStructure':");
		//////////////System.out.printlnArrays.deepToString(st));
		return new Pair<String[][], HashMap<Character, ItemStack>>(st, keys);
	}
	
	public String[] toOneD(String[][] s){
		//////////////System.out.printlnArrays.deepToString(s));
		String[] nS = new String[s.length];
		//String[] b = new String[9];
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
					
					//////////////System.out.println"Value: " + s[x][z]);
				
				//i.add(s[x][z]);
			}
			nS[x] = t;
		}
		
		//////System.out.println(Arrays.deepToString(nS));
		
		/*for(int i = 0; i < s.length; i++){
			if(s[i].length >=3){
				if(s[i][0].isEmpty()){
					s[i][0] = " ";
				}else if(s[i][1].isEmpty()){
					s[i][1] = " ";
				}else if(s[i][2].isEmpty()){
					s[i][2] = " ";
				}
				nS[i] = s[i][0] + s[i][1] + s[i][2];
				//////////////System.out.println"more than three");
				//////////////System.out.printlnnS[i]);
			}else{
				//////////////System.out.println"less than three");
				String t = "";
				for(int c = 0; c < s[i].length; c++){
					if(s[i][c].isEmpty()){
						s[i][c] = " ";
					}
						t += s[i][c];
					//////////////System.out.printlns[i][c] + "T");
				}
				nS[i] = t;
				//////////////System.out.printlnnS[i]);
			}
			
		}*/
		//////////////System.out.println"Final Shape:");
		//////////////System.out.printlnArrays.deepToString(nS));
		//////////////////System.out.printlnnS);
		return nS;
 	}
	
	public ItemStack[] toOneD(ItemStack[][] s){
		//ArrayList<ItemStack> i = new ArrayList<ItemStack>();
		ItemStack[] items = new ItemStack[9];
		for(int x = 0; x < 3; x++){
			for(int z = 0; z < 3; z++){
				if(x >= s.length || z >= s[x].length){
					items[x*3 + z] = null;
				}else{
					items[x*3 + z] = s[x][z];
				}
				//i.add(s[x][z]);
			}
		}
		
		return items;
	}
	
	
	public void setStructure(String[][] s){
		this.structure = s;
		//////////////System.out.println"setStructure");
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
		
		////////System.out.println("AAAAAAAAAAAA");
		for(java.util.Map.Entry<Character, ItemStack> e : a.entrySet()){
			
			String item = e.getValue() != null ? ProRecipes.itemToStringBlob(e.getValue(), true) : "null";
			////////System.out.println(e.getKey().toString() + " " + item);
			aS.add(e.getKey().toString() + " " + item);
		}
		////////System.out.println("\nBBBBBBBBBBBBBBBBBBBBB");
		for(java.util.Map.Entry<Character, ItemStack> e : b.entrySet()){
			String item = e.getValue() != null ? ProRecipes.itemToStringBlob(e.getValue(), true) : "null";
			////////System.out.println(e.getKey().toString() + " " + item);
			bS.add(e.getKey().toString() + " " + item);
		}
		
		for(String s : aS){
			//System.out.println(s);
			if(!bS.contains(s)){
				//System.out.println("FALSE 1");
				match = false;
				break;
			}
		}
		for(String s : bS){
			//System.out.println(s);
			if(!aS.contains(s)){
				//System.out.println("FALSE 2");
				match = false;
				break;
			}
		}
		
		
		return match;
	}
	
	public boolean register(){
		
		//Remove anything that will make a mess of things
		////////////System.out.println(getResult().getType().toString() + "\n\n\n\n\n\n");
		//System.out.println(registerer.getIngredientMap().toString() + "\n");
      //  System.out.println(Arrays.deepToString(registerer.getShape()) + "\n");
		Iterator<org.bukkit.inventory.Recipe> it = ProRecipes.getPlugin().defaultRecipes.iterator();
		org.bukkit.inventory.Recipe recipe;
        while(it.hasNext())
        {
        	//////System.out.println("Has next");
            recipe = it.next();
          /*  if(recipe.getResult().getType().equals(Material.WOOD_AXE)){
            	 if (recipe != null && recipe instanceof ShapedRecipe)
                 {
                 	System.out.println("is shaped");
                 	ShapedRecipe b = (ShapedRecipe)recipe;
                 	
                 	System.out.println(b.getIngredientMap().toString());
                 	System.out.println(Arrays.deepToString(b.getShape())+"\n");
                 	
         			System.out.println(registerer.getIngredientMap().toString());
         			System.out.println(Arrays.deepToString(registerer.getShape()) + "\n\n");
               
                 	
                 	System.out.println(b.getIngredientMap().toString());
                 	System.out.println(b.getResult().getType().toString());
               
                 	if(matchMaps(b.getIngredientMap(), registerer.getIngredientMap())){
                 	System.out.println("Ingredients are the same");
                 		if(Arrays.deepEquals(b.getShape(), registerer.getShape())){
                 			
                 		System.out.println("Shape is same");
                 			////////////System.out.println("adding conflict.. : " + b.toString());
                 			//////System.out.println(b.getIngredientMap());
                 			//////System.out.println(b.getShape());
                 			////System.out.println("ADDED CONFLICT! IAEJGIOESGJOISJREGPIOJESOIGJR");
                 		//System.out.println("ADDING CONFLICT");
                 			RPGRecipes.getPlugin().getRecipes().addConflict(this, b);
                 			//it.remove();
                 		}
                 	}
                 }
            }*/
            if (recipe != null && recipe instanceof ShapedRecipe)
            {
            	//////////////System.out.println("is shaped");
            	ShapedRecipe b = (ShapedRecipe)recipe;
            	/*for(ItemStack i : b.getIngredientMap().values()){
            		if(i == null)continue;
            		if(i.getType().equals(Material.COBBLESTONE)){
            			////System.out.println(b.getIngredientMap().toString());
            			////System.out.println(Arrays.deepToString(b.getShape())+"\n");
            			////System.out.println(registerer.getIngredientMap().toString());
            			////System.out.println(Arrays.deepToString(registerer.getShape()) + "\n\n");
            			break;
            		}
            	}*/
            	
            	////////System.out.println(b.getIngredientMap().toString());
            	////////System.out.println(b.getResult().getType().toString());
          
            	if(matchMaps(b.getIngredientMap(), registerer.getIngredientMap())){
            	//	System.out.println("Ingredients are the same");
            		if(Arrays.deepEquals(b.getShape(), registerer.getShape())){
            			
            		//	System.out.println("Shape is same");
            			////////////System.out.println("adding conflict.. : " + b.toString());
            			//////System.out.println(b.getIngredientMap());
            			//////System.out.println(b.getShape());
            			////System.out.println("ADDED CONFLICT! IAEJGIOESGJOISJREGPIOJESOIGJR");
            		//System.out.println("ADDING CONFLICT");
            			ProRecipes.getPlugin().getRecipes().addConflict(this, b);
            			//it.remove();
            		}
            	}
            }
        }
		//You need your plugin here
       // System.out.println("Register recipes");
		ProRecipes.getPlugin().getServer().addRecipe(registerer);
		////////System.out.println(registerer.getIngredientMap().toString());
		////////System.out.println(registerer.getShape().toString());
		////////////////System.out.printlnregisterer.getIngredientMap().toString());
		////////////////System.out.printlnArrays.deepToString(registerer.getShape()));
		//This is the Recipes.class(only have one instance of this class) 
		
		//System.out.println("Add to manager");
		return ProRecipes.getPlugin().getRecipes().addShaped(this);
		//RPGRecipes.getPlugin().printAllWithResultType(registerer.getResult().getType());
	}
	
	public ItemStack getResult(){
		return this.result;
	}
	
	
	
	public boolean matchLowest(ItemStack[][] i){
		if(!match(i)){
			//System.out.println("Does not match");
			//System.out.println(Arrays.deepToString(i));
			//System.out.println(Arrays.deepToString(itemsCache));
			if(i.length != itemsCache.length){
				//System.out.println("Length is not the same!");
				return false;
			}
			if(i[0].length != itemsCache[0].length){
				// System.out.println("Second length is not the same");
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
							// System.out.println("Not both air!");
							return false;
						}
					}
					if(compare.getAmount() < orig.getAmount()){
						// System.out.println("Not enough!");
						return false;
					}
					compare.setAmount(orig.getAmount());
					//System.out.println(RPGRecipes.itemToStringBlob(compare));
				//	System.out.println(RPGRecipes.itemToStringBlob(orig));
					if(!ProRecipes.itemToStringBlob(compare).equals(ids[x][z])){
						//System.out.println("Do not match exactly");
						return false;
					}
					//if(!compare.isSimilar(orig)){
						//System.out.println("Are not similar");
						//return false;
					//}
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
            	
            	 //recipe = it.next();
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
		////System.out.println("Matching default!");
		//////////////////System.out.println"MAtch array");
		String a = getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		
		String b = getId(i).replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		////System.out.println("Recipe id: "+ a);
		//System.out.println("AAAAA: \n" + a +  "\n");
	//	System.out.println("BBBBBB: \n" + b +  "\n");
		////System.out.println("Matching id: " + b);
		return a.equalsIgnoreCase(b);
	}
	
	public boolean match(RecipeShaped p){
		String a = getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		if(p == null){
			return false;
		}
		//System.out.println("AAAAA: \n" + a +  "\n");
		String b = p.getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		//System.out.println("BBBBBB: \n" + b +  "\n");
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
