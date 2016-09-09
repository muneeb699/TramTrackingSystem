package assign;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface tracking extends Remote{
	public static final String STUDENTID = "3560544";
	public Message retrieveNextStop(Message m) throws RemoteException;
	public Message updateTramLocation(Message Message) throws RemoteException;
	//public RPCMessage startTram() throws RemoteException;
}
