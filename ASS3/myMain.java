import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class myMain{
	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");
		
		m0();
		
		My.cout("---------------"); My.cout("| MAIN END |");
		return;
	}

    public static void m0(){
        My.cout("m0");
        My.cout("---------------");

		int[] size = new int[]{3,2};

		double[][] weights = initMatrix(size[0], size[1]);

		double[] biases = new double[size[1]];

		My.cout(printMatrix(weights));
		

    }

	public static double calcN(int j, double b, int[] p, int[][] w){
		/*
		*	n = SUM(w[i][j] * p[i] + b)where m = number of inputs, i = index of input
		*/

		double n = 0;
        for(int i=0; i<p.length; i++){
            n += w[i][j] * p[i] + b;
        }
        return n;

	}

	public static double ReLu(double n){
		double x = Math.max(0, n);

		return (x>=0) ? x : 0;
	}

	public static double[][] initMatrix(int rows, int cols){
		return new double[rows][cols];
	}

	public static String printVector(double[] vec){
		String out = "[ ";
        for(int i=0;i<vec.length;i++){
            out += vec[i];
            if(i<vec.length-1) out += " , ";
        }
		out += " ]";
        return out;
	}

	public static String printMatrix(double[][] mat){
		String out = "";
		for(int r=0;r<mat.length;r++){
			double[] row = mat[r];
			out += printVector(row);
			if(r<mat.length-1) out += "\n";
		}
		return out;
	}
}