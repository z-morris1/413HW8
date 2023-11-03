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
		// Open and parse the diff file. Print out change sets as in the example
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
		
		// Open the before-changes file
		File before = new File("./" + args[0]);
		// Make a new, empty file, test.java
		File test = new File("./test.java");
		ChangeObject[] results = deltaDebug(before, test, ChangeObjects);
		
		return;
	}
	
	// Assumes monotony, but multiple points of failure can be returned
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
	
	private static ChangeObject[] deltaDebugRecursive(File beforeFile, File testFile, ChangeObject[] changeSet) {
		// Edge case
		if (changeSet.length == 0) {
			return new ChangeObject[0];
		}
		
		// First, run the test on the full changeSet. If it passes, return an empty array
		testFile = applyChangeSet(beforeFile, testFile, changeSet);
		boolean pass = runTests(testFile);
		if (pass) {
			return new ChangeObject[0];
		}
		else if (changeSet.length == 1) {
			// We've reached a single failing change, return this changeSet
			return changeSet;
		}
		else {
			// changeSet fails but is still size > 1, split again and make recursive call
			// Split diffs into the left and right side of the array
			int length = changeSet.length;
			ChangeObject[] leftObjects = new ChangeObject[length / 2];
			ChangeObject[] rightObjects = new ChangeObject[length / 2];
			// Populate diff halves
			for (int i = 0, j = 0; i < length; i++) {
				if (i == j) {
					leftObjects[j] = changeSet[i];
				}
				else { // Moved on to right half
					rightObjects[j] = changeSet[i];
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
	}
	
	// Takes a before file, truncates the change file, and writes the before file with the changeSet 
	// applied to the change file. Returns the written change file
	private static File applyChangeSet(File before, File change, ChangeObject[] changeSet) {
		//TO DO
		return change;
	}
	
	// Runs our test set on the given file. Returns true if no errors occur, otherwise false
	private static boolean runTests(File f) {
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
		
		String fileName = f.getName();
		// TO DO: Run all tests instead of just the one
		try {
			runProcess("javac " + fileName);
			for(int i = 0; i <= tests.length; i++) {
				runProcess("java " + fileName.substring(0, fileName.length() - i) + tests[i]);
			}
			
		} catch (Exception e) {
			return false;
		}
		return true;
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