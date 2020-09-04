package com.hotmail.adriansr.brbungui.arena;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 21 August, 2020 / 02:12 PM
 */
public class StatusResponse {
	
	/**
	 * Represents a failure status response.
	 */
	public static final StatusResponse FAILURE = new StatusResponse ( null , -1 , -1 );

	protected final String        motd;
	protected final int online_players;
	protected final int    max_players;
	
	public StatusResponse ( String motd , int online_players , int max_players ) {
		this.motd           = motd;
		this.online_players = online_players;
		this.max_players    = max_players;
	}
	
	public String getMotd ( ) {
		return motd;
	}

	public int getOnlinePlayers ( ) {
		return online_players;
	}

	public int getMaxPlayers ( ) {
		return max_players;
	}
}