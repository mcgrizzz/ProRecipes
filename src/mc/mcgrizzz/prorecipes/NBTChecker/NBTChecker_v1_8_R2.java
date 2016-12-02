package mc.mcgrizzz.prorecipes.NBTChecker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import net.minecraft.server.v1_8_R2.ItemStack;
import net.minecraft.server.v1_8_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R2.NBTTagCompound;

public class NBTChecker_v1_8_R2 implements NBTChecker{

	@Override
	public List<String> getTags(String s) {
		ArrayList<String> t = new ArrayList<String>();
		byte[] testBytes = Base64Coder.decode(s);
		ByteArrayInputStream buf = new ByteArrayInputStream(testBytes);
    	 try
        {
          NBTTagCompound tag = NBTCompressedStreamTools.a(buf);
          Set<String> keys = tag.c();
          for (String key : keys) {
        	 // System.out.println(key);
        	  String b = tag.get(key).toString();
        	  //System.out.println(b);
        	  String[] arr = b.split(",");
        	  ArrayList<String> list = new ArrayList<String>();
        	  for(int c = 0; c < arr.length; c++){
        		  //System.out.println(arr[c]);
        		  if(arr[c].toLowerCase().contains("uuid")){
        			
        		  }else{
        			  list.add(arr[c]);
        		  }
        	  }
        	  
        	  b =  new StringBuilder().append(list).toString();
        	t.add(key + ":" + b);
          }
        }
        catch (IOException ex)
        {
        	ex.printStackTrace();
        }
		return t;
	}
	
	public String getPotionType(String s) {
		ArrayList<String> t = new ArrayList<String>();
		byte[] testBytes = Base64Coder.decode(s);
		ByteArrayInputStream buf = new ByteArrayInputStream(testBytes);
    	 try
        {
          NBTTagCompound tag = NBTCompressedStreamTools.a(buf);
          Set<String> keys = tag.c();
          for (String key : keys) {
        	  String b = tag.get(key).toString();
        	  
        	  String[] arr = b.split(",");
        	  for(int c = 0; c < arr.length; c++){
        		 
        		 // System.out.println("SOMETHING IN A LOOP ?   "  + arr[c]);
        		  if(arr[c].contains("minecraft:")){
        			  return arr[c].replace("minecraft:", "").replace('"', ' ').replace(" ", "");
        		  }
        		  
        	  }
          }
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
		return t.toString();
	}
	
	@Override
	public org.bukkit.inventory.ItemStack addTag(org.bukkit.inventory.ItemStack i, String key, String value) {
		ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null) {
			tag = nmsStack.getTag();
		}
		
		tag.setString(key, value);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

}
