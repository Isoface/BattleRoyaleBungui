package com.hotmail.adriansr.brbungui.gui;

import org.bukkit.scheduler.BukkitTask;

import com.hotmail.adriansr.brbungui.arena.StatusResponse;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 21 August, 2020 / 11:41 PM
 */
public class ChooserGUIUpdater implements Runnable {
	
	protected final ChooserGUI      gui;
	protected final BukkitTask executor;
	
	public ChooserGUIUpdater ( ChooserGUI gui ) {
		this.gui = gui;
		// FIXME: update delay must be configurable
		this.executor = SchedulerUtil.runTaskTimer ( this , 10 , 10 , BattleRoyaleBungui.getInstance ( ) );
	}

	@Override
	public void run ( ) {
		gui.refreshHandle ( );
		for ( int i = 0 ; i < gui.getItems ( ).length ; i ++ ) {
			ChooserGUIItem item = gui.getItems ( ) [ i ];
			if ( item == null ) {
				// empty slot.
				continue;
			}
			
			if ( StatusResponse.FAILURE.equals ( item.getStatus ( ).getCurrentStatus ( ) ) ) {
				gui.getHandle ( ).setItem ( i , gui.getUnavailableItem ( ).getHandle ( ) );
			} else {
				gui.getHandle ( ).setItem ( i , item.getHandle ( ) );
			}
		}
		
		gui.getHandle ( ).updateOnlinePlayers ( );
	}
}