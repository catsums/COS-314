import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class myMain{

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

	public static String stateToString(boolean[] st){
		if(st == null) return "[]";

		byte[] _st = new byte[st.length];
		for(int i=0;i<st.length;i++){
			if(st[i]) _st[i] = 1;
		}
		return Arrays.toString(_st);
	}

	public static boolean[] mutate(boolean[] st, ArrayList<Item> items, int w){
		boolean[] newSt = st.clone();

		//check if bit is invalid
		Sack sack = new Sack(w);
		boolean valid = sack.setState(newSt, items);
		if(!valid){
			// My.cout("> Correcting bit...");
			//check for largest item
			int largestItem = 0, leastItem = 0, largestAndLeastItem = -1;
			for(int i=0;i<st.length;i++){
				if(items.get(i).value < items.get(leastItem).value){
					leastItem = i;
				}
				// else if((largestAndLeastItem<0) || (items.get(i).weight > items.get(largestAndLeastItem).weight)){
				// 	largestAndLeastItem = i;
				// }
				if(items.get(i).weight > items.get(largestItem).weight){
					largestItem = i;
				}
				// else if((largestAndLeastItem<0) || (items.get(i).value < items.get(largestAndLeastItem).value)){
				// 	largestAndLeastItem = i;
				// }
				
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

			// My.cout("least "+leastItem);
			// My.cout("largest "+largestItem);
			// My.cout("largestAndLeast "+leastItem);

			if(largestAndLeastItem>=0) newSt[largestAndLeastItem] = false;
			else newSt[largestItem] = false;

			return newSt;

		}

		
		for(int i=0;i<st.length;i++){
			//check if bit can be added once
			
			// My.cout("> Adding bit...");
			if(!newSt[i]){
				boolean gotAdded = sack.addItem(items.get(i));
				if(gotAdded){
					newSt = sack.getState(items);
					return newSt;
				}
			}

		}
		//check if bit can be swapped for better
		for(int i=0;i<st.length;i++){
			// My.cout("> Swapping bit...");
			if(!newSt[i]) continue;
			for(int j=0;j<st.length;j++){
				if(i == j || newSt[j]) continue;
				boolean[] trySt = newSt.clone();
				trySt[i] = !trySt[i];
				trySt[j] = !trySt[j];

				Sack trySack = new Sack(w);
				boolean tryIsValid = trySack.setState(trySt, items);
				if(tryIsValid && trySack.getTotalValue() >= sack.getTotalValue()){
					newSt = trySt;

					return newSt;
				}
			}
		}

		// My.cout("> Not edited");
		
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

	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");
		
		m0();
		
		My.cout("---------------"); My.cout("| MAIN END |");
        return;
    }

	public static void m0(){

		ArrayList<Item> items = new ArrayList<>();
		items.add( new Item(401, 3));
		items.add( new Item(302, 6));
		items.add( new Item(105, 4));
		items.add( new Item(400, 2));
		items.add( new Item(290, 5));
		items.add( new Item(270, 2));
		items.add( new Item(206, 7));
		items.add( new Item(404, 5));
		items.add( new Item(110, 6));
		items.add( new Item(510, 7));
		items.add( new Item(991, 6));
		items.add( new Item(302, 6));
		items.add( new Item(105, 4));
		items.add( new Item(423, 2));
		items.add( new Item(390, 8));
		items.add( new Item(280, 2));
		items.add( new Item(306, 2));
		items.add( new Item(394, 5));
		items.add( new Item(190, 1));
		items.add( new Item(510, 7));

		int weight = 2500;

		boolean[] st = antColonyOptimization(items, weight, 100);

		My.cout("Final State: ");
		My.cout(stateToString(st));

		///////////

		// boolean[] st = {false,false,false,true,true};

		// int num = booleanArrayToInt(st);

		// My.cout(stateToString(st));
		// My.cout(num);

		// st = intToBooleanArray(num+2, st.length);
		// My.cout(stateToString(st));
		// My.cout(booleanArrayToInt(st));

		/////////////////

		// ArrayList<Item> items = new ArrayList<>();
		// items.add( new Item(401, 3));
		// items.add( new Item(302, 6));
		// items.add( new Item(105, 4));
		// items.add( new Item(400, 2));
		// items.add( new Item(390, 5));
		// items.add( new Item(270, 2));
		// items.add( new Item(206, 7));
		// items.add( new Item(304, 5));
		// items.add( new Item(100, 1));
		// items.add( new Item(510, 7));

		// int weight = 990;

		// boolean[] st = geneticAlgo(items, weight, 10, 4, 0.9,0.9);

		// My.cout("Best Sack: "+stateToString(st));

		// Sack sack = new Sack(10);

		// st = randomState(items.size());

		// My.cout(Arrays.toString(st));
		
		// st = mutate(st, items, sack.maxWeight);
		
		// My.cout(Arrays.toString(st));
	}

	/*
		* init population with random objects
		* evaluate each obj
		* while term condition not met
			* select parents/original objs
			* reproduce objs to make new objs
			* mutate/genetic manipulation of parents/original objs
			* evaluate new objs
			* select objs for the next gen (remove the rest)
		* end while
	*/

	/*
		[0,0,0,0][0,1,0,1][0,1,1,1][1,0,1,0]
		[0,1,0,1][0,1,1,1][1,0,1,0]
		[0,1,1,1][0,1,1,0][0,1,1,0][1,0,1,1][0,1,0,1][1,0,1,1]
		
	*/

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
			Sack sackA = new Sack(weight);
			Sack sackB = new Sack(weight);
			double aVal = -1, bVal = -1;

			if(sackA.setState(a, items)){
				aVal = sackA.getTotalValue();
			}
			if(sackB.setState(b, items)){
				bVal = sackB.getTotalValue();
			}
			return (int) (bVal - aVal);
		};

		HashMap<Integer,Double> map = new HashMap<>();
		ArrayList<boolean[]> ants = new ArrayList<>();

		ArrayList<boolean[]> explored;

		for(int i=0;i<colonySize;i++){
			boolean[] ant = new boolean[items.size()];
			ants.add(ant);
		}

		My.cout("Starting ants: ");
		for(boolean[] ant:ants) My.cout(stateToString(ant));
		My.cout("Start Matrix: ");
		for(int key:map.keySet()) My.cout(stateToString(intToBooleanArray(key, items.size()))+" : "+map.get(key));

		while(ants.size()>0){
			explored = new ArrayList<>();

			for(int i=0;i<ants.size();i++){

				boolean[] ant = ants.get(i);

				double pts = 0;
				if(map.containsKey(booleanArrayToInt(ant)))
					pts = map.get(booleanArrayToInt(ant));

				boolean[] st = ant.clone();

				int len = ants.get(i).length;

				// My.cout("init st "+stateToString(st));
				// My.cout("ant "+stateToString(ant));
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

					Sack sack = new Sack(weight);
					flag = sack.setState(st, items);

					if(flag){
						if(sortingAlgo.compare(st, ant)<0){
							My.cout("> Path open...");
							if(map.containsKey(booleanArrayToInt(st))){
								My.cout("> Increment pheromone on path");
								pts += map.get(booleanArrayToInt(st));
							}
							break;
						}
					}else{
						st = ant.clone();
					}

					
				}
				// My.cout("final st "+stateToString(st));
				// My.cout("ant "+stateToString(ant));

				if(booleanArrayToInt(st) != booleanArrayToInt(ant)){
					double newPts = pts + 1;
					map.put( booleanArrayToInt(st) , newPts );
					ants.set(i, st);
				}else{
					Sack sack = new Sack(weight);
					sack.setState(st, items);

					double newPts = pts + sack.getTotalValue() + 1;
					map.put( booleanArrayToInt(st) , newPts );
					ants.remove(ant);
				}
				explored.add(st);

			}

			My.cout("Curr ants: ");
			for(boolean[] ant:ants) My.cout(stateToString(ant));
			My.cout("Curr Matrix: ");
			for(int key:map.keySet()) My.cout(stateToString(intToBooleanArray(key, items.size()))+" : "+map.get(key));

			explored = new ArrayList<>();


			// int newAntAmt = My.rndInt(0, initColonySize);

			// for(int i=0;i<1;i++){
			// 	boolean[] ant = new boolean[items.size()];
			// 	ants.add(ant);
			// }

			// if(map.hashCode() == lastHash){
			// 	deadEndCount--;
			// }else{
			// 	lastHash = map.hashCode();
			// }

		}

		// My.cout("Final ants: ");
		// for(boolean[] ant:ants) My.cout(stateToString(ant));
		My.cout("Final Matrix: ");
		for(int key:map.keySet()) My.cout(stateToString(intToBooleanArray(key, items.size()))+" : "+map.get(key));

		int bestKey = -1;

		for(int key:map.keySet()){
			if(bestKey<0) bestKey = key;
			else if(map.get(bestKey) < map.get(key)){
				bestKey = key;
			}
		}

		if(bestKey<0) return null;

		boolean[] st = intToBooleanArray(bestKey, items.size());
		My.cout("Final is valid? "+( new Sack(weight).setState(st, items) ));

		return st;
	}

	public static boolean[] geneticAlgo(ArrayList<Item> items, int weight, int initPopulationSize, int iterations, double crossOverRate, double mutationRate){

		Comparator<? super boolean[]> sortingAlgo = (a,b)->{
			Sack sackA = new Sack(weight);
			Sack sackB = new Sack(weight);
			double aVal = -1, bVal = -1;

			if(sackA.setState(a, items)){
				aVal = sackA.getTotalValue();
			}
			if(sackB.setState(b, items)){
				bVal = sackB.getTotalValue();
			}
			return (int) (bVal - aVal);
		};

		ArrayList<boolean[]> population = new ArrayList<>();
		do{
			//generate population
			for(int i=0;i<initPopulationSize;i++){
				population.add( randomState(items.size()) );
			}

			
			//Evaluating each object
			for(int i=0;i<population.size();i++){
				Sack sack = new Sack(weight);
				if(!sack.setState(population.get(i), items)){
					population.remove(i);
					i--;
				}
			}
		}while(population.size() <= 0);
		
		
		My.cout("Start Population:");
		for(boolean[] st:population){
			My.cout(stateToString(st));
		}
		ArrayList<boolean[]> oldGen = (ArrayList) population.clone();
		ArrayList<boolean[]> lastGen = new ArrayList<>();
		ArrayList<boolean[]> currGen = new ArrayList<>();

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
			}

			//mutate parents or old gen
			for(int i=0; i<oldGen.size(); i++){
				int m = oldGen.get(i).length;
				while(My.rndDouble(0, 1) < mutationRate && m>0){
					oldGen.set(i, mutate(oldGen.get(i), items, weight));
					m--;
				}
			}

			//evaluate new objects
			for(int i=0; i<childs.size();i++){
				Sack sack = new Sack(weight);
				if(!sack.setState(childs.get(i), items)){
					childs.remove(i); i--;
				}
			}
			//select for next gen 
			for(int i=0; i<oldGen.size();i++){
				Sack sack = new Sack(weight);
				if(!sack.setState(oldGen.get(i), items)){
					oldGen.remove(i); i--;
				}
			}
			for(boolean[] st:lastGen){
				oldGen.add(st);
			}

			lastGen = childs;

			//show current generation
			My.cout("Population for generation "+(iters+1)+": ");
			My.cout("> Old gen: ");
			population = new ArrayList<>();
			for(boolean[] st:oldGen){
				My.cout(stateToString(st));
				population.add(st);
			}
			My.cout("> New gen: ");
			for(boolean[] st:lastGen){
				My.cout(stateToString(st));
				population.add(st);
			}

			iters ++;
		}

		population.sort(sortingAlgo);

		My.cout("Final Population:");
		for(boolean[] st:population){
			My.cout(stateToString(st));
		}

		if(population.size()<=0) return null;

		return population.get(0);

	}

	public static class Result{
		public HashMap<String,Object> params;
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

			out += name + " : {";
			
			for(String key:params.keySet()){
				out += key + " : " +  params.get(key).toString();
			}

			out += "}";

			return out;
		} 
	}

	public static class TResult{
		public String name;
		public ArrayList<Result> results;

		public TResult(String n){ name = n; }

		public void addResult(Result r){ results.add(r); }

		@Override
		public String toString(){
			String out = "[";

			for(Result r:results){
				out += r.toString() + "\n";
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
		
	public static void m2(){

		///settings

		int popSize = 5;
		int iterations = 5;
		double crossOverRate = 0.5;
		double mutationRate = 0.5;

		int colonySize = 100;


		try{
			File dir = new File("Knapsack Instances/f9_l-d_kp_5_80");
			My.cout("Reading file directories from "+dir.getName()+"...");
			ArrayList<String> paths = getFilesPaths(dir);
			My.cout("Read file directories.");

			HashMap<String,ArrayList<Item>> datasets = new HashMap<>();

			My.cout("Reading files...");

			int len = (int) ((double)(paths.size()) / 2.5);

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

				Scanner scanner = new Scanner(file);

				String name = file.getName();

				String[] settings = scanner.nextLine().split(" ");

				int num = (Integer.parseInt(settings[0]));
				int cap = (Integer.parseInt(settings[1]));
				
				Dataset dataset = new Dataset(cap);
				
				int i = 0;
				while(scanner.hasNextLine() && i<num){
					String[] itemSet = scanner.nextLine().split(" ");

					double v = Double.parseDouble(itemSet[0]);
					double w = Double.parseDouble(itemSet[1]);

					dataset.items.add(new Item(w,v));
					i++;
				}

				// datasets.put(name, dataset);

				My.cout(name + " got read\t [" + (k+1) + " of " + len + "]");


			// }

			// My.cout("Read files.");

			
			My.cout("Using algorithms on dataset...");

			// for (int k=0; k<datasets.size(); k++) {
			// 	String setName = datasetKeys[k];

				Result res0 = new Result(name);

				res0.setParam("initialPopulation", popSize);
				res0.setParam("iterationCount", iterations);
				res0.setParam("crossoverRate", crossOverRate);

				res0.setParam("timeTaken", 0);

				res0.setParam("mutationRate", mutationRate);
				
				Timestamp timer = new Timestamp();
				
				timer.start();
				boolean[] st = geneticAlgo(dataset.items, dataset.capacity, popSize, iterations, crossOverRate,mutationRate);
				res0.setParam("timeTaken", timer.stop() );

				res0.finalState = st;

				My.cout(name + "(GeneticAlgo) is complete\t [" + (k+1) + " of " + len + "]");

				Result res1 = new Result(name);

				res1.setParam("initialPopulation", popSize);
				res1.setParam("iterationCount", iterations);
				res1.setParam("crossoverRate", crossOverRate);

				res1.setParam("timeTaken", 0);

				res1.setParam("mutationRate", mutationRate);
				
				timer = new Timestamp();
				
				timer.start();
				st = geneticAlgo(dataset.items, dataset.capacity, popSize, iterations, crossOverRate,mutationRate);
				res1.setParam("timeTaken", timer.stop() );

				res1.finalState = st;

				My.cout(name + "(AntColonyOpt) is complete\t [" + (k+1) + " of " + len + "]");
				
				TResult tresults = new TResult(name);

				tresults.addResult(res0);
				tresults.addResult(res1);

				results.add(tresults);

			}

			My.cout("Completed searches");
			
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
				File newLogFile = new File("myLogs/"+dir.getName()+".txt");
				new File("myLogs").mkdir();
				newLogFile.createNewFile();
	
				String logStr = String.join("\n", strs);
	
				Files.write(Paths.get(newLogFile.getPath()), logStr.getBytes());

				My.cout("Successfully wrote to log file.");
			}catch(Exception err){
				My.cout("Failed to write to log file...");
				My.cout(err.toString());
			}



		}catch(Exception err){
			My.cout(err);
		}
	}

	public static void m1(){
		File dir = new File("Knapsack Instances/f1_l-d_kp_10_269");
		My.cout("Reading file directories from "+dir.getName()+"...");

		ArrayList<Item> assets = new ArrayList<>();

		int numOfItems; int sackWeight;

		Sack sack;

		try{
			dir = new File(dir.getPath());

			Scanner scanner = new Scanner(dir);

			String name = dir.getName();

			String amtAndCapacity = scanner.nextLine();
			String[] xxx = amtAndCapacity.split(" ");
			if(xxx.length > 1){
				numOfItems = Integer.parseInt(xxx[0]);
				sackWeight = Integer.parseInt(xxx[1]);
				My.cout("NumOfItems: "+numOfItems);
				My.cout("sackWeight: "+sackWeight);

				sack = new Sack(sackWeight);
			}
			
			int i = 0;
			while(scanner.hasNextLine()){
				String asset = scanner.nextLine();

				String[] items = asset.split(" ");
				if(items.length > 1){
					Item item = new Item(Integer.parseInt(items[0]), Integer.parseInt(items[1]));
					My.cout(item.toString());
					assets.add(item);
				}

				i++;
			}

			My.cout("Read a total of "+i+" items.");

		}catch(Exception bruh){
			My.cout("Reading "+dir.getPath()+" failed due to an error. Going to next");
			return;
		}

		

	}
}