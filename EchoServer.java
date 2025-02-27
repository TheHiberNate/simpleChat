// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  final private String key = "loginID";
  
  ChatIF serverUI;
    
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String message = (String) msg;
    if (!message.startsWith("#")) {
    	String name = (String) client.getInfo(key);
        this.sendToAllClients(name + " > " + msg);
        System.out.println("Message received: " + msg + " from " + name);
    }
    
    else if (message.equals("#login")) {
    	try {
			client.sendToClient("Command #login is not authorized");
			client.close();
		} catch (IOException e) {
			serverUI.display("Error sending message to client");
		}
    }
    
    else {
    	String username = "";
    	System.out.println("Message received: " + msg + " from " + client);
    	for (int i = 7; i < message.length(); i++) {
    		username += message.charAt(i);
    	}
    	System.out.println(username + " has logged on.");
    	client.setInfo(key, username);
    }
  }
  
  /**
   * This method handles all data coming from the server console
   * @param message
   */
  public void handleMessageFromServerUI (String message) {
	  if (message.startsWith("#")) {
		  serverCommands(message);
	  }
	  else {
		  sendToAllClients("SERVER MSG> " + message);
		  serverUI.display(message); 
	  }
  }
    
  /**
   * Method responsible of handling commands specified by the server user
   * @param message
   */
  private void serverCommands(String command) {
	  if (command.equals("#quit")) {
		  serverUI.display("Server will shut down");
		  quit();
	  }
	  
	  else if (command.equals("#stop")) {
//		  serverUI.display("Server will stop listening");
		  stopListening();
	  }
	  
	  else if (command.equals("#close")) {
		  serverUI.display("Server will shut down");
		  try {
			close();
		  } catch (IOException e) {
			serverUI.display("Error closing server");
		  }
	  }
	  
	  else if (command.startsWith("#setport")) {
		  serverUI.display("Changing port number");
		  if (isListening()) {
			  serverUI.display("Server is running, cannot change port");
		  }
		  else {
			  String portNum = "";
			  for (int i = 9; i < command.length(); i++) {
				  portNum += command.charAt(i);
			  }
			  try {
			  setPort(Integer.parseInt(portNum));
			  serverUI.display("Port has been set to " + portNum);
			  } 
			  catch (NumberFormatException nef) {
				  serverUI.display("Invalid valid number");
			  }
		  }
	  }
	  
	  else if (command.equals("#start")) {
		  if (isListening()) {
			  serverUI.display("Server is already listening to connections");
		  }
		  else {
			  try {
				listen();
				serverUI.display("Server is now listening to connections");
			} catch (IOException e) {
				serverUI.display("Could not listen for connections");
			} 
		  }
	  }
	  
	  else if (command.equals("#getport")) {
		  serverUI.display("Port Number is: " + getPort());
	  }
	  
	  else {
		 serverUI.display(command + " is not a valid command!");
	  }
  }
  
  /**
   * Terminates server
   */
  private void quit() {
	  try {
		  close();
	  } catch (IOException e) {}
	  System.exit(0);
  }


/**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    serverUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	 serverUI.display("Server has stopped listening for connections.");
  }
  
  /**
   * Implemented hook method called each time a new client connection is
   * accepted. 
   * 
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("Client has connected to server");
  }

  /**
   * Implemented hook method called each time a client disconnects.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display("Client has disconnected from server");
  }
  
  /**
   * Implemented hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  @Override
  synchronized protected void clientException(
		    ConnectionToClient client, Throwable exception) {
	  serverUI.display("Client has disconnected from server");
  } 
}
  

//End of EchoServer class
