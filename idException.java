package assign;

public class idException extends Exception{
	private String err;
	
	public idException(){
		err = "Received and Send ID's Mismatch";
	}
	public void printError(){
		System.err.println(err);
	}

}
