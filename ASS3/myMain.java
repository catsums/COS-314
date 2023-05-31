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

		double[][] v = initMatrix(inputSize+1, instSize, 0.5);
		double[][] w = initMatrix(instSize+1, instSize, 0.5);
		
		My.cout("V:\n"+printMatrix(v));
		My.cout("W:\n"+printMatrix(w));
		
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
			// {-1, -1},
		};
		
		double lRate = 1;

		boolean conv = false;
		int epochCount = 0;
		int epochLimit = 100;
		// while(!conv){
			My.cout("EPOCH "+epochCount);
			conv = true;
			double[][] V = v;
			double[][] W = w;

			int J = V[0].length;
			int M = W[0].length;

			/*
			 * [p0, p1, p2] inputSize = 3
			 * 
			 * [v00, v01] (inputSize+1, instSize)
			 * [v10, v11]
			 * [v20, v21]
			 * [v30, v31]
			 * 
			 * [h0, h1] (J = instSize)
			 * 
			 * [w00, w01] (instSize+1, outputSize)
			 * [w10, w11]
			 * [w20, w21]
			 * 
			 * [t0, t1] (M = outputSize)
			 */

			ArrayList<Double> fn1 = new ArrayList<>();
			ArrayList<Double> dirFn1 = new ArrayList<>();
			ArrayList<Double> fn2 = new ArrayList<>();
			ArrayList<Double> dirFn2 = new ArrayList<>();

			int N = V.length;
			double[] _p = p[0];

			My.cout("p: "+printVector(_p));
			
			/// FEEDFORWARD
			for(int i=0; i<J; i++){
				double _n1 = 0;
				_n1 += V[0][i];
				for(int l=1; l<N; l++){
					_n1 += V[l][i] * _p[l-1];
				}
				double _fn1 = ReLu(_n1, false);
				double _dirFn1 = Dir_ReLu(_n1, false);
				fn1.add(_fn1);
				dirFn1.add(_dirFn1);
			}
			for(int k=0; k<M; k++){
				double _n2 = 0;
				_n2 += W[0][k];
				for(int i=1; i<J; i++){
					_n2 += W[i][k] * fn1.get(i-1);
				}
				double _fn2 = ReLu(_n2, false);
				double _dirFn2 = Dir_ReLu(_n2, false);
				fn2.add(_fn2);
				dirFn2.add(_dirFn2);
			}

			My.cout("f(n1): "+Arrays.toString(fn1.toArray()));
			My.cout("f'(n1): "+Arrays.toString(dirFn1.toArray()));
			My.cout("f(n2): "+Arrays.toString(fn2.toArray()));
			My.cout("f'(n2): "+Arrays.toString(dirFn2.toArray()));

			ArrayList<ArrayList<Double>> weightCorrs = new ArrayList<>(); //ki
			ArrayList<ArrayList<Double>> weightHiddens = new ArrayList<>(); //il

			ArrayList<ArrayList<Double>> errInfoOuts = new ArrayList<>();
			ArrayList<ArrayList<Double>> errInfoHiddens = new ArrayList<>();
			ArrayList<ArrayList<Double>> sumsOfDeltaInputs = new ArrayList<>();
			for(int l=0;l<N;l++){
				ArrayList<Double> errO = new ArrayList<>();

				for(int k=0;k<M;k++){
					double Qk = (t[0][k] - fn2.get(k)) * dirFn2.get(k);
					
					ArrayList<Double> wC = new ArrayList<>();
					
					for(int i=0;i<J;i++){
						if(i==0){
							double w0k = lRate * Qk;
							wC.add(w0k);
						}else{
							double Wik = lRate * Qk * dirFn1.get(i-1);
							wC.add(Wik);
						}
					}

					errO.add(Qk);
					weightCorrs.add(wC);
				}
				// errInfoOuts.add(errO);

				ArrayList<Double> sumDI = new ArrayList<>();
				ArrayList<Double> errH = new ArrayList<>();
				ArrayList<Double> wH = new ArrayList<>();

				for(int i=0;i<J;i++){
					double Qni = 0;
					for(int k=1;k<=M;k++){
						double Qk = errO.get(k-1);
						Qni += Qk * weightCorrs.get(k-1).get(i);
					}
					sumDI.add(Qni);

					double Qi = Qni * dirFn1.get(i);
					errH.add(Qi);
					
					if(l==0){
						double v0i = lRate * Qi;
						wH.add(v0i);
					}else{
						double Vli  = lRate * Qi * _p[l-1];
						wH.add(Vli);
					}
				}
				weightHiddens.add(wH);

			}
			
			for(int l=0;l<N;l++){
				ArrayList<Double> vli = weightHiddens.get(l);
				for(int i=0;i<J;i++){
					V[l][i] += vli.get(i);
				}
			}
			
			for(int k=0;k<M;k++){
				ArrayList<Double> wik = weightCorrs.get(k);
				for(int i=0;i<J;i++){
					W[i][k] += wik.get(i);
				}
			}


		// }
		My.cout("V:\n"+printMatrix(v));
		My.cout("W:\n"+printMatrix(w));
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
	public static double[][] initMatrix(int rows, int cols, double rs){
		double[][] mat = initMatrix(rows, cols);

		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				mat[r][c] = rs;
			}
		}

		return mat;
	}
	public static double[][] initMatrix(int rows, int cols, double rx, double ry){
		double[][] mat = initMatrix(rows, cols);

		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				mat[r][c] = My.rndDouble(rx, ry);
			}
		}

		return mat;
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