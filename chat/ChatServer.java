import java.rmi.server.*; 
import java.rmi.registry.*;

public class ChatServer {

  public static void  main(String [] args) {
	  try {
		  // Create a Hello remote object
	    Chatroom c = new ChatroomImpl();
	    Chatroom c_stub = (Chatroom) UnicastRemoteObject.exportObject(c, 0);

	    // Register the remote object in RMI registry with a given identifier
	    Registry registry = null;
	    if (args.length>0)
		    registry= LocateRegistry.getRegistry(Integer.parseInt(args[0])); 
	    else
		    registry = LocateRegistry.getRegistry();
	    registry.rebind("Chatservice", c_stub);

	    System.out.println ("Server ready");

	  } catch (Exception e) {
		  System.err.println("Error on server :" + e) ;
		  e.printStackTrace();
	  }
  }
}
