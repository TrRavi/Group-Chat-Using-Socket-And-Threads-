package com.chat.app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class Server extends Thread {
	private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
	private final int serverPort ;
	
	private Set<String> userNames = new HashSet<>();
	private Set<ServerWorker> serverWorkerList = new HashSet<ServerWorker>();

	

	

	public Server(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void run(){
		try{
			ServerSocket serverSocket = new ServerSocket(serverPort);
			while(true){
				LOGGER.info("about to accept connection..");
				Socket clientSocket = serverSocket.accept();
				LOGGER.info("Connection established.."+clientSocket);
				ServerWorker sw = new ServerWorker(this,clientSocket);
				serverWorkerList.add(sw);
				sw.start();
			}
		}catch(Exception e){
			LOGGER.warning("Error = "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public Set<ServerWorker> getServerWorkerList() {
		return serverWorkerList;
	}
	
	//my code 
	
	void broadcast(String message, ServerWorker excludeWorker) {
        for (ServerWorker worker : serverWorkerList) {
            if (worker != excludeWorker) {
            	try {
            		System.out.println("Here it is ");
					worker.sendMessage(message);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
            }
        }
    }
	
	void addUserName(String userName) {
        userNames.add(userName);
    }
	
	boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
	
	Set<String> getUserNames() {
        return this.userNames;
    }

	public void removeUser(String userName, ServerWorker serverWorker) {
		boolean removed = userNames.remove(userName);
        if (removed) {
        	serverWorkerList.remove(serverWorker);
            System.out.println("The user " + userName + " quitted");
        }
		
	}
}
