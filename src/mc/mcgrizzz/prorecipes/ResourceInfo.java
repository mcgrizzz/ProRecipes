package mc.mcgrizzz.prorecipes;


import org.bukkit.scheduler.BukkitRunnable;


public class ResourceInfo {
	
	//public static String spigotUserId = "%%__USER__%%";
	
	public static char[] arr;
	
	boolean notAuthentic;
	
	int hostTask, disableTask;
	
	public ResourceInfo(){
		//arr = spigotUserId.toCharArray();
		
	/*if(!Authentication.Integrity.integrityCheck()){
			notAuthentic = true;
			//System.out.println("Integrity fail");
		}
		
		int auth = Authentication.buyer.isBuyer.initialAuthentication();
		
		if(!notAuthentic){
			try{
				notAuthentic = false;
				int b = 69699/auth;
			}catch(Exception e){
				//System.out.println("Authentication failed");
				notAuthentic = true;
			}
		}
		
		if(!notAuthentic){
			if(Authentication.Host.can.canHost()){
				Authentication.Host.add.addHost();
				//System.out.println("is hosting");
			}else{
				//System.out.println("cannot host");
				notAuthentic = true;
			}
		}*/
		
			
		
		
		
		/*hostTask = new BukkitRunnable(){

			@Override
			public void run() {
				if(Authentication.Host.is.isHosting()){
					return;
				}
				if(Authentication.Host.can.canHost()){
					Authentication.Host.add.addHost();
					return;
				}else{
					
				}
				
			}
			
		}.runTaskLaterAsynchronously(ProRecipes.getPlugin(), 27*60*2).getTaskId();*/
	//	notAuthentic = false;
		/*disableTask = new BukkitRunnable(){

			@Override
			public void run() {
				if(notAuthentic){
					System.out.println("[ProRecipes] I see you didn't purchase this copy. That's okay. I just don't like you now. Please consider purchasing to make up for it.");
					//ProRecipes.getPlugin().getServer().getPluginManager().disablePlugin(ProRecipes.getPlugin());
				}
				
			}
			
		}.runTaskLaterAsynchronously(ProRecipes.getPlugin(), 27*15*60).getTaskId();*/
	}
	
	public static int getResourceId(){
		return 9039;
	}
	
	public static int getAuthorId(){
		return 13001;
	}
	
	/*public static class InnerInner{
		
		
		public static class InTheInner{
			
			
			public static class InTheInnerMost{
				
				public static String executePost(String targetURL, String urlParameters) {
					try{
						URL url = new URL(targetURL + urlParameters);
						//System.out.println(targetURL + urlParameters);
						URLConnection con = url.openConnection();
						InputStream in = con.getInputStream();
						String encoding = con.getContentEncoding();
						encoding = encoding == null ? "UTF-8" : encoding;
						String body = IOUtils.toString(in, encoding);
						return body;
					}catch(Exception e){
						return "";
					}
				}
			}
			
			public static class InAnotherInner{
				
				public static String createAPIHash(int buyerId){
					
					try{
						long id = ((long)buyerId)*9039;
						
						//System.out.println(id);
						
						byte[] bytesOfMessage = ("" + id).getBytes("UTF-8");

						MessageDigest md = MessageDigest.getInstance("SHA-256");
						
						byte[] thedigest = md.digest(bytesOfMessage);
						
						return toHexString(thedigest);
						
					}catch(Exception e){
						return "0000";
					}
					
				}
				
				public static String toHexString(byte[] bytes) {
				    StringBuilder hexString = new StringBuilder();

				    for (int i = 0; i < bytes.length; i++) {
				        String hex = Integer.toHexString(0xFF & bytes[i]);
				        if (hex.length() == 1) {
				            hexString.append('0');
				        }
				        hexString.append(hex);
				    }

				    return hexString.toString();
				}
				
			}
			
			public static class requestStrings{
				
				public static char[] key = new char[] {'?', 'k','e','y','='};
				public static char[] buyerCheck = new char[] {'&','c','m','d','=','i','s','_','b','u','y','e','r','&','b','u','y',
						'e','r','=','^','&','p','l','u','g','i','n','=','9','0','3','9'};
				public static char[] hostCheck = new char[] {'&','c','m','d','=','c','a','n','_','h','o','s','t','&','b','u','y',
						'e','r','=','^','&','p','l','u','g','i','n','=','9','0','3','9'};
				public static char[] addHost = new char[] {'&','c','m','d','=','a','d','d','_','h','o','s','t','&','b','u','y',
						'e','r','=','^','&','p','l','u','g','i','n','=','9','0','3','9'};
				public static char[] removeHost = new char[] {'&','c','m','d','=','r','e','m','o','v','e','_','h','o','s','t','&','b','u','y',
						'e','r','=','^','&','p','l','u','g','i','n','=','9','0','3','9'};
				public static char[] isHost = new char[] {'&','c','m','d','=','i','s','_','h','o','s','t','i','n','g','&','b','u','y',
						'e','r','=','^','&','p','l','u','g','i','n','=','9','0','3','9'};
				public static char[] baseURL = new char[] {'h','t','t','p',':','/','/','a','u','t','h','.','d','r','e','p','i','c','.',
						'x','y','z','/','a','u','t','h','/','r','e','q'};
				
			}
			
			
		}
		
		
		
	}*/
	
	
	
	

}
