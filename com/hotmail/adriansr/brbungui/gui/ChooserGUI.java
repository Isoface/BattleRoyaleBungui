package com.hotmail.adriansr.brbungui.gui;

import org.apache.commons.lang.Validate;

import com.hotmail.adriansr.core.menu.ItemMenu;
import com.hotmail.adriansr.core.menu.size.ItemMenuSize;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 01:30 PM
 */
public class ChooserGUI {
	
	protected final String      title;
	protected final ItemMenuSize size;
	
	protected final ItemMenu                      handle;
	protected final ChooserGUIItem [ ]             items;
	protected ChooserGUIItemUnavailable unavailable_item;
	
	protected final ChooserGUIUpdater updater;
	
	public ChooserGUI ( String title , ItemMenuSize size ) {
		this.title            = title;
		this.size             = size;
		this.handle           = new ItemMenu ( title , size , null );
		this.items            = new ChooserGUIItem [ size.getSize ( ) ];
		this.unavailable_item = ChooserGUIItemUnavailable.DEFAULT;
		this.updater          = new ChooserGUIUpdater ( this );
	}
	
	public ChooserGUI ( String title , int size ) {
		this ( title , ItemMenuSize.fitOf ( size ) );
	}

	public String getTitle ( ) {
		return title;
	}

	public ItemMenuSize getSize ( ) {
		return size;
	}
	
	public ItemMenu getHandle ( ) {
		return handle;
	}
	
	public ChooserGUIItem [ ] getItems ( ) {
		return items;
	}
	
	public ChooserGUIItemUnavailable getUnavailableItem ( ) {
		return unavailable_item;
	}
	
	public ChooserGUIUpdater getUpdater ( ) {
		return updater;
	}
	
	public void setItem ( int index , ChooserGUIItem item ) {
		rangeCheck ( index );
		items [ index ] = item;
		handle.setItem ( index , item.handle );
	}
	
	public void setUnavailableItem ( ChooserGUIItemUnavailable unavailable_item ) {
		Validate.notNull ( unavailable_item , "the unavailable item cannot be null!" );
		this.unavailable_item = unavailable_item;
	}
	
	public void refreshHandle ( ) {
		handle.clear ( );
		for ( int i = 0 ; i < items.length ; i ++ ) {
			ChooserGUIItem item = items [ i ];
			if ( item != null ) {
				handle.setItem ( i , item.handle );
			}
		}
	}
	
	protected void rangeCheck ( int index ) {
		Validate.isTrue ( index >= 0 , "index cannot be < 0!" );
		Validate.isTrue ( index < size.getSize ( ) , "index out of range!" );
	}
}