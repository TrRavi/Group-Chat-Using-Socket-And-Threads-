package com.chat.app.server;

import java.util.logging.Logger;


public class InitServer {
	private static final Logger LOGGER = Logger.getLogger( InitServer.class.getName() );
	public static void main(String [] args){
		int port = 8081;
		Server server = new Server(port);
		server.start();
		
		
	}

}
