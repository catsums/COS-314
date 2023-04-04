import java.util.*;

public class BPP {

	public static int[] generateRandomState(int[] st, int depth){
		return generateRandomState(st, depth, false, -1, null);
	}

	public static int[] generateRandomState(int[] st, int depth, boolean checkValidity, int cap, Bin.Pack[] dataset){
		
		// My.cout("st: "+Arrays.toString(st)+"depth: "+depth);

		if(st.length >= depth){
			return st;
		}

		ArrayList<Integer> initSt = new ArrayList<>();
		for(int x:st) initSt.add(x);

		for(int i=initSt.size(); i<depth; i++){
			boolean valid = false;
			int validTryCount = -1;
			int max = -1;
			for(Integer x:initSt) if(max < x) max = x;
			
			ArrayList<Integer> trySt = null;
			// My.cout("i: "+i);
			// My.cout("max: "+max);

			while(!valid && validTryCount < initSt.size()){
				validTryCount++;
				int index = My.rndInt(0, max+1);
				trySt = (ArrayList<Integer>) initSt.clone();

				trySt.add(index);
	
				if(checkValidity){
					Bin.Set set = new Bin.Set(cap);
					int[] _trySt = new int[trySt.size()];

					for(int c=0;c<trySt.size();c++){
						_trySt[c] = trySt.get(i);
					}

					if(set.setState(_trySt, dataset, cap)){
						valid = true;
					}
				}else{
					valid = true;
				}
			}

			if(trySt != null){
				initSt = trySt;
			}

		}

		int[] newSt = new int[initSt.size()];
		for(int i=0; i<initSt.size(); i++) newSt[i] = initSt.get(i);

		return newSt;
	}

	public static ArrayList<int[]> getNeighbourSolutions(int[] st){
		ArrayList<int[]> hood = new ArrayList<>();

		for(int i=1; i<st.length; i++){
			int _max = -1;
			for(int j=0; j<i; j++){
				if(st[j] > _max) _max = st[j];
			}

			for(int c=0; c<=_max+1; c++){
				int[] initSt = st.clone();

				initSt[i] = c;

				if(!Arrays.equals(st, initSt)){
					boolean existsInHood = false;
					for(int[] _st:hood){
						if(Arrays.equals(_st, initSt)){
							existsInHood = true;
							break;
						}
					}
					if(!existsInHood) hood.add(initSt);
				}
			}

		}

		return hood;
	}

	public static ArrayList<int[]> getNeighbourhood(int[] st, int depth){
		ArrayList<int[]> hood = new ArrayList<>();
		if(depth >= st.length || depth<0) return hood;

		int _max = -1;
		int[] parentSt = new int[st.length - (st.length - depth)];
		int[] base = new int[st.length - (st.length - depth) + 1];
		
		for(int i=0; i<parentSt.length; i++){
			parentSt[i] = st[i];
			base[i] = st[i];
			if(parentSt[i] > _max) _max = parentSt[i];
		}

		// My.cout("st: "+Arrays.toString(st));
		// My.cout("parentSt: "+Arrays.toString(parentSt));
		// My.cout("base: "+Arrays.toString(base));

		for(int i=0; i<=depth; i++){
			int[] otherSt = base.clone();
			otherSt[depth] = i;

			if(i <= (_max + 1)){
				hood.add(otherSt);
			}
		}

		if(depth+1 < st.length){
			ArrayList<int[]> leaves = new ArrayList<>();

			for(int i=0; i<hood.size();i++){
				My.cout("> "+Arrays.toString(hood.get(i)));
				_getStates(st.length, hood.get(i), leaves, depth+1);
			}

			hood = leaves;

		}

		return hood;

	}

	public static void FirstFit(Bin.Pack[] packs, Bin.Set set){
		for(Bin.Pack pack:packs){
			boolean s = false;
			for(Bin bin:set.bins){
				s = bin.tryPush(pack);
				if(s) break;
			}
			if(!s){
				Bin newbin = new Bin(set.capacity);
				newbin.tryPush(pack);
				set.bins.add(newbin);
			}
		}
	}
	public static void NextFit(Bin.Pack[] packs, Bin.Set set){
		for(Bin.Pack pack:packs){
			boolean s = false;
			if(set.bins.size()>0){
				Bin bin = set.bins.get(set.bins.size()-1);
				s = bin.tryPush(pack);
			}

			if(!s){
				Bin newbin = new Bin(set.capacity);
				newbin.tryPush(pack);
				set.bins.add(newbin);
			}
		}
	}
	public static void BestFit(Bin.Pack[] packs, Bin.Set set){
		for(Bin.Pack pack:packs){
			boolean s = false;

			Bin bin = null;

			for(int i=0;i<set.bins.size();i++){
				Bin b = set.bins.get(i);

				if(
					(bin == null || (bin.getFreeSpace() > b.getFreeSpace())
					&&
					(b.getFreeSpace() >= pack.size)
				)){
					bin = b;
				}
			}
			if(bin != null){
				s = bin.tryPush(pack);
			}
			if(!s || bin==null){
				Bin newbin = new Bin(set.capacity);
				newbin.tryPush(pack);
				set.bins.add(newbin);
			}
		}
	}

	public static ArrayList<int[]> filterStates(ArrayList<int[]> states, ArrayList<Bin.Pack> dataset, int cap){
		return filterStates(states, dataset.toArray(new Bin.Pack[dataset.size()]), cap);
	}public static ArrayList<int[]> filterStates(ArrayList<int[]> states, Bin.Pack[] dataset, int cap){
		ArrayList<int[]> newStates = new ArrayList<>();

		for(int[] st:states){
			Bin.Set set;
			set = new Bin.Set(cap);
			if(set.setState(st, dataset, cap)){
				newStates.add(st);
			}
			
		}

		return newStates;
	}

	public static ArrayList<int[]> getStates(Bin.Pack[] dataset, int cap){
		ArrayList<int[]> states = new ArrayList<>();
		_getStates(dataset.length, new int[0], states, 0);

		states = filterStates(states, dataset, cap);

		return states;
	}public static ArrayList<int[]> getStates(ArrayList<Bin.Pack> dataset, int cap){
		return getStates(dataset.toArray(new Bin.Pack[dataset.size()]), cap);
	}public static ArrayList<int[]> getStates(int datasetSize){
		ArrayList<int[]> states = new ArrayList<>();
		_getStates(datasetSize, new int[0], states, 0);
		return states;
	}
	public static void _getStates( Bin.Pack[] dataset, int[] initPath, ArrayList<int[]> states){
		_getStates(dataset.length, initPath, states, 0);
		return;
	}public static void _getStates( int datasetSize, int[] initPath, ArrayList<int[]> states){
		_getStates(datasetSize, initPath, states, 0);
		return;
	}public static void _getStates( Bin.Pack[] dataset, int[] initPath, ArrayList<int[]> states , int depth ){
		_getStates(dataset.length, initPath, states, depth);
		return;
	}public static void _getStates( int datasetSize, int[] initPath, ArrayList<int[]> states , int depth ){
		// My.cout("set:" + datasetSize + " path: " + Arrays.toString(initPath) + " depth: " + depth);
		
		if(initPath.length >= datasetSize){
			states.add(initPath);
			return;
		}

		int _max = -1;

		int[] newPath = new int[initPath.length + 1];
		for(int i=0; i<initPath.length; i++){
			newPath[i] = initPath[i];
			if(initPath[i] > _max) _max = initPath[i];
		}

		for(int i=0; i<=depth; i++){
			int[] _path = newPath.clone();
			_path[_path.length-1] = i;

			if(i <= (_max + 1)){
				_getStates(datasetSize, _path, states, depth+1);
			}
		}

	}

	// public static <T> ArrayList<ArrayList<T>> deepCloneArrayList(ArrayList<ArrayList<T>> arr){
	// 	ArrayList<ArrayList<T>> newArr = new ArrayList<ArrayList<T>>();
	// 	for(ArrayList<T> item:arr){
	// 		newArr.add((ArrayList<T>) item.clone());
	// 	}

	// 	return newArr;
	// }
	// @SuppressWarnings("")
	// public static <T,K> ArrayList<T> deepCloneArrayList(ArrayList<T> arr){
	// 	ArrayList<T> newArr = new ArrayList<>();
	// 	for(T item:arr){
	// 		if(item instanceof ArrayList){
	// 			ArrayList<K> _item = (ArrayList<K>) item;
	// 			newArr.add((T) deepCloneArrayList(_item));
	// 		}else{
	// 			newArr.add(item);
	// 		}
	// 	}

	// 	return newArr;
	// }
	
	// public static Tree.Node.State createNode(ArrayList<ArrayList<Integer>>  binSetState, ArrayList<Integer> indexSet){

	// 	ArrayList<ArrayList<Integer>> currSt = deepCloneArrayList(binSetState);
	// 	ArrayList<Tree.Node.State> childs = new ArrayList<>();
		
	// 	if(indexSet.size() > 0){
	// 		ArrayList<Integer> dataSet = new ArrayList<>();
			
	// 		Integer currIndex = indexSet.get(0);
	// 		for(int i=1;i<indexSet.size();i++){
	// 			dataSet.add(indexSet.get(i));
	// 		}

	// 		for(int i=0;i<=currSt.size();i++){
	// 			ArrayList<ArrayList<Integer>> initSt = deepCloneArrayList(currSt);
	// 			ArrayList<Integer> _bin = null;
	// 			if(i == currSt.size()){
	// 				_bin = new ArrayList<>();
	// 				initSt.add(_bin);
	// 			}else{
	// 				_bin = initSt.get(i);
	// 			}
	// 			if(_bin == null) continue;

	// 			_bin.add(currIndex);
	// 			childs.add( createNode(initSt, dataSet));
	// 		}
	// 	}

	// 	Tree.Node.State st = new Tree.Node.State(currSt);
	// 	st.childs = childs;

	// 	return st;
	// }

	// public static String printNode(Tree.Node.State node, int lvl){
	// 	String out = "Level " + lvl + ": \t";

	// 	if(node.data.getClass().isArray()){
	// 		Object[] d = (Object[]) node.data;
	// 		out += "( " + Arrays.toString(d) + " )";
	// 	}else{
	// 		out += "( " + node.data.toString() + " )";
	// 	}
		
	// 	out += " --> { ";
	// 	for(int i=0; i<node.childs.size();i++){
	// 		Tree.Node.State ch = node.childs.get(i);
			
	// 		if(ch.data.getClass().isArray()){
	// 			Object[] d = (Object[]) ch.data;
	// 			out += Arrays.toString(d) + " ";
	// 		}else{
	// 			out += ch.data.toString() + " ";
	// 		}
	// 		if(i < node.childs.size()-1) out += ", ";
	// 	}
	// 	out += "} ";
	// 	if(node.childs.size() > 0){
	// 		out += "\n";
	// 	}
	// 	for(Tree.Node.State ch:node.childs){
	// 		out += printNode(ch, lvl+1) + "\n";
	// 	}

	// 	return out;
	// }

}
