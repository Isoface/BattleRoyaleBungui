package com.hotmail.adriansr.brbungui.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.hotmail.adriansr.core.menu.action.ItemClickAction;
import com.hotmail.adriansr.core.menu.item.Item;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 02:39 PM
 */
public class ChooserGUIItem {
	
	protected final int                   port;
	protected final Material          material;
	protected final String               title;
	protected final Collection < String > lore;
	
	protected Item                              handle;
	protected ChooserGUIItemClickHandler click_handler;
	protected ChooserGUIItemStatus      status_updater;

	public ChooserGUIItem ( int port , Material material , String title , Collection < String > lore ) {
		this.port     = port;
		this.material = material;
		this.title    = title;
		this.lore     = lore;
		
		this.click_handler = new ChooserGUIItemClickHandler ( this );
		this.handle        = new Item ( title , new ItemStack ( material ) , lore ) {
			@Override public void onClick ( ItemClickAction action ) {
				click_handler.onClick ( action );
			}
		};
		
		this.status_updater = new ChooserGUIItemStatus ( this );
	}
	
	public ChooserGUIItem ( int port , Material material , String title , String... lore ) {
		this ( port , material , title , Arrays.asList ( lore ) );
	}

	public int getPort ( ) {
		return port;
	}

	public String getTitle ( ) {
		return title;
	}

	public Collection < String > getLore ( ) {
		return Collections.unmodifiableCollection ( lore );
	}
	
	public Item getHandle ( ) {
		return handle;
	}
	
	public ChooserGUIItemClickHandler getClickHandler ( ) {
		return click_handler;
	}
	
	public ChooserGUIItemStatus getStatus ( ) {
		return status_updater;
	}
	
//	public boolean isUpdateStatus ( ) {
//		return status_updater != null;
//	}
//	
//	public void setUpdateStatus ( boolean flag ) {
//		if ( flag ) {
//			if ( status_updater == null ) {
//				status_updater = new ChooserGUIItemStatus ( this );
//			}
//		} else {
//			if ( status_updater != null ) {
//				status_updater.dispose ( );
//				status_updater = null;
//			}
//		}
//	}
}