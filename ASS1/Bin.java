import java.util.*;

public class Bin{
	ArrayList<Pack> packs;
	int capacity = 1;

	public static class Pack{
		public int size = 0;

		public Pack(){}
		public Pack(int s){ size = s; }

		@Override
		public String toString(){
			return "p-" + size;
		}
	}
	public static class Set{
		public ArrayList<Bin> bins = new ArrayList<Bin>();
		int capacity = 1;

		// State: int[]

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
				Bin b = bins.get(i);
				out += b.toString();
				if(i<bins.size()-1) out += " , ";
			}
			out += " }";
			return out;
		}

		public boolean setState(int[] st, Pack[] dataset, int cap){
			ArrayList<Bin> newBins;
			boolean s = false;
			int maxBin = -1;

			for(int i=0;i<st.length;i++){
				if(st[i] > maxBin) maxBin = st[i];
			}

			if(maxBin < 0) return false;

			newBins = new ArrayList<>(maxBin);
			for(int i=0;i<maxBin+1;i++){
				Bin bin = new Bin(cap);
				newBins.add(bin);
			}

			for(int i=0; i<st.length; i++){
				Bin bin = newBins.get(st[i]);
				s = bin.tryPush(dataset[i]);
				if(!s) break;
			}
			if(s){
				this.bins = newBins;
				this.capacity = cap;
				return true;
			}

			return false;
		}public boolean setState(int[] st, Pack[] dataset){
			return setState(st, dataset, capacity);
		}public boolean setState(int[] st, ArrayList<Pack> dataset, int cap){
			return setState(st, dataset.toArray(new Pack[dataset.size()]), cap);
		}public boolean setState(int[] st, ArrayList<Pack> dataset){
			return setState(st, dataset.toArray(new Pack[dataset.size()]), capacity);
		}
		
		public int[] getState(Pack[] dataset){
			int[] st = new int[dataset.length];

			for(int i=0; i<bins.size();i++){
				Bin bin = bins.get(i);
				if(bin == null) continue;
				for(int j=0;j<bin.packs.size();j++){
					Bin.Pack pack = bin.packs.get(j);
					int index = Arrays.asList(dataset).indexOf(pack);

					if(index >= 0){
						st[index] = i;
					}
				}
				
			}

			return st;
		}public int[] getState(ArrayList<Pack> dataset){
			return getState(dataset.toArray(new Pack[dataset.size()]));
		}

	}

	public Bin(){
		this.capacity = 1;
		packs = new ArrayList<Pack>();
	}public Bin(int cap){
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