import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class myMain{
	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");
		
		m1();
		
		My.cout("---------------"); My.cout("| MAIN END |");
		return;
	}

    public static void m0(){
        My.cout("m0");
        My.cout("---------------");

		int inputSize = 3;
		int instSize = 2;

		double[][] w = initMatrix(inputSize+1, instSize);
		
		My.cout(printMatrix(w));
		
		double[][] p = new double[][]{
			{1,-1,-1},
			{1,1,-1},
			{1,-1,1},
			{0,0,0},
		};
		double[][] t = new double[][]{
			{-1, 1},
			{1, -1},
			{1, 1},
			{-1, -1},
		};
		
		double lRate = 1;

		boolean conv = false;
		int epochCount = 0;
		int epochLimit = 100;
		while(!conv){
			My.cout("EPOCH "+epochCount);
			conv = true;

			for(int j=0; j<instSize; j++){
				int numOfInputs = p.length;
				for(int i=0; i<numOfInputs; i++){
					My.cout("i:"+(i)+" j:"+j);

					double _t = t[i][j];
					double[] _p = p[i];

					My.cout("t: "+_t);
					My.cout("p: "+printVector(_p));
					
					double n = weightedSum(j, _p, w);
					double fn = MCPitts(n, 0, true);
					
					My.cout("n:"+n);
					My.cout("f(n):"+fn);
		
					if(fn != _t){
						conv = false;
						double[] col = getMatrixCol(w, j);
						col = updateWeight(col, lRate, _t, fn, _p);
						col[0] = col[0] + lRate * (_t - fn);
						setMatrixCol(w, j, col);
					}
		
					My.cout("w:\n"+printMatrix(w));
					My.cout("------------");
				}
			}
			epochCount++;
		}
		

		
		My.cout("w:\n"+printMatrix(w));
		// My.cout("t:\n"+printMatrix(t));
		// My.cout("p:\n"+printMatrix(p));

		My.cout("-----------");

		double[] input = new double[]{-1,-1,-1};
		double[] output = new double[instSize];
		for(int j=0;j<instSize;j++){	
			double n = weightedSum(j, input, w);
			double fn = MCPitts(n, 0, true);
	
			My.cout("f(n): "+fn);
			output[j] = fn;
		}
		

		My.cout("in: "+printVector(input));
		My.cout("out: "+printVector(output));

    }

	public static void m1(){
		My.cout("m0");
        My.cout("---------------");

		int inputSize = 3;
		int instSize = 2;

		double[][] w = initMatrix(inputSize+1, instSize);
		double[][] v = initMatrix(instSize+1, instSize);

		ArrayList<double[][]> layers = new ArrayList<>();
		
		My.cout(printMatrix(w));
		
		double[][] p = new double[][]{
			{1,-1,-1},
			{1,1,-1},
			{1,-1,1},
			{0,0,0},
		};
		double[][] t = new double[][]{
			{-1, 1},
			{1, -1},
			{1, 1},
			{-1, -1},
		};
		
		double lRate = 1;

		boolean conv = false;
		int epochCount = 0;
		int epochLimit = 100;
		while(!conv){
			My.cout("EPOCH "+epochCount);
			conv = true;
			for(int c=0; c<layers.size();c++){
				for(int i=0; i<instSize; i++){
					int N = p.length;
					for(int l=0;l<N;l++){
	
					}
				}
			}
		}
	}

	public static double weightedSum(int c, double[] p, double[][] w){
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
        for(int r=1; r<p.length+1; r++){
            n += w[r][c] * p[r-1] + w[0][c];
        }
        return n;

	}
	
	// w = weight matrix between inputs and hidden
	// v = weight matrix between hidden and outputs
	// j = num of nodes in hidden layer
	// m = num of nodes in output layer
	// n = num of inputs
	// i = 1...j
	// k = 1...m
	// l = 1...n
	// n1j = v0j + SUM(vlj * pl) for l=1...n
	// n2m = w0m + SUM(wim * f(n1i)) for i=1...j

	public static double errorInfoOutput(double t, double fn2, double dirFn2){
		//Qk = (tk - f(n2k)) * f'(n2k)
		//where k = 1 to m
		return 0;
	}
	public static double errorInfoHidden(double[][] w, int index, double tk, double fn2k, double dirFn2k, double dirFn1i){
		// Qi = Qni * f'(n1i)
		// = SUM( ( (tk - f(n2k) ) * f'(n2k)) * wik ) * f'(n1i)
		//where i = 1 to j
		//where k = 1 to m
		return 0;
	}
	
	public static double weightCorrectionOutput(double lRate, double tk, double fn2k, double dirFn2k, double fn1i){
		// DELTA wik = lRate * Qk * f'(n1i)
		// = lRate * ((tk - f(n2k)) * f'(n2k)) * f'(n1i)
		//where k = 1 to m
		//where i = 1 to j
		return 0;
	}
	public static double weightCorrectionHidden(double lRate, double Qi, double pl){
		// DELTA vli = lRate * Qi * pl
		// = lRate * (SUM( ( (tk - f(n2k) ) * f'(n2k)) * wik ) * f'(n1i)) * pl
		//where l = 1 to n
		//where i = 1 to j
		return 0;
	}

	public static double biasCorrectionOutput(double lRate, double tk, double fn2k, double dirFn2k){
		// DELTA w0k = lRate * Qk
		// = lRate * ((tk - f(n2k)) * f'(n2k))
		//where k = 1 to m
		return 0;
	}

	public static double biasCorrectionHidden(double lRate){
		// DELTA v0i = lRate * Qi
		// = lRate * (SUM( ( (tk - f(n2k) ) * f'(n2k)) * wik ) * f'(n1i))
		//where i = 1 to j

		return 0;
	}

	public static double backPropUpdateWeights(double[] wi){
		//wik(new) = wik(old) + DELTA wik
		//where i = 0 to j
		//where k = 1 to m
		return 0;
	}
	public static double backPropUpdateBias(double[] vl){
		//vli(new) = vli(old) + DELTA vli
		// where l = 0 to n
		// where i = 1 to j
		return 0;
	}


	public static double sumOfDeltaInputs(double[][] w, int index, double tk, double fn2k, double dirFn2k){
		// Qni = SUM(Qk * wik)
		// = SUM( ( (tk - f(n2k) ) * f'(n2k)) * wik )
		//where k = 1 to m
		//where i = i to j
		return 0;
	}

	public static double[] updateWeight(double[] wi, double lRate, double t, double fn, double[] p){
		double[] wx = new double[wi.length];
		wx[0] = wi[0];

		for(int i=1; i<wi.length; i++){
			wx[i] = wi[i] + lRate * (t - fn) * p[i-1];
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