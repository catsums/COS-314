import java.lang.reflect.Array;
import java.lang.Math;
import java.util.*;
import java.io.*;

public class My{
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	public static void cout(Object x){
		System.out.println(x);
	}
	public static String colorise(Object x, String color){
		String colcode = "";
		switch(color.toUpperCase()){
			case "BLACK": colcode = ANSI_BLACK; break;
			case "RED": colcode = ANSI_RED; break;
			case "GREEN": colcode = ANSI_GREEN; break;
			case "YELLOW": colcode = ANSI_YELLOW; break;
			case "BLUE": colcode = ANSI_BLUE; break;
			case "PURPLE": colcode = ANSI_PURPLE; break;
			case "CYAN": colcode = ANSI_CYAN; break;
			case "WHITE": colcode = ANSI_WHITE; break;
			default: break;
		}
		return (colcode+x+ANSI_RESET);
	}
	public static String colorise(Object x, String color, String bg){
		String colcode = "";
		String bgcode = "";
		switch(color.toUpperCase()){
			case "BLACK": colcode = ANSI_BLACK; break;
			case "RED": colcode = ANSI_RED; break;
			case "GREEN": colcode = ANSI_GREEN; break;
			case "YELLOW": colcode = ANSI_YELLOW; break;
			case "BLUE": colcode = ANSI_BLUE; break;
			case "PURPLE": colcode = ANSI_PURPLE; break;
			case "CYAN": colcode = ANSI_CYAN; break;
			case "WHITE": colcode = ANSI_WHITE; break;
			default: break;
		}
		switch(bg.toUpperCase()){
			case "BLACK": bgcode = ANSI_BLACK_BACKGROUND; break;
			case "RED": bgcode = ANSI_RED_BACKGROUND; break;
			case "GREEN": bgcode = ANSI_GREEN_BACKGROUND; break;
			case "YELLOW": bgcode = ANSI_YELLOW_BACKGROUND; break;
			case "BLUE": bgcode = ANSI_BLUE_BACKGROUND; break;
			case "PURPLE": bgcode = ANSI_PURPLE_BACKGROUND; break;
			case "CYAN": bgcode = ANSI_CYAN_BACKGROUND; break;
			case "WHITE": bgcode = ANSI_WHITE_BACKGROUND; break;
			default: break;
		}
		return (colcode+x+ANSI_RESET);
	}
	public static String arrayToString(Object[] arr){
		String out = "[";
		for(int c=0;c<arr.length;c++){
			out += arr[c];
			if(c<arr.length-1){
				out+=", ";
			}
		}
		out+="]";
		return out;
	}
	public static String arrayToString(int[] arr){
		Object[] _arr = new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			_arr[i] = arr[i];
		}
		return arrayToString(_arr);
	}
	public static String arrayToString(char[] arr){
		Object[] _arr = new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			_arr[i] = arr[i];
		}
		return arrayToString(_arr);
	}
	public static String arrayToString(boolean[] arr){
		Object[] _arr = new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			_arr[i] = arr[i];
		}
		return arrayToString(_arr);
	}
	public static String arrayToString(long[] arr){
		Object[] _arr = new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			_arr[i] = arr[i];
		}
		return arrayToString(_arr);
	}
	public static String arrayToString(short[] arr){
		Object[] _arr = new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			_arr[i] = arr[i];
		}
		return arrayToString(_arr);
	}
	public static String arrayToString(double[] arr){
		Object[] _arr = new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			_arr[i] = arr[i];
		}
		return arrayToString(_arr);
	}
	public static int nextPrime(int num){
		boolean isprime = false;
		int val = num;
		while(!isprime){
			isprime = isPrime(val);
			// cout("isprime: "+isprime);
			if(!isprime){
				val++;
			}
		}
		return val;
	}
	public static boolean isPrime(int num){
		int s = (int) Math.sqrt((double) num);
	    for(int i = 2; i <= s; i++){
	        if(num % i == 0){
	        	return false; 
	        }
	    }
	    return (num > 1);
	}
	public static double sigDigRounder(double value, int nSigDig, int dir) {

		double intermediate = value/Math.pow(10,Math.floor(Math.log10(Math.abs(value)))-(nSigDig-1));

		if(dir > 0)      intermediate = Math.ceil(intermediate);
		else if (dir< 0) intermediate = Math.floor(intermediate);
		else             intermediate = Math.round(intermediate);

		double result = intermediate * Math.pow(10,Math.floor(Math.log10(Math.abs(value)))-(nSigDig-1));

		return(result);

	}

	public static int sum(int[] arr){
		int s = 0;
		for(int item:arr) s += item;
		return s;
	}public static double sum(double[] arr){
		double s = 0;
		for(double item:arr) s += item;
		return s;
	}public static long sum(long[] arr){
		long s = 0;
		for(long item:arr) s += item;
		return s;
	}

	public static <T extends Comparable<T>> T min(T[] arr){
		T min = null;
		for(T item:arr){
			if(min == null || min.compareTo(item) > 0) min = item;
		}
		return min;
	}
	public static int min(int[] arr){
		int min = Integer.MAX_VALUE;
		for(int item:arr){
			if(min > item) min = item;
		}
		return min;
	}public static double min(double[] arr){
		double min = Double.MAX_VALUE;
		for(double item:arr){
			if(min > item) min = item;
		}
		return min;
	}public static long min(long[] arr){
		long min = Long.MAX_VALUE;
		for(long item:arr){
			if(min > item) min = item;
		}
		return min;
	}

	public static <T extends Comparable<T>> T max(T[] arr){
		T max = null;
		for(T item:arr){
			if(max == null || max.compareTo(item) < 0) max = item;
		}
		return max;
	}
	public static int max(int[] arr){
		int max = Integer.MIN_VALUE;
		for(int item:arr){
			if(max < item) max = item;
		}
		return max;
	}public static double max(double[] arr){
		double max = Double.MIN_VALUE;
		for(double item:arr){
			if(max < item) max = item;
		}
		return max;
	}public static long max(long[] arr){
		long max = Long.MIN_VALUE;
		for(long item:arr){
			if(max < item) max = item;
		}
		return max;
	}

	public static String hash32(String str, int val){
		long hash = 0;
		if(str.length()!=0){
			for(int i=0;i<str.length();i++){
				char chr = str.charAt(i);
				int code = (int) chr;
				hash = ((hash << 3) - val) + code;
				hash |= 0;
			}
		}
		hash = hash % (val+1);
		return (String) Long.toString(hash);
	}
	public static String hash64(String str, int val){
		String initHash = hash32(str, val);
		return hash32(initHash, val);
	}
	public static String hash128(String str, int val){
		String initHash = hash64(str, val);
		return hash64(initHash, val);
	}

	public static double log(double base, double num){
		return (double) (Math.log(num) / Math.log(base));
	}public static double log(int base, int num){
		return (double) (Math.log(num) / Math.log(base));
	}public static double log(double base, int num){
		return (double) (Math.log(num) / Math.log(base));
	}public static double log(int base, double num){
		return (double) (Math.log(num) / Math.log(base));
	}

	public static int logInt(double base, double num){
		return (int) (Math.log(num) / Math.log(base));
	}public static int logInt(int base, int num){
		return (int) (Math.log(num) / Math.log(base));
	}public static int logInt(double base, int num){
		return (int) (Math.log(num) / Math.log(base));
	}public static int logInt(int base, double num){
		return (int) (Math.log(num) / Math.log(base));
	}

	public static double rndDouble(double min, double max){
		Random rnd = new Random();
		// random.nextDouble()*(maxX - minX) + minX;
		return rnd.nextDouble() * (max - min) + min;
	}

	public static int rndInt(int min, int max){
		Random rnd = new Random();
		return rnd.nextInt(max - min + 1) + min;
	}

	public static double rndDouble(double min, double max, long seed){
		Random rnd = new Random(seed);
		// random.nextDouble()*(maxX - minX) + minX;
		return rnd.nextDouble() * (max - min) + min;
	}

	public static int rndInt(int min, int max, long seed){
		Random rnd = new Random(seed);
		return rnd.nextInt(max - min + 1) + min;
	}
	
	public static double stepify(double value, double step){
		if(step == 0) return value;
		if(step == Double.POSITIVE_INFINITY) return 1;
		
		double _step = (1/step);
		return Math.round((value) * _step) / _step;
	}
	public static float stepify(float value, float step){
		if(step == 0) return value;
		if(step == Float.POSITIVE_INFINITY) return 1;

		return Math.round((value + Math.ulp(1)) / step) * step;
	}

	public static <T> T[] shuffleArray(T[] ar, long seed){
		ar = ar.clone();
		//Fisher–Yates shuffle
		for (int i = ar.length - 1; i > 0; i--){
			int index = My.rndInt(0, i, seed);
			// Simple swap
			T a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		return ar;
	}

	public static double mod(double n, double d){
		return ((n % d) + d) % d;
	}
	public static float mod(float n, float d){
		return ((n % d) + d) % d;
	}
	public static int mod(int n, int d){
		return ((n % d) + d) % d;
	}

	public static String SerializeObject(Serializable obj) throws Exception{
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(obj);
			so.flush();
			return bo.toString();
		} catch (Exception e) {
			System.out.println(e);
			throw e;
		}
	}
	public static <T extends Serializable> T DeserializeObject(String str) throws Exception{
		try {
			byte b[] = str.getBytes(); 
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			return (T) si.readObject();
		} catch (Exception e) {
			System.out.println(e);
			throw e;
		}
	}

	 /** Read the object from Base64 string. */
	public static Object DeserializeFromBase64( String s ) throws IOException , ClassNotFoundException {
		byte [] data = Base64.getDecoder().decode( s );
		ObjectInputStream ois = new ObjectInputStream( 
			new ByteArrayInputStream( data )
		);
		Object o  = ois.readObject();
		ois.close();
		return o;
	}

	/** Write the object to a Base64 string. */
	public static String SerializeToBase64( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray()); 
	}

	public static Scanner inputSc = new Scanner(System.in);

	public static double makeChoiceDouble(String prompt){
		while(true){
			
			System.out.print(prompt+":\t");
			
				double val = inputSc.nextDouble();
				
				return val;
			
			
		}
	}
	public static String makeChoiceString(String prompt){
		while(true){
			
			System.out.print(prompt+":\t");
			
				String val = inputSc.next();
				
				return val;
			
			
		}
	}

	public static long makeChoiceLong(String prompt){
		while(true){
			
			System.out.print(prompt+":\t");
			
				long val = inputSc.nextLong();
				
				return val;
			
			
		}
	}

	public static int makeChoiceInt(String prompt){
		while(true){
			
			System.out.print(prompt+":\t");
			
				int val = inputSc.nextInt();
				
				return val;
			
			
		}
	}

	public static int makeChoiceRange(String prompt, int min, int max){
		while(true){
			
			System.out.print(prompt+":\t");
			
				int val = inputSc.nextInt();
				if(val>=min && val<=max)  return val;
			
			
		}
	}

	public static int makeChoiceUnsignedInt(String prompt){
		while(true){
			
			System.out.print(prompt+":\t");
			
				int val = inputSc.nextInt();
				if(val<0) return val;
			
			
		}
	}

	public static boolean makeChoiceBool(String prompt){
		while(true){
			
			System.out.print(prompt+":\t");
			
				String val = inputSc.next();
				switch(val.toLowerCase()){
					case "1":
					case "yes":
					case "t":
					case "true":
					case "maybe":
					case "i":
					case "y": 
						
						return true;
					case "0":
					case "no":
					case "f":
					case "false":
					case "maybe not":
					case "n":
						
						return false;
					default:
						break;
				}
			
			
		}
	}

	// public static void writeToFile(ArrayList<String> lines, String filePath) {
	// 	try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	// 		for (String line : lines) {
	// 			writer.write(line);
	// 			writer.newLine();
	// 		}
	// 		System.out.println("File written successfully.");
	// 	} catch (IOException e) {
	// 		System.out.println("An error occurred while writing to the file: " + e.getMessage());
	// 	}
	// }

	// public static ArrayList<String> readFromFile(String filePath) {
	// 	ArrayList<String> lines = new ArrayList<>();
	// 	try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	// 		String line;
	// 		while ((line = reader.readLine()) != null) {
	// 			lines.add(line);
	// 		}
	// 		System.out.println("File read successfully.");
	// 	} catch (IOException e) {
	// 		System.out.println("An error occurred while reading the file: " + e.getMessage());
	// 	}
	// 	return lines;
	// }

}