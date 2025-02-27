// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  
  /**
   * Client loginID
   */
  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
  
  /**
   * Getter & Setter methods for private instance variable 
   */
  public String getLoginID() {
	  return loginID;
  }
  
  public void setLoginID(String loginID) {
	  this.loginID = loginID;
  }
  
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.startsWith("#")) {
    		clientCommands(message);
    	}
    	else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * Method responsible of handling commands specified by the user
   * @param command
   */
  private void clientCommands(String command) {
	  if (command.equals("#quit")) {
		  clientUI.display("Client will quit");
		  quit();
	  } 

	  else if (command.equals("#logoff")) {
		  clientUI.display("Client will log off");
		  try {
			if (!this.isConnected()) {
				clientUI.display("Client is already logged off");
			}
			else {
				closeConnection();
			}
		} catch (IOException e) {
			clientUI.display("Error closing connection");
		}  
	  } 

	  else if (command.startsWith("#sethost")) {
		  clientUI.display("Client is changing host name");
		  if (this.isConnected()) {
			 clientUI.display("Currently connected, cannot change host");
		  }
		  else {
			  String hostName = "";
			  for (int i = 9; i < command.length(); i++) {
				  hostName += command.charAt(i);
			  }
			  setHost(hostName);
			  clientUI.display("Host has been set to " + hostName);
		  }
	  }

	  else if (command.startsWith("#setport")) {
		  clientUI.display("Client is changing port number");
		  if (this.isConnected()) {
				 clientUI.display("Currently connected, cannot change port number");
			  }
		  else {
			  String portNum = "";
			  for (int i = 9; i < command.length(); i++) {
				  portNum += command.charAt(i);
			  }
			  try {
				  setPort(Integer.parseInt(portNum));
				  clientUI.display("Port has been set to " + portNum);
			  }
			  catch (NumberFormatException nfe) {
				  clientUI.display("Invalid number");
			  }
			  
		  }
	 }

	  else if (command.startsWith("#login")) {
		  clientUI.display("Client will log in");
		  try {
			  if (this.isConnected()) {
				clientUI.display("Client is already logged in");
				sendToServer("#login");
			  }
			  else {
				  openConnection();
			  }
		  } catch (IOException e) {
			  clientUI.display("Error opening connection");
		  }  
	  }

	  else if (command.equals("#gethost")) {
		  clientUI.display("Host is: " + getHost());
	  }

	  else if (command.equals("#getport")) {
		  clientUI.display("Port Number is: " + getPort());
	  }

	  else {
		  clientUI.display(command + " is not a valid command!"); 
	  }
  }


/**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
	/**
	 * Implemented Hook method called after the connection has been closed
	 */
	protected void connectionClosed() {
		clientUI.display("Connection is closed");
	}

	/**
	 * Implemented Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. 
	 * 
	 * @param exception
	 */
	protected void connectionException(Exception exception) {
		clientUI.display("Server has shut down");
		System.exit(0);
	}
	
	/**
	 * Implemented hook method called after a connection has been established. 
	 */
	protected void connectionEstablished() {
		try {
			sendToServer("#login " + loginID);
			clientUI.display(loginID + " has logged on.");
			clientUI.display("Welcome! You can start chatting!");
		} catch (IOException e) {
			clientUI.display("Error client login");
		}
	}
	
}
//End of ChatClient class
