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

	public static void m2(){
		My.cout("m2");
        My.cout("---------------");

		File dataFile = new File("data/breast-cancer.data");

		ArrayList<double[]> dataset = new ArrayList<>();
		ArrayList<double[]> classSet = new ArrayList<>();

		ArrayList<double[]> fixSet = new ArrayList<>();

		String[][] attributes = {
			{"no-recurrence-events", "recurrence-events"},
			{"10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99"},
			{"lt40", "ge40", "premeno"},
			{"0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44","45-49", "50-54", "55-59"},
			{"0-2", "3-5", "6-8", "9-11", "12-14", "15-17", "18-20", "21-23", "24-26","27-29", "30-32", "33-35", "36-39"},
			{"yes", "no"},
			{"1", "2", "3"},
			{"left", "right"},
			{"left_up", "left_low", "right_up", "right_low", "central"},
			{"yes", "no"},
		};

		double ratio = (0.75);

		long _seed = 9_876_543_210l;

		double acc = 0.001;
		double lRate = 1;
		boolean isBipolar = false;

		double factor = 0.01;
		
		ArrayList<String[]> lines = new ArrayList<>();
		try{
			Scanner scanner = new Scanner(dataFile);
			int count = 0; int len = 286;
			

			while(scanner.hasNextLine()){
				String line = scanner.nextLine();

				String[] vars = line.split(",");
				lines.add(vars);

				count++;
			}

			My.cout("Counted "+count+" batch instances.");
			
		}catch(Exception e){
			e.printStackTrace();
		}

		String[][] _lines = shuffleArray(lines.toArray(new String[0][]), _seed);
		_lines = shuffleArray(_lines, _seed);

		for(String[] vars:_lines){
			// double[] data = new double[9];
			ArrayList<Double> data = new ArrayList<>();
			ArrayList<Double> classifier = new ArrayList<>();

			boolean dataMissing = false;

			for(int i=0;i<attributes.length;i++){
				double attr = -1;
				for(int a=0;a<attributes[i].length;a++){
					String _var = vars[i].trim().toLowerCase();
					String _att = attributes[i][a].trim().toLowerCase();
					if(_var.compareTo(_att)==0){
						attr = a;
						break;
					}
				}
				if(attr!=-1){
					if(i==0 || i==6 || i==9){
						double k = ((attr+1) / attributes[i].length);
						k = (k>0.5 ? 1 : 0);
						classifier.add(k);
						// classifier = attr;
					}else{
						double k = ((attr+1) / attributes[i].length);
						// k = (k>0 ? 1 : 0);
						data.add(My.stepify(k, acc));
						// data[i-1] = attr;
					}
				}else{
					dataMissing = true;
					data.add(-1.0);
				}
			}
			double[] dataArr = new double[data.size()];
			for(int i=0;i<dataArr.length;i++) dataArr[i] = data.get(i);
			double[] classArr = new double[classifier.size()];
			for(int i=0;i<classArr.length;i++) classArr[i] = classifier.get(i);

			if(dataMissing){
				fixSet.add(dataArr);
			}else{
				dataset.add(dataArr);
				classSet.add(classArr);
			}
		}

		// My.cout(printMatrix(dataset.toArray(new double[0][])));

		ArrayList<double[]> trainData = new ArrayList<>();
		ArrayList<double[]> trainOutData = new ArrayList<>();

		for(int i=0;i<(int)(ratio * (double)dataset.size()); i++){
			// if(classSet.get(i)[0] != 0){
				trainData.add(dataset.get(i));
				trainOutData.add(classSet.get(i));
			// }
			// trainOutData.add(new double[]{arr[i][0],arr[i][5], arr[i][8]});
		}

		ArrayList<double[]> testData = new ArrayList<>();
		ArrayList<double[]> testOutData = new ArrayList<>();

		for(int i=trainData.size();i<dataset.size(); i++){
			testData.add(dataset.get(i));
			testOutData.add(classSet.get(i));
			// testOutData.add(new double[]{arr[i][0],arr[i][5], arr[i][8]});
		}

		// for(double[] fix:fixSet){
		// 	testData.add(fix);
		// }

		My.cout("Dataset: "+dataset.size());
		My.cout("TrainData: "+trainData.size());
		My.cout("TrainOutData: "+trainOutData.size());
		My.cout("TestData: "+testData.size());
		My.cout("TestOutdata: "+testOutData.size());
		My.cout("FixData: "+fixSet.size());

		int inputSize = trainData.get(0).length;
		int outputSize = trainOutData.get(0).length;

		My.cout("");
		My.cout("InputSize: "+inputSize);
		My.cout("OutputSize: "+outputSize);

		int[] instSizes = {5};

		NNetwork net = new NNetwork(inputSize, instSizes, outputSize);

		net.accuracy = acc;
		net.isBipolar = isBipolar;
		net.learningRate = lRate;

		// net.activationFuncs = new String[net.getNumberOfLayers()];
		net.activationFuncs[0] = "sigmoid";
		for(int f=0;f<net.activationFuncs.length;f++){
			net.activationFuncs[f] = "sigmoid";
		}
		net.outputFunc = "mcpitts";

		My.cout("");
		My.cout("Tensor:");
		for(int en=0;en<net.tensor.length;en++){
			net.tensor[en] = initMatrix(net.tensor[en].length, net.tensor[en][0].length, -0.5, 0.5, _seed);
			double[][] mat = net.tensor[en];
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					// if(r==0) mat[0][c] = 1;
					mat[r][c] = My.stepify(mat[r][c], acc);
				}
			}
			My.cout("M"+en+": \n"+printMatrix(net.tensor[en]));
		}

		net.trainNetwork(
			trainData.toArray(new double[0][]),
			trainOutData.toArray(new double[0][]),
			factor, 1_000l
		);

		My.cout("");
		My.cout("Trained Tensor:");
		for(int en=0;en<net.tensor.length;en++){
			My.cout("M"+en+": \n"+printMatrix(net.tensor[en]));
		}

		My.cout("");

		double correct = 0; 

		for(int i=0; i<testData.size(); i++){
			double[] input = testData.get(i);
			double[] expOut = testOutData.get(i);

			double[][] res = net.process(input, factor);
	
			My.cout("In: "+printVector(input));
			My.cout("Out: "+printVector(res[0]));
			My.cout("OutDIR: "+printVector(res[1]));
			My.cout("ExpOut: "+printVector(expOut));
			My.cout("------");

			if(Arrays.equals(res[1], expOut)) correct++;
		}

		My.cout("SuccessRate: "+(correct/testData.size()));
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

	
	public static void m1(){
		My.cout("m1");
        My.cout("---------------");

		//[number of inputs][inputSize]
		double[][] p = new double[][]{
			{0,0,0},
			{0,1,0},
			{0,1,1},
			{1,0,1},
			{1,1,0},
			{0,0,1},
			{1,0,0},
			// {-1,-1,-1},
		};
		//[number of inputs][outputSize]
		double[][] t = new double[][]{
			{1},{0},{1},{1},{1},{0},{0}
			// {-1, -1},
		};

		double acc = 0.001;
		double lRate = 1.5;
		boolean isBipolar = false;
		long seed = 20;

		int[] instSizes = {4};
		
		NNetwork net = new NNetwork(p[0].length, instSizes, t[0].length);

		My.cout("Before:");
		for(int e=0;e<net.tensor.length;e++){
			double[][] mat = net.tensor[e];
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					mat[r][c] = My.stepify(
						My.rndDouble(
							-0.5, 0.5, (-(r+c)*seed + 2*(r-c)*seed + 2*(c-r)*seed - (mat.length-mat[r].length))*1234567890
						), acc
					);
					// mat[r][c] = 0;
				}

			}
			My.cout("M"+e+":\n "+printMatrix(mat));
		}
		
		net.isBipolar = isBipolar;
		net.learningRate = lRate;
		net.accuracy = acc;

		for(int i=0;i<net.activationFuncs.length;i++){
			net.activationFuncs[i] = "sigmoid";
		}
		net.outputFunc = "relu";
		// net.activationFuncs[0] = "relu";

		net.trainNetwork(p, t, 0, 420);
		
		My.cout("After:");
		for(int e=0;e<net.tensor.length;e++){
			double[][] mat = net.tensor[e];
			My.cout("M"+e+":\n "+printMatrix(mat));
		}

		/// TESTING NETWORK

		double[][] tests = {
			{0,1,0}, {1,1,0}, {0,1,1}, {0,0,1}
		};

		for(int _p=0;_p<tests.length;_p++){
			double[] input = tests[_p];
			double[] output;
			double[] outDir;

			double[][] res = net.process(input, 0);
			output = res[0];
			outDir = res[1];

			My.cout("");
			My.cout("in: "+printVector(input));
			My.cout("out: "+printVector(output));
			My.cout("outDIR: "+printVector(outDir));
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
	public static double[][] initMatrix(int rows, int cols, double rx, double ry, long seed){
		double[][] mat = initMatrix(rows, cols);

		for(int r=0;r<rows;r++){
			for(int c=0;c<cols;c++){
				mat[r][c] = My.rndDouble(rx, ry, ( My.rndInt(-999,999,r+c+seed) + seed ));
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