package mc.mcgrizzz.prorecipes;

import io.puharesource.mc.titlemanager.api.TitleObject;

import org.bukkit.entity.Player;

public class Messenger {
	
	public static void sendMessage(Player player, String title, String subtitle){
		if(!ProRecipes.getPlugin().prompts)return;
		if(ProRecipes.getPlugin().title){
			TitleObject to = new TitleObject(title, subtitle);
			to.setFadeIn(5);
			to.setStay(25);
			to.setFadeOut(5);
			to.send(player);
		}else{
		player.sendMessage(title + " - " + subtitle);
			
		}
			
	}

}
