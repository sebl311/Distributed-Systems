import java.rmi.*;

public  class ParticipantImpl implements Participant {

	private Vector<Participant> participants;
 
	public ChatroomImpl() {
		participants=new Vector<Participant>();
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