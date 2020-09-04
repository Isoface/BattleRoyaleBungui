package com.hotmail.adriansr.brbungui.gui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.ChatColor;

import com.hotmail.adriansr.brbungui.arena.Arena;
import com.hotmail.adriansr.brbungui.handler.ArenasHandler;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.menu.action.ItemClickAction;
import com.hotmail.adriansr.core.util.bungeecord.MessagingUtil;
import com.hotmail.adriansr.core.util.bungeecord.Written;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 02:46 PM
 */
public class ChooserGUIItemClickHandler {

	protected final ChooserGUIItem item;
	
	public ChooserGUIItemClickHandler ( ChooserGUIItem item ) {
		this.item = item;
	}
	
	public void onClick ( ItemClickAction action ) {
		Arena arena = ArenasHandler.getInstance ( ).getArenas ( ).get ( item.getPort ( ) );
		if ( arena != null ) {
			if ( arena.getPing ( ) != null ) {
				try {
					MessagingUtil.sendPluginMessage ( BattleRoyaleBungui.getInstance ( ) , new Written ( )
							.writeUTF ( MessagingUtil.CONNECT_OTHER_ARGUMENT )
							.writeUTF ( action.getPlayer ( ).getName ( ) )
							.writeUTF ( arena.getName ( ) ) );
				} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | IOException ex ) {
					ex.printStackTrace ( );
				}
			} else {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Invalid configuration for arena '" + arena.getName ( ) + "'" , 
						BattleRoyaleBungui.getInstance ( ) );
			}
		}
	}
}
