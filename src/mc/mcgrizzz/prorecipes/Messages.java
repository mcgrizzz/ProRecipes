package mc.mcgrizzz.prorecipes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
	
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	
	ProRecipes plugin;
	public Messages(){
		plugin = ProRecipes.getPlugin();
		loadMessages();
	}

	public void loadMessages(){
		  if (customConfigFile == null) {
		    	customConfigFile = new File(plugin.getDataFolder(), "messages.yml");
		    }
		    
		    if(!customConfigFile.exists()){
	    		try {
	    			plugin.getDataFolder().mkdir();
					customConfigFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		 InputStream link = (getClass().getResourceAsStream("/messages.yml"));
	    		// write the inputStream to a FileOutputStream
	    		 try{
	    			 	OutputStream outputStream = new FileOutputStream(customConfigFile);
			    	 
		    			int read = 0;
		    			byte[] bytes = new byte[1024];
		    	 
		    			while ((read = link.read(bytes)) != -1) {
		    				outputStream.write(bytes, 0, read);
		    			}
	    		 }catch(Exception e){
	    			 
	    		 }
	    		
	    	}
		    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
		    
	}
	
	public void saveMessages(){
		try{
			
			customConfig.save(customConfigFile);
			
		}catch(Exception e){
			
		}
	}
	//public String getMessage(String s){
		//return customConfig.getString(s, "");
	//}

	
	public String getMessage(String s, String def){
		if(customConfig.contains(s)){
			return ChatColor.translateAlternateColorCodes('&', customConfig.getString(s));
		}else{
			customConfig.set(s, def);
			return def;
		}
	}
}