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

	///CLASSES
	public static class Timestamp{

		private long startTime = 0;
		private long stopTime = 0;

		public void start(){
			startTime = System.currentTimeMillis();
		}public long stop(){
			stopTime = System.currentTimeMillis();
			return getTimeTakenInMillis();
		}
		public long getTimeTakenInMillis(){
			return (stopTime - startTime);
		}
		public void reset(){
			startTime = 0; stopTime = 0;
		}
	}

	public static class Sack{
		public ArrayList<Item> items = new ArrayList<>();
		public double maxWeight;

		public Sack(double w){
			maxWeight = w;
		}

		public boolean addItem(Item item){
			if(getCurrentWeight() + item.weight <= maxWeight){
				items.add(item);
				return true;
			}
			return false;
		}

		public boolean hasItem(Item item){
			return items.contains(item);
		}

		public int getCurrentWeight(){
			int w = 0;
			for(Item item:items){
				w += item.weight;
			}
			return w;
		}

		public double getMaxWeight(){
			return maxWeight;
		}

		public double getTotalValue(){
			double w = 0;
			for(Item item:items){
				w += item.value;
			}
			return w;
		}

		public boolean[] getState(ArrayList<Item> dataset){
			boolean[] st = new boolean[dataset.size()];

			for(int i=0;i<dataset.size();i++){
				st[i] = (items.contains(dataset.get(i))) ? true : false;
			}

			return st;
		}
		public boolean setState(boolean[] st, ArrayList<Item> dataset){
			if(st == null) return false;
			ArrayList<Item> newItems = new ArrayList<>();
			int w = 0;

			for(int i=0;i<st.length;i++){
				if(st[i]){
					newItems.add(dataset.get(i));
					w += dataset.get(i).weight;
				}
				if(w > maxWeight) return false;
			}

			items = newItems;
			return true;
		}
		public static double GetStateValue(boolean[] st, ArrayList<Item> dataset, double _maxWeight){
			if(st == null) return 0;
			double w = 0; double v=0;
			
			for(int i=0;i<st.length;i++){
				if(st[i]){
					w += dataset.get(i).weight;
					v += dataset.get(i).value;
				}
				if(w > _maxWeight) return 0;
			}
			return v;
		}
		public static boolean IsValidState(boolean[] st, ArrayList<Item> dataset, double _maxWeight){
			if(st == null) return false;
			double w = 0;
			
			for(int i=0;i<st.length;i++){
				if(st[i]){
					w += dataset.get(i).weight;
				}
				if(w > _maxWeight) return false;
			}
			return true;
		}
	}

	public static class Item{
		public double weight;
		public double value;
		
		public Item(double w,double v){
			weight = w; value = v;
		}

		@Override
		public String toString(){
			String out = "{ ";

			out += ("w:"+weight);
			out += " , ";
			out += ("v:"+value);

			out += " }";

			return out;
		}
	}

	///PROGRAM FUNCTIONS

	public static String stateToString(boolean[] st){
		if(st == null) return "[]";

		byte[] _st = new byte[st.length];
		for(int i=0;i<st.length;i++){
			if(st[i]) _st[i] = 1;
		}
		return Arrays.toString(_st);
	}

	public static boolean[] mutate(boolean[] st, ArrayList<Item> items, double w){
		boolean[] newSt = st.clone();

		//check if bit is invalid
		if(!Sack.IsValidState(newSt, items, w)){
			//check for largest item
			int largestItem = 0, leastItem = 0, largestAndLeastItem = -1;
			for(int i=0;i<st.length;i++){
				if(items.get(i).value < items.get(leastItem).value){
					leastItem = i;
				}
				if(items.get(i).weight > items.get(largestItem).weight){
					largestItem = i;
				}
				
				if(
					(items.get(i).value < items.get(largestItem).value)
					||
					(items.get(i).weight > items.get(largestItem).weight)
				){
					if(largestAndLeastItem < 0) largestAndLeastItem = i;
					else if(
						items.get(largestAndLeastItem).value > items.get(i).value
						||
						items.get(largestAndLeastItem).weight < items.get(i).weight
					){
						largestAndLeastItem = i;
					}
				}
			}

			if(largestAndLeastItem>=0) newSt[largestAndLeastItem] = false;
			else newSt[largestItem] = false;

			return newSt;

		}
		
		for(int i=0;i<st.length;i++){
			//check if bit can be added once
			
			if(!newSt[i]){
				newSt[i] = true;
				if(Sack.IsValidState(newSt, items, w)){
					return newSt;
				}
			}

		}
		//else just randomly set a bit
		int index = My.rndInt(0, newSt.length-1);
		newSt[index] = !newSt[index];
		
		return newSt; // deez nuts
	}

	public static boolean[][] crossOver(boolean[] stA, boolean[] stB){

		boolean[] cA = new boolean[stA.length];
		boolean[] cB = new boolean[stA.length];

		for(int i=0;i<(stA.length/2);i++){
			cA[i] = stA[i];
			cB[i] = stB[i];
		}
		for(int i=(stB.length/2);i<stB.length;i++){
			cA[i] = stB[i];
			cB[i] = stA[i];
		}

		return new boolean[][]{cA, cB};
	}

	public static boolean[] randomState(int size){
		boolean[] st = new boolean[size];

		for(int i=0;i<size;i++){
			int x = My.rndInt(0, 1);
			if(x == 1) st[i] = true;
		}

		return st;
	}


	public static int booleanArrayToInt(boolean[] arr){
		int n = 0, len = arr.length;
		for (int i=0; i<len; i++) {
			n = (n << 1) + (arr[i] ? 1 : 0);
		}
		return n;
	}

	public static boolean[] intToBooleanArray(int num, int len){
		boolean[] st = new boolean[len];
		for (int i=0; i<len; i++) {
			st[len-i-1] = (num & (1 << i)) != 0;
		}
		return st;
	}

	public static boolean[] antColonyOptimization(ArrayList<Item> items, int weight, int colonySize){

		Comparator<? super boolean[]> sortingAlgo = (a,b)->{
			double aVal = Sack.GetStateValue(a, items, weight);
			double bVal = Sack.GetStateValue(b, items, weight);
			return (int) (bVal - aVal);
		};

		HashMap<Integer,Double> map = new HashMap<>();
		ArrayList<boolean[]> ants = new ArrayList<>();

		ArrayList<boolean[]> explored;

		for(int i=0;i<colonySize;i++){
			boolean[] ant = new boolean[items.size()];
			ants.add(ant);
		}

		My.cout("Starting ants: "+ants.size());

		while(ants.size()>0){
			explored = new ArrayList<>();

			for(int i=0;i<ants.size();i++){

				boolean[] ant = ants.get(i);

				double pts = 0;
				if(map.containsKey(booleanArrayToInt(ant))){
					pts = map.get(booleanArrayToInt(ant));
				}

				boolean[] st = ant.clone();

				int len = ants.get(i).length;

				for(int b=0;b<len;b++){
					st = ant.clone();

					int rndIndex = My.rndInt(0, len-1);
					st[rndIndex] = true;
					
					boolean flag = true;
					for(boolean[] _st:explored){
						if(booleanArrayToInt(st) == booleanArrayToInt(_st)){
							flag = false; break;
						}
					}
					if(!flag) continue;

					flag = Sack.IsValidState(st, items, weight);

					if(flag){
						if(sortingAlgo.compare(st, ant)<0){
							if(map.containsKey(booleanArrayToInt(st)) && map.get(booleanArrayToInt(st))!=null){
								pts += map.get(booleanArrayToInt(st));
							}
							break;
						}
					}else{
						st = ant.clone();
					}
					
				}

				if(booleanArrayToInt(st) != booleanArrayToInt(ant)){
					double newPts = pts + 1;
					if(Sack.IsValidState(st, items, weight)){
						map.put( booleanArrayToInt(st) , newPts );
						ants.set(i, st);
					}
				}else{
					double newPts = pts + Sack.GetStateValue(st, items, weight) + 1;
					ants.remove(i);
					if(Sack.IsValidState(st, items, weight)){
						map.put( booleanArrayToInt(st) , newPts );
						// ants.set(i, st);
					}
				}
				explored.add(st);

			}

			My.cout("Curr ants: "+ants.size());

			explored = new ArrayList<>();

		}
		My.cout("Final Hashmap: ");
		for(int key:map.keySet())
			My.cout(stateToString(intToBooleanArray(key, items.size()))+" : "+map.get(key));

		int bestKey = 0;

		for(int key:map.keySet()){
			if(bestKey<0 || !map.containsKey(bestKey)) bestKey = key;
			else if(map.get(bestKey) < map.get(key) && Sack.IsValidState(intToBooleanArray(key, items.size()), items, weight)){
				bestKey = key;
			}
		}

		if(bestKey<0) return null;

		boolean[] st = intToBooleanArray(bestKey, items.size());
		My.cout("Final is valid? "+( Sack.IsValidState(st, items, weight) ));

		return st;
	}

	public static boolean[] geneticAlgo(ArrayList<Item> items, double weight, int initPopulationSize, int iterations, double crossOverRate, double mutationRate){

		int MAX_CAPACITY = 1000;

		Comparator<? super boolean[]> sortingAlgo = (a,b)->{
			double aVal = Sack.GetStateValue(a, items, weight);
			double bVal = Sack.GetStateValue(b, items, weight);
			return (int) (bVal - aVal);
		};

		ArrayList<boolean[]> population = new ArrayList<>();
		
		//generate population
		My.cout("Generating Population...");
		for(int i=0;i<initPopulationSize;i++){
			population.add( randomState(items.size()) );
		}
		
		//Evaluating each object
		My.cout("Evaluating First Population...");
		for(int i=0;i<population.size();i++){
			boolean[] _st = population.get(i);
			if(!Sack.IsValidState(_st, items, weight)){
				population.set(i, mutate(_st, items, weight));
			}
		}
		
		My.cout("Start Population: "+population.size());

		ArrayList<boolean[]> oldGen = (ArrayList) population.clone();
		ArrayList<boolean[]> lastGen = new ArrayList<>();
		int iters = 0;

		while(iters<iterations && population.size() > 0){
			//selecting parents
			ArrayList<boolean[]> parents = (ArrayList) oldGen.clone();

			parents.sort(sortingAlgo);

			ArrayList<boolean[]> childs = new ArrayList<>();
			//reproduce parents
			for(int i=0; i<parents.size();i++){
				if(My.rndDouble(0, 1) > crossOverRate){
					continue;
				}
				for(int j=1; j<parents.size();j++){
					if(My.rndDouble(0, 1) > crossOverRate){
						continue;
					}

					boolean[] pA = parents.get(i);
					boolean[] pB = parents.get(j);

					if(pA == pB) continue;

					boolean[][] _childs = crossOver(pA, pB);

					childs.add(_childs[0]);
					childs.add(_childs[1]);

				}

				if(childs.size() > MAX_CAPACITY/2) break;
			}

			//mutate parents or old gen
			for(int i=0; i<oldGen.size(); i++){
				int m = oldGen.get(i).length;
				while(My.rndDouble(0, 1) < mutationRate && m>0){
					boolean[] _st = oldGen.get(i);

					_st = mutate(_st, items, weight);
					
					oldGen.set(i, _st);
					m--;
				}
			}

			//evaluate new objects
			for(int i=0; i<childs.size();i++){
				if(!Sack.IsValidState(childs.get(i), items, weight)){
					//force mutate
					for(int m=0;m<items.size();m++){
						boolean[] xst = childs.get(i);
						for(int b=0;b<xst.length;b++){
							if(xst[b]) xst[b] = false;
							if(Sack.IsValidState(xst, items, weight)) break;
						}
						childs.set(i, xst);

						if(Sack.IsValidState(childs.get(i), items, weight)) break;
					}
					if(!Sack.IsValidState(childs.get(i), items, weight)){
						childs.remove(i); i--;
					}
				}
			}
			//select for next gen 
			for(int i=0; i<oldGen.size();i++){
				if(!Sack.IsValidState(oldGen.get(i), items, weight)){
					//force mutate
					for(int m=0;m<items.size();m++){
						boolean[] xst = oldGen.get(i);
						xst[My.rndInt(0, xst.length-1)] = false;
						oldGen.set(i, xst);
					}
					if(!Sack.IsValidState(oldGen.get(i), items, weight)){
						oldGen.remove(i); i--;
					}
				}
			}
			for(boolean[] st:lastGen){
				oldGen.add(st);
			}

			lastGen = childs;

			//evaluate old generation
			if(oldGen.size() > MAX_CAPACITY/2){
				// My.cout("Capped oldGen at MAX CAPACITY of "+MAX_CAPACITY/2);
				oldGen.sort(sortingAlgo);
				ArrayList<boolean[]> temp = new ArrayList<>();
				for(int i=0;i<MAX_CAPACITY/2;i++) temp.add(oldGen.get(i));

				oldGen = temp;
			}
			// My.cout("> Old gen: "+oldGen.size());
			population = new ArrayList<>();
			for(boolean[] st:oldGen){
				// My.cout(stateToString(st));
				population.add(st);
			}
			//evaluate new generation
			if(lastGen.size() > MAX_CAPACITY/2){
				// My.cout("Capped newGen at MAX CAPACITY of "+MAX_CAPACITY/2);
				lastGen.sort(sortingAlgo);
				ArrayList<boolean[]> temp = new ArrayList<>();
				for(int i=0;i<MAX_CAPACITY/2;i++) temp.add(lastGen.get(i));

				lastGen = temp;
			}
			// My.cout("> New gen: "+lastGen.size());
			for(boolean[] st:lastGen){
				// My.cout(stateToString(st));
				population.add(st);
			}

			//select current population
			if(population.size() > MAX_CAPACITY){
				My.cout("Capped at MAX CAPACITY of "+MAX_CAPACITY);
				population.sort(sortingAlgo);
				ArrayList<boolean[]> temp = new ArrayList<>();
				for(int i=0;i<MAX_CAPACITY;i++) temp.add(population.get(i));

				population = temp;
			}

			My.cout("Population for generation "+(iters++)+": "+population.size());

		}

		population.sort(sortingAlgo);

		My.cout("Final Population:" + population.size());

		if(population.size()<=0) return null;

		return population.get(0);

	}

	public static class Result{
		public HashMap<String,Object> params = new HashMap<>();
		public String name;
		public boolean[] finalState;

		public Result(String n){ name = n; }

		public Object getParam(String key){
			if(params.containsKey(key)) return params.get(key);
			return null;
		}
		public Object setParam(String key, Object val){
			Object x = getParam(key);
			params.put(key, val);
			return x;
		}

		@Override
		public String toString(){
			String out = "";

			out += name + " : {\n";
			
			for(String key:params.keySet()){
				out += key + " : " +  params.get(key).toString() + ", \n";
			}

			out += "RESULT : " + stateToString(finalState);

			out += "\n}";

			return out;
		} 
	}

	public static class TResult{
		public String name;
		public ArrayList<Result> results = new ArrayList<>();

		public TResult(String n){ name = n; }

		public void addResult(Result r){ results.add(r); }

		@Override
		public String toString(){
			String out = "[\n";

			for(Result r:results){
				out += r.toString() + ", \n";
			}

			out += "]";

			return out;
		} 
	}
	//public static awesome Shiza()
	//{
		//Cassim is great at coding and he totally got this assignment in the bag cos hes a salaying bestie fr fr and hes fricken smart and what not yeah... thats about it so yeah 
//return shiza is the bestest bestie in the world hahahahahahahahahahahahahahahahahahahahahaha i fell in love with an emo girl. all I want is emo gurllllll sheeesshhh....

	//}

	
	public static ArrayList<String> getFilesPaths(File dir){
		ArrayList<String> paths = new ArrayList<>();
		if(dir.isDirectory()){
			for(File file:dir.listFiles()){
				if(file.isFile()){
					paths.add(file.getPath());
				}else if(file.isDirectory()){
					ArrayList<String> _paths = getFilesPaths(file);
					if(_paths != null){
						for(String _path:_paths){
							paths.add(_path);
						}
					}
				}
			}

			return paths;
		}else if(dir.isFile()){
			paths.add(dir.getPath());

			return paths;
		}

		return null;
	}

	public static class Dataset{
		public ArrayList<Item> items = new ArrayList<>();
		public int capacity = 0;

		public Dataset(int w){
			capacity = w;
		}
	}
		
	public static void m0(){
		///settings

		int popSize = 5;
		int iterations = 5;
		double crossOverRate = 0.5;
		double mutationRate = 0.5;

		int colonySize = 100;

		Scanner inputSc = new Scanner(System.in);

		System.out.print("Enter PopulationSize: ");
		int _popSize = inputSc.nextInt();
		if(_popSize>0) popSize = _popSize;
		System.out.print("Enter Iterations: ");
		int _iter = inputSc.nextInt();
		if(_iter>1) iterations = _iter;
		System.out.print("Enter CrossOver Rate: ");
		double _cross = inputSc.nextDouble();
		if(_cross>0 && _cross<=1) crossOverRate = _cross;
		System.out.print("Enter Mutation Rate: ");
		double _mut = inputSc.nextDouble();
		if(_mut>0 && _mut<=1) mutationRate = _mut;
		System.out.print("Enter Colony Size: ");
		int _col = inputSc.nextInt();
		if(_col>1) colonySize = _col;

		// try{
			File dir = new File("Knapsack Instances/");
			My.cout("Reading file directories from "+dir.getName()+"...");
			ArrayList<String> paths = getFilesPaths(dir);
			My.cout("Read file directories.");

			HashMap<String,ArrayList<Item>> datasets = new HashMap<>();

			My.cout("Reading files...");

			int len = paths.size();

			ArrayList<TResult> results = new ArrayList<>();

			String[] datasetKeys = datasets.keySet().toArray(new String[len]);

			for(int k=0; k<len; k++){
				String _path = paths.get(k);

				File file = null;

				try{
					file = new File(_path);
				}catch(Exception bruh){
					My.cout("Reading "+_path+" failed due to an error. Going to next");
					continue;
				}

				Scanner scanner = null;
				Dataset dataset = null;

				int num = 0, cap = 0;
				String name = file.getName();

				try{
					scanner = new Scanner(file);
					if(!scanner.hasNextLine()){
						My.cout("File is lacking settings");
						continue;
					}
					String[] settings = scanner.nextLine().split(" ");
	
					num = (Integer.parseInt(settings[0]));
					cap = (Integer.parseInt(settings[1]));

					dataset = new Dataset(cap);
				}catch(Exception deez){
					My.cout(deez);
				}

				if(dataset == null){
					My.cout("Dataset not available");
					continue;
				}
				
				
				int i = 0;
				try{
					while(i<num && scanner.hasNextLine()){
						String[] itemSet = scanner.nextLine().split(" ");
	
						double v = Double.parseDouble(itemSet[0]);
						double w = Double.parseDouble(itemSet[1]);
	
						dataset.items.add(new Item(w,v));
						i++;
					}
				}catch(Exception e){
					My.cout("Error counting all scanner lines");
					My.cout(e);
				}

				My.cout(name + " got read\t [" + (k+1) + " of " + len + "]");

			
				My.cout("Using algorithms on dataset...");
				TResult tresults = new TResult(name);

				Result res = new Result(name + "(GeneticAlgo)");
				res.setParam("initialPopulation", popSize);
				res.setParam("iterationCount", iterations);
				res.setParam("crossoverRate", crossOverRate);
				res.setParam("mutationRate", mutationRate);
				Timestamp timer = new Timestamp();
				
				timer.start();
				boolean[] st = geneticAlgo(dataset.items, dataset.capacity, popSize, iterations, crossOverRate,mutationRate);
				res.setParam("RUNTIME", timer.stop() );

				res.finalState = st;
				res.setParam("FITNESS", Sack.GetStateValue(st, dataset.items, dataset.capacity));

				My.cout(name + "(GeneticAlgo) is complete\t [" + (k+1) + " of " + len + "]");

				tresults.addResult(res);

				res = new Result(name+"(AntColonyOpt)");

				res.setParam("colonySize", colonySize);
			
				timer = new Timestamp();
				
				timer.start();
				st = antColonyOptimization(dataset.items, dataset.capacity, colonySize);
				res.setParam("RUNTIME", timer.stop() );

				res.finalState = st;
				res.setParam("FITNESS", Sack.GetStateValue(st, dataset.items, dataset.capacity));

				My.cout(name + "(AntColonyOpt) is complete\t [" + (k+1) + " of " + len + "]");
				
				tresults.addResult(res);

				results.add(tresults);

			}

			My.cout("Completed algorithms");
			
			ArrayList<String> strs = new ArrayList<>();
			
			My.cout("Results:\n -----------");
			for(TResult res:results){

				String str = res.toString();
				strs.add(str);

				My.cout(str);
			}

			strs.add("\n");

			My.cout("Writing to log file...");

			try{
				new File("myLogs").mkdir();
				int c = 0;
				File newLogFile = new File("myLogs/"+dir.getName()+".txt");
				while(newLogFile.exists()){
					newLogFile = new File("myLogs/"+dir.getName()+"("+(++c)+").txt");
				}
				newLogFile.createNewFile();
	
				String logStr = String.join("\n", strs);
	
				Files.write(Paths.get(newLogFile.getPath()), logStr.getBytes());

				My.cout("Successfully wrote to log file.");
			}catch(Exception err){
				My.cout("Failed to write to log file...");
				My.cout(err.toString());
			}

	}

}

