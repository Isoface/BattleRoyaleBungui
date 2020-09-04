package com.hotmail.adriansr.brbungui.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hotmail.adriansr.brbungui.gui.ChooserGUI;
import com.hotmail.adriansr.brbungui.handler.GUIHandler;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.command.CommandHandler;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Saturday 22 August, 2020 / 12:05 PM
 */
public class BunguiCommand extends CommandHandler {

	public BunguiCommand ( BattleRoyaleBungui plugin ) {
		super ( plugin , "Bungui" );
	}

	@Override
	public boolean onCommand ( CommandSender sender , Command command , String label , String [ ] args ) {
		if ( sender instanceof Player ) {
			ChooserGUI gui = GUIHandler.getInstance ( ).getGUI ( );
			if ( gui != null ) {
				gui.refreshHandle ( );
				gui.getHandle ( ).open ( (Player) sender );
			} else {
				sender.sendMessage ( ChatColor.RED + "Arena chooser has an invalid configuration!" );
			}
		} else {
			sender.sendMessage ( ChatColor.RED + "Cannot execute this command from console!" );
		}
		return true;
	}
}