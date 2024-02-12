
import java.rmi.*;

public  class ChatroomImpl implements Chatroom {

	private Vector<Participant> participants;
    private Vector<String> history;
 
	public ChatroomImpl() {
		participants=new Vector<Participant>();
        history=new Vector<String>();
	}

	public Vector<Participant> join(Participant p)  throws RemoteException {
		participants.add(p);
        for(String s: history){
            p.send(s);
        }
        return participants;
	}

    public void leave(Participant p)  throws RemoteException {
		participants.remove(p);
	}
}