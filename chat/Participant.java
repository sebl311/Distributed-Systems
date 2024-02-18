import java.rmi.*;
import java.util.Vector;

public interface Participant extends Remote {
	public void receive(String msg)  throws RemoteException;

  public String getName()  throws RemoteException;
    public void receiveHistory(Vector<String> history) throws RemoteException;
    public void add_Participant(Participant p)  throws RemoteException;
    public void remove_Participant(Participant p)  throws RemoteException;
    public void send(String msg) throws RemoteException;
}
