import java.util.ArrayList;
import java.util.Arrays;

import java.util.*;
import java.io.*;

public class myMain{

	public static int itCount = 0;

	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");
		
		m4();
		
		My.cout("---------------"); My.cout("| MAIN END |");
        return;
    }

	public static void m1(){
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		// int[] packsizes = new int[]{3,7,1,5};
		int[] packsizes = new int[]{3,5,1,7,9,7,4,1,8,2};
		// int[] packsizes = new int[]{3,5,1};
		ArrayList<Bin.Pack> packs = new ArrayList<>();
		
		int cap = 10;

		for(int i=0; i<packsizes.length; i++){
			int p = packsizes[i];
			packs.add(new Bin.Pack(p));
		}

		Bin.Set set = new Bin.Set(cap);

		BPP.FirstFit(packs.toArray(new Bin.Pack[packs.size()]), set);
		// BPP.NextFit(packs.toArray(new Bin.Pack[packs.size()]), set);
		// BPP.BestFit(packs.toArray(new Bin.Pack[packs.size()]), set);

		My.cout("Set:\n" + set.toString());
		int[] st = set.getState(packs);
		My.cout("State:\n" + Arrays.toString(st));

		int[] _st = new int[st.length - 1];
		for(int i=0;i<_st.length;i++) _st[i] = st[i];
		
		// st = BPP.generateRandomState(_st, st.length);

		// My.cout("RndState:\n" + Arrays.toString(st));

		// ArrayList<int[]> hood = BPP.getNeighbourhood(st, st.length-1);
		ArrayList<int[]> hood = BPP.getNeighbourSolutions(st);
		
		My.cout("Hood:");
		for(int[] nSt:hood){
			int max = -1;
			for(int x:nSt){
				if(max < x) max = x;
			}
			My.cout(Arrays.toString(nSt)+"("+(max+1)+")");
		}
		ArrayList<int[]> fHood = BPP.filterStates(hood, packs, cap);
		My.cout("FHood:");
		for(int[] nSt:fHood){
			int max = -1;
			for(int x:nSt){
				if(max < x) max = x;
			}
			My.cout(Arrays.toString(nSt)+"("+(max+1)+")");
		}

		// ArrayList<int[]> states = BPP.getStates(packs.size());

		// My.cout("Possible States:");
		// for(int[] st:states){
		// 	My.cout(Arrays.toString(st));
		// }

		// ArrayList<int[]> _states = BPP.filterStates(states, packs, cap);
		// My.cout("Valid States:");
		// for(int[] st:_states){
		// 	My.cout(Arrays.toString(st));
		// }
		
		
	}

	public static void m2() throws Exception{
		long sysstart = System.currentTimeMillis();
		try{
			File file = new File("Falkenauer/Falkenauer_T/Falkenauer_t60_00.txt");
			Scanner scanner = new Scanner(file);

			int num = Integer.parseInt(scanner.nextLine());
			int cap = Integer.parseInt(scanner.nextLine());

			ArrayList<Bin.Pack> packs = new ArrayList<>();

			while(scanner.hasNextLine()){
				int packsize = Integer.parseInt(scanner.nextLine());

				Bin.Pack pack = new Bin.Pack(packsize);

				// My.cout(pack);

				packs.add(pack);
			}

			Bin.Set set = new Bin.Set(cap);

			BPP.FirstFit(packs.toArray(new Bin.Pack[packs.size()]), set);
			// BPP.NextFit(packs.toArray(new Bin.Pack[packs.size()]), set);
			// BPP.BestFit(packs.toArray(new Bin.Pack[packs.size()]), set);

			// My.cout("Set:\n" + set.toString());

			int[] st = set.getState(packs);
			My.cout("State:\n" + Arrays.toString(st));
			
			int[] _st = new int[st.length - 1];
			// int[] _st = new int[st.length - 20];
			// int[] _st = new int[st.length - 2];
			// int[] _st = new int[58];
			for(int i=0;i<_st.length;i++) _st[i] = st[i];
			My.cout("PState:\n" + Arrays.toString(_st));
			
			st = BPP.generateRandomState(_st, st.length, true, cap, packs.toArray(new Bin.Pack[0]));

			My.cout("RndState:\n" + Arrays.toString(st));

			// ArrayList<int[]> hood = BPP.getNeighbourSolutions(st);
			ArrayList<int[]> hood = BPP.getNeighbourhood(st, st.length-1);
			
			// My.cout("Hood:");
			// for(int[] nSt:hood){
			// 	int max = -1;
			// 	for(int x:nSt){
			// 		if(max < x) max = x;
			// 	}
			// 	My.cout(Arrays.toString(nSt)+"("+(max+1)+")");
			// }
			ArrayList<int[]> fHood = BPP.filterStates(hood, packs, cap);
			My.cout("FHood:");
			for(int[] nSt:fHood){
				My.cout(Arrays.toString(nSt)+"("+(My.max(nSt)+1)+")");
			}
			My.cout("Set Size:" + st.length);
			My.cout("Cap Size:" + cap);
			My.cout("Hood Size:" + hood.size());
			My.cout("FHood Size:" + fHood.size());

			Bin.Set bestSet = null;
			boolean s = false;

			if(fHood.size() > 0){
				int _i = 0; int _sum = Integer.MAX_VALUE;
				while(_i < fHood.size()){
					Bin.Set _set = new Bin.Set(cap);

					if(_set.setState(fHood.get(_i), packs.toArray(new Bin.Pack[packs.size()]), cap)){
						int fsum = My.sum(fHood.get(_i));
						s = true;
						if(bestSet == null) bestSet = _set;
						else if(_set.bins.size() <= bestSet.bins.size() && fsum<=_sum){
							bestSet = _set;
							_sum = fsum;
						}
					}

					_i++;
				}
			}

			if(bestSet==null) bestSet = new Bin.Set(cap);

			// My.cout("FirstSet:\n"+Arrays.toString(set.getState(packs)));
			// My.cout("BestSet:\n"+Arrays.toString(bestSet.getState(packs)));
			My.cout("--------");
			My.cout("FirstSet size: "+set.bins.size());
			My.cout("BestSet size: "+bestSet.bins.size());
			My.cout("BestSet validity: "+s);
			

		}catch(Exception deezNuts){
			throw deezNuts;
		}
		long sysend = System.currentTimeMillis();
		long time = sysend - sysstart;
		My.cout("TIme: "+time);
	}

	public static void m3() throws Exception{
		try {
			// String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			// // int[] packsizes = new int[]{3,7,1,5};
			// int[] packsizes = new int[]{3,5,1,7,9,7,4,1,8,2};
			// // int[] packsizes = new int[]{3,5,1};
			// ArrayList<Bin.Pack> packs = new ArrayList<>();
			
			// int cap = 10;

			// for(int i=0; i<packsizes.length; i++){
			// 	int p = packsizes[i];
			// 	packs.add(new Bin.Pack(p));
			// }

			File file = new File("Falkenauer/Falkenauer_T/Falkenauer_t60_00.txt");
			Scanner scanner = new Scanner(file);

			int num = Integer.parseInt(scanner.nextLine());
			int cap = Integer.parseInt(scanner.nextLine());

			ArrayList<Bin.Pack> packs = new ArrayList<>();

			while(scanner.hasNextLine()){
				int packsize = Integer.parseInt(scanner.nextLine());

				Bin.Pack pack = new Bin.Pack(packsize);

				// My.cout(pack);

				packs.add(pack);
			}

			packs.sort((a,b)->{
				if(a.size > b.size) return 1;
				if(a.size < b.size) return -1;
				return 0;
			});

			Bin.Set set = new Bin.Set(cap);

			BPP.FirstFit(packs.toArray(new Bin.Pack[packs.size()]), set);
			int[] st = set.getState(packs);
			
			// int[] st = new int[packs.size()];
			// for(int i=0;i<st.length;i++) st[i] = i;

			// int[] st = set.getState(packs);
			// My.cout("InitState:\n" + Arrays.toString(st));

			// int[] _st = new int[st.length - 1];
			// for(int i=0;i<_st.length;i++) _st[i] = st[i];
			
			// st = BPP.generateRandomState(_st, st.length, true, cap, packs.toArray(new Bin.Pack[0]));

			// set.setState(st, packs.toArray(new Bin.Pack[packs.size()]));

			Bin.Set bestSet = Tabu(st, packs.toArray(new Bin.Pack[packs.size()]), cap);
			int[] nSt = bestSet.getState(packs);
			for(int i=0;i<100;i++){
				bestSet = Tabu(nSt, packs.toArray(new Bin.Pack[packs.size()]), cap);
				nSt = bestSet.getState(packs);
			}

			// My.cout("Best State: "); My.cout(bestSet.toString());
			My.cout("State: " + Arrays.toString(st));
			My.cout("Size: " + (My.max(st)+1));
			My.cout("New State: " + Arrays.toString(nSt));
			My.cout("Size: " + (My.max(nSt)+1));

		} catch (Exception deeznutz) {
			throw deeznutz;
		}
		
	}

	public static ArrayList<File> getFiles(File dir){
		ArrayList<File> files = new ArrayList<>();
		if(dir.isDirectory()){
			// My.cout("> Checking inside: "+dir.getName());
			for(File file:dir.listFiles()){
				// My.cout(">> Checking: "+file.getName());
				if(file.isFile()){
					// My.cout(">> Adding: "+file.getName());
					files.add(file);
				}else if(file.isDirectory()){
					ArrayList<File> _files = getFiles(file);
					// My.cout("> Checked inside: "+file.getName());
					My.cout(_files.size());
					if(_files != null){
						for(File _file:_files){
							// My.cout(">> ReAdding: "+_file.getName());
							files.add(_file);
						}
					}
				}
			}

			My.cout("<<<< "+files.size()+" >>>>");

			return files;
		}

		return null;
	}

	public static class Dataset{
		public int[] packs;
		public int capacity;

		public Dataset(int num, int cap){
			packs = new int[num];
			capacity = cap;

		}
	}

	public static void m4(){
		try{
			File dir = new File("Falkenauer");
			ArrayList<File> files = getFiles(dir);

			Dictionary<String,Dataset> datasets = null;

			for(File file:files){
				My.cout(file.getName());
				// Scanner scanner = new Scanner(file);

				// int num = Integer.parseInt(scanner.nextLine());
				// int cap = Integer.parseInt(scanner.nextLine());

				// int i = 0;

				// Dataset dataset = new Dataset(num, cap);

				// while(scanner.hasNextLine() && i<num){
				// 	int packsize = Integer.parseInt(scanner.nextLine());

				// 	dataset.packs[i] = packsize;
				// 	i++;
				// }

			}
		}catch(Exception err){
			My.cout(err);
		}
	}

	public static Bin.Set Tabu(int[] st, Bin.Pack[] dataset, int cap){

		int maxTabuSize = st.length;
		
		int[] currSt = st.clone();
		ArrayList<int[]> tabuList = new ArrayList<>();

		// My.cout("currSt: "+Arrays.toString(currSt));
		
		int[] bestSet = currSt;
		boolean kindaValid = false;
		int minMax = My.max(st);
		int minSum = My.max(st);

		if(minMax <= 1) minMax = Integer.MAX_VALUE;
		if(minSum <= 0) minSum = Integer.MAX_VALUE;

		tabuList.add(currSt);

		for(int c=1; c<st.length; c++){

			// My.cout("len: "+(currSt.length));

			int[] randSt = BPP.generateRandomState(currSt, currSt.length, true, cap, dataset);
			
			int[] parentSt = new int[currSt.length - c];
			for(int i=0;i<parentSt.length;i++) parentSt[i] = currSt[i];

			ArrayList<Integer> currParentSt = new ArrayList<>();
			for(int x:parentSt) currParentSt.add(x);

			ArrayList<Integer> invalidBins = new ArrayList<>();

			for(int d=0; d<c; d++){
				boolean isInvalidBin = false;
				for(int invalidBin:invalidBins){
					if(invalidBin == d){
						isInvalidBin = true; break;
					}
				}
				if(isInvalidBin) continue;

				int max = -1;
				for(Integer x:currParentSt) if(max < x) max = x;

				boolean _valid = false;

				for(int i=0; i<=max+1; i++){
					ArrayList<Integer> initSt = (ArrayList<Integer>) currParentSt.clone();

					initSt.add(i);

					int[] _initSt = new int[initSt.size()];
					for(int _i=0;_i<_initSt.length;_i++) _initSt[_i] = initSt.get(_i);
					
					_valid = new Bin.Set(cap).setState(_initSt, dataset, cap);
					if(_valid){
						boolean isInTabuList = false;
						for(int[] tabu:tabuList){
							if(Arrays.equals(tabu, _initSt)){
								isInTabuList = true; break;
							}
						}
						if(!isInTabuList){
							currParentSt = initSt;
						}
						break;
					}

				}

				if(!_valid){
					if(d == 0) break;
					invalidBins.add(d);
					d = 0;
				}
			}

			if(currParentSt.size() < currSt.length){
				continue;
			}

			currSt = new int[currParentSt.size()];
			for(int _i=0;_i<currSt.length;_i++) currSt[_i] = currParentSt.get(_i);

			int currMax = My.max(currSt);
			int currSum = My.sum(currSt);

			if((currMax < minMax) || (currMax == minMax && currSum < minSum)){
				minMax = currMax;
				minSum = currSum;
				bestSet = currSt;
			}
			tabuList.add(currSt);

			if(tabuList.size() > maxTabuSize && tabuList.size()>0){
				tabuList.remove(0);
			}

		}

		Bin.Set set = new Bin.Set(cap);

		set.setState(bestSet, dataset, cap);
		return set;
	}

	public static Bin.Set TabuSearch(int[] st, Bin.Pack[] dataset, int cap){

		Bin.Set bestSet = null;
		boolean kindaValid = false;
		int minMax = My.max(st);
		int minSum = My.sum(st);
		
		if(minMax <= 1) minMax = Integer.MAX_VALUE;
		if(minSum <= 0) minSum = Integer.MAX_VALUE;

		Bin.Set currSet = new Bin.Set(cap);
		//xyz is true if valid
		boolean xyz = currSet.setState(st, dataset);
		if(xyz) bestSet = currSet;

		boolean hasImproved = false;
		// int improveChance = st.length/2;
		int improveChance = 10;
		
		while(!hasImproved && improveChance>0){

			for(int c=1; c<st.length; c++){
				My.cout("Tabu Lvl: "+c);


				int[] currSt = bestSet.getState(dataset);
	
				int[] parentSt = new int[currSt.length - c];
				for(int i=0;i<parentSt.length;i++) parentSt[i] = currSt[i];
		
				boolean isValid = false;
				int validCount = st.length;
				Bin.Set _set = new Bin.Set(cap);
	
				int[] newSt = currSt;
	
				while(validCount>0 && !isValid){
					newSt = BPP.generateRandomState(parentSt, st.length, true, cap, dataset);
					
					return null;

					// ArrayList<int[]> hood = BPP.getNeighbourhood(newSt, st.length-1);

					// int[] bestOfHood = null;

					// My.cout("hood size: "+hood.size());
					
					// if(hood.size() > 0){
					// 	for(int[] hoodSt:hood){
					// 		Bin.Set _bset = new Bin.Set(cap);
					// 		boolean _isValid = _bset.setState(hoodSt, dataset, cap);
					// 		if(_isValid){
					// 			bestOfHood = hoodSt;
					// 			break;
					// 		}
					// 	}
					// }
					// // My.cout("bestOfhood: "+Arrays.toString(bestOfHood));
	
					// if(bestOfHood == null){
					// 	validCount--;
					// }else{
					// 	newSt = bestOfHood;
					// 	isValid = true;
					// }
				}

				if(isValid){
					int _max = My.max(newSt);
					int _sum = My.sum(newSt);

					if((_max < minMax)||(_max == minMax && _sum < minSum)){
						My.cout("Improvement: max("+_max+" <- "+minMax+")");
						// My.cout("sum("+_sum+" <- "+minSum+")");
						minMax = _max;
						minSum = _sum;
						bestSet = _set;
						kindaValid = true;
						hasImproved = true;
					}
				}

				
			}
			if(!hasImproved){
				improveChance--;
				My.cout("Try and improve again..."+improveChance);
			}
		}

		return bestSet;
	}

	public static Bin.Set IteratedLocalSearch(int[] st, Bin.Pack[] dataset, int cap){
		My.cout("ILS i: "+itCount);
		itCount++;

		Bin.Set currSet = new Bin.Set(cap);
		//xyz is true if valid
		boolean xyz = currSet.setState(st, dataset);
		
		// My.cout("curr is valid: "+xyz);
		if(!xyz) return currSet;

		ArrayList<Bin.Pack> packs = new ArrayList<>();
		for(Bin.Pack pack:dataset) packs.add(pack);

		ArrayList<int[]> initHood = BPP.getNeighbourSolutions(st);
		ArrayList<int[]> hood = BPP.filterStates(initHood, packs, cap);

		Bin.Set bestSet = null;
		boolean s = false;

		if(hood.size() > 0){
			int minMax = My.max(st);
			int minSum = My.sum(st);

			if(minMax <= 1) minMax = Integer.MAX_VALUE;
			if(minSum <= 0) minSum = Integer.MAX_VALUE;

			for(int[] _st:hood){
				int _max = My.max(_st);
				int _sum = My.sum(_st);
				boolean isValid = false;

				Bin.Set _set = new Bin.Set(cap);
				isValid = _set.setState(_st, dataset, cap);

				if(isValid){
					if((_max < minMax)||(_max == minMax && _sum < minSum)){
						minMax = _max;
						minSum = _sum;
						bestSet = _set;
						s = true;
						// My.cout("Improvement");
					}
				}
			}
		}else{
			// My.cout("Hoodless");
		}

		if(s){
			int[] _st = bestSet.getState(dataset);
			if(!Arrays.equals(_st, st)){
				currSet = IteratedLocalSearch(bestSet.getState(dataset), dataset, cap);
			}
			// My.cout("Yee fam");
		}else{
			// My.cout("Nah fam");
		}


		return currSet;
	}
}