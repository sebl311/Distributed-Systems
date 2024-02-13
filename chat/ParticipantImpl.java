import java.rmi.*;
import java.util.Vector;

public  class ParticipantImpl implements Participant {

	private Vector<Participant> participants;
	private String name;
 
	public ParticipantImpl(String name) {
		this.name=name;
		participants=new Vector<Participant>();
	}

	public void send(String msg)  throws RemoteException{
		System.out.println(msg);
	}

    public void add_Participant(Participant p)  throws RemoteException{
		participants.add(p);
		System.out.println("Participant joined the conversation");
	}

    public void remove_Participant(Participant p)  throws RemoteException{
		participants.remove(p);
		System.out.println("Participant left the conversation");
	}
}