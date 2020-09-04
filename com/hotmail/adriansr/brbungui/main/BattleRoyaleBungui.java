package com.hotmail.adriansr.brbungui.main;

import org.bukkit.ChatColor;

import com.hotmail.adriansr.brbungui.command.BunguiCommand;
import com.hotmail.adriansr.brbungui.handler.ArenasHandler;
import com.hotmail.adriansr.brbungui.handler.GUIHandler;
import com.hotmail.adriansr.brbungui.listener.OpenItemListener;
import com.hotmail.adriansr.core.plugin.Plugin;
import com.hotmail.adriansr.core.plugin.PluginAdapter;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.version.CoreVersion;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 12:03 PM
 */
public class BattleRoyaleBungui extends PluginAdapter {

	public static BattleRoyaleBungui getInstance ( ) {
		return Plugin.getPlugin ( BattleRoyaleBungui.class );
	}
	
	@Override
	protected boolean setUp ( ) {
		ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "Enabled!" , this );
		return true;
	}
	
	@Override
	protected boolean setUpCommands ( ) {
		new BunguiCommand ( this );
		return true;
	}
	
	@Override
	protected boolean setUpHandlers ( ) {
		new ArenasHandler ( this );
		new GUIHandler ( this );
		return true;
	}
	
	@Override
	protected boolean setUpListeners ( ) {
		new OpenItemListener ( this );
		return true;
	}
	
	@Override
	public CoreVersion getRequiredCoreVersion ( ) {
		return CoreVersion.v2_0_0;
	}
}