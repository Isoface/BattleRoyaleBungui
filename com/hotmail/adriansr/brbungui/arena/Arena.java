package com.hotmail.adriansr.brbungui.arena;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Thursday 20 August, 2020 / 12:56 PM
 */
public class Arena {

	protected final String name;
	protected final int    port;
	protected ServerPing   ping;
	
	public Arena ( String name , int port ) {
		this.name = name;
		this.port = port;
	}
	
	public String getName ( ) {
		return name;
	}

	public int getPort ( ) {
		return port;
	}
	
	public ServerPing getPing ( ) {
		return ping;
	}
	
	public void setPing ( ServerPing ping ) {
		this.ping = ping;
	}
}