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

		int numOfInputs = 3;
		int numOfInst = 2;

		double[][] w = initMatrix(numOfInputs, numOfInst);
		double[] b = new double[numOfInst];
		for(int i=0;i<b.length;i++) b[i] = 0;
		
		My.cout(printMatrix(w));
		
		double[][] p = new double[][]{
			{1,-1,-1},
			{1,1,-1},
		};
		double[] t = new double[]{
			-1,
			1
		};
		
		double lRate = 1;
		
		int j = 0;
		for(int i=0; i<numOfInst; i++){
			My.cout("i:"+i);
			
			double n = calcN(j, b[j], p[i], w);
			double fn = MCPitts(n, 0, true);
			
			My.cout("n:"+n);
			My.cout("f(n):"+fn);

			if(fn != t[i]){
				setMatrixCol(w, j, 
					updateWeight(
						getMatrixCol(w, j)
					, lRate, t[i],fn, p[i])
				);

				b[j] = b[j] + lRate * (t[i] - fn);
			}

			My.cout("w:\n"+printMatrix(w));
			My.cout("b:"+b[j]);
			My.cout("------------");
		}

		
		My.cout(printMatrix(w));
		My.cout(b[j]);

    }

	public static double calcN(int c, double b, double[] p, double[][] w){
		/*
		 * n = SUM(w[i][j] * p[i] + b)
		 * where m = number of inputs
		 * i = index of input/ index of row
		 * j = col index
		 * b = bias
		 * p = input
		 * w = weight matrix
		*/

		double n = 0;
        for(int r=0; r<p.length; r++){
            n += w[r][c] * p[r] + b;
        }
        return n;

	}

	public static double[] updateWeight(double[] wi, double lRate, double t, double fn, double[] p){
		double[] wx = new double[wi.length];

		for(int i=0; i<wi.length; i++){
			wx[i] = wi[i] + lRate * (t - fn) * p[i];
		}

		return wx;
	}
	public static double[] updateBias(double[] bi, double lRate, double t, double fn){
		double[] bx = new double[bi.length];

		for(int i=0; i<bi.length; i++){
            bx[i] = bi[i] + lRate * (t - fn);
        }

		return bx;
    }

	public static double MCPitts(double n, double x, boolean bipolar){
		return ((n>=x) ? 1 : (bipolar ? -1 : 0));
	}

	public static double ReLu(double n){
		double x = Math.max(0, n);

		return (x>=0) ? x : 0;
	}
	public static double Dir_ReLu(double n){
		return (n>0) ? 1 : 0;
	}

	public static double[][] initMatrix(int rows, int cols){
		return new double[rows][cols];
	}

	public static double[] getMatrixRow(double[][] mat, int rowIndex){
		if(rowIndex < 0 || rowIndex >= mat.length) return null;

		double[] row = new double[mat[rowIndex].length];
		for(int i=0; i<mat[rowIndex].length; i++){
            row[i] = mat[rowIndex][i];
        }
        return row;
	}
	public static double[] getMatrixCol(double[][] mat, int colIndex){
		if(colIndex < 0 ||  colIndex >= mat.length) return null;

		double[] col = new double[mat.length];
		for(int i=0; i<mat.length; i++){
            col[i] = mat[i][colIndex];
        }
        return col;
	}

	public static double[] setMatrixRow(double[][] mat, int rowIndex, double[] newRow){
		if(rowIndex < 0 || rowIndex >= mat.length) return null;

		double[] row = mat[rowIndex];
		for(int i=0; i<mat[rowIndex].length; i++){
            mat[rowIndex][i] = newRow[i];
        }
        return row;
	}
	public static double[] setMatrixCol(double[][] mat, int colIndex, double[] newCol){
		if(colIndex < 0 || colIndex >= mat.length) return null;

		double[] col = mat[colIndex];
		for(int i=0; i<mat.length; i++){
            mat[i][colIndex] = newCol[i];
        }
        return col;
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
			double[] row = getMatrixRow(mat, r);
			out += printVector(row);
			if(r<mat.length-1) out += "\n";
		}
		return out;
	}
}