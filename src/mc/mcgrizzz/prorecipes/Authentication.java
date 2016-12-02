package mc.mcgrizzz.prorecipes;

import java.util.ArrayList;

import com.licel.stringer.annotations.insecure;
import com.licel.stringer.annotations.secured;

public class Authentication {
	
	/* public static  boolean isInt(String s){
		 try{
			 Integer.parseInt(s);
		 }catch(Exception e){
			 return false;
		 }
		 return true;
	 }
	 
	public static class Host{
		
		public static class add{
			 public static void addHost(){
				 int id = isInt(new String(ResourceInfo.arr)) ? Integer.parseInt(new String(ResourceInfo.arr)) : 0;
				 
				 //System.out.println(id);
				 //if(id == 0)return;
				 
				 String args = new String(ResourceInfo.InnerInner.InTheInner.requestStrings.key) + 
						 ResourceInfo.InnerInner.InTheInner.InAnotherInner.createAPIHash(id) + 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.addHost).replaceAll("\\^", "" + id);
				 String test  = new String(ResourceInfo.InnerInner.InTheInner.requestStrings.baseURL);
						 
				// System.out.println(test + " " + args);
				 
				 String response = ResourceInfo.InnerInner.InTheInner.InTheInnerMost.executePost(test, args);
				 
				 //System.out.println(response);
				
				
			 }
		}
		
		public static class remove{
			 public static void removeHost(){
				 int id = isInt(new String(ResourceInfo.arr)) ? Integer.parseInt(new String(ResourceInfo.arr)) : 0;
				 
				 if(id == 0)return;
				 
				 String response = ResourceInfo.InnerInner.InTheInner.InTheInnerMost.executePost(new String(ResourceInfo.InnerInner.InTheInner.requestStrings.baseURL), 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.key) + 
						 ResourceInfo.InnerInner.InTheInner.InAnotherInner.createAPIHash(id) + 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.removeHost).replaceAll("\\^", "" + id));
				 
				 //System.out.println(response);
			 }
		}
		
		public static class is{
			public static boolean isHosting(){
				 int id = isInt(new String(ResourceInfo.arr)) ? Integer.parseInt(new String(ResourceInfo.arr)) : 0;
				 
				 if(id == 0)return false;
				 
				 String response = ResourceInfo.InnerInner.InTheInner.InTheInnerMost.executePost(new String(ResourceInfo.InnerInner.InTheInner.requestStrings.baseURL), 
						new String(ResourceInfo.InnerInner.InTheInner.requestStrings.key) + 
						 ResourceInfo.InnerInner.InTheInner.InAnotherInner.createAPIHash(id) + 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.isHost).replaceAll("\\^", "" + id));
				 
				// System.out.println(response);
				 
				 return response.contains("true");
			 }
		}
		
		public static class can{
			
			
			 
			public static boolean canHost(){
				 int id = isInt(new String(ResourceInfo.arr)) ? Integer.parseInt(new String(ResourceInfo.arr)) : 0;
				 
				 if(id == 0)return false;
				 
				 String response = ResourceInfo.InnerInner.InTheInner.InTheInnerMost.executePost(new String(ResourceInfo.InnerInner.InTheInner.requestStrings.baseURL), 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.key) + 
						 ResourceInfo.InnerInner.InTheInner.InAnotherInner.createAPIHash(id) + 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.hostCheck).replaceAll("\\^", "" + id));
				 
				// System.out.println(response);
				 
				 return response.contains("true");
			 }
		}
		
	}
	
	
	public static class buyer{
		
		public static class isBuyer{
			
			public static int initialAuthentication(){
				 //0 = failed, not a buyer, disable
				 //1 = failed, check in 10 mins
				 //2 = success!
				 int id = isInt(new String(ResourceInfo.arr)) ? Integer.parseInt(new String(ResourceInfo.arr)) : 0;
				 
				 if(id == 0)return 0;
				 
				 String response = ResourceInfo.InnerInner.InTheInner.InTheInnerMost.executePost(new String(ResourceInfo.InnerInner.InTheInner.requestStrings.baseURL), 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.key) + 
						 ResourceInfo.InnerInner.InTheInner.InAnotherInner.createAPIHash(id) + 
						 new String(ResourceInfo.InnerInner.InTheInner.requestStrings.buyerCheck).replaceAll("\\^", "" + id));
				 
				// System.out.println(response);
				 
				if(response.contains("not authorized")){
					return 0;
				}else if(response.contains("check again")){
					return 1;
				}else if(response.contains("false")){
					return 0;
				}else if(response.contains("true")){
					return 1;
				}
				
				return 0;
			 }
			
		}
	}
	
	public static class Integrity { 
		
		
		public static boolean integrityCheck(){
			ArrayList<String> ids = new ArrayList<String>();
			ids.add(ResourceInfo.spigotUserId);
			ids.add(spigotUserId);
			ids.add(RecipeAPI.spigotUserId);
			ids.add(Metrics.spigotUserId);
			ids.add(ItemBuilder.spigotUserId);
			ids.add(RecipeBuilder.spigotUserId);
			ids.add(RecipeChest.spigotUserId);
			ids.add(RecipeManager.spigotUserId);
			ids.add(Recipes.spigotUserId);
			ids.add(RecipeShaped.spigotUserId);
			
			for(int i = 0; i < ids.size(); i++){
				if(i - 1 >= 0){
					if(!ids.get(i).equals(ids.get(i - 1)))return false;
				}
				
				if(i + 1 < ids.size()){
					if(!ids.get(i).equals(ids.get(i + 1)))return false;
				}
			}
			
			return true;
		}
	}*/

}
