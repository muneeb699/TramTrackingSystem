package assign;

public class typeException extends Exception{
	private String err;
	public typeException(){
		err ="Message type is not correct";
	}
	
	public void printError(){
		System.err.println(err); 
	}

}