import java.rmi.*;
import java.util.Vector;

public  class ParticipantImpl implements Participant {

	private Vector<Participant> participants;
	private String name;
	private Chatroom chatroom;

	private ChatClient gui; // Reference to the GUI
	public ParticipantImpl(String name, Chatroom c, ChatClient gui) {
		this.chatroom=c;
		this.name=name;
		this.gui = gui; // Initialize the GUI reference
		participants=new Vector<Participant>();
	}

	public void receive(String msg)  throws RemoteException{
		System.out.println(msg);
		gui.displayMessage(msg);
	}
	public String getName()  throws RemoteException{
		return name;
	}
    public void add_Participant(Participant p)  throws RemoteException{
		participants.add(p);
		System.out.println(p.getName() + " joined the conversation");
		gui.displayMessage(p.getName() + " joined the conversation");
	}

    public void remove_Participant(Participant p)  throws RemoteException{
			System.out.println(p.getName() + " left the conversation");
			gui.displayMessage(p.getName() + " left the conversation");
			participants.remove(p);
	}

	public void receiveHistory(Vector<String> history) throws RemoteException{
		for(String msg :history){
			System.out.println(msg);
			gui.displayMessage(msg);
		}
	}

	public void send(String msg) throws RemoteException{
		msg= name +": "+msg;
		try {
			chatroom.receive(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		for(Participant p:participants){
			try {
				p.receive(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}