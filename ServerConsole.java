import java.util.Scanner;

import common.ChatIF;

/**
 * Class responsible of the server console
 * @author Nathan Gawargy (300232268)
 *
 */
public class ServerConsole implements ChatIF{
	
	//Class variables *************************************************
	/**
	* The default port to listen on.
	*/
	final public static int DEFAULT_PORT = 5555;

	//Instance variables *************************************************
	EchoServer server;
	Scanner fromConsole;
	
	/**
	 * Constructs an instance of EchoServer with a serverUI.
	 * Starts listening for connections.
	 * 
	 * @param port
	 */
	public ServerConsole (int port) {
		server = new EchoServer(port, this);
	    try 
	    {
	      server.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
		fromConsole = new Scanner(System.in);
	}
	
	//Instance Methods *************************************************
	/**
	 * Method to display messages on server console
	 * 
	 * @param message
	 */
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	/**
	 * Method responsible of receiving the messages typed on the server console.
	 * Calls handleMessageFromServerUI to know what to do with the message.
	 */
	public void accept() {
		try {
			String message;
			while(true) {
				message = fromConsole.nextLine();
				server.handleMessageFromServerUI(message);
			}
		}
		catch (Exception ex) {
			System.out.println("Unexpected error while reading from the console!");
		}
	}
	
	 //Class methods ***************************************************
	  /**
	   * This method is responsible for the creation of 
	   * the server instance (there is no UI in this phase).
	   *
	   * @param args[0] The port number to listen on.  Defaults to 5555 
	   *          if no argument is entered.
	   */
	public static void main(String[] args) 
	{
		int port = 0; //Port to listen on
		
		try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole sv = new ServerConsole(port);
	    sv.accept();
	}
}
