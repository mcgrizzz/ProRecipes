package mc.mcgrizzz.prorecipes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;

public class RecipeChest {
	
	String idCache = "";
	
	HashMap<Character, ItemStack> ingredients = new HashMap<Character, ItemStack>();
	ItemStack[] results = new ItemStack[4];
	String[][] structure;
	ItemStack[][] itemsCache;
	String permission;
	
	//This is used to identify any special recipe (With armorstand as a result)
	//ShapedRecipe registerer;
	//ItemStack result;
	
	public RecipeChest(ItemStack[] it){
		results = it.clone();
	//	for(ItemStack i : results){
		//	//System.out.println(i);
		//}
		//ItemStack i = new ItemStack(Material.ARMOR_STAND);
		//ItemMeta m = i.getItemMeta();
		//m.setDisplayName("recipedchestitem");
		//i.setItemMeta(m);
		//registerer = new ShapedRecipe(i);
	}
	
	
	public void setPermission(String s){
		this.permission = s;
	}
	
	public String getPermission(){
		return this.permission;
	}
	
	public boolean hasPermission(){
		return permission != null && !permission.isEmpty();
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
			//registerer.setIngredient(c, in.get(c).getData());
		}
	}
	
	public ItemStack[] getItems(){
		ItemStack[] items = new ItemStack[16];
		
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
		idCache = Arrays.deepToString(convertToMinimizedStructure(i, ProRecipes.airString));
		itemsCache = convertToMinimizedStructure(k);
		////////System.out.println(idCache);
		return idCache;
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
				//////////////////System.out.println"adding something ");
				//////////////////System.out.printlningredients.get(structure[t][z].toCharArray()[0]).getType());
				b[t][z] = ProRecipes.itemToStringBlob(i[t][z]);
			}
		}
		k = Arrays.deepToString(convertToMinimizedStructure(b, ProRecipes.airString));
		return k;
	}
	
	protected static ItemStack[][] convertToArray(ItemStack[] i){
		if(i.length / 4 == 4){
			//////////////////System.out.printlni.length);
			//System.out.println("Before it's converted");
			//System.out.println(Arrays.deepToString(i));
			ItemStack[][] arr = new ItemStack[4][4];
			//////////////////System.out.printlnarr.length);
			//////////////////System.out.printlnarr[0].length);
			for(int x = 0; x < 4; x++){
				//////////////////System.out.println"x" + x);
				for(int z = 0; z < 4; z++){
				//	//("z" + z);
					if(i[x*4 + z] == null){
						arr[x][z] = new ItemStack(Material.AIR, 0);
						continue;
					}
					arr[x][z] = i[x*4 + z]; 
				}
			}
		    //System.out.println("Converted array: ");
			//System.out.println(Arrays.deepToString(arr));
			return arr;
 		}else{
 			//////////////////System.out.println"Not nine");
			return null;
		}
	}
	
	
 	
	protected static ItemStack[][] convertToMinimizedStructure(ItemStack[][] i){
		
		///GO THROUGH ROWS
		//////////System.out.println("Going through rows");
		
		//System.out.println("Before anything: ");
		//System.out.println(Arrays.deepToString(i));
		
		
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
			
			//System.out.println("First column\n");
			//System.out.println(Arrays.deepToString(i));
			
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
			
			//System.out.println("last column  \n");
			//System.out.println(Arrays.deepToString(i));
		}
		
		//Only check middle row if one is empty
		if((fClear || lClear) && !(fClear && lClear)){
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
				//if clear check for next first column
				if(clear){
					//////////System.out.println("Middle(now first) is clear");
					i = copyIgnore(ItemStack.class, i, 0, -1);
					x--;
					//clear
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
					
					//if clear check next last row
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
			
		}else if(fClear && lClear){
			clear = true;
			for(int z = 0; z < i[0].length; z++){
				if(i[0][z] == null)continue;
				if(i[0][z] != null && !i[0][z].getType().equals(Material.AIR)){
					clear = false;
					break;
				}
			}
			//if clear check for next first column
			if(clear){
				//////////System.out.println("Middle(now first) is clear");
				i = copyIgnore(ItemStack.class, i, 0, -1);
				x--;
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
		if((fClear || lClear) && !(fClear && lClear)){
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
			
		}else if(fClear && lClear){
			
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
		
		///GO THROUGH ROWS
				//////////System.out.println("Going through rows");
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
					//////////System.out.println("First row is clear");
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
					//////////System.out.println("Last row is clear");
					i = copyIgnore(String.class, i, x, -1);
					x--;
				}
				
				//Only check middle row if one is empty
				if((fClear || lClear) && !(fClear && lClear)){
					//////////System.out.println("One is clear");
					if(fClear){
						//////////System.out.println("First is clear");
						clear = true;
						for(int z = 0; z < i[0].length; z++){
							if(i[0][z] == null)continue;
							if(i[0][z] != null && !i[0][z].isEmpty() && !i[0][z].equals(filter)){
								clear = false;
								break;
							}
						}
						//if clear check for next first column
						if(clear){
							//////////System.out.println("Middle(now first) is clear");
							i = copyIgnore(String.class, i, 0, -1);
							x--;
							//clear
							clear = true;
							for(int z = 0; z < i[0].length; z++){
								if(i[0][z] == null)continue;
								if(i[0][z] != null && !i[0][z].isEmpty() && !i[0][z].equals(filter)){
									clear = false;
									break;
								}
							}
							
							if(clear){
								//////////System.out.println("Middle(now first) is clear");
								i = copyIgnore(String.class, i, 0, -1);
								x--;
								
								
							}
							
						}
					}else{
						//////////System.out.println("Last is clear");
						clear = true;
						for(int z = 0; z < i[i.length-1].length; z++){
							if(i[i.length-1][z] == null)continue;
							if(i[i.length-1][z] != null && !i[i.length-1][z].isEmpty() && !i[i.length-1][z].equals(filter)){
								clear = false;
								break;
							}
						}
						if(clear){
							//////////System.out.println("Middle(Now last) is clear");
							i = copyIgnore(String.class, i, i.length-1, -1);
							x--;
							
							//if clear check next last row
							for(int z = 0; z < i[i.length-1].length; z++){
								if(i[i.length-1][z] == null)continue;
								if(i[i.length-1][z] != null && !i[i.length-1][z].isEmpty() && !i[i.length-1][z].equals(filter)){
									clear = false;
									break;
								}
							}
							if(clear){
								//////////System.out.println("Middle(Now last) is clear");
								i = copyIgnore(String.class, i, i.length-1, -1);
								x--;
								
							}
							
						}
					}
					
				}else if(fClear && lClear){
					clear = true;
					for(int z = 0; z < i[0].length; z++){
						if(i[0][z] == null)continue;
						if(i[0][z] != null && !i[0][z].isEmpty() && !i[0][z].equals(filter)){
							clear = false;
							break;
						}
					}
					//if clear check for next first column
					if(clear){
						//////////System.out.println("Middle(now first) is clear");
						i = copyIgnore(String.class, i, 0, -1);
						x--;
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
							//////////System.out.println("Middle(Now last) is clear");
							i = copyIgnore(String.class, i, i.length-1, -1);
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
					if(i[xX][0] != null && !i[xX][0].isEmpty() && !i[xX][0].equals(filter)){
						clear = false;
						fClear = false;
						break;
					}
				}
				if(clear){
					//////////System.out.println("First is clear");
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
					//////////System.out.println("Last is clear");
					i = copyIgnore(String.class, i, -1, z);
					z--;
				}
				
				//Only check middle row if one is empty
				if((fClear || lClear) && !(fClear && lClear)){
					//////////System.out.println("One was clear");
					if(fClear){
						//////////System.out.println("First was clear");
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
							//////////System.out.println("Middle now clear");
							i = copyIgnore(String.class, i, -1, 0);
							z--;
							for(int xX = 0; xX < i.length; xX++){
								if(i[xX][0] == null)continue;
								if(i[xX][0] != null && !i[xX][0].isEmpty() && !i[xX][0].equals(filter)){
									clear = false;
									//fClear = false;
									break;
								}
							}
							if(clear){
								//////////System.out.println("Middle now clear");
								i = copyIgnore(String.class, i, -1, 0);
								z--;
								
							}
							
						}
					}else{
						clear = true;
						//////////System.out.println("last was clear");
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
							//////////System.out.println("MIddle now clear");
							i = copyIgnore(String.class, i, -1, z);
							z--;
							for(int xX = 0; xX < i.length; xX++){
								if(i[xX][z] == null)continue;
								if(i[xX][z] != null && !i[xX][z].isEmpty() && !i[xX][z].equals(filter)){
									clear = false;
									//lClear = false;
									break;
								}
							}
							if(clear){
								//////////System.out.println("MIddle now clear");
								i = copyIgnore(String.class, i, -1, z);
								z--;
							}
						}
					}
					
				}else if(fClear && lClear){
					
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
						//////////System.out.println("Middle now clear");
						i = copyIgnore(String.class, i, -1, 0);
						z--;
					}else{
						clear = true;
						//////////System.out.println("last was clear");
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
							//////////System.out.println("MIddle now clear");
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
		Character[] chars = {'a','b','c','d','e','f','g','h','i', 'j','k','l', 'm', 'n', 'o', 'p'};
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
		ItemStack[] items = new ItemStack[16];
		for(int x = 0; x < 4; x++){
			for(int z = 0; z < 4; z++){
				if(x >= s.length || z >= s[x].length){
					items[x*4 + z] = null;
				}else{
					items[x*4 + z] = s[x][z];
				}
				//i.add(s[x][z]);
			}
		}
		
		return items;
	}
	
	
	public void setStructure(String[][] s){
		this.structure = s;
		//////////////System.out.println"setStructure");
		//registerer.shape(toOneD(s));
	}
	
	public void setIngredient(ItemStack it, Character i){
		
		if(it.getType() != Material.AIR){
			ingredients.put(i, it);
			//registerer.setIngredient(i, it.getData());
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
			String item = e.getValue() != null ? ProRecipes.itemToStringBlob(e.getValue()) : "null";
			////////System.out.println(e.getKey().toString() + " " + item);
			aS.add(e.getKey().toString() + " " + item);
		}
		////////System.out.println("\nBBBBBBBBBBBBBBBBBBBBB");
		for(java.util.Map.Entry<Character, ItemStack> e : b.entrySet()){
			String item = e.getValue() != null ? ProRecipes.itemToStringBlob(e.getValue()) : "null";
			////////System.out.println(e.getKey().toString() + " " + item);
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
		
		return ProRecipes.getPlugin().getRecipes().addChest(this);
	}
	
	public ItemStack[] getResult(){
		return this.results;
	}
	
	public ItemStack getDisplayResult(){
		ItemStack d = new ItemStack(Material.AIR);
		for(ItemStack i : results){
			if(i != null && !i.getType().equals(Material.AIR)){
				d = i.clone();
				break;
			}
		}
		
		return d;
	}
	
	
	
	public boolean matchLowest(RecipeChest i){
		if(!match(i)){
			ItemStack[][] passItems = convertToMinimizedStructure(i.itemsCache);
			ItemStack[][] meItems = convertToMinimizedStructure(itemsCache);
			////System.out.println("Does not match");
			if(passItems.length != meItems.length){
				//System.out.println("FALSE 1");
				return false;
			}
			if(passItems[0].length != meItems[0].length){
				//System.out.println("FALSE 2");
				////System.out.println("Second length is not the same");
				return false;
			}
			for(int x = 0; x < passItems.length; x++){
				for(int z = 0; z < passItems[x].length; z++){
					ItemStack compare = passItems[x][z].clone();
					ItemStack orig = meItems[x][z].clone();
					if(orig == null || orig.getType().equals(Material.AIR)){
						if(compare == null || compare.getType().equals(Material.AIR)){
							continue;
						}else{
							////System.out.println("Not both air!");
							//System.out.println("FALSE 3");
							return false;
						}
					}
					if(compare.getAmount()  >= orig.getAmount()){
						////System.out.println("Not enough!");
						System.out.println(ProRecipes.itemToStringBlob(compare));
						System.out.println(ProRecipes.itemToStringBlob(orig));
						//System.out.println("FALSE 4");
						return false;
					}
					compare.setAmount(orig.getAmount());
					if(!ProRecipes.itemToStringBlob(compare).equals(ProRecipes.itemToStringBlob(orig))){
						////System.out.println("Not the same amount");
						//System.out.println("FALSE 5");
						return false;
					}
				}
			}
			return true;
		}else{
			return true;
		}
		
	}
	
	//Is this the same recipe as the passed recipe
	public boolean match(ItemStack[][] i){
		////System.out.println("Matching default!");
		//////////////////System.out.println"MAtch array");
		String a = getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		String b = getId(i).replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		//System.out.println("Recipe id: "+ a);
		//System.out.println("Matching id: " + b);
		return a.equalsIgnoreCase(b);
	}
	
	public boolean match(RecipeChest p){
		String a = getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		if(p == null){
			return false;
		}
		
		String b = p.getId().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\{", "").replaceAll("\\}","").replaceAll(" =null, ", "").replaceAll(",  =null", "");
		//System.out.println("\n + ONE: \n" + a);
		//System.out.println("\n + TWO: \n" + b);
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
	

}
