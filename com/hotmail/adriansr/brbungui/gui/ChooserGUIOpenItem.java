package com.hotmail.adriansr.brbungui.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Saturday 22 August, 2020 / 12:19 PM
 */
public class ChooserGUIOpenItem {
	
	protected final int               position;
	protected final Material          material;
	protected final String               title;
	protected final Collection < String > lore;

	/**
	 * 
	 * @param position position in inventory, between 0 and 8.
	 * @param material
	 * @param title
	 * @param lore
	 */
	public ChooserGUIOpenItem ( int position , Material material , String title , Collection < String > lore ) {
		this.position = position;
		this.material = material;
		this.title    = title;
		this.lore     = lore;
	}

	/**
	 * 
	 * @param position position in inventory, between 0 and 8.
	 * @param material
	 * @param title
	 * @param lore
	 */
	public ChooserGUIOpenItem ( int position , Material material , String title , String... lore ) {
		this ( position , material , title , Arrays.asList ( lore ) );
	}

	public int getPosition ( ) {
		return position;
	}
	
	public Material getMaterial ( ) {
		return material;
	}

	public String getTitle ( ) {
		return title;
	}

	public Collection < String > getLore ( ) {
		return lore;
	}
	
	public ItemStack toItemStack ( int amount ) {
		return ItemStackUtil.addSoulbound ( ItemStackUtil
				.setLore ( ItemStackUtil.setName ( new ItemStack ( material , amount ) , title ) , new ArrayList < > ( lore ) ) );
	}
	
	public ItemStack toItemStack ( ) {
		return toItemStack ( 1 );
	}
}