package com.hotmail.adriansr.brbungui.arena;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 21 August, 2020 / 02:09 PM
 */
public class ServerPing {
	
	protected static final int DEFAULT_TIMEOUT = 7000;

	private InetSocketAddress host;
	private int            timeout;
	
	public ServerPing ( InetSocketAddress host , int timeout ) {
		this.host    = host;
		this.timeout = timeout;
	}
	
	public ServerPing ( InetSocketAddress host ) {
		this ( host , DEFAULT_TIMEOUT );
	}

	public InetSocketAddress getAddress ( ) {
		return host;
	}

	public int getTimeout ( ) {
		return timeout;
	}

    public StatusResponse fetchData ( ) throws IOException {
    	Socket socket = new Socket ( );
    	socket.setSoTimeout ( timeout );
    	socket.connect ( host , timeout );
    	
		DataOutputStream   data_output_stream = new DataOutputStream ( socket.getOutputStream ( ) );
		InputStream              input_stream = socket.getInputStream ( );
		InputStreamReader input_stream_reader = new InputStreamReader ( input_stream , Charset.forName ( "UTF-16BE" ) );
		
		data_output_stream.write ( new byte [ ] { (byte) 0xFE , (byte) 0x01 } );
		
		int packet_id = input_stream.read ( );
		if ( packet_id == -1 || packet_id != 0xFF ) {
			socket.close ( );
			throw new IOException ( 
					packet_id != 0xFF ? "invalid packet id (" + packet_id + ")" : "premature end of stream!" );
		}
		
		int length = input_stream_reader.read ( );
		if ( length <= 0 ) {
			socket.close ( );
			throw new IOException ( "premature end of stream!" );
		}
		
		char [ ] chars = new char [ length ];
		if ( input_stream_reader.read ( chars , 0 , length ) != length ) {
			socket.close ( );
			throw new IOException ( "premature end of stream!" );
		}
		
		StatusResponse response = null;
		String  response_string = new String ( chars );
		if ( response_string.startsWith ( "§" ) ) {
			String data [ ] = response_string.split ( "\0" );
			response        = new StatusResponse ( data [ 3 ] , Integer.parseInt ( data [ 4 ] ) , 
					Integer.parseInt ( data [ 5 ] ) );
		} else {
			String data [ ] = response_string.split ( "§" );
			response        = new StatusResponse ( data [ 0 ] , Integer.parseInt ( data [ 1 ] ) , 
					Integer.parseInt ( data [ 2 ] ) );
		}
		
		socket.close ( );
		return response;
    }
}