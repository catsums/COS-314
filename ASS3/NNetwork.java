import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class NNetwork{
	public double[][][] tensor;
	public String[] activationFuncs;
	public String outputFunc = "relu";
	public double accuracy = 0.001;
	public double learningRate = 0.69;
	public boolean isBipolar = true;

	public NNetwork(int inputSize, int instSize, int outputSize, int numOfHiddenLayers){
		int[] layerSizes = new int[1+1+numOfHiddenLayers+1];
		for(int i=0;i<layerSizes.length;i++){
			if(i==0) layerSizes[i] = inputSize;
			if(i==1) layerSizes[1] = instSize;
			else layerSizes[i] = outputSize;
		}

		tensor = new double[layerSizes.length-1][][];
		for(int en=0; en<tensor.length; en++){
			int rows = layerSizes[en] + 1;
			int cols = layerSizes[en + 1];

			double[][] mat = initMatrix(rows, cols, 0);
			tensor[en] = mat;
		}

		activationFuncs = new String[layerSizes.length-1];
		for(int i=0;i<activationFuncs.length;i++){
			activationFuncs[i] = "sigmoid";
		}

	}

	public int getInputSize(){
		if(tensor.length<=0) return 0;
		
		double[][] layer = tensor[0];
		if(layer.length<=0) return 0;

		return layer.length-1;
	}

	public int getOutputSize(){
		if(tensor.length<=0) return 0;

		double[][] layer = tensor[tensor.length-1];
		if(layer.length<=0) return 0;

		double[] row = layer[0];
		return row.length;
	}

	public double[][] process(double[] input, double factor){
		double[] output = new double[getOutputSize()];
		double[] outputDIR = new double[getOutputSize()];

		double[][] eN = new double[tensor.length][];
		double[][] FN = new double[tensor.length][];

		for(int e=0; e<tensor.length; e++){
			double[][] V = tensor[e];
			int N = V.length;
			int J = V[0].length;
			FN[e] = new double[J];
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

				double _fn = Activate(_n, factor, activationFuncs[e], isBipolar);
				_fn = My.stepify(_fn,accuracy);

				eN[e][i] = _n;
				FN[e][i] = _fn;

				if(e==tensor.length-1){
					output[i] = Activate(_n, factor, outputFunc, isBipolar);
					outputDIR[i] = DirActivate(_n, factor,outputFunc, isBipolar);
				}
			}
		}

		return new double[][]{output, outputDIR};
	}

	public double[][][] trainNetwork(double[][] inputs, double[][] expectedOutputs, double factor, long epochLimit){
		double[][][] oldTensor = tensor.clone();

		double acc = accuracy;
		double lRate = learningRate;
		
		for(int en=0; en<tensor.length; en++){
			double[][] mat = tensor[en];
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					mat[r][c] = My.stepify(mat[r][c], acc);
				}
			}
		}

		boolean conv = false;
		long epochCount = 0;

		double[][] p = inputs;
		double[][] t = expectedOutputs;

		while(!conv && epochCount<epochLimit){
			
			conv = true;
			
			int setSize = p.length;
			
			double[][] prev_FN = new double[tensor.length][];
			double[][] prevERRs = null;

			for(int c=0; c<setSize; c++){

				double[] _p = p[c];
				double[] _t = t[c];

				double[][] FN = new double[tensor.length][];
				double[][] dFN = new double[tensor.length][];
				double[][] ERRs = new double[tensor.length-1][];
				
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
						double _fn = Activate(_n, factor, activationFuncs[e], isBipolar);
						double _dirFn = DirActivate(_n, factor, activationFuncs[e], isBipolar);
						_fn = My.stepify(_fn,acc);
						_dirFn = My.stepify(_dirFn,acc);
	
						FN[e][i] = _fn;
						dFN[e][i] = _dirFn;
					}
				}

				//if the FNs did not change from last time, then theres convergence
				if(
					Arrays.deepEquals(FN, prev_FN)
				){
					My.cout("Convergence in network detected. No changes in FN");
					continue;
				}else{
					conv = false;
					prev_FN = FN;
				}
				
				for(int e=tensor.length-1;e>0; e--){
					double[][] V = tensor[e-1];
					double[][] W = tensor[e];
					int N = V.length;
					int J = W.length;
					int M = W[0].length;

					double[] FN1 = FN[e-1];
					double[] dFN1 = dFN[e-1];
					double[] FN2 = FN[e];
					double[] dFN2 = dFN[e];

					double[][] VLI = new double[N][J-1]; //li
					double[][] WIK = new double[J][M]; //ik
					double[] sumDI = new double[J];

					double[] QK = new double[M];
					double[] postFN = _t;
					if(e<tensor.length-1) postFN = FN[e+1];

					for(int i=0;i<J;i++){
	
						for(int k=0;k<M;k++){
							//Calculate the error information term for each node in the output layer
							//Qk = (tk - f(n2k)) * f'(n2k)
							//where k = 1 to m
							

							double Qk = (postFN[(k%postFN.length)] - FN2[k]) * dFN2[k];
							
							QK[k] = My.stepify(Qk, acc);
							
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
								
								WIK[i][k] = Wik;
	
								
							}
							
						}
					}
					
					// My.cout("WeightCorrection for Layer ("+e+"): \n"+printMatrix(WIK));
					
					ERRs[e-1] = QK;
					
					//Update weights in the output layer
					for(int i=0;i<J;i++){
						for(int k=0;k<M;k++){
							W[i][k] += WIK[i][k];
							W[i][k] = My.stepify(W[i][k],acc);
						}
					}
					
					
					//Calculate the sum of delta inputs for each node in the hidden layer
					// Qni = SUM(Qk * wik)
					//where k = 1 to m
					//where i = 0 to j-1
					for(int i=0;i<J;i++){
						double Qni = 0;
						for(int k=0;k<M;k++){
							double Qk = (ERRs[e-1][k] - FN2[k]) * dFN2[k];
							Qni += Qk * W[i][k];
							// Qni += Qk * WIK;
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
								double[] preFN = _p;
								if(e-1>0) preFN = FN[e-2];

								double Vli  = lRate * Qi * preFN[l-1];
	
								VLI[l][i-1] = Vli;
							}
						}
		
					}
	
					//Update weights in the hidder layer
					for(int l=0;l<N;l++){
						for(int i=1;i<J;i++){
							V[l][i-1] += VLI[l][i-1];
							V[l][i-1] = My.stepify(V[l][i-1],acc);
						}
					}
				}

				
				// My.cout("ErrorCorrection Terms per Layer:");
				// for(double[] corr:ERRs){
				// 	if(corr==null) continue;
				// 	My.cout(printVector(corr));
				// }
				if(prevERRs==null){
					prevERRs = new double[ERRs.length][ERRs[0].length];
				}

				//if there's no change in error terms, then theres convergence
				if(Arrays.deepEquals(ERRs, prevERRs)){
					My.cout("Convergence in network detected. No changes in ERRs");
					conv = true;
					break;
				}else{
					prevERRs = ERRs;
				}
				
			}

			epochCount++;
		}

		// My.cout("Took "+epochCount+" epochs");
		for(int en=0; en<tensor.length; en++){
			double[][] mat = tensor[en];
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					mat[r][c] = My.stepify(mat[r][c], acc);
				}
			}
		}

		return oldTensor;
	}

	public static double Activate(double n, boolean bipolar){
		return Activate(n, 0, "relu", bipolar);
	}
	public static double DirActivate(double n, boolean bipolar){
		return DirActivate(n, 0, "relu", bipolar);
	}
	public static double Activate(double n, double x, String func, boolean bipolar){
		switch(func){
			case "mcpitts": return MCPitts(n, x, bipolar);
			case "relu": return ReLu(n, x, bipolar);
			case "sigmoid": return Sigmoid(n, x, bipolar);
			default: return 1;
		}
	}
	public static double DirActivate(double n, double x, String func, boolean bipolar){
		switch(func){
			case "mcpitts": return MCPitts(n, x, bipolar);
			case "relu": return Dir_ReLu(n, x, bipolar);
			case "sigmoid": return Dir_Sigmoid(n, x, bipolar);
			default: return 1;
		}
	}

	public static double MCPitts(double n, double x, boolean bipolar){
		return ((n>=x) ? 1 : (bipolar ? -1 : 0));
	}

	public static double ReLu(double n, double r, boolean bipolar){
		double x = Math.max(0, n);

		return (x>=r) ? x : (bipolar ? -1 : 0);
	}
	public static double Dir_ReLu(double n, double r, boolean bipolar){
		return (n>r) ? 1 : (bipolar ? -1 : 0);
	}
	public static double Sigmoid(double n, double r, boolean bipolar){
		double e_n = Math.pow(Math.E,-n);
		if(bipolar){
			return 2 / (1 + e_n) - 1 + r;
		}
		return 1 / (1 + e_n) + r;
	}
	public static double Dir_Sigmoid(double n, double r, boolean bipolar){
		double fn = Sigmoid(n,r, bipolar);
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