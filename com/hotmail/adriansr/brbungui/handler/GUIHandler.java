package com.hotmail.adriansr.brbungui.handler;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.adriansr.brbungui.gui.ChooserGUI;
import com.hotmail.adriansr.brbungui.gui.ChooserGUIItem;
import com.hotmail.adriansr.brbungui.gui.ChooserGUIItemUnavailable;
import com.hotmail.adriansr.brbungui.gui.ChooserGUIOpenItem;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.menu.size.ItemMenuSize;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;


/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 01:10 PM
 */
public class GUIHandler extends PluginHandler {
	
	public static final String GUI_CONFIGURATION_FILE_NAME = "BattleRoyaleArenasChooser.yml";
	
	protected static final String            GUI_TITLE_KEY = "title";
	protected static final String             GUI_SIZE_KEY = "size";
	protected static final String            GUI_ITEMS_KEY = "items";
	protected static final String GUI_UNAVAILABLE_ITEM_KEY = "item-unavailable";
	
	protected static final String          GUI_OPEN_ITEM_KEY = "open-item";
	protected static final String   GUI_OPEN_ITEM_ENABLE_KEY = "enable";
	protected static final String GUI_OPEN_ITEM_POSITION_KEY = "position";
	protected static final String GUI_OPEN_ITEM_MATERIAL_KEY = "material";
	protected static final String    GUI_OPEN_ITEM_TITLE_KEY = "title";
	protected static final String     GUI_OPEN_ITEM_LORE_KEY = "lore";
	
	protected static final String     GUI_ITEM_PORT_KEY = "arena-port";
	protected static final String GUI_ITEM_POSITION_KEY = "position";
	protected static final String GUI_ITEM_MATERIAL_KEY = "material";
	protected static final String    GUI_ITEM_TITLE_KEY = "title";
	protected static final String     GUI_ITEM_LORE_KEY = "lore";
	
	protected static final String  GUI_UNAVAILABLE_ITEM_MESSAGE_KEY = "unavailable-message";
	protected static final String GUI_UNAVAILABLE_ITEM_MATERIAL_KEY = GUI_ITEM_MATERIAL_KEY;
	protected static final String         GUI_UNAVAILABLE_TITLE_KEY = GUI_ITEM_TITLE_KEY;
	protected static final String          GUI_UNAVAILABLE_LORE_KEY = GUI_ITEM_LORE_KEY;
	
	public static GUIHandler getInstance ( ) {
		return (GUIHandler) HANDLER_INSTANCES.get ( GUIHandler.class );
	}
	
	protected ChooserGUI                   gui;
	protected ChooserGUIOpenItem gui_open_item;

	public GUIHandler ( BattleRoyaleBungui plugin ) {
		super ( plugin ); load ( );
	}
	
	public ChooserGUI getGUI ( ) {
		return gui;
	}
	
	public ChooserGUIOpenItem getGUIOpenItem ( ) {
		return gui_open_item;
	}
	
	protected void load ( ) {
		// we're saving the default GUI configuration, then server users can use this
		// file as a guide when setting it up.
		File yaml_file = new File ( plugin.getDataFolder ( ) , GUI_CONFIGURATION_FILE_NAME );
		if ( !yaml_file.exists ( ) ) {
			plugin.saveResource ( GUI_CONFIGURATION_FILE_NAME , false );
		}
		
		// and now, we are loading the GUI configuration from the file mentioned above.
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( yaml_file );
		if ( !yaml.isString ( GUI_TITLE_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , 
					"The title for the chooser gui is not set!" , plugin );
		}
		
		if ( !yaml.isInt ( GUI_SIZE_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The size for the chooser gui is not set!" , plugin );
			return;
		}
		
		String      title = StringUtil.translateAlternateColorCodes ( yaml.getString ( GUI_TITLE_KEY , "" ) );
		ItemMenuSize size = ItemMenuSize.fitOf ( yaml.getInt ( GUI_SIZE_KEY ) );
		ChooserGUI    gui = new ChooserGUI  ( title , size );
		
		// loading items.
		ConfigurationSection items = yaml.getConfigurationSection ( GUI_ITEMS_KEY );
		if ( items != null ) {
			for ( String key : items.getKeys ( false ) ) {
				ConfigurationSection item_section = items.getConfigurationSection ( key );
				if ( item_section == null ) {
					continue;
				} else {
					ChooserGUIItem item = loadItem ( item_section );
					int        position = item_section.getInt ( GUI_ITEM_POSITION_KEY );
					
					if ( item == null ) {
						// yes, this mean the item has an invalid configuration.
						continue;
					}
					
					if ( position < 0 ) {
						ConsoleUtil.sendPluginMessage ( ChatColor.RED , "The position for item '" + item_section.getName ( ) 
								+ "' is invalid! (position cannot be less than 0)" , plugin );
						continue;
					} else if ( position >= gui.getSize ( ).getSize ( ) ) {
						ConsoleUtil.sendPluginMessage ( ChatColor.RED , "The position for item '" + item_section.getName ( ) 
								+ "' is invalid! (position cannot be greater the size of your gui)" , plugin );
						continue;
					}
					
					// oh, it was set up correctly!
					gui.setItem ( position , item );
					ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , 
							"Configuration for the item '" + item_section.getName ( ) + "' loaded correctly!" , plugin );
				}
			}
		}
		
		// loading unavailable item.
		ConfigurationSection unavailable_item = yaml.getConfigurationSection ( GUI_UNAVAILABLE_ITEM_KEY );
		if ( unavailable_item != null ) {
			ChooserGUIItemUnavailable item = loadUnavailableItem ( unavailable_item );
			if ( item != null ) {
				gui.setUnavailableItem ( item );
			}
		}
		
		// gui might be null.
		this.gui = gui;
		this.gui.getHandle ( ).registerListener ( plugin );
		
		// loading GUI open item
		ConfigurationSection open_item = yaml.getConfigurationSection ( GUI_OPEN_ITEM_KEY );
		if ( open_item != null && open_item.getBoolean ( GUI_OPEN_ITEM_ENABLE_KEY ) ) {
			gui_open_item = loadOpenItem ( open_item );
		}
	}
	
	protected ChooserGUIItem loadItem ( ConfigurationSection section ) {
		// first of all, we're making sure the configuration is set up.
		if ( !section.isInt ( GUI_ITEM_PORT_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The port of the arena for the item '" + section.getName ( ) + "' is not set!" , plugin );
			return null;
		}
		
		if ( !section.isSet ( GUI_ITEM_POSITION_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The position for the item '" + section.getName ( ) + "' is not set!" , plugin );
			return null;
		}
		
		if ( !section.isSet ( GUI_ITEM_MATERIAL_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The material for the item '" + section.getName ( ) + "' is not set!" , plugin );
			return null;
		}
		
		int             port = section.getInt ( GUI_ITEM_PORT_KEY );
		String         title = StringUtil.translateAlternateColorCodes ( section.getString ( GUI_ITEM_TITLE_KEY , "" ) );
		List < String > lore = StringUtil.translateAlternateColorCodes ( section.getStringList ( GUI_ITEM_LORE_KEY ) );
		
		// material checking
		Material material = EnumReflection.getEnumConstant ( Material.class , section.getString ( GUI_ITEM_MATERIAL_KEY ) );
		if ( material == null ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The material for the item '" + section.getName ( ) + "' is invalid!" , plugin );
			return null;
		}
		
		// port checking
		try {
			new InetSocketAddress ( port );
		} catch ( IllegalArgumentException ex ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The specified port ('" + port + "') for the arena of the item '" + section.getName ( ) + "' is invalid!" , 
					plugin );
			return null;
		}
		
		return new ChooserGUIItem ( port , material , title , lore );
	}
	
	protected ChooserGUIItemUnavailable loadUnavailableItem ( ConfigurationSection section ) {
		// first of all, we're making sure the configuration is set up.
		if ( !section.isSet ( GUI_UNAVAILABLE_ITEM_MESSAGE_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , 
					"The message for the unavailable item is not set, default will be used!" , plugin );
		}
		
		if ( !section.isSet ( GUI_UNAVAILABLE_TITLE_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , 
					"The title for the unavailable item is not set, default will be used!" , plugin );
		}
		
		if ( !section.isSet ( GUI_UNAVAILABLE_ITEM_MATERIAL_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The material for the unavailable item is not set, default unavailable item will be used!" , plugin );
			return null;
		}
		
		String message = section.isSet ( GUI_UNAVAILABLE_ITEM_MESSAGE_KEY ) 
				? StringUtil.translateAlternateColorCodes ( section.getString ( GUI_UNAVAILABLE_ITEM_MESSAGE_KEY , "" ) )
				: ChooserGUIItemUnavailable.DEFAULT.getMessage ( );
		String title = section.isSet ( GUI_UNAVAILABLE_TITLE_KEY ) 
				? StringUtil.translateAlternateColorCodes ( section.getString ( GUI_UNAVAILABLE_TITLE_KEY , "" ) )
				: ChooserGUIItemUnavailable.DEFAULT.getTitle ( );
		List < String > lore = StringUtil.translateAlternateColorCodes ( section.getStringList ( GUI_UNAVAILABLE_LORE_KEY ) );
		
		// material checking
		Material material = EnumReflection.getEnumConstant ( Material.class , section.getString ( GUI_UNAVAILABLE_ITEM_MATERIAL_KEY ) );
		if ( material == null ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The material for the unavailable item is invalid!" , plugin );
			return null;
		}
		
		return new ChooserGUIItemUnavailable ( message , material , title , lore );
	}
	
	protected ChooserGUIOpenItem loadOpenItem ( ConfigurationSection section ) {
		// first of all, we're making sure the configuration is set up.
		if ( !section.isSet ( GUI_OPEN_ITEM_POSITION_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The position for the open item is not set!" , plugin );
			return null;
		}
		
		if ( !section.isSet ( GUI_OPEN_ITEM_MATERIAL_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The material for the open item is not set!" , plugin );
			return null;
		}
		
		if ( !section.isSet ( GUI_OPEN_ITEM_TITLE_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , 
					"The title for the open item is not set!" , plugin );
		}
		
		// position checking
		int position = section.getInt ( GUI_ITEM_POSITION_KEY );
		if ( position < 0 || position > 8 ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The position for the open item must be between 0 and 8!" , plugin );
			return null;
		}
		
		String         title = StringUtil.translateAlternateColorCodes ( section.getString ( GUI_OPEN_ITEM_TITLE_KEY , "" ) );
		List < String > lore = StringUtil.translateAlternateColorCodes ( section.getStringList ( GUI_OPEN_ITEM_LORE_KEY ) );
		
		// material checking
		Material material = EnumReflection.getEnumConstant ( Material.class , section.getString ( GUI_ITEM_MATERIAL_KEY ) );
		if ( material == null ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The material for the open item is invalid!" , plugin );
			return null;
		}
		
		return new ChooserGUIOpenItem ( position , material , title , lore );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}