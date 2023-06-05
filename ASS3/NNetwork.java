import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class NNetwork implements Serializable{
	public double[][][] tensor;
	public String[] activationFuncs;
	public double accuracy = 0.001;
	public double learningRate = 0.69;
	public boolean isBipolar = true;

	public NNetwork(int inputSize, int[] instSizes, int outputSize){
		int[] layerSizes = new int[1+instSizes.length+1];
		layerSizes[0] = inputSize;
		layerSizes[layerSizes.length-1] = outputSize;
		for(int i=1;i<layerSizes.length-1;i++){
			layerSizes[i] = instSizes[i-1];
		}

		tensor = new double[layerSizes.length-1][][];
		for(int en=0; en<tensor.length; en++){
			int rows = layerSizes[en] + 1;
			int cols = layerSizes[en + 1];

			double[][] mat = initMatrix(rows, cols, 0);
			tensor[en] = mat;
		}

		activationFuncs = new String[layerSizes.length];
		for(int i=0;i<activationFuncs.length;i++){
			activationFuncs[i] = "sigmoid";
		}

	}

	public void setOutputActivationFunc(String func){
		activationFuncs[activationFuncs.length-1] = func;
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

	public int getNumberOfLayers(){
		return tensor.length;
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

			// My.cout("e: "+e);
			
			for(int i=0; i<J; i++){
				double _n = V[0][i];
				
				for(int l=1; l<N; l++){
					_n += (V[l][i] * preFN[l-1]);
				}

				double _fn = Activate(_n, factor, activationFuncs[e], isBipolar);
				double _dirfn = DirActivate(_n, factor, activationFuncs[e], isBipolar);
				_fn = My.stepify(_fn,accuracy);

				eN[e][i] = _n;
				FN[e][i] = _fn;

				// My.cout("N: "+_n);

				if(e==tensor.length-1){
					output[i] = _fn;
					outputDIR[i] = _dirfn;
					// output[i] = Activate(_n, factor, outputActivation, isBipolar);
					// outputDIR[i] = DirActivate(_n, factor, outputActivation, isBipolar);
					
					output[i] = My.stepify(output[i], accuracy);
					outputDIR[i] = My.stepify(outputDIR[i], accuracy);
				}
			}
		}

		return new double[][]{output, outputDIR};
	}

	public double[][][] trainNetwork(double[][] inputs, double[][] expectedOutputs, double factor, long seed, long epochLimit){
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

		HashMap<double[],double[]> PTMap = new HashMap<>();

		for(int i=0;i<inputs.length;i++){
			PTMap.put(p[i], t[i]);
		}
		double[][] shiffledInputs = My.shuffleArray(PTMap.keySet().toArray(new double[0][]), 12345*seed*(epochCount*2)*(epochCount+1));

		while(!conv && epochCount<epochLimit){
			
			conv = true;
			
			int setSize = PTMap.size();
			
			double[][] prev_FN = new double[tensor.length][];
			double[] prevSums = null;
			double[][] prevERRs = null;
			double errors = 0;
			
			for(int c=0; c<setSize; c++){
				
				double[] _p = shiffledInputs[c];
				double[] _t = PTMap.get(_p);
				// double[] _p = p[c];
				// double[] _t = t[c];
				
				double[][] FN = new double[tensor.length][];
				double[][] dFN = new double[tensor.length][];
				double[][] ERRs = new double[tensor.length][];
				
				/// FEEDFORWARD

				for(int e=0; e<tensor.length; e++){
					double[][] V = tensor[e];
					int N = V.length;
					int J = V[0].length;
					FN[e] = new double[J];
					dFN[e] = new double[J];

					double[] preFN = _p;
					// double[] postFN = _t;

					if(e>0){
						preFN = FN[e-1];
					}

					for(int i=0; i<J; i++){
						double _n = V[0][i]; //bias
	
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

				
				// //if the FNs did not change from last time, then theres convergence
				if(
					Arrays.deepEquals(FN, prev_FN)
				){
					// My.cout("Convergence in network detected. No changes in FN");
					continue;
				}else{
					conv = false;
					prev_FN = FN;
				}

				///BACKPROPAGATION

				double partialFN = 0;
				double totalErr = 0;

				ArrayList<Double> ptFN = null;
				
				for(int e=tensor.length-1;e>=0; e--){
					double[][] W = tensor[e];
					int J = W.length;
					int M = W[0].length;
					
					// My.cout("----"+e+"----");

					// My.cout("M: "+M);
					// My.cout("J: "+J);
					
					double[] FN2 = FN[e];
					double[] dFN2 = dFN[e];

					// My.cout("FN2: "+Arrays.toString(FN2));
					
					// My.cout("W:\n"+printMatrix(W)+"\n");
					
					if(e==0){
						for(int i=1;i<J;i++){
							for(int k=0;k<M;k++){
								// My.cout("i: "+i+" k:"+k);
								// My.cout("fn2k: "+FN2[k]);
								// My.cout("dfn2k: "+dFN2[k]);
								// My.cout("partialFN:"+partialFN);

								double Q = ptFN.get(k) * dFN2[k];
								double delta = (lRate * Q);
								if(i>0){
									delta = delta * (_p[i-1]);
								}

								// My.cout("Q: "+Q);
								// My.cout("delta: "+Q);
								// My.cout("--");
	
								W[i][k] += delta;
	
								partialFN += (Q * W[i][k]);
	
							}
						}
					}else{
						double[][] V = tensor[e-1];
						int N = V.length;
						double[][] WIK = new double[J][M]; //ik
						double[] FN1 = FN[e-1];
						double[] dFN1 = dFN[e-1];
						
						// My.cout("FN1: "+Arrays.toString(FN1));
						// My.cout("N: "+N);
	
						double[] postFN = _t;
						if(e<tensor.length-1){
							postFN = FN[e+1];
						}

						double[][] VLI = new double[N][J-1]; //li

						// My.cout("V:\n"+printMatrix(V)+"\n");
						ArrayList<Double> _ptFN = new ArrayList<>();
						
						for(int i=1;i<J;i++){
							double _partialFN = 0;
							for(int k=0;k<M;k++){
								// My.cout("i: "+i+" k:"+k);
								// My.cout("tk: "+_t[k]);
								// My.cout("fn2k: "+FN2[k]);
								// My.cout("dfn2k: "+dFN2[k]);
								// My.cout("diff "+diff);
								double Q = dFN2[k];
								double diff;
								if(e==tensor.length-1){
									diff = (_t[k]-FN2[k]);
								}else{
									// diff = (postFN[k] - FN2[k]);
									diff = (FN2[k] * ptFN.get(k));
								}
								Q = dFN2[k] *  diff;
								// if(e<tensor.length-1){
								// 	// My.cout("initQ: "+Q);
								// 	// My.cout("partialFN:"+partialFN);
								// 	Q *= partialFN;
								// }

								double delta = (lRate * Q);
								if(i>0){
									delta = delta * (FN1[i-1]);
								}

								// My.cout("Q: "+Q);
								// My.cout("delta: "+delta);
								// My.cout("--");
	
								W[i][k] += delta;
	
								_partialFN += (Q * W[i][k]);
	
								totalErr += Math.pow(diff, 2);
							}
							_ptFN.add(_partialFN);
						}

						ptFN = _ptFN;
	
						totalErr *= 0.5;

					}

				}
				errors += totalErr;

				if(totalErr==0){
					My.cout("Convergence in network detected. No changes in totalErr");
					conv = true;
					break;
				}

				// ArrayList<Double> sumDeltas = null;
				
				// for(int e=tensor.length-1;e>=0;e--){
				// 	double[][] V = tensor[e];
				// 	int N = V.length;
				// 	int J = V[0].length + 1;
					
				// 	My.cout("-----"+e+"-------");
				// 	My.cout("-----");
				// 	My.cout("V:\n"+printMatrix(V));
				// 	My.cout("N: "+N);
				// 	My.cout("J: "+J);

				// 	double[] FN1 = FN[e];
				// 	double[] dFN1 = dFN[e];
				// 	My.cout("FN["+e+"] : "+Arrays.toString(FN[e]));
				// 	My.cout("dFN["+e+"] : "+Arrays.toString(dFN[e]));
				// 	// double[] FN2 = FN[e+1];
				// 	// double[] dFN2 = dFN[e+1];

				// 	// double[][] VLI = new double[N][J-1]; //li
				// 	// double[][] WIK = new double[J][M]; //ik
					
				// 	double[] preFN = _p;
				// 	if(e>0){
				// 		preFN = FN[e-1];
				// 	}
				// 	double[] postFN = _t;
				// 	if(e<tensor.length-1){
				// 		postFN = FN[e+1];
				// 	}
					
				// 	double[] errTerms = new double[J-1];

				// 	if(e==tensor.length-1){
				// 		for(int i=0; i<J-1; i++){
				// 			double fn = FN1[i];
				// 			double Qi = (_t[i] - fn);
				// 			errTerms[i] = Qi;
				// 			My.cout("_t["+(i)+"]:"+_t[i]);
				// 			My.cout("fn:"+fn);
				// 			My.cout("Qi("+i+"):"+Qi);
				// 		}
				// 	}else{
				// 		double[][] W = tensor[e+1];
				// 		int M = W[0].length;
				// 		My.cout("W:\n"+printMatrix(W));
				// 		for(int i=1; i<J; i++){
				// 			double Qk = 0;
				// 			for(int k=0; k<M; k++){
				// 				My.cout("sumDelta("+k+"):"+sumDeltas.get(k));
				// 				Qk += W[i][k] * sumDeltas.get(k);
				// 			}
				// 			My.cout("Qk("+i+"):"+Qk);
				// 			errTerms[i-1] = Qk;
				// 		}
				// 	}

				// 	ERRs[e] = errTerms;
					
				// 	for(int l=0;l<N;l++){
				// 		for(int i=0;i<J-1;i++){
				// 			double Qk = errTerms[i];
				// 			My.cout("Q("+i+"):"+Qk);
				// 			V[l][i] += lRate * Qk;
				// 			V[l][i] = My.stepify(V[l][i], acc);
				// 		}
				// 	}
					
				// 	if(e>0){
				// 		sumDeltas = new ArrayList<>();
				// 		for(int i=0; i<J-1; i++){
				// 			double Qk = errTerms[i];
				// 			double Qni = Qk * (dFN1[i]) * (1 - dFN1[i]);
				// 			// double Qni = Qk * (FN1[i]) * (1 - FN1[i]);
				// 			// double Qni = Qk * (FN1[i]);
				// 			// double Qni = Qk * (dFN1[i]);
				// 			sumDeltas.add(Qni);
				// 		}
				// 	}

				// }
				
				if(prevERRs==null){
					prevERRs = new double[ERRs.length][];
				}
				//if there's no change in error terms, then theres convergence
				// if(Arrays.deepEquals(ERRs, prevERRs)){
				// 	My.cout("Convergence in network detected. No changes in ERRs");
				// 	conv = true;
				// 	break;
				// }else{
				// 	conv = false;
				// 	prevERRs = ERRs;
				// }
				
			}
			epochCount++;

			My.cout("Error: "+errors);

		}

		My.cout("Took "+epochCount+" epochs");
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