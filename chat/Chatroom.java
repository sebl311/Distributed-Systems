import java.rmi.*;

public interface Chatroom extends Remote {
	public Vector<Participant> join(Participant p)  throws RemoteException;
    public void leave(Participant p)  throws RemoteException;
    //public void send(String msg);
    //public String getHistory() throws RemoteException;
}
