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

		double[] input = new double[]{1,-1,1};
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

		double[][] v = initMatrix(inputSize+1, instSize);
		double[][] w = initMatrix(instSize+1, instSize);
		
		My.cout(printMatrix(v));
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
			double[][] V = v;
			double[][] W = w;

			int J = V.length;
			int M = W.length;

			ArrayList<Double> fn1 = new ArrayList<>();
			ArrayList<Double> dirFn1 = new ArrayList<>();
			ArrayList<Double> fn2 = new ArrayList<>();
			ArrayList<Double> dirFn2 = new ArrayList<>();

			int N = p.length;


			/// FEEDFORWARD
			for(int l=1; l<N; l++){
				double _n1 = 0;
				for(int i=0; i<J; i++){
					_n1 += V[l][i] * p[l-1][i] + V[0][i];
				}
				double _fn1 = ReLu(_n1, true);
				double _dirFn1 = Dir_ReLu(_n1, true);
				fn1.add(_fn1);
				dirFn1.add(_dirFn1);
			}
			for(int i=1; i<J; i++){
				double _n2 = 0;
				for(int k=0; k<M; k++){
					_n2 += W[i][k] * fn1.get(i-1) + W[0][k];
				}
				double _fn2 = ReLu(_n2, true);
				double _dirFn2 = Dir_ReLu(_n2, true);
				fn2.add(_fn2);
				dirFn2.add(_dirFn2);
			}

			for(int l=0;l<N;l++){
				ArrayList<Double> errInfoOuts = new ArrayList<>();
				ArrayList<ArrayList<Double>> weightCorrs = new ArrayList<>();
				ArrayList<Double> biasCorrs = new ArrayList<>();
				for(int k=0;k<M;k++){
					double Qk = errorInfoOutput(t[l][k], fn2.get(k), dirFn2.get(k));
					double w0k = lRate * Qk;
					
					ArrayList<Double> wC = new ArrayList<>();
					
					for(int i=0;i<J;i++){
						double Wik = w0k * dirFn1.get(i);
						wC.add(Wik);
					}

					errInfoOuts.add(Qk);
					biasCorrs.add(w0k);

					weightCorrs.add(wC);
				}

				for(int i=0;i<J;i++){
					double Qni = 0;
					for(int k=0;k<M;k++){
						Qni += errInfoOuts.get(k) * weightCorrs.get(k).get(i);
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
	
	// v = weight matrix between inputs and hidden
	// w = weight matrix between hidden and outputs
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
		return (t - fn2) * dirFn2;
	}
	public static double errorInfoHidden(double[][] w, int index, double tk, double fn2k, double dirFn2k, double dirFn1i){
		// Qi = Qni * f'(n1i)
		// = SUM( ( (tk - f(n2k) ) * f'(n2k)) * wik ) * f'(n1i)
		//where i = 1 to j
		//where k = 1 to m
		return 0;
	}
	
	public static double weightCorrectionOutput(double lRate, double tk, double fn2k, double dirFn2k, double fn1i){
		// DELTA wik = lRate * Qk * f(n1i)
		// = lRate * ((tk - f(n2k)) * f'(n2k)) * f(n1i)
		//where k = 1 to m
		//where i = 1 to j
		return (lRate * ((tk - fn2k) * dirFn2k) * fn1i);
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

	public static double ReLu(double n, boolean bipolar){
		double x = Math.max(0, n);

		return (x>=0) ? x : (bipolar ? -1 : 0);
	}
	public static double Dir_ReLu(double n, boolean bipolar){
		return (n>0) ? 1 : (bipolar ? -1 : 0);
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