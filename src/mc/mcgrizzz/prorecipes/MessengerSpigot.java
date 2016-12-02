package mc.mcgrizzz.prorecipes;

import io.puharesource.mc.titlemanager.api.TitleObject;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessengerSpigot {
	
	public static void sendMessage(Player player, String title, String subtitle){
		if(!ProRecipes.getPlugin().prompts)return;
		System.out.println(title + " " + subtitle);
		if(ProRecipes.getPlugin().title){
			TitleObject to = new TitleObject(title, subtitle);
			to.setFadeIn(5);
			to.setStay(25);
			to.setFadeOut(5);
			to.send(player);
		}else{
				net.md_5.bungee.api.chat.TextComponent t = new net.md_5.bungee.api.chat.TextComponent(title);
				t.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/prorecipes-a-better-way-to-craft.9039/"));
				t.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.ComponentBuilder(ChatColor.DARK_AQUA + "Click to learn more about ProRecipes").create() ));
				t.addExtra(" - " + subtitle);
				player.spigot().sendMessage(t);
		}
			
	}

}
