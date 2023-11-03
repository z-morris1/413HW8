import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		String[] tests = {" 0 0 summation",
						  " 0 0 multiplication", 
						  " 0 0 minus", 
						  " 0 0 modulous", 
						  " 0 0 addsquare", 
						  " 0 0 division", 
						  " 0 0 subsquare", 
						  " 0 0 addsubsquare", 
						  " 0 0 "
						 };
		
		// TEST: Run args[0]
		try {
			runProcess("javac " + args[0]);
			runProcess("java " + args[0].substring(0, args[0].length() - 5) + tests[5]);
		} catch (Exception e) {
			System.out.println("Error");
		}
		
		// Open the before-changes file
		File before = new File("./" + args[0]);
		// Make a new, empty file, test.java
		File test = new File("./test.java");
		
		// Assumes monotony, but multiple points of failure can be returned
		ArrayList<diff> results = deltaDebug(before, test, diffs);
		
		return;
	}
	
	private static void runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
        while ((line = in.readLine()) != null) {
			// If anything is printed to the error stream, that's a failure. Throw an exception
			//System.out.println(line);
            throw new Exception();
        }
		pro.waitFor();
		return;
	}
	
	private static ArrayList<diff> deltaDebug(File beforeFile, File testFile, ArrayList<diff> diffs) {
		// Split diffs into the left and right side of the array
		int length = diffs.size();
		diff[] leftDiffs = new diff[Math.ceil(length / 2)];
		diff[] rightDiffs = new diff[length - Math.ceil(length / 2)];
		// Populate diff halves
		for (int i = 0, int j = 0; i < length; i++) {
			if (i == j) {
				leftDiffs[j] = diffs.get(i);
			}
			else { // Moved on to right half
				rightDiffs[j] = diffs.get(i);
			}
			j++;
			// Shift j back to 0 if the end of leftDiffs is hit
			if (j >= leftDiffs.length()) {
				j = 0;
			}
		}
		
		// Call the recursive function on each half
		diff[] leftResults = deltaDebugRecursive(beforeFile, testFile, leftDiffs);
		diff[] rightResults = deltaDebugRecursive(beforeFile, testFile, rightDiffs);
		
		// Combine results in the return set
		diff[] results = diff[leftResults.length() + rightResults.length()];
		for (int i = 0; i < leftResults.length(); i++) {
			results[i] = leftResults[i];
		}
		for (int i = 0; i < rightResults.length(); i++) {
			results[i + leftResults.length()] = rightResults[i];
		}
		
		return results;
	}
	
	private static diff[] deltaDebugRecursive(File beforeFile, File testFile, diff[] diffs){
		
	}
}