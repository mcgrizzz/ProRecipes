![ProRecipes Banner](https://proxy.spigotmc.org/3b2198b65d3286658db78fce65f93d280428f90e?url=https://www.dropbox.com/s/2z0yhk0ap8qyw4u/Header.png?raw=1)

# ProRecipes | ![Jenkins](http://ci.drepic.xyz/job/ProRecipes/job/master/badge/icon) ![JavaDocs](https://javadoc.io/badge/dummy/dummy/1.0.0.svg?color=blue) 


An advanced, yet easy to use, system to create items, create recipes and manage recipes. Create infinite possibilities for your server, release your creativity.

# About Project

	  
 I have never had time to officially clean this up. That's why I decided to
 release it open source, for free. I don't have nearly as much time as I used to. 
 
 Please submit code changes + optimizations that you feel are urgent and report any issues.
 
 Thank you to everyone who purchased ProRecipes and left kind comments in regards to it. 
 
 I've left the anti-piracy code commented out. 
	 

# API

Getting started​
-
  

  
Access the API object like this.

    RecipeAPI api = ProRecipes.getAPI();
  You can then create, delete, and gain info about recipes using all the methods contained in the API class. The [javadocs](http://mcgrizzz.github.io/ProRecipesDocs/index.html?mc/mcgrizzz/prorecipes/RecipeAPI.html) can be very useful if you have any questions.
  
**Here's an example** of **creating** a shaped recipe then **retrieving info** about it then **deleting** it only using methods within the API class.  
  
First, I will create an array of all the ingredients I want (**length 9 max**). This will be converted into **3 rows of 3** or into a smaller structure later within the plugin (such as 2x2) to allow for mobility within a crafting table. 

    ItemStack apple = new ItemStack(Material.APPLE);  //create ItemStack
  
    ItemMeta appleMeta = apple.getItemMeta();  //Get ItemMeta and set properties
    appleMeta.setDisplayName(ChatColor.GREEN + "I am an apple");  
    apple.setItemMeta(appleMeta);  
      
    ItemStack arrow = new ItemStack(Material.ARROW);  //Repeat for another item
      
    ItemMeta arrowMeta = arrow.getItemMeta();  
    arrowMeta.setDisplayName(ChatColor.RED + "Sharp pointy stick");  
    arrow.setItemMeta(arrowMeta);  
    
    //Define an array of ingredients
    ItemStack[] arr = api.toArray(apple, arrow, apple,  
    arrow, apple, arrow);
 Next,  create a **result.**
 

    ItemStack result = new ItemStack(Material.GOLD_BLOCK);
Lastly, we create the recipe. All the methods that create recipes **return an integer**. This integer is the recipe **ID**. If the method returns less than 0, the recipe was **not able to be created.** 
> *Documentation on what each value less than 0 mean can be seen in the [javadocs](http://mcgrizzz.github.io/ProRecipesDocs/index.html?mc/mcgrizzz/prorecipes/RecipeAPI.html).*

       int recipeId = api.createShapedRecipe(arr, result);
Using the recipe ID, retrieve the recipe.

    RecipeContainer container = api.getRecipe(RecipeType.SHAPED, recipeId);
   The RecipeContainer object has the following methods.
   

    container.getIngredients();  
    container.getResult();  
    container.getType();
Recipes can also be **deleted** by recipeID.

    api.removeRecipe(RecipeType.SHAPED, recipeId);
There are also cancellable bukkit events dedicated as part of the API. 

 - **WorkbenchCraftEvent:** Called when a shapeless recipe or shaped recipe is crafted  
- **FurnaceCraftEvent:** Called when something is smelted in the furnace in a furnace recipe  
- **MulticraftEvent:** Called when something is crafted in the multicraft table

 Other Links
 -
[CI Server](http://ci.drepic.xyz/blue/organizations/jenkins/ProRecipes/activity)

[JavaDocs](http://mcgrizzz.github.io/ProRecipesDocs/index.html?mc/mcgrizzz/prorecipes/RecipeAPI.html)

[Plugin Page](https://www.spigotmc.org/resources/prorecipes.52533/)

Services
-

[![Plugin Shop](https://i.imgur.com/enlPypU.gif)](https://www.spigotmc.org/threads/%E2%98%85-drepics-plugin-shop-%E2%98%85-over-7-years-of-experience-fast-service.59741/)

