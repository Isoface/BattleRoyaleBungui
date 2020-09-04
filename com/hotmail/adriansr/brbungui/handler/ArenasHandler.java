package com.hotmail.adriansr.brbungui.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.hotmail.adriansr.brbungui.arena.Arena;
import com.hotmail.adriansr.brbungui.arena.ServerPing;
import com.hotmail.adriansr.brbungui.main.BattleRoyaleBungui;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.bungeecord.MessagingUtil;
import com.hotmail.adriansr.core.util.bungeecord.ReadUtil;
import com.hotmail.adriansr.core.util.bungeecord.Written;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 12:19 PM
 */
public class ArenasHandler extends PluginHandler implements PluginMessageListener , Listener {
	
	protected static final String PLUGIN_MESSAGES_CHANNEL_NAME = "BungeeCord";
	protected static final String   REQUEST_SERVER_IP_ARGUMENT = "ServerIP";
	
	public    static final String   ARENAS_CONFIGURATION_FILE_NAME = "BattleRoyaleArenas.yml";
	protected static final String ARENAS_CONFIGURATION_SECTION_KEY = "arenas";
	protected static final String                   ARENA_NAME_KEY = "name";
	protected static final String                   ARENA_PORT_KEY = "port";
	
	public static ArenasHandler getInstance ( ) {
		return (ArenasHandler) HANDLER_INSTANCES.get ( ArenasHandler.class );
	}
	
	protected final Map < Integer , Arena > arenas = new HashMap < > ( );

	public ArenasHandler ( BattleRoyaleBungui plugin ) {
		super ( plugin ); 
		
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
		plugin.getServer ( ).getMessenger ( ).registerOutgoingPluginChannel ( plugin , PLUGIN_MESSAGES_CHANNEL_NAME );
		plugin.getServer ( ).getMessenger ( ).registerIncomingPluginChannel ( plugin , PLUGIN_MESSAGES_CHANNEL_NAME , this );
		
		// loading ServerPing for arenas when server starts.
		if ( Bukkit.getOnlinePlayers ( ).size ( ) > 0 ) {
			loadPings ( );
		}
		
		// loading arenas configuration.
		load ( );
	}
	
	public Map < Integer , Arena > getArenas ( ) {
		return Collections.unmodifiableMap ( arenas );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onJoin ( PlayerJoinEvent event ) {
		if ( arenas.values ( ).stream ( )
				.filter ( arena -> ( arena.getPing ( ) == null ) ).findAny ( ).isPresent ( ) ) {
			// loading ServerPing for arenas when a player joins.
			loadPings ( );
		}
	}
	
	@Override
	public void onPluginMessageReceived ( String channel , Player player , byte [ ] message ) {
		if ( !PLUGIN_MESSAGES_CHANNEL_NAME.equals ( channel ) ) {
			return;
		}
		
		Object [ ] response = ReadUtil.read ( message );
		if ( response.length == 0 ) {
			return;
		}
		
		String argument = (String) response [ 0 ];
		if ( !REQUEST_SERVER_IP_ARGUMENT.equals ( argument ) ) {
			return;
		}
		
		String server_name = (String) response [ 1 ];
		String   server_ip = (String) response [ 2 ];
		int    server_port = (int) response [ 3 ];
		
		if ( arenas.containsKey ( server_port ) ) {
			Arena arena = arenas.get ( server_port );
			if ( arena.getPing ( ) == null ) {
				if ( server_name.equals ( arena.getName ( ) ) ) {
					arena.setPing ( new ServerPing ( new InetSocketAddress ( server_ip , server_port ) ) );
				} else {
					ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
							"The arena " + arena.getName ( ) + " and port " + server_port + " doesn't match!" , plugin );
//					ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , 
//							"The specified server name for port " + server_port + " doesn't match, fixing it..." , plugin );
//					
//					arena = new Arena ( server_name , server_port );
//					arena.setPing ( new ServerPing ( new InetSocketAddress ( server_ip , server_port ) ) );
//					
//					arenas.replace ( server_port , arena );
				}
			}
		}
	}
	
	/**
	 * Load {@link ServerPing} for arenas.
	 */
	protected void loadPings ( ) {
		SchedulerUtil.runTaskLater ( new Runnable ( ) {
			@Override public void run ( ) {
				for ( Arena arena : arenas.values ( ) ) {
					try {
						MessagingUtil.sendPluginMessage ( plugin , new Written ( )
								.writeUTF ( REQUEST_SERVER_IP_ARGUMENT )
								.writeUTF ( arena.getName ( ) ) );
					} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | IOException ex ) {
						ex.printStackTrace ( );
					}
				}
			}
		} , 60 , plugin );
	}
	
	/**
	 * Load arenas from configuration file {@link #ARENAS_CONFIGURATION_FILE_NAME}.
	 */
	protected void load ( ) {
		// we're saving the default arenas configuration, then server users can use this
		// file as a guide when setting it up.
		File yaml_file = new File ( plugin.getDataFolder ( ) , ARENAS_CONFIGURATION_FILE_NAME );
		if ( !yaml_file.exists ( ) ) {
			plugin.saveResource ( ARENAS_CONFIGURATION_FILE_NAME , false );
		}
		
		// and now, we are loading the arenas configuration from the file mentioned above.
		YamlConfiguration      yaml = YamlConfiguration.loadConfiguration ( yaml_file );
		ConfigurationSection arenas = yaml.getConfigurationSection ( ARENAS_CONFIGURATION_SECTION_KEY );
		if ( arenas != null ) {
			for ( String key : arenas.getKeys ( false ) ) {
				ConfigurationSection arena = arenas.getConfigurationSection ( key );
				if ( arena != null ) {
					loadArena ( arena );
				}
			}
		}
	}
	
	/**
	 * Loads an {@link Arena} from the provided {@code section}.
	 * <p>
	 * Also, this method maps the parsed arena to {@link #arenas}.
	 * <p>
	 * @param section the section to parse.
	 */
	protected void loadArena ( ConfigurationSection section ) {
		// first of all, we're making sure the configuration is set up
		if ( !section.isString ( ARENA_NAME_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The name for the arena '" + section.getName ( ) + "' is not set!" , plugin );
			return;
		}
		
		if ( !section.isInt ( ARENA_PORT_KEY ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The port for the arena '" + section.getName ( ) + "' is not set!" , plugin );
			return;
		}
		
		String  name = section.getString ( ARENA_NAME_KEY );
		int     port = section.getInt ( ARENA_PORT_KEY );
		
		// arena name checking
		if ( StringUtil.isBlank ( name ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The name for the arena '" + section.getName ( ) + "' cannot be empty/blank!" , plugin );
			return;
		}
		
		// arena port checking
		try {
			new InetSocketAddress ( port );
		} catch ( IllegalArgumentException ex ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"(Fatal) The specified port ('" + port + "') for the arena '" + section.getName ( ) + "' is invalid!" , 
					plugin );
			return;
		}
		
		// oh, it was set up correctly.
		arenas.put ( port , new Arena ( name , port ) );
		ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , 
				"Configuration for the arena '" + section.getName ( ) + "' loaded correctly!" , plugin );
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}