// Delta Debugs a given program using the provided diff file
class DeltaDebug {
	public static void main(String[] args){
		// Check to make sure all 3 args are provided
		if (args.length != 3) {
			System.out.println("Usage: java DeltaDebug before.java after.java diff_file");
			exit(0);
		}
		
		// This isn't a program writing class, so at this point we're just going to assume
		// the correct data is supplied.
		// TO DO: Open and parse the diff file. Print out change sets as in the example
		
		// TO DO: Binary search algo on the diff set
	}
}