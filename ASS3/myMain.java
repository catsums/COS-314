import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class myMain{
	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");

		m3();

		My.cout("---------------"); My.cout("| MAIN END |");
		return;
	}

	public static void m4(){
		My.cout("m4");
        My.cout("---------------");

		File dataFile = new File("data/breast-cancer.data");

		ArrayList<ArrayList<String>> dataset = new ArrayList<>();

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
		double acc = 0.01;

		long _seed = 9_876_543_210l;

		double factor = 0.01;
		
		ArrayList<String[]> lines = new ArrayList<>();
		try{
			Scanner scanner = new Scanner(dataFile);
			int count = 0;
			// int len = 286;
			int len = 286;

			while(scanner.hasNextLine() && count<len){
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
		_lines = shuffleArray(_lines, _seed);

		for(String[] vars:_lines){
			ArrayList<String> data = new ArrayList<>();

			boolean missing = false;

			for(int i=0;i<attributes.length;i++){
				int attr = -1;
				for(int a=0;a<attributes[i].length;a++){
					String _var = vars[i].trim().toLowerCase();
					String _att = attributes[i][a].trim().toLowerCase();
					if(_var.compareTo(_att)==0){
						attr = a;
						break;
					}
				}
				if(attr!=-1){
					data.add(attributes[i][attr]);
				}else{
					missing = true;
					data.add("?");
				}
			}

			if(!missing) dataset.add(data);
		}


		DT tree = new DT(new String[]{
			"Class","age","menopause","tumor-size","inv-nodes","node-caps","deg-malig","breast","breast-quad","irradiat"
		});

		for(ArrayList<String> data:dataset){
			tree.addData(data.toArray(new String[0]));
		}

		String chosenAtt = "irradiat";
		int attIndex = Arrays.asList(tree.attributes).indexOf(chosenAtt);

		My.cout("Attr: "+chosenAtt+" ("+attIndex+")");

		// for(int i=0;i<tree.attributes.length;i++){
		// 	if(tree.attributes[i].compareTo(chosenAtt)==0){
		// 		attIndex = i; break;
		// 	}
		// }

		DT.Node root = tree.ID3(chosenAtt);

		My.cout(root);

		ArrayList<ArrayList<String>> set = dataset;

		double correct = 0;
		double len = set.size();


		for(ArrayList<String> input:set){
			String expOutput = input.get(attIndex);
			String output = tree.decideLabel(root, input);

			if(output==null){
				My.cout("Err in output...");
			}else if(expOutput.compareTo(output)==0){
				correct++;
			}

			My.cout("Output: "+output);
			My.cout("ExpOutput: "+expOutput);
			My.cout("------");
		}

		double successRate = My.stepify((correct/len)*100, acc);

		My.cout("");
		My.cout("SuccessRate: "+successRate);

	}

	public static void m3(){
		My.cout("m3");
        My.cout("---------------");

		DT tree = new DT(new String[]{"Outlook","Temp","Humidity","Wind","Netball"});

		String[][] data = new String[][]{
			new String[]{"sun",	"hot",	"high",	"weak",		"no"},
			new String[]{"sun",	"hot",	"high",	"strong",	"no"},
			new String[]{"oc",		"hot",	"high",	"weak",		"yes"},
			new String[]{"rain",	"mild",	"high",	"weak",		"yes"},
			new String[]{"rain",	"cool",	"normal",	"weak",		"yes"},
			new String[]{"rain",	"cool",	"normal",	"strong",	"no"},
			new String[]{"oc",		"cool",	"normal",	"strong",	"yes"},
			new String[]{"sun",	"mild",	"high",	"weak",		"no"},
			new String[]{"sun",	"cool",	"normal",	"weak",		"yes"},
			new String[]{"rain",	"mild",	"normal",	"weak",		"yes"},
			new String[]{"sun",	"mild",	"normal",	"strong",	"yes"},
			new String[]{"oc",		"mild",	"high",	"strong",	"yes"},
			new String[]{"oc",		"hot",	"normal",	"weak",		"yes"},
			new String[]{"rain",	"mild",	"high",	"strong",	"no"},
		};

		for(String[] dat:data){
			tree.addData(dat);
		}

		ArrayList<DT.Node> trees = new ArrayList<>();

		String targetAttr = "Netball";

		trees.add( tree.ID3(targetAttr) );
		long seed = 696969;

		ArrayList<ArrayList<String>> dataset = new ArrayList<>();
		for(String[] dat:data){
			dataset.add(new ArrayList<>(Arrays.asList(dat)));
		}
		
		////////////

		DT.Node finalTree = GPClassfication(tree, targetAttr, dataset, 5, 20, 0.5, 0.5, seed);

		My.cout("FinalTree:\n "+finalTree);
		My.cout("Fitness:"+GetFitnessOfNode(finalTree, dataset, tree, targetAttr));

		/////////

		// for(int i=0; i<5; i++){
		// 	trees.add( tree.RandomID3("Netball", seed * i * i-1) );
		// }

		// for(DT.Node root:trees){
		// 	My.cout(root);
		// }

		// DT.Node nA = trees.get(2);
		// DT.Node nB = trees.get(1);
		// DT.Node nC = trees.get(3);

		// My.cout("nA:\n "+nA);
		// My.cout("nB:\n "+nB);
		// My.cout("nC:\n "+nC);

		// DT.Node[] offspring = CrossOver(new DT.Node[]{
		// 	nA, nB
		// }, tree, 1, seed);

		// ArrayList<DT.Node> stuff = new ArrayList<>(Arrays.asList(offspring));

		// stuff.add(
		// 	Mutate(nC, "Netball", tree, 1, seed)
		// );

		// for(DT.Node ch:stuff){
		// 	My.cout(">\n "+ch);

		// 	double corr = 0; double len = data.length;
			
		// 	for(String[] dat:data){
		// 		String expOut = dat[4];
		// 		String out = tree.decideLabel(ch, new ArrayList<>(Arrays.asList(dat)));

		// 		if(out.compareTo(expOut)==0){
		// 			corr++;
		// 		}
				
		// 	}
		// 	double fitness = (corr/len);
		// 	My.cout("Node Count: "+ch.DFSCountNodes());
		// 	My.cout("Node Depth: "+ch.DFSDepth());
		// 	My.cout("Fitness: "+fitness);
		// }

		//////////////
		
		// My.cout("nA:\n "+nA);
		// DT.Node nA1 = nA.DFSFindNodeByAttr("Wind");
		// My.cout("nA1:\n "+nA1);
		// DT.Node nA1p = nA1.DFSFindParent(nA);
		// My.cout("nA1p:\n "+nA1p);

		// My.cout("");

		// My.cout("nB:\n "+nB);
		// DT.Node nB1 = nB.DFSFindNodeByAttr("Wind");
		// My.cout("nB1:\n "+nA1);
		// DT.Node nB1p = nB1.DFSFindParent(nB);
		// My.cout("nB1p:\n "+nB1p);
		
		// DT.Node nAx = new DT.Node(nA);
		// My.cout("nAx:\n "+nAx);
		// DT.Node nBx = new DT.Node(nB);
		// My.cout("nBx:\n "+nBx);
		
		// My.cout("CROSSOVER");
		
		// DT.Node nAx1 = nAx.DFSFindNodeByLabel("Temp", "mild");
		// DT.Node nAx1p = nAx1.DFSFindParent(nAx);
		// My.cout("nAx1:\n "+nAx1);
		// My.cout("nAx1p:\n "+nAx1p);
		// // String labelAx1 = nAx1p.getLabelForChild(nAx1);
		// // My.cout("labelAx1: "+labelAx1);
		
		// DT.Node nBx1 = nBx.DFSFindNodeByLabel("Temp","mild");
		// DT.Node nBx1p = nBx1.DFSFindParent(nBx);
		// My.cout("nBx1:\n "+nBx1);
		// My.cout("nBx1p:\n "+nBx1p);
		// // String labelBx1 = nBx1p.getLabelForChild(nBx1);
		// // My.cout("labelBx1: "+labelBx1);

		// nAx1p.removeChild("mild");
		// nAx1p.addChild("mild", nBx1);
		// nBx1p.removeChild("mild");
		// nBx1p.addChild("mild", nAx1);
		
		// My.cout("nAx:\n "+nAx);
		// My.cout("nBx:\n "+nBx);

	}

	public static DT.Node GPClassfication(DT dt, String targetAttr, ArrayList<ArrayList<String>> data, int initPop, int iterations, double mutationRate, double crossOverRate, long seed){
		int MAX_CAPACITY = 200;
		int MAX_SIZE = 100;

		Comparator<? super DT.Node> sortingAlgo = (a,b)->{
			double aVal = GetFitnessOfNode(a, data, dt, targetAttr);
			double bVal = GetFitnessOfNode(a, data, dt, targetAttr);
			return (int) (bVal - aVal);
		};

		ArrayList<DT.Node> population = new ArrayList<>();
		///generate population
		My.cout("Generating Population...");
		for(int i=0;i<initPop;i++){
			DT.Node newNode = dt.RandomID3(targetAttr, seed * i * 123456789 * (i+1));
			population.add(newNode);
		}

		//Evaluating each object
		My.cout("Evaluating First Population...");
		for(int i=0;i<population.size();i++){
			DT.Node node = population.get(i);
			if(node.DFSCountNodes() >= MAX_SIZE){
				population.remove(i);
				i--;
			}
		}

		My.cout("Start Population: "+population.size());

		ArrayList<DT.Node> oldGen = (ArrayList) population.clone();
		ArrayList<DT.Node> lastGen = (ArrayList) population.clone();
		int iters = 0;

		while(iters<iterations && population.size() > 0){
			//selecting parents
			ArrayList<DT.Node> parents = (ArrayList) oldGen.clone();

			parents.sort(sortingAlgo);

			//reproduce parents
			DT.Node[] _childs = CrossOver(population.toArray(new DT.Node[0]), dt, crossOverRate, seed*iters);
			ArrayList<DT.Node> childs = new ArrayList<>(Arrays.asList(_childs));

			//mutate parents or old gen
			for(int i=0; i<oldGen.size(); i++){
				DT.Node m = oldGen.get(i);
				m = Mutate(m, targetAttr, dt, mutationRate, seed*i);
				oldGen.set(i, m);
			}

			//evaluate new childs
			for(int i=0;i<childs.size();i++){
				DT.Node node = childs.get(i);
				if(node.DFSCountNodes() >= MAX_SIZE){
					childs.remove(i);
					i--;
				}
			}
			//select for next gen
			if(oldGen.size()>0){
				oldGen.sort(sortingAlgo);
				double _topFitness = GetFitnessOfNode(oldGen.get(0), data, dt, targetAttr);
	
				for(int i=0; i<oldGen.size();i++){
					DT.Node m = oldGen.get(i);
					double _fit = GetFitnessOfNode(m, data, dt, targetAttr);
					if(_fit < (_topFitness/2)){
						oldGen.remove(i);
						i++;
					}
				}
			}
			for(DT.Node node:lastGen){
				oldGen.add(node);
			}

			lastGen = childs;

			//evaluate old generation
			if(oldGen.size() > MAX_CAPACITY/2){
				oldGen.sort(sortingAlgo);
				ArrayList<DT.Node> temp = new ArrayList<>();
				for(int i=0;i<MAX_CAPACITY/2;i++) temp.add(oldGen.get(i));
				oldGen = temp;
			}
			population = new ArrayList<>();
			for(DT.Node node:oldGen){
				population.add(node);
			}
			//evaluate new generation
			if(lastGen.size() > MAX_CAPACITY/2){
				lastGen.sort(sortingAlgo);
				ArrayList<DT.Node> temp = new ArrayList<>();
				for(int i=0;i<MAX_CAPACITY/2;i++) temp.add(lastGen.get(i));
				lastGen = temp;
			}
			for(DT.Node node:lastGen){
				population.add(node);
			}
			//select current pop
			if(population.size() > MAX_CAPACITY/2){
				population.sort(sortingAlgo);
				ArrayList<DT.Node> temp = new ArrayList<>();
				for(int i=0;i<MAX_CAPACITY/2;i++) temp.add(population.get(i));
				population = temp;
			}

			My.cout("Population for generation "+(iters++)+": "+population.size());

		}

		population.sort(sortingAlgo);
		My.cout("Final Population:" + population.size());

		if(population.size()<=0) return null;

		return population.get(0);
	}

	public static double GetFitnessOfNode(DT.Node node, ArrayList<ArrayList<String>> dataset, DT dt, String targetAttr){
		double correct = 0;
		double len = dataset.size();

		if(len<=0) return 0;

		int index = new ArrayList<>(Arrays.asList(dt.attributes)).indexOf(targetAttr);

		for(ArrayList<String> data:dataset){
			String expOutput = data.get(index);
			String output = dt.decideLabel(node, data);
			if(output==null) continue;
			if(output.compareTo(expOutput)==0){
				correct++;
			}
		}

		double successRate = (correct/len);

		return successRate;

	}

	public static DT.Node Mutate(DT.Node node, String targetAttr, DT dt, double mutationRate, long seed){

		node = new DT.Node(node);
		
		double r = My.rndDouble(0, 1, seed*node.childs.size());
		if(r > mutationRate){
			return node;
		}

		HashMap<String, ArrayList<String>> labels = dt.labels;
		String[] attrs = dt.attributes;

		String breakPoint = null;
		for(String att:attrs){
			if(att.compareTo(targetAttr)==0) continue;
			DT.Node xi = node.DFSFindNodeByAttr(att);

			if(xi!=null){
				breakPoint = att;
				break;
			}
		}
		if(breakPoint==null) return node;

		ArrayList<String> bruh = new ArrayList<>(Arrays.asList(attrs));
		bruh.remove(targetAttr);

		String highGainAttr = null; 
		while(breakPoint!=highGainAttr && bruh.size()>0){
			highGainAttr = null;
			double highGainVal = Double.NEGATIVE_INFINITY;
			for(String attr:bruh){
				double gain = dt.calculateGain(targetAttr,attr);
				if(gain>highGainVal){
					highGainAttr = attr;
					highGainVal = gain;
				}
			}
			bruh.remove(highGainAttr);
		}
		bruh.add(breakPoint);

		DT.Node newSubTree = dt.RandomID3(labels, targetAttr, bruh, seed);

		ArrayList<String> possVals = dt.possibleValuesForAttr(highGainAttr);

		for(String val:possVals){
			if(node.hasLabel(val) && !node.getChild(val).isLeaf())
				continue;
			node.addChild(val, newSubTree);
			break;
		}

		return node;
		
	}

	public static DT.Node[] CrossOver(DT.Node[] nodes, DT dt, double crossOverRate, long seed){
		ArrayList<DT.Node> newNodes = new ArrayList<>();

		String[] attrs = dt.attributes;

		
		for(int i=0;i<nodes.length;i++){
			for(int j=i+1;j<nodes.length;j++){
				if(i == j) continue;
				
				double r = My.rndDouble(0, 1, seed*(i+j));
				if(r > crossOverRate){
					continue;
				}
				
				attrs = shuffleArray(attrs, seed*123456789*(j-i));

				DT.Node rootI = nodes[i];
				DT.Node rootJ = nodes[j];

				String breakPoint = null;
				for(String att:attrs){
					DT.Node xi = rootI.DFSFindNodeByAttr(att);
					DT.Node xj = rootJ.DFSFindNodeByAttr(att);

					if(xi!=null && xj!=null){
						if(xi.isLeaf() || xj.isLeaf()) continue;

						breakPoint = att;
						break;
					}
				}
				if(breakPoint==null) continue;
				
				rootI = new DT.Node(rootI);
				rootJ = new DT.Node(rootJ);

				//parents of subtrees
				DT.Node xi = rootI.DFSFindNodeByAttr(breakPoint);
				DT.Node xj = rootJ.DFSFindNodeByAttr(breakPoint);

				String[] possVals = dt.possibleValuesForAttr(breakPoint).toArray(new String[0]);

				possVals = shuffleArray(possVals, seed*123456789*possVals.length);
				String breakVal = possVals[0];
				
				//subtrees
				DT.Node xiSubTree = rootI.DFSFindNodeByLabel(breakPoint, breakVal);
				DT.Node xjSubTree = rootJ.DFSFindNodeByLabel(breakPoint, breakVal);

				xi.removeChild(breakVal);
				xi.addChild(breakVal, xjSubTree);
				xj.removeChild(breakVal);
				xj.addChild(breakVal, xiSubTree);

				//adding to list of childs
				newNodes.add(rootI);
				newNodes.add(rootJ);

			}
		}

		return newNodes.toArray(new DT.Node[0]);
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

		for(int i=0; i<dataset.size(); i++){
			double[] input = dataset.get(i);
			double[] expOut = classSet.get(i);

			double[][] res = net.process(input, factor);
	
			My.cout("In: "+printVector(input));
			My.cout("Out: "+printVector(res[0]));
			My.cout("OutDIR: "+printVector(res[1]));
			My.cout("ExpOut: "+printVector(expOut));
			My.cout("------");

			if(Arrays.equals(res[1], expOut)) correct++;
		}

		My.cout("SuccessRate: "+My.stepify(correct/dataset.size()*100,0.01)+"%");
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
			{2,3},
			{3,2},
		};
		//[number of inputs][outputSize]
		double[][] t = new double[][]{
			{1},{0}
		};

		double acc = 0.01;
		double lRate = 0.8;
		boolean isBipolar = false;
		long seed = 20;

		int[] instSizes = {2};
		
		NNetwork net = new NNetwork(p[0].length, instSizes, t[0].length);

		My.cout("Before:");
		for(int e=0;e<net.tensor.length;e++){
			double[][] mat = net.tensor[e];
			for(int r=0;r<mat.length;r++){
				for(int c=0;c<mat[r].length;c++){
					mat[r][c] = My.stepify(
						My.rndDouble(
							0, 0.5, (-(r+c)*seed + 2*(r-c)*seed + 2*(c-r)*seed - (mat.length-mat[r].length))*1234567890
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
			net.activationFuncs[i] = "relu";
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
			{0,3},{2,0},{2,3},{0,0}
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