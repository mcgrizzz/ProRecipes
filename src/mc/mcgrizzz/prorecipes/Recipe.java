package mc.mcgrizzz.prorecipes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mc.mcgrizzz.prorecipes.RecipeAPI.RecipeType;

public class Recipe implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String s,
			String[] arg3) {
		if(sender instanceof Player){
			
			Player p = (Player)sender;
			if(p.hasPermission("prorecipes.createrecipe") || p.hasPermission("prorecipes.lookup")){
				if(arg3.length == 0 || !arg3[0].equalsIgnoreCase("lookup")){
					if(p.hasPermission("prorecipes.createrecipe")){
						ProRecipes.getPlugin().removeMeta(p);
						ProRecipes.getPlugin().rb.askPermission(p);
					}else{
						p.sendMessage(ChatColor.RED + "You do not have permission to create recipes");
					}
				}else{
					if(p.hasPermission("prorecipes.lookup")){
						ProRecipes.getPlugin().removeMeta(p);
						ProRecipes.getPlugin().rb.openRecipe(p, p.getItemInHand());
					}else{
						ProRecipes.getPlugin().removeMeta(p);
						ProRecipes.getPlugin().rb.askPermission(p);
					}
				}
			}else{
				p.sendMessage(ChatColor.RED + "You do not have permission");
			}
		}else{
			sender.sendMessage("You must be a player to execute this command");
		}
		
		return false;
	}

}
