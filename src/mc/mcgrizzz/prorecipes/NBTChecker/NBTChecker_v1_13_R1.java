package mc.mcgrizzz.prorecipes.NBTChecker;

import mc.mcgrizzz.prorecipes.ProRecipes;
import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Recipe;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class NBTChecker_v1_13_R1 implements NBTChecker{

    @Override
    public List<String> getTags(String s) {
        ArrayList<String> t = new ArrayList<String>();
        byte[] testBytes = Base64Coder.decode(s);
        ByteArrayInputStream buf = new ByteArrayInputStream(testBytes);
        try
        {
            NBTTagCompound tag = NBTCompressedStreamTools.a(buf);
            Set<String> keys = tag.getKeys();
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
        //HashMap<String, String> ke = new HashMap<String, String>();
        ArrayList<String> t = new ArrayList<String>();
        byte[] testBytes = Base64Coder.decode(s);
        ByteArrayInputStream buf = new ByteArrayInputStream(testBytes);
        try
        {
            NBTTagCompound tag = NBTCompressedStreamTools.a(buf);
            Set<String> keys = tag.getKeys();
            for (String key : keys) {
                // System.out.println(key);
                String b = tag.get(key).toString();
                //System.out.println(b);
                String[] arr = b.split(",");
                for(int c = 0; c < arr.length; c++){

                    //System.out.println("SOMETHING IN A LOOP ?   "  + arr[c]);
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
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    /*
     *
     * (non-Javadoc)
     * @see mc.mcgrizzz.prorecipes.NBTChecker.NBTChecker#removeRecipe(java.util.Iterator, org.bukkit.inventory.Recipe)
     *
     * Really hacky way of doing this. Not tested yet. In new versions of spigot there will hopefully be official ways to remove a recipe.
     */

    @Override
    public void removeRecipe(Iterator<Recipe> it, Recipe recipe) {
        System.out.println("REMOVING: " + recipe.getResult().getType().toString());
        Iterator<Recipe> recipes = ProRecipes.getPlugin().getServer().recipeIterator();
        HashSet<Recipe> storedRecipes = new HashSet<Recipe>();
        while(recipes.hasNext()){
            storedRecipes.add(recipes.next());
        }
        storedRecipes.remove(recipe);
        ProRecipes.getPlugin().getServer().clearRecipes();
        for(Recipe rec : storedRecipes){
            ProRecipes.getPlugin().getServer().addRecipe(rec);
        }
        System.out.println("REMOVED: " + recipe.getResult().getType().toString());

    }
}