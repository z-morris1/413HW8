public class test {
	
	public static int fun(int a, int b, String type) {
		int x = 0;
		if (type.equals("summation")) {
			return a + b;
		}
		
		int y;
		if (type.equals("minus")) {
			
			return a - b;
		}
		
		if (type.equals("modulous")) {
			a--;
			a++;
			return a % b;
		}
		
		int bcbc = 1;
		
		if (type.equals("subsquare")) {
			return (a * a - b * b);
		}
		

		if (type.equals("addsubsquare")) {
			return (((a + b) * (a * b)) - ((a - b) * (a - b)));
		}
		
		return 1;
	}
	
	public static void main(String[] args) {
		fun(Integer.parseInt(args[0]), Integer.parseInt(args[1]), 
			args[2]);
	}
}
