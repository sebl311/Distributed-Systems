import java.rmi.*;
import java.util.Vector;

public  class ParticipantImpl implements Participant {

	private Vector<Participant> participants;
	private String name;
	private Chatroom chatroom;
 
	public ParticipantImpl(String name, Chatroom c) {
		this.chatroom=c;
		this.name=name;
		participants=new Vector<Participant>();
	}

	public void receive(String msg)  throws RemoteException{
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

	public void receiveHistory(Vector<String> history) throws RemoteException{
		for(String msg :history){
			System.out.println(msg);
		}
	}

	public void send(String msg) throws RemoteException{
		msg= name +": "+msg;
		chatroom.receive(msg);
		for(Participant p:participants){
			p.receive(msg);
		}
	}
}