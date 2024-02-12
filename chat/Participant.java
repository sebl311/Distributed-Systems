import java.rmi.*;

public interface Participant extends Remote {
	public void send(String msg)  throws RemoteException;
    public void add_Participant(Participant p)  throws RemoteException;
    public void remove_Participant(Participant p)  throws RemoteException;
}
