import java.rmi.*;

public interface Participant extends Remote {
	public void forward(String msg)  throws RemoteException;
}
