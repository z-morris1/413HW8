import java.util.ArrayList;

// Delta Debugs a given program using the provided diff file
class DeltaDebug {
	public static void main(String[] args){
		// Check to make sure all 3 args are provided
		if (args.length != 3) {
			System.out.println("Usage: java DeltaDebug before.java after.java diff_file");
			System.exit(0);
		}
		
		// This isn't a program writing class, so at this point we're just going to assume
		// the correct data is supplied.
		// TO DO: implement some sort of way to store the individual diffs for later testing
		ArrayList<diff> diffs = new ArrayList<diff>();
		
		// TO DO: Open and parse the diff file. Print out change sets as in the example
		
		// This isn't a coverage generation project, so we'll hard code a test set of inputs here
		String[][] tests = { {"0", "0", "summation"},
						  {"0", "0", "multiplication"}, 
						  {"0", "0", "minus"}, 
						  {"0", "0", "modulous"}, 
						  {"0", "0", "addsquare"}, 
						  {"0", "0", "division"}, 
						  {"0", "0", "subsquare"}, 
						  {"0", "0", "addsubsquare"}, 
						  {"0", "0", ""}
		};
		
		// TO DO: Open the file in args[0]
		
		// TO DO: Make a new, empty file, test.java
		
		// TO DO: Binary search algo as pg. 6 in the paper
		
		return;
	}
}