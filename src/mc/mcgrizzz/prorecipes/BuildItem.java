package mc.mcgrizzz.prorecipes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildItem implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label,
			String[] arg3) {
		
		if(sender instanceof Player){
			Player p = (Player)sender;
			if(p.hasPermission("prorecipes.createitem")){
				ProRecipes.getPlugin().removeMeta(p);
				ProRecipes.getPlugin().b.openItemBuilder(p);
			}else{
				p.sendMessage(ChatColor.RED + "You do not have permission to build custom items.");
			}
		}else{
			sender.sendMessage("You must be a player to execute this command");
		}
		
		
		return false;
	}

}
