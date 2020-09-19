package com.chat.app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class ServerWorker extends Thread {
	private static final Logger LOGGER = Logger.getLogger( ServerWorker.class.getName() );
	private final Socket clientSocket;
	private String loginUser = null;
	private final Server server;
	private OutputStream outputStream;
	private PrintWriter writer;

	public ServerWorker(Server server,Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		
	}
	
	@Override
	public void run(){
		handleClientSocket();
		
	}
	
	private void handleClientSocket(){
		try {
			InputStream inputStream = clientSocket.getInputStream();
			this.outputStream = clientSocket.getOutputStream();
			writer = new PrintWriter(outputStream, true);
			printUsers();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String userName = reader.readLine();
			server.addUserName(userName);
			String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);
            String clientMessage;
            
            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
 
            } while (!clientMessage.equals("bye"));
 
            server.removeUser(userName, this);
            //socket.close();
 
            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);
			
			
			/*String line;
			while((line = reader.readLine()) != null){
				if("quit".equalsIgnoreCase(line)){
					break;
				}else{
					handleUser(outputStream,line);
				}
				//outputStream.write(("you entered \n"+line+"\n").getBytes());
			}*/
			
			
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("error = "+e.getMessage());
			e.printStackTrace();
		}
		
		
	}

	private void handleUser(OutputStream outputStream,String name) {
		
		String msg = "Hello "+name;
		loginUser = name;
		try {
			writer.println(msg);
			LOGGER.info("User "+name+" entered");
			
			//for sending message to other user about current user login
			String message = "\n"+name+" online to chat";
			Set<ServerWorker> workerList = server.getServerWorkerList();
			for(ServerWorker worker:workerList){
				if(!loginUser.equals(worker.getLogin())){
					worker.sendMessage(message);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	 void sendMessage(String message) throws IOException {
		//outputStream.write(message.getBytes());
		writer.println(message);
		
	}
	
	public String getLogin(){
		return loginUser;
	}
	void printUsers() {
		//System.out.println("going to write");
        if (server.hasUsers()) {
            writer.println("\nConnected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }
	
	

}
