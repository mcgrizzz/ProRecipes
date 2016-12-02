package mc.mcgrizzz.prorecipes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecipesCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		if(sender instanceof Player){
			Player p = (Player)sender;
			if(p.hasPermission("prorecipes.managerecipes")){
				ProRecipes.getPlugin().removeMeta(p);
				ProRecipes.getPlugin().rm.openRecipeManager(p);
			}else{
				p.sendMessage(ChatColor.RED + "You do not have permission to manage recipes.");
			}
		}else{
			sender.sendMessage("You must be a player to execute this command");
		}
		
		return false;
	}

}
