import java.util.ArrayList;
import java.util.Arrays;

import java.util.*;
import java.io.*;

public class myMain{

	public static int itCount = 0;

	public static void main(String[] args) throws Exception {
		My.cout("| MAIN START |"); My.cout("---------------");
		
		m3();
		
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
			File file = new File("Hard28/Hard28_BPP13.txt");
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

			// BPP.FirstFit(packs.toArray(new Bin.Pack[packs.size()]), set);
			
			int[] st = new int[packs.size()];
			for(int i=0;i<st.length;i++) st[i] = i;
			// int[] st = set.getState(packs);
			// My.cout("InitState:\n" + Arrays.toString(st));

			// int[] _st = new int[st.length - 1];
			// for(int i=0;i<_st.length;i++) _st[i] = st[i];
			
			// st = BPP.generateRandomState(_st, st.length, true, cap, packs.toArray(new Bin.Pack[0]));

			// set.setState(st, packs.toArray(new Bin.Pack[packs.size()]));

			Bin.Set bestSet = TabuSearch(st, packs.toArray(new Bin.Pack[packs.size()]), cap);
			int[] nSt = bestSet.getState(packs);

			// My.cout("Best State: "); My.cout(bestSet.toString());
			My.cout("State: " + Arrays.toString(st));
			My.cout("Size: " + (My.max(st)+1));
			My.cout("New State: " + Arrays.toString(nSt));
			My.cout("Size: " + (My.max(nSt)+1));

		} catch (Exception deeznutz) {
			throw deeznutz;
		}
		
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
		// else return null;

		for(int c=1; c<st.length; c++){
			My.cout("Tabu Lvl: "+c);

			boolean hasImproved = false;
			int improveChance = st.length/2;

			while(!hasImproved && improveChance>0){
				int[] currSt = bestSet.getState(dataset);
	
				int[] parentSt = new int[currSt.length - c];
				for(int i=0;i<parentSt.length;i++) parentSt[i] = currSt[i];
		
				boolean isValid = false;
				int validCount = st.length;
				Bin.Set _set = new Bin.Set(cap);
	
				int[] newSt = currSt;
	
				while(validCount>0 && !isValid){
					newSt = BPP.generateRandomState(parentSt, st.length, true, cap, dataset);
					isValid = _set.setState(newSt, dataset, cap);
	
					if(!isValid){
						// My.cout("checking again");
						validCount--;
					}
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

				ArrayList<int[]> hood = BPP.getNeighbourhood(newSt, st.length-1);

				if(hood.size() > 0){
					for(int[] hoodSt:hood){

						int _max = My.max(hoodSt);
						int _sum = My.sum(hoodSt);

						Bin.Set _bset = new Bin.Set(cap);

						boolean _isValid = _bset.setState(hoodSt, dataset, cap);
			
						if(_isValid){
							// My.cout("Is valid");
							if((_max < minMax)||(_max == minMax && _sum < minSum)){
								My.cout("Improvement: max("+_max+" <- "+minMax+")");
								// My.cout("sum("+_sum+" <- "+minSum+")");
								minMax = _max;
								minSum = _sum;
								bestSet = _bset;
								kindaValid = true;
								hasImproved = true;
							}
						}
					}
				}
				

				if(!hasImproved){
					improveChance--;
					My.cout("Try and improve again..."+improveChance);
				}
					
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