import java.rmi.*;
import java.util.Vector;

public interface Chatroom extends Remote {
	public Vector<Participant> join(Participant p) throws RemoteException;
    public void leave(Participant p) throws RemoteException;
    //public void send(String msg) throws RemoteException;  //send to the server so it can be saved in the history
    //public String getHistory() throws RemoteException;
}
