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
		
		// TO DO: Open the file in args[0]
		// TEST: Run args[0]
		File before = new File("./" + args[0]);
		try {
			runProcess("javac " + args[0]);
			runProcess("java " + args[0].substring(0, args[0].length() - 5) + tests[5]);
		} catch (Exception e) {
			System.out.println("Error");
		}
		//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		//int result = compiler.run(null, null, null, "./" + args[0]);
		//System.out.println(result);
		
		// TO DO: Make a new, empty file, test.java
		
		
		// TO DO: Binary search algo as pg. 6 in the paper
		
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
}