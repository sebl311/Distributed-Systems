
import java.rmi.*;

public  class HelloImpl implements Hello {

	private String message;
 
	public HelloImpl(String s) {
		message = s ;
	}

	public String sayHello() throws RemoteException {
		System.out.println(message);
		return message ;
	}
}

