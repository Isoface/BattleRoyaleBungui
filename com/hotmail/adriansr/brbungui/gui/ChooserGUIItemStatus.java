package com.hotmail.adriansr.brbungui.gui;

import java.io.IOException;

import org.bukkit.scheduler.BukkitTask;

import com.hotmail.adriansr.brbungui.arena.Arena;
import com.hotmail.adriansr.brbungui.arena.ServerPing;
import com.hotmail.adriansr.brbungui.arena.StatusResponse;
import com.hotmail.adriansr.brbungui.handler.ArenasHandler;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 04:11 PM
 */
public class ChooserGUIItemStatus implements Runnable {
	
	protected final ChooserGUIItem item;
	protected final BukkitTask executor;
	
	protected StatusResponse status = StatusResponse.FAILURE;
	
	public ChooserGUIItemStatus ( ChooserGUIItem item ) {
		this.item     = item;
		// FIXME: update delay must be configurable
		this.executor = SchedulerUtil.runTaskTimerAsynchronously ( this , 60 , 60 , BattleRoyaleBungui.getInstance ( ) );
	}
	
	public StatusResponse getCurrentStatus ( ) {
		return status;
	}

	@Override
	public void run ( ) {
		Arena arena = ArenasHandler.getInstance ( ).getArenas ( ).get ( item.getPort ( ) );
		if ( arena == null ) {
			// this is possible only when the arenas is removed from map.
			dispose ( );
			return;
		}
		
		ServerPing ping = arena.getPing ( );
		if ( ping != null ) {
			try {
				status = ping.fetchData ( );
			} catch ( IOException ex ) {
				status = StatusResponse.FAILURE;
			}
		}
	}
	
	public void dispose ( ) {
		this.executor.cancel ( );
	}
}