package assign;


import java.rmi.NotBoundException; 
import java.rmi.Remote; 
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class tramClient {
	
	private static final String tramRoutes[] = new String[]{"1","96","101","109","112"};
	private static AtomicLong tramIdPool = new AtomicLong();
	private static long RPCId = 0; /* Globally unique identifier */
	long TransactionId=0; /* transaction id */
	static long RequestIdPool=0; /* Client request message counter */
	
	public static String tramId(){
		return String.valueOf(tramIdPool.getAndIncrement());
	}
	static long tramId = Long.valueOf(tramClient.tramId()).longValue();
	public static void main(String[] args) 
	{
	
	short procedureIdRequest1=25; /* e.g.(1,2,3,4) */
	short procedureIdRequest2=50;
	short status=0;
	String csv_data; /* data as comma separated values*/
	String routeId;
	String currentStop="";
	String nextStop="0";
	String preStop= "0" ;
	Random r = new Random();
	
		if (args.length != 4) { 
			System.err.println("Usage: EchoServerClient <host> <port> <url> <message>");
			System.exit(1); 
		}
		try
		{
		int port = Integer.parseInt(args[1]);
		Registry registry = LocateRegistry.getRegistry("localhost", port);
		tracking remoteServer = (tracking) registry.lookup(args[2]);
		
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		
		routeId = tramRoutes[r.nextInt(tramRoutes.length)];
		
		
		
		if(routeId == "1")
		{
			String poolArray[] = new String[]{"1", "5"};
			currentStop = poolArray[r.nextInt(2)];
		}
		else if(routeId == "96")
		{
			String poolArray[] = new String[]{"23","22"};
			currentStop = poolArray[r.nextInt(2)];
		}
		else if(routeId == "101")
		{
			String poolArray[] = new String[]{"123","7"};
			currentStop = poolArray[r.nextInt(2)];
		}
		else if(routeId == "109")
		{
			String poolArray[] = new String[]{"88","1"};
			currentStop = poolArray[r.nextInt(2)];
		}
		else if(routeId == "112")
		{
			String poolArray[] = new String[]{"110","4"};
			currentStop = poolArray[r.nextInt(2)];
		}
		while(true)
		{
			
			//initialize random
			long transactionId =0;
			long requestId = 0;
			Random rand = new Random();
			int random = rand.nextInt(11)+10;
			
			transactionId++;
			long traId = transactionId;
			
			RPCId++;
			long rpcid = RPCId;
			
			RequestIdPool++;
			long reqId = RequestIdPool++;
			
			//csv Data include routeId, cureentStopNumber, PreviousStopNumber
			String csv_Data = routeId + "," + currentStop + "," + preStop;
			System.out.println("Tram "+tramId +" initialize with route "+routeId+" at stop "+currentStop +" at "+ date.format(System.currentTimeMillis()));
			
								
			//Marshall the message
			RPCMessage message = new RPCMessage(RPCMessage.REQUEST,traId, RPCId, reqId, procedureIdRequest1, status, csv_Data);
			Message mmessage = new Message();
			mmessage.Marshall(message);
							
			
			//Send the Message and get response
			Message mresponse = remoteServer.retrieveNextStop(mmessage);
			
			//Unmarshall the message
			RPCMessage response = mresponse.unMarshal();
			
			//get next stop
			String nxtStop = response.getdata();
			
			System.out.println("Wait for 12 second");
			try {
			    Thread.sleep(12000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			
			RPCId++;
			rpcid = RPCId;
			
			RequestIdPool++;
			reqId = RequestIdPool++;
			
			//validate the different Ids
			if (response.getType() == response.REPLY&&
				response.getTransactionId()== message.getTransactionId() && 
				response.getRPCId() == message.getRPCId() &&
				response.requestId() == message.requestId() &&
				response.procedureId() == message.procedureId() )
			{
				
				//Wait for 12 second
					
				String data = routeId + "," + tramId + "," + nxtStop;
				
				//new RPCId for another call
				RPCId++; 
														
				System.out.println("Going to update the next stop " + date.format(System.currentTimeMillis()));
				
				//create messaga for update and Marshall it.
				RPCMessage updateMessage = new RPCMessage(RPCMessage.REQUEST,
						response.getTransactionId(),RPCId,response.requestId(),
						procedureIdRequest2,status,data);
				Message ummessage = new Message();
				ummessage.Marshall(updateMessage);
				
				//new request id for another request
				requestId++;
				
				//send Message to update
				Message update = remoteServer.updateTramLocation(ummessage);
				
				//Unmarshall the message
				RPCMessage ureponse = update.unMarshal();
				
				if(ureponse.procedureId() == updateMessage.procedureId())
				{
				//Set current and previous stop
				preStop = currentStop;
				currentStop = nxtStop;
				System.out.println("Tram "+tramId+" having route "+routeId+
						" update next stop "+preStop+" to "+currentStop+ " at "+date.format(System.currentTimeMillis()));
				}
			}
			
		
		}
		
			}
		catch (RemoteException ex) {
		System.err.println("Couldn't contact registry.");
		System.err.println(ex);
		System.exit(1);
	} catch (NotBoundException ex) {
		System.err.println("There is no object bound to " + args[0]);
		System.exit(1);
	}
	}
}
 