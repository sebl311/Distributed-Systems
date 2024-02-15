
import java.rmi.*;
import java.util.Vector;

public  class ChatroomImpl implements Chatroom {

	private Vector<Participant> participants;
    private Vector<String> history;
 
	public ChatroomImpl() {
		participants=new Vector<Participant>();
        history=new Vector<String>();
	}

	public Vector<Participant> join(Participant p)  throws RemoteException {
		for(Participant p_i:participants){
			p.add_Participant(p_i);
		}
		participants.add(p);
		for(Participant p_i:participants){
			p_i.add_Participant(p);
		}
		System.out.println("Participant joined the conversation");
		p.receiveHistory(history);
        return participants;
	}

    public void leave(Participant p)  throws RemoteException {
		participants.remove(p);
		for(Participant p_i:participants){
			p_i.remove_Participant(p);
		}
		System.out.println("Participant left the conversation");
	}

	public void receive(String msg){
		System.out.println(msg);
		history.add(msg);
	}
}