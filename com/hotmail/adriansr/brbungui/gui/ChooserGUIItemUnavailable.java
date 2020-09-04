package com.hotmail.adriansr.brbungui.gui;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.hotmail.adriansr.core.menu.action.ItemClickAction;
import com.hotmail.adriansr.core.util.StringUtil;

/**
 * An implementation of {@link ChooserGUIItem} that represents an unavailable arena.
 * <p>
 * @author AdrianSR / Friday 21 August, 2020 / 11:58 PM
 */
public class ChooserGUIItemUnavailable extends ChooserGUIItem {
	
	/**
	 * Default unavailable item.
	 */
	public static final ChooserGUIItemUnavailable DEFAULT = new ChooserGUIItemUnavailable ( 
			ChatColor.RED + "This arena is currently unavailable!" , Material.BARRIER , 
			ChatColor.RED + "Restaring..." ,
			ChatColor.GOLD + "Pleas wait, this arena should be available soon" );
	
	protected final String message;

	/**
	 * 
	 * @param message the unavailable message.
	 * @param material
	 * @param title
	 * @param lore
	 */
	public ChooserGUIItemUnavailable ( String message , Material material , String title , Collection < String > lore ) {
		super ( -1 , material , title , lore );
		this.message       = message;
		this.click_handler = new ChooserGUIItemClickHandler ( this ) {
			@Override public void onClick ( ItemClickAction action ) {
				// this only tells the player the arena is unavailable.
				if ( !StringUtil.isBlank ( message ) ) {
					action.getPlayer ( ).sendMessage ( message );
				}
			}
		};
		this.status_updater.dispose ( );
		this.status_updater = null;
	}

	/**
	 * 
	 * @param message the unavailable message.
	 * @param material
	 * @param title
	 * @param lore
	 */
	public ChooserGUIItemUnavailable ( String message , Material material , String title , String... lore ) {
		this ( message , material , title , Arrays.asList ( lore ) );
	}
	
	public String getMessage ( ) {
		return message;
	}
	
	@Override
	public int getPort ( ) {
		return -1;
	}
}