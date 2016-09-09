package assign;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class tramServer implements tracking{
	
	public static final int count =5;
	private long tramIdpool;
	private HashMap trams;
	//Singleton 
	private static tramServer SingletonTram;
	private static tramServer getSingletonTram(int port){
		if(SingletonTram == null){
			SingletonTram = new tramServer(port);
		}
		return SingletonTram;
	}
	
	public static final String SERVERNAME = "tramServer"; 
    private String url;
    
    //Routes and Stops 
    private int[][] trStops = new int[5][];{
	trStops[0] = new int[]{1,1,2,3,4,5};
	trStops[1] = new int[]{96,23,24,2,34,22};
	trStops[2] = new int[]{101,123,11,22,34,5,4,7};
	trStops[3] = new int[]{109,88,87,85,80,9,7,2,1};
	trStops[4] = new int[]{112,110,123,11,22,34,33,29,4};
    } 
        
    //Date and Time
    SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	    
	public tramServer(int port){
		//to store tram id and its route
	    trams = new HashMap<Integer, Integer>();
	    tramIdpool=0;
	    
		try {
			String host = "localhost";
			url = "rmi://" + host + "/"+ STUDENTID +"/" + SERVERNAME + "/";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*public Message startTram(){
		long tramId;
		short procid=0;
		short status=0;
		if(trams.isEmpty())
		{
		String  tramData = "0";
		RPCMessage m = new RPCMessage(RPCMessage.REPLY,0,0,0,procid,status,tramData);
		Message mm= new Message();
		mm.Marshall(m);
		return mm;
		}
		else if (trams.containsKey(getClass()));
		{
			String
		}
	}*/
	public Message retrieveNextStop(Message mmsg) throws RemoteException{
		
		//unmarshall the message
		RPCMessage msg = mmsg.unMarshal();
		
		//get data
		String data = msg.getdata();
		String[] datum = data.split(",");
		
		//copy Id data
		//short type = msg.getType();
		long transactionId = msg.getTransactionId();
		long RPCId = msg.getRPCId();
		long reqid = msg.requestId();
		short procid = msg.procedureId();
		
		//set status
		short status =1;
		
		//get stops
		int routeId = Integer.parseInt(datum[0]);
		int currStopId = Integer.parseInt(datum[1]);
		int perstopId = Integer.parseInt(datum[2]);
		int next = -1;
		String csv_data="";
		
		//retrieve next stop
		if (procid == 25 && msg.getType()==0){
			
			for(int k=0; k<5;k++){
				if(routeId == trStops[k][0]){
					int length = trStops[k].length;
					for (int i=1;i<length;i++){
						if(currStopId == trStops[k][i]){
							
							if(i == length-1)
							{
								next = trStops[k][length-2];
							}
							else if(i == 1)
							{
								next = trStops[k][i+1];
							}
							else if(perstopId == trStops[k][i+1])
							{
								next = trStops[k][i-1];
							}
							else if(perstopId == trStops[k][i-1])
							{
								next = trStops[k][i+1];
							}
						}
					}
				}
				csv_data = String.valueOf(next);
				//set status for complete transaction
				status = 0;
				
			}
			System.out.println("Tram is running in route "+routeId+" having current Stop "+
					currStopId + " with previous stop is "+ perstopId+" at "+date.format(System.currentTimeMillis()));	
		}
		//save into RPCMessage and marshall it 
		RPCMessage message = new RPCMessage(RPCMessage.REPLY,transactionId,RPCId,reqid,procid,status,csv_data);
		
		Message mmessage = new Message();
		mmessage.Marshall(message);
		return mmessage;
	}
	public Message updateTramLocation(Message mmsg) throws RemoteException{
		//unmarshall
		RPCMessage rpcMessage = mmsg.unMarshal();
		
		//get data
		String data = rpcMessage.getdata();
		String[] datum = data.split(",");
		int routeId = Integer.parseInt(datum[0]);
		int tramId = Integer.parseInt(datum[1]);
		int nextStop = Integer.parseInt(datum[2]);
		long transactionId = rpcMessage.getTransactionId();
		long RPCId = rpcMessage.getRPCId();
		long reqid = rpcMessage.requestId();
		short procid = rpcMessage.procedureId();
		short status =1;
		
		if (procid == 50 && rpcMessage.getType()==0){
			System.out.println("Success");
			
			trams.put(tramId, routeId);
			
			data = "";
			System.out.println("Tram " + tramId + " in route " + routeId + " has arrived at stop " + nextStop);
			
		}
		else{
			System.out.println("Unable to update at "+ date.format(date.format(System.currentTimeMillis())));
		}
		RPCMessage mesage = new RPCMessage(RPCMessage.REPLY,transactionId,RPCId,reqid,procid,status,data);
		Message mmessage = new Message();
		mmessage.Marshall(mesage);
		return mmessage;
	}
	public static void main(String[] args){
		try {
			int port = Integer.parseInt(STUDENTID.substring(STUDENTID.length() - 4, STUDENTID.length())); 
			tramServer server = new tramServer(port);
			tracking stub = (tracking) UnicastRemoteObject.exportObject((tracking)server, 0);
			Registry registry = LocateRegistry.createRegistry(port);

			registry.bind(server.getURL(), stub);
			System.out.println("Server bound to: " + server.getURL());
		} catch (RemoteException ex) {
			System.err.println("Couldn't contact rmiregistry.");
			ex.printStackTrace(); 
			System.exit(1);		
		} catch (NumberFormatException ex) {
		    ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public String getURL() {
	    	return url;
	    }
	

}
