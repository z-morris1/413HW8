import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

// Delta Debugs a given program using the provided diff file
class DeltaDebug {
	public static void main(String[] args){
		// Check to make sure all 3 args are provided
		if (args.length != 3) {
			System.out.println("Usage: java DeltaDebug before.java after.java diff_file");
			return;
		}
		
		// This isn't a program writing class, so at this point we're just going to assume
		// the correct data is supplied.
		// TO DO: implement some sort of way to store the individual diffs for later testing
		ArrayList<diff> diffs = new ArrayList<diff>();
		
		// TO DO: Open and parse the diff file. Print out change sets as in the example
		ArrayList<ChangeObject> ChangeObjects = new ArrayList<ChangeObject>();
		try {
			ChangeObjects = readDiffFile();
		} catch (FileNotFoundException e) {
			System.out.println("Diff file not found!");
			return;
		}

		System.out.println("Delta-debugging Project");
		for(int i = 0; i < ChangeObjects.size(); i++){
			System.out.println(ChangeObjects.get(i).Location);
		}
		System.out.println("# of Total Change sets is = " + ChangeObjects.size());
		
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
		ChangeObject[] results = deltaDebug(before, test, ChangeObjects);
		
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
	
	private static ChangeObject[] deltaDebug(File beforeFile, File testFile, ArrayList<ChangeObject> objects) {
		// Split diffs into the left and right side of the array
		int length = objects.size();
		ChangeObject[] leftObjects = new ChangeObject[length / 2];
		ChangeObject[] rightObjects = new ChangeObject[length / 2];
		// Populate diff halves
		for (int i = 0, j = 0; i < length; i++) {
			if (i == j) {
				leftObjects[j] = objects.get(i);
			}
			else { // Moved on to right half
				rightObjects[j] = objects.get(i);
			}
			j++;
			// Shift j back to 0 if the end of leftDiffs is hit
			if (j >= leftObjects.length) {
				j = 0;
			}
		}
		
		// Call the recursive function on each half
		ChangeObject[] leftResults = deltaDebugRecursive(beforeFile, testFile, leftObjects);
		ChangeObject[] rightResults = deltaDebugRecursive(beforeFile, testFile, rightObjects);
		
		// Combine results in the return set
		ChangeObject[] results = new ChangeObject[leftResults.length + rightResults.length];
		for (int i = 0; i < leftResults.length; i++) {
			results[i] = leftResults[i];
		}
		for (int i = 0; i < rightResults.length; i++) {
			results[i + leftResults.length] = rightResults[i];
		}
		
		return results;
	}
	
	private static ChangeObject[] deltaDebugRecursive(File beforeFile, File testFile, ChangeObject[] objects) {
		return objects;
	}

	private static ArrayList<ChangeObject> readDiffFile() throws FileNotFoundException {
		ArrayList<ChangeObject> Stuff = new ArrayList<ChangeObject>();

		File file = new File("diff");
		Scanner scanner = new Scanner(file);

		String Location = "";
		String AddString = "";
		String RemoveString = "";
		
		String saved = "";

		saved = scanner.nextLine();
		saved = scanner.nextLine();

		while(scanner.hasNextLine()) {
			saved = scanner.nextLine();
			Scanner checker = new Scanner(saved);
			String first = checker.next();
			checker.close();

			if (first.equals("@@")) {
				ChangeObject last = new ChangeObject(Location, AddString, RemoveString);
				Stuff.add(last);

				Location = "";
				AddString = "";
				RemoveString = "";
				
				Location = saved;
			}
 
			else if (first.equals("-")) {
				if(RemoveString.equals("")) {
					RemoveString = saved.substring(1, saved.length()-1);
				}
				else {
					String result = "\n" + saved.substring(1, saved.length());
					RemoveString += result;
				}
				
			}
 
			else if (first.equals("+")) {
				if(AddString.equals("")) {
					AddString = saved.substring(1, saved.length()-1);
				}
				else {
					String result = "\n" + saved.substring(1, saved.length());
					AddString += result;
				}
			}
		}

		ChangeObject last = new ChangeObject (Location, AddString, RemoveString);
		Stuff.add(last);

		Stuff.remove(Stuff.get(0));
		scanner.close();
		
		return Stuff;
	}
}