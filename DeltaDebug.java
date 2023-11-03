import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

// Delta Debugs a given program using the provided diff file
class DeltaDebug {
	static int n;
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
			System.out.println(ChangeObjects.get(i).id);
		}
		System.out.println("# of Total Change sets is = " + ChangeObjects.size());
		
		// Open the before-changes file
		File before = new File("./" + args[0]);
		// Make a new, empty file, test.java
		File test = new File("./test.java");
		ChangeObject[] results = deltaDebug(before, test, ChangeObjects);
		
		// Output final results
		System.out.print("Changes where bugs occurred: [");
		for (int i = 0; i < results.length; i++) {
			System.out.print(results[i].id);
			if (i != results.length - 1) {
				System.out.print(" ");
			}
		}
		System.out.println("]");
		
		return;
	}
	
	// Assumes monotony, but multiple points of failure can be returned
	private static ChangeObject[] deltaDebug(File beforeFile, File testFile, ArrayList<ChangeObject> objects) {
		n = 0;
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
		
		// Increment case iterator and print case details
		n++;
		System.out.print("Step " + n + ":  c_" + n + ":  ");
		for (int i = 0; i < changeSet.length; i++) {
			System.out.print(changeSet[i].id + " ");
		}
		
		// First, run the test on the full changeSet. If it passes, return an empty array
		testFile = applyChangeSet(beforeFile, testFile, changeSet);
		boolean pass = runTests(testFile);
		if (pass) {
			System.out.print("PASS\n");
			return new ChangeObject[0];
		}
		else if (changeSet.length == 1) {
			// We've reached a single failing change, return this changeSet
			System.out.print("FAIL\n");
			return changeSet;
		}
		else {
			System.out.print("FAIL\n");
			// changeSet fails but is still size > 1, split again and make recursive call
			// Split diffs into the left and right side of the array
			int length = changeSet.length;
			ChangeObject[] leftObjects = new ChangeObject[length / 2];
			ChangeObject[] rightObjects = new ChangeObject[length / 2 + length % 2];
			// Populate diff halves
			for (int i = 0, j = 0; i < length; i++, j++) {
				// Shift j back to 0 if the end of leftDiffs is hit
				if (i == j && j >= leftObjects.length) {
					j = 0;
				}
				
				// Assign each change to either left or right
				if (i == j) {
					leftObjects[j] = changeSet[i];
				}
				else { // Moved on to right half
					rightObjects[j] = changeSet[i];
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
		// Read before file, truncate/write change file
		BufferedReader reader;
		BufferedWriter writer;
		int curChangeNum = 0;
		ChangeObject curChange = changeSet[curChangeNum];
		int curLine = 0;
		int curLocation = Integer.parseInt(curChange.Location.split("\\s*[ ,]+")[1].substring(1));

		try {
			reader = new BufferedReader(new FileReader("./" + before.getName()));
			writer = new BufferedWriter(new FileWriter("./" + change.getName()));
			String line = reader.readLine();
			
			while (line != null) {
				curLine++;
				
				if (curLine == curLocation) {
					// We've hit the next change location, make changes to file
					System.out.println("Need to remove: \n" + curChange.Remove);
					System.out.println("Need to add: \n" + curChange.Add);
					
					curChangeNum++;
					curChange = changeSet[curChangeNum];
					curLocation = Integer.parseInt(curChange.Location.split("\\s*[ ,]+")[1].substring(1));
				}
				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// TESTING. Stop program after running this function the first time.
		// Changes 1-5 should be applied in test.java.
		System.exit(0);
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
			runProcess("java " + fileName.substring(0, fileName.length() - 5) + tests[5]);
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
				ChangeObject last = new ChangeObject(Stuff.size(), Location, AddString, RemoveString);
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

		ChangeObject last = new ChangeObject (Stuff.size(), Location, AddString, RemoveString);
		Stuff.add(last);

		Stuff.remove(Stuff.get(0));
		scanner.close();
		
		return Stuff;
	}
}