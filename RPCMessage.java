package assign;

import java.io.Serializable;

public class RPCMessage implements Serializable{
	public static final short REQUEST =0;
	public static final short REPLY = 1;
	public enum MessageType{REQUEST, REPLY};
	private MessageType messageType;
	private long TransactionId; /* transaction id */
	private long RPCId; /* Globally unique identifier */
	private long RequestId; /* Client request message counter */
	private short procedureId; /* 25=retrieve next stop, 50= update next stop */
	private short status;	//Status 0 for successful transaction
	private String csv_data; /* data as comma separated values*/
	
	
	public RPCMessage(short type, long t, long rid, long reqid, short procid, short status, String data)
	{
		super();
		messageType= setType(type);
		TransactionId = t;
		RPCId = rid;
		RequestId = reqid;
		procedureId = procid;
		status = 0;
		csv_data = data; 
	}
	public String getdata(){
		return csv_data;
		
	}
	public void setType(MessageType type){
		this.messageType = type;
	}
	public short getType(){
		switch(messageType)
		{
		case REQUEST:
			return 0;
		case REPLY:
			return 1;
		default:
			return -1;
		}
		
	}
	public MessageType setType(short msgType){
		if (msgType ==0){
			return MessageType.REQUEST;
		}
		else if(msgType ==1){
			return MessageType.REPLY;
		}else{
			return null;
		}
			
	}
	public void setTransactionId(long tId){
		this.TransactionId =tId;
	}
	public long getTransactionId(){
		return TransactionId;
	}
	public void setRPCId(long rpcId){
		this.RPCId =rpcId;
	}
	public long getRPCId(){
		return RPCId;
	}
	public void setRequestId(long rId){
		this.RequestId = rId;
	}
	public long requestId(){
		return RequestId;
	}
	public void SetprocedureId(short pId){
		this.procedureId = pId;
	}
	public short procedureId(){
		return procedureId;
	}
	public void Setgetstatus(short status){
		this.status = status;
	}
	public short getstatus(){
		return status;
	}
	public int lenInbytes(){
		return 2+8+8+8+2+2+csv_data.length()*2;
	}
	@Override
	public String toString(){
		return "RPCMessage [Type=" + messageType + 
				", Transaction ID ="+TransactionId + 
				", RPCId="+RPCId+" , RequestId=" +
				RequestId+" , procedureId="+procedureId+", data="+csv_data+"]"; 
	}

}
