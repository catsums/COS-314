import java.util.ArrayList;

public class Binx {
	ArrayList<Pack> packs;
	int capacity = 1;

	// State: int[] packs or ArrayList<int>

	public static class Pack{
		public int size = 0;

		// State: int size

		public Pack(){}
		public Pack(int s){ size = s; }

		@Override
		public String toString(){
			return "p-" + size;
		}
	}
	public static class Set{
		public ArrayList<Binx> bins = new ArrayList<Binx>();
		int capacity = 1;

		// State: int[][] bins or ArrayList<ArrayList<int>>

		public Set(){}
		public Set(int cap){ capacity = cap; }

		public boolean tryPush(Pack newPack, int binIndex){
			if(binIndex < 0 || binIndex >= bins.size()) return false;

			return bins.get(binIndex).tryPush(newPack);
		}

		@Override
		public String toString(){
			String out = "SET{ ";
			for(int i=0;i<bins.size();i++){
				Binx b = bins.get(i);
				out += b.toString();
				if(i<bins.size()-1) out += " , ";
			}
			out += " }";
			return out;
		}

		public boolean setState(ArrayList<ArrayList<Integer>> st, Pack[] dataset){
			ArrayList<Binx> oldBins = this.bins;
			this.bins = new ArrayList<Binx>();
			boolean s = false;

			for(int i=0;i<st.size();i++){
				Binx bin = new Binx(capacity);
				s = bin.setState(st.get(i), dataset);
				if(!s) break;

				this.bins.add(bin);
			}

			if(s) return true;
			this.bins = oldBins;

			return false;
		}public boolean setState(ArrayList<ArrayList<Integer>> st, ArrayList<Pack> dataset){
			return setState(st, dataset.toArray(new Pack[dataset.size()]));
		}
		public boolean setState(ArrayList<ArrayList<Integer>> st, Pack[] dataset, int cap){
			int oldCap = this.capacity;
			this.capacity = cap;

			boolean s = setState(st,dataset);
			if(!s) this.capacity = oldCap;

			return s;
		}public boolean setState(ArrayList<ArrayList<Integer>> st, ArrayList<Pack> dataset, int cap){
			return setState(st, dataset.toArray(new Pack[dataset.size()]), cap);
		}
		public ArrayList<ArrayList<Integer>> getState(Pack[] dataset){
			ArrayList<ArrayList<Integer>> st = new ArrayList<ArrayList<Integer>>();

			for(int i=0;i<bins.size();i++){
				Binx bin = bins.get(i);
				st.add(bin.getState(dataset));
			}

			return st;
		}public ArrayList<ArrayList<Integer>> getState(ArrayList<Pack> dataset){
			return getState(dataset.toArray(new Pack[dataset.size()]));
		}

	}

	public Binx(){
		this.capacity = 1;
		packs = new ArrayList<Pack>();
	}public Binx(int cap){
		if(cap > 1) this.capacity = cap;
		packs = new ArrayList<Pack>();
	}

	///Main Funcs

	public boolean tryPush(Pack newPack){
		int newSize = this.getSize() + newPack.size;
		if(newSize > this.capacity) return false;

		this.packs.add(newPack);
		return true;
	}

	///Getters and Setters

	@Override
	public String toString(){
		String out = "BIN[ ";
		for(int i=0;i<packs.size();i++){
			Pack p = packs.get(i);
			out += p.toString();
			if(i<packs.size()-1) out += " | ";
		}
		out += " ]";
		return out;
	}

	public boolean setState(ArrayList<Integer> st, Pack[] dataset){
		ArrayList<Pack> oldPacks = this.packs;
		this.packs = new ArrayList<Pack>();

		for(int ind:st){
			this.packs.add(dataset[ind]);
		}

		if(this.getFreeSpace() >= 0) return true;
		this.packs = oldPacks;

		return false;
	}

	public ArrayList<Integer>  getState(Pack[] dataset){
		ArrayList<Integer> st = new ArrayList<Integer>();

		for(Object p:packs.toArray()){
			Pack _p = (Pack) p;
			int index = -1;
			for(int i=0;i<dataset.length;i++){
				if(dataset[i] == _p){
					index = i; break;
				}
			}
			if(index >= 0){
				st.add(index);
			}
		}

		return st;
	}

	public int getFreeSpace(){
		return this.capacity - this.getSize();
	}

	public int getSize(){
		int s = 0;
		for(Pack pack:packs){
			s += (int) pack.size;
		}
		return s;
	}

	public int getCapacity(){ return capacity; }

}