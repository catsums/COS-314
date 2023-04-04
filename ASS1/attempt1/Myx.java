import java.lang.reflect.Array;
import java.lang.Math;
import java.util.ArrayList;

public class Myx{
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

	public static String hash32(String str, int val){
		int hash = 0;
		if(str.length()!=0){
			for(int i=0;i<str.length();i++){
				char chr = str.charAt(i);
				int code = (int) chr;
				hash = ((hash << 3) - val) + code;
				hash |= 0;
			}
		}
		hash = hash % (val+1);
		return (String) Integer.toString(hash);
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

	// public static <T> ArrayList<T> deepClone(ArrayList<T> arr){
	// 	ArrayList<T> newArr = new ArrayList<T>();
	// 	for(T item:arr){
	// 		newArr.add(item);
	// 	}

	// 	return newArr;
	// }

}