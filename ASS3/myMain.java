import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class myMain{
	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");

		m2();

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
		My.cout("m1");
        My.cout("---------------");

		double acc = 0.1;

		int inputSize = 3; //N
		int instSize = 2; //J
		int outputSize = 2; //M

		double[][] v = initMatrix(inputSize+1, instSize, 0.25);
		for(int r=1;r<v.length;r++){
			for(int c=0;c<v[r].length;c++){
				if(c%2==0) v[r][c] = -1;
				v[r][c] = My.stepify(v[r][c], acc);
			}
		}
		double[][] w = initMatrix(instSize+1, outputSize, 0.45);
		for(int r=1;r<w.length;r++){
			for(int c=0;c<w[r].length;c++){
				if(c%2==0) w[r][c] = -1;
				w[r][c] = My.stepify(v[r][c], acc);
			}
		}
		
		My.cout("V:\n"+printMatrix(v));
		My.cout("W:\n"+printMatrix(w));
		
		//[number of inputs][inputSize]
		double[][] p = new double[][]{
			{1,-1,-1},
			{1,1,-1},
			{1,-1,1},
			// {1,1,1},
			// {-1,-1,-1},
			// {-1,1,1},
		};
		//[number of inputs][outputSize]
		double[][] t = new double[][]{
			{-1, 1},
			{1, -1},
			{1, 1},
			// {-1, -1},
			// {-1, -1},
			// {-1, -1},
		};
		
		double lRate = 1;

		boolean conv = false;
		int epochCount = 0;
		int epochLimit = 100000;

		double[][] V = v;
		double[][] W = w;
		
		int N = V.length; //inputSize+1
		int J = W.length; //instSize+1
		int M = W[0].length; //outputSize

		double[] lastdFN1 = new double[J-1];
		double[] lastdFN2 = new double[M];

		while(!conv && epochCount<epochLimit){
			// My.cout("EPOCH "+epochCount);
			conv = true;
			/*
			* [p0, p1, p2] (N = inputSize) [3]
			 * 
			 * [v00, v01] (inputSize+1, instSize) [3+1, 2]
			 * [v10, v11]
			 * [v20, v21]
			 * [v30, v31]
			 * 
			 * V [N+1,J] [l,i]
			 * 
			 * [h0, h1] (J = instSize) [2]
			 * 
			 * [w00, w01] (instSize+1, outputSize) [2+1, 2]
			 * [w10, w11]
			 * [w20, w21]
			 * 
			 * W [J+1,M] [i,k]
			 * 
			 * [t0, t1] (M = outputSize) [2]
			 */

			int setSize = p.length;

			for(int c=0; c<setSize; c++){
				double[] _p = p[c];
				double[] _t = t[c];

				// My.cout("p: "+printVector(_p));

				double[] FN1 = new double[J-1];
				double[] dFN1 = new double[J-1];
				double[] FN2 = new double[M];
				double[] dFN2 = new double[M];
				
				/// FEEDFORWARD

				//FeedForward Hidden Layer
				for(int i=1; i<J; i++){
					double _n1 = V[0][i-1];

					for(int l=1; l<N; l++){
						_n1 += My.stepify(V[l][i-1], acc) * My.stepify(_p[l-1], acc);
					}
					double _fn1 = Activate(My.stepify(_n1,acc), true);
					double _dirFn1 = DirActivate(My.stepify(_n1,acc), true);
					_fn1 = My.stepify(_fn1,acc);
					_dirFn1 = My.stepify(_dirFn1,acc);

					FN1[i-1] = _fn1;
					dFN1[i-1] = _dirFn1;
				}
				//FeedForward Output Layer
				for(int k=0; k<M; k++){
					double _n2 = W[0][k];

					for(int i=1; i<J; i++){
						_n2 += My.stepify(W[i][k],acc) * FN1[i-1];
					}
					double _fn2 = Activate(My.stepify(_n2,acc), true);
					double _dirFn2 = DirActivate(My.stepify(_n2,acc), true);
					_fn2 = My.stepify(_fn2,acc);
					_dirFn2 = My.stepify(_dirFn2,acc);

					FN2[k] = _fn2;
					dFN2[k] = _dirFn2;
				}

				//if the FNs did not change from last time, then theres convergence
				if(
					Arrays.equals(dFN1, lastdFN1) && 
					Arrays.equals(dFN2, lastdFN2)
				){
					// My.cout("convergence");
					continue;
				}else{
					// My.cout("not Conv");
					conv = false;
				}
				
				double[][] VLI = new double[N][J-1]; //li
				double[][] WIK = new double[J][M]; //ik
				double[] sumDI = new double[J];
	
				for(int i=0;i<J;i++){
					double Qni = 0;

					for(int k=0;k<M;k++){
						//Calculate the error information term for each node in the output layer
						//Qk = (tk - f(n2k)) * f'(n2k)
						//where k = 1 to m
						double Qk = (_t[k] - FN2[k]) * dFN2[k];
						Qk = My.stepify(Qk,acc);

						if(i==0){
							//Calculate the bias correction term for each node in the output layer
							// DELTA w0k = lRate * Qk
							//where k = 1 to m
							double w0k = lRate * Qk;
							w0k = My.stepify(w0k, acc);

							WIK[0][k] = w0k;
						}else{
							//Calculate the weight correction term for each node in the output layer
							// DELTA wik = lRate * Qk * f(n1i)
							//where l = 1 to n
							//where i = 1 to j
							double Wik = lRate * Qk * FN1[i-1];
							Wik = My.stepify(Wik, acc);
							
							WIK[i][k] = Wik;

							//Calculate the sum of delta inputs for each node in the hidden layer
							// Qni = SUM(Qk * wik)
							//where k = 1 to m
							//where i = 0 to j-1
							// Qni += Qk * Wik;
							Qni += Qk * W[i][k];
						}


					}
					Qni = My.stepify(Qni,acc);
					sumDI[i] = Qni;
				}
				for(int l=0;l<N;l++){
	
					for(int i=1;i<J;i++){

						double Qni = sumDI[i-1];
	
						//Calculate the error information term for each node in the hidden layer
						// Qi = Qni * f'(n1i)
						//where i = 0 to j-1
						double Qi = Qni * dFN1[i-1];
						
						if(l==0){
							//Calculate the bias error term for each node in the hidden layer
							// DELTA v0i = lRate * Qi
							//where i = 0 to j-1
							double v0i = lRate * Qi;
							v0i = My.stepify(v0i,acc);

							VLI[0][i-1] = v0i;
						}else{
							//Calculate the weight error term for each node in the hidden layer
							// DELTA vli = lRate * Qi * pl
							//where l = 1 to n
							//where i = 0 to j-1
							double Vli  = lRate * Qi * _p[l-1];
							Vli = My.stepify(Vli,acc);

							VLI[l][i-1] = Vli;
						}
					}
	
				}

				// My.cout("weightH: \n"+printMatrix(VLI));
				// My.cout("weightO: \n"+printMatrix(WIK));

				for(int l=0;l<N;l++){
					for(int i=1;i<J;i++){
						V[l][i-1] += VLI[l][i-1];
						V[l][i-1] = My.stepify(V[l][i-1],acc);
					}
				}
				for(int i=0;i<J;i++){
					for(int k=0;k<M;k++){
						W[i][k] += WIK[i][k];
						W[i][k] = My.stepify(W[i][k],acc);
					}
				}

				lastdFN1 = dFN1;
				lastdFN2 = dFN2;
				
			}

			epochCount++;

		}
		My.cout("V:\n"+printMatrix(v));
		My.cout("W:\n"+printMatrix(w));

		double[] input = p[0];
		double[] output = new double[M];

		double[] N1 = new double[J-1];
		double[] FN1 = new double[J-1];
		double[] dFN1 = new double[J-1];
		double[] N2 = new double[M];
		double[] FN2 = new double[M];
		double[] dFN2 = new double[M];
		
		/// FEEDFORWARD

		//FeedForward Hidden Layer
		for(int i=1; i<J; i++){
			double _n1 = 0;
			_n1 += My.stepify(V[0][i-1], acc);
			for(int l=1; l<N; l++){
				_n1 += My.stepify(V[l][i-1], acc) * My.stepify(input[l-1], acc);
			}
			double _fn1 = Activate(My.stepify(_n1,acc), true);
			double _dirFn1 = DirActivate(My.stepify(_n1,acc), true);
			_fn1 = My.stepify(_fn1,acc);
			_dirFn1 = My.stepify(_dirFn1,acc);

			N1[i-1] = _n1;
			FN1[i-1] = _fn1;
			dFN1[i-1] = _dirFn1;
		}
		//FeedForward Output Layer
		for(int k=0; k<M; k++){
			double _n2 = 0;
			_n2 += My.stepify(W[0][k],acc);
			for(int i=1; i<J; i++){
				_n2 += My.stepify(W[i][k],acc) * FN1[i-1];
			}
			double _fn2 = Activate(My.stepify(_n2,acc), true);
			double _dirFn2 = DirActivate(My.stepify(_n2,acc), true);
			_fn2 = My.stepify(_fn2,acc);
			_dirFn2 = My.stepify(_dirFn2,acc);

			N1[k] = _n2;
			FN2[k] = _fn2;
			dFN2[k] = _dirFn2;

			output[k] = _dirFn2;
		}

		My.cout("N1: "+printVector(N1));
		My.cout("FN1: "+printVector(FN1));
		My.cout("dFN1: "+printVector(dFN1));
		My.cout("N2: "+printVector(N2));
		My.cout("FN2: "+printVector(FN2));
		My.cout("dFN2: "+printVector(dFN2));
		My.cout("");
		My.cout("in: "+printVector(input));
		My.cout("out: "+printVector(output));

	}
	public static void m2(){
		My.cout("m2");
        My.cout("---------------");

		//[number of inputs][inputSize]
		double[][] p = new double[][]{
			{1,0,0},
			{1,1,0},
			{1,0,1},
			// {1,1,1},
		};
		//[number of inputs][outputSize]
		double[][] t = new double[][]{
			{0, 1},
			{1, 0},
			{1, 1},
			// {-1, -1},
		};
		int[] layerSizes = {p[0].length,2,2, t[0].length}; // {input, hidden...., output}

		String[] activationFuncs = {"sigmoid", "sigmoid", "sigmoid"};

		double[][][] tensor = new double[layerSizes.length-1][][];

		double acc = 0.001;

		// int inputSize = 3; //N
		// int instSize = 2; //J
		// int outputSize = 2; //M
		for(int en=0; en<tensor.length; en++){
			int rows = layerSizes[en] + 1;
			int cols = layerSizes[en+1];

			double[][] mat = initMatrix(rows, cols, -0.25, 0.25);
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					// if(r==0) mat[r][c] = 1;
					mat[r][c] = My.stepify(mat[r][c], acc);
				}
			}
			tensor[en] = mat;
			My.cout("M"+en+": "+printMatrix(mat));
		}
		
		double lRate = 1;
		boolean isBipolar = false;

		boolean conv = false;
		int epochCount = 0;
		int epochLimit = 100;
		
		while(!conv && epochCount<epochLimit){
			// My.cout("EPOCH "+epochCount);
			conv = true;
			/*
			* [p0, p1, p2] (N-1 = inputSize) [3]
			* 
			* [v00, v01] (inputSize+1, instSize) [3+1, 2]
			* [v10, v11]
			 * [v20, v21]
			 * [v30, v31]
			 * 
			 * V [N+1,J] [l,i]
			 * 
			 * [h0, h1] (J-1 = instSize) [2]
			 * 
			 * [w00, w01] (instSize+1, outputSize) [2+1, 2]
			 * [w10, w11]
			 * [w20, w21]
			 * 
			 * W [J+1,M] [i,k]
			 * 
			 * [t0, t1] (M = outputSize) [2]
			 */
			
			int setSize = p.length;
			
			double[][] prev_FN = new double[tensor.length][];
			double[][] prev_dFN = new double[tensor.length][];
			for(int c=0; c<setSize; c++){
				// double[][] V = tensor[layerCount];
				// double[][] W = tensor[layerCount+1];
				
				// int N = V.length; //inputSize+1
				// int J = W.length; //instSize+1
				// int M = W[0].length; //outputSize

				double[] _p = p[c];
				double[] _t = t[c];

				// My.cout("p: "+printVector(_p));

				// double[] FN1 = new double[J-1];
				// double[] dFN1 = new double[J-1];
				// double[] FN2 = new double[M];
				// double[] dFN2 = new double[M];

				double[][] FN = new double[tensor.length][];
				double[][] dFN = new double[tensor.length][];
				
				/// FEEDFORWARD

				for(int e=0; e<tensor.length; e++){
					double[][] V = tensor[e];
					int N = V.length;
					int J = V[0].length;
					FN[e] = new double[J];
					dFN[e] = new double[J];

					double[] preFN = _p;

					if(e>0){
						preFN = FN[e-1];
					}

					for(int i=0; i<J; i++){
						double _n = V[0][i];
	
						for(int l=1; l<N; l++){
							_n += V[l][i] * preFN[l-1];
						}
						double _fn = Activate(_n, activationFuncs[e], isBipolar);
						double _dirFn = DirActivate(_n, activationFuncs[e], isBipolar);
						_fn = My.stepify(_fn,acc);
						_dirFn = My.stepify(_dirFn,acc);
	
						FN[e][i] = _fn;
						dFN[e][i] = _dirFn;
					}
				}

				//if the FNs did not change from last time, then theres convergence
				if(
					Arrays.deepEquals(FN, prev_FN) && 
					Arrays.deepEquals(dFN, prev_dFN)
				){
					// My.cout("convergence");
					continue;
				}else{
					// My.cout("not Conv");
					conv = false;
					prev_FN = FN;
					prev_dFN = dFN;
				}

				for(int e=tensor.length-1; e>0; e--){
					double[][] W = tensor[e];
					double[][] V = tensor[e-1];
					int N = V.length;
					int J = W.length;
					int M = W[0].length;

					double[] FN1 = FN[e-1];
					double[] FN2 = FN[e];
					double[] dFN1 = dFN[e-1];
					double[] dFN2 = dFN[e];

					double[][] VLI = new double[N][J-1]; //li
					double[][] WIK = new double[J][M]; //ik
					double[] sumDI = new double[J];

					for(int i=0;i<J;i++){
						double Qni = 0;
	
						for(int k=0;k<M;k++){
							//Calculate the error information term for each node in the output layer
							//Qk = (tk - f(n2k)) * f'(n2k)
							//where k = 1 to m
							double Qk = (_t[k] - FN2[k]) * dFN2[k];
							// My.cout("tk: "+_t[k]);
							// My.cout("FN2k: "+FN2[k]);
							// My.cout("Qk: "+Qk);
							
							if(i==0){
								//Calculate the bias correction term for each node in the output layer
								// DELTA w0k = lRate * Qk
								//where k = 1 to m
								double w0k = lRate * Qk;
	
								WIK[0][k] = w0k;
	
								// Qni += Qk * W[0][k];
							}else{
								//Calculate the weight correction term for each node in the output layer
								// DELTA wik = lRate * Qk * f(n1i)
								//where l = 1 to n
								//where i = 1 to j
								double Wik = lRate * Qk * FN1[i-1];
								// My.cout("FN1i: "+FN1[i-1]);
								// My.cout("Wik: "+Wik);
								
								WIK[i][k] = Wik;
	
								//Calculate the sum of delta inputs for each node in the hidden layer
								// Qni = SUM(Qk * wik)
								//where k = 1 to m
								//where i = 0 to j-1
								// Qni += Qk * Wik;
								Qni += Qk * W[i][k];
							}
	
	
						}
						sumDI[i] = Qni;
					}
					for(int l=0;l<N;l++){
						for(int i=1;i<J;i++){
							double Qni = sumDI[i-1];
		
							//Calculate the error information term for each node in the hidden layer
							// Qi = Qni * f'(n1i)
							//where i = 0 to j-1
							double Qi = Qni * dFN1[i-1];
							
							if(l==0){
								//Calculate the bias error term for each node in the hidden layer
								// DELTA v0i = lRate * Qi
								//where i = 0 to j-1
								double v0i = lRate * Qi;
	
								VLI[0][i-1] = v0i;
							}else{
								//Calculate the weight error term for each node in the hidden layer
								// DELTA vli = lRate * Qi * pl
								//where l = 1 to n
								//where i = 0 to j-1
								double Vli  = lRate * Qi * _p[l-1];
	
								VLI[l][i-1] = Vli;
							}
						}
		
					}
	
					// My.cout("weightH: \n"+printMatrix(VLI));
					// My.cout("weightO: \n"+printMatrix(WIK));
	
					for(int l=0;l<N;l++){
						for(int i=1;i<J;i++){
							V[l][i-1] += VLI[l][i-1];
							V[l][i-1] = My.stepify(V[l][i-1],acc);
						}
					}
					for(int i=0;i<J;i++){
						for(int k=0;k<M;k++){
							W[i][k] += WIK[i][k];
							W[i][k] = My.stepify(W[i][k],acc);
						}
					}
				}
				
			}

			epochCount++;

		}
		My.cout("");
		for(int e=0;e<tensor.length;e++){
			double[][] mat = tensor[e];
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					// if(r==0) mat[r][c] = 1;
					mat[r][c] = My.stepify(mat[r][c], acc);
				}
			}
			My.cout("M"+e+": "+printMatrix(mat));
		}

		/// TESTING NETWORK

		for(int _p=0;_p<p.length;_p++){
			double[] input = p[_p];
			double[] output = new double[t[0].length];

			double[][] eN = new double[tensor.length][];
			double[][] FN = new double[tensor.length][];
			double[][] dFN = new double[tensor.length][];
	
			for(int e=0; e<tensor.length; e++){
				double[][] V = tensor[e];
				int N = V.length;
				int J = V[0].length;
				FN[e] = new double[J];
				dFN[e] = new double[J];
				eN[e] = new double[J];

				double[] preFN = input;

				if(e>0){
					preFN = FN[e-1];
				}

				for(int i=0; i<J; i++){
					double _n = V[0][i];

					for(int l=1; l<N; l++){
						_n += V[l][i] * preFN[l-1];
					}

					double _fn = Activate(_n, activationFuncs[e], isBipolar);
					double _dirFn = DirActivate(_n, activationFuncs[e], isBipolar);
					_fn = My.stepify(_fn,acc);
					_dirFn = My.stepify(_dirFn,acc);

					eN[e][i] = _n;
					FN[e][i] = _fn;
					dFN[e][i] = _dirFn;

					if(e==tensor.length-1){
						output[i] = _dirFn;
					}
				}
				My.cout("N"+e+": "+printVector(eN[e]));
				My.cout("FN"+e+": "+printVector(FN[e]));
				My.cout("dFN"+e+": "+printVector(dFN[e]));
				My.cout("");
			}
			My.cout("");
			My.cout("in: "+printVector(input));
			My.cout("out: "+printVector(output));
			My.cout("");

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
	
	// V = weight matrix between inputs and hidden
	// W = weight matrix between hidden and outputs
	// J = num of nodes in hidden layer
	// M = num of nodes in output layer
	// N = num of inputs
	// i = 0...J
	// k = 0...M
	// l = 0...N
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

	public static double Activate(double n, boolean bipolar){
		return Activate(n, "relu", bipolar);
	}
	public static double DirActivate(double n, boolean bipolar){
		return DirActivate(n, "relu", bipolar);
	}
	public static double Activate(double n, String func, boolean bipolar){
		switch(func){
			case "mcpitts": return MCPitts(n, 0, bipolar);
			case "relu": return ReLu(n, bipolar);
			case "sigmoid": return Sigmoid(n, bipolar);
			default: return 1;
		}
	}
	public static double DirActivate(double n, String func, boolean bipolar){
		switch(func){
			case "mcpitts": return MCPitts(n, 0, bipolar);
			case "relu": return Dir_ReLu(n, bipolar);
			case "sigmoid": return Dir_Sigmoid(n, bipolar);
			default: return 1;
		}
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
	public static double Sigmoid(double n, boolean bipolar){
		double e_n = Math.pow(Math.E,-n);
		if(bipolar){
			return 2 / (1 + e_n) - 1;
		}
		return 1 / (1 + e_n);
	}
	public static double Dir_Sigmoid(double n, boolean bipolar){
		double fn = Sigmoid(n, bipolar);
		if(bipolar){
			return 0.5 * (1 + fn) * (1 - fn);
		}
		return fn * (1 - fn);
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