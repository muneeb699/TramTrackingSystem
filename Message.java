package assign;

import java.io.Serializable;
import java.nio.ByteBuffer;


public class Message implements Serializable {
	
	protected byte data[] = null;
	protected int length = 0;
	
	public void Marshall(RPCMessage rpcMessage){
		int buffSize = rpcMessage.lenInbytes();
		ByteBuffer bb = ByteBuffer.allocate(buffSize);
		
		short typeNum = (short)rpcMessage.getType();
		int index = 0;
		bb.putShort(0,typeNum);
		index+=2;
		
		bb.putLong(index, rpcMessage.getTransactionId());
		index+=8;
		
		bb.putLong(index, rpcMessage.getRPCId());
		index+=8;
		
		bb.putLong(index,rpcMessage.requestId());
		index+=8;
		
		bb.putShort(index, rpcMessage.procedureId());
		index+=2;
		
		bb.putShort(index, rpcMessage.getstatus());
		index+=2;
		
		String data = rpcMessage.getdata();
		for(int i=0; i<data.length(); i++, index+=2){
			bb.putChar(index,data.charAt(i));
		}
		this.length = index;
		this.data = bb.array();
	}
	public RPCMessage unMarshal(){
		
	
		ByteBuffer bb = ByteBuffer.wrap(this.data);
		
		int index=0;
		
		short msgType = bb.getShort(index);
		index+=2;
		
		long transactionId = bb.getLong(index);
		index+=8;
		
		long RPCId = bb.getLong(index);
		index+=8;
		
		long RequestId = bb.getLong(index);
		index+=8;
		
		short ProcedureId = bb.getShort(index);
		index+=2;
		
		short status = bb.getShort(index);
		index+=2;
		
		StringBuffer sb = new StringBuffer();
		for(; index< bb.array().length; index+=2){
			sb.append(bb.getChar(index));
		}
		String csv_Data = sb.toString();
		
		RPCMessage message = new RPCMessage(msgType,transactionId, RPCId, RequestId, ProcedureId, status, csv_Data);
		
		return message;
		
	}
	

}
