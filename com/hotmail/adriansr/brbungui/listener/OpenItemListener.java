package com.hotmail.adriansr.brbungui.listener;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.adriansr.brbungui.gui.ChooserGUIOpenItem;
import com.hotmail.adriansr.brbungui.handler.GUIHandler;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Saturday 22 August, 2020 / 12:40 PM
 */
public class OpenItemListener implements Listener {
	
	public OpenItemListener ( BattleRoyaleBungui plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onJoin ( final PlayerJoinEvent event ) {
		if ( GUIHandler.getInstance ( ).getGUI ( ) != null 
				&& GUIHandler.getInstance ( ).getGUIOpenItem ( ) != null ) {
			SchedulerUtil.runTaskLater ( new Runnable ( ) {
				@Override public void run ( ) {
					ChooserGUIOpenItem item = GUIHandler.getInstance ( ).getGUIOpenItem ( );
					Player           player = event.getPlayer ( );
					
					player.getInventory ( ).clear ( item.getPosition ( ) );
					player.getInventory ( ).setItem ( item.getPosition ( ) , item.toItemStack ( ) );
					player.updateInventory ( );
				}
			} , 15 , BattleRoyaleBungui.getInstance ( ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onClick ( PlayerInteractEvent event ) {
		if ( event.getItem ( ) != null && isOpenItem ( event.getItem ( ) ) ) {
			event.getPlayer ( ).performCommand ( "Bungui" );
			event.setCancelled ( true );
			event.setUseInteractedBlock ( Result.DENY );
			event.setUseItemInHand ( Result.DENY );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onClick ( InventoryClickEvent event ) {
		if ( event.getCurrentItem ( ) != null && isOpenItem ( event.getCurrentItem ( ) ) ) {
			( (Player) event.getWhoClicked ( ) ).performCommand ( "Bungui" );
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onDrop ( PlayerDropItemEvent event ) {
		if ( isOpenItem ( event.getItemDrop ( ).getItemStack ( ) ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onDrag ( InventoryDragEvent event ) {
		if ( isOpenItem ( event.getCursor ( ) ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onMoveItem ( InventoryMoveItemEvent event ) {
		if ( isOpenItem ( event.getItem ( ) ) ) {
			event.setCancelled ( true );
		}
	}
	
	protected boolean isOpenItem ( ItemStack itemstack ) {
		ChooserGUIOpenItem item = GUIHandler.getInstance ( ).getGUIOpenItem ( );
		if ( item != null ) {
			ItemStack open_item = item.toItemStack ( );
			
			if ( Objects.equals ( ItemStackUtil.extractName ( open_item , false ) , 
					ItemStackUtil.extractName ( itemstack , false ) ) 
					&& ItemStackUtil.equalsLore ( open_item , itemstack ) ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}