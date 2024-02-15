import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;


public class ChatClient {
    public static void main(String [] args) {
	
        try {
          if (args.length < 4) {
           System.out.println("Usage: java ChatClient <Client name> <Client Port> <rmiregistry host> <rmiregistry port>");
           return;}
    
        String name= args[0];
        int clientPort= Integer.parseInt(args[1]);
        String host = args[2];
        int port = Integer.parseInt(args[3]);

    
        Registry registry = LocateRegistry.getRegistry(host, port); 
        Chatroom c = (Chatroom) registry.lookup("Chatservice");
    
        Participant p= new ParticipantImpl(name, c);
        Participant p_stub = (Participant) UnicastRemoteObject.exportObject(p, clientPort);
        
        
            
        // Remote method invocation
        c.join(p_stub);
        Thread.sleep(10000);
        p_stub.send("Hi");
        c.leave(p_stub);

        System.exit(0);
        
    
        } catch (Exception e)  {
    		System.err.println("Error on client: " + e);
            e.printStackTrace();
        }
      }
}
