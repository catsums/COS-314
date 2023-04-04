import java.util.ArrayList;
import java.util.Arrays;

public class BPPx {

	// public static <T> ArrayList<ArrayList<T>> deepCloneArrayList(ArrayList<ArrayList<T>> arr){
	// 	ArrayList<ArrayList<T>> newArr = new ArrayList<ArrayList<T>>();
	// 	for(ArrayList<T> item:arr){
	// 		newArr.add((ArrayList<T>) item.clone());
	// 	}

	// 	return newArr;
	// }
	@SuppressWarnings("")
	public static <T,K> ArrayList<T> deepCloneArrayList(ArrayList<T> arr){
		ArrayList<T> newArr = new ArrayList<>();
		for(T item:arr){
			if(item instanceof ArrayList){
				ArrayList<K> _item = (ArrayList<K>) item;
				newArr.add((T) deepCloneArrayList(_item));
			}else{
				newArr.add(item);
			}
		}

		return newArr;
	}
	
	public static Treex.Node.State createNode(ArrayList<ArrayList<Integer>>  binSetState, ArrayList<Integer> indexSet){

		ArrayList<ArrayList<Integer>> currSt = deepCloneArrayList(binSetState);
		ArrayList<Treex.Node.State> childs = new ArrayList<>();
		
		if(indexSet.size() > 0){
			ArrayList<Integer> dataSet = new ArrayList<>();
			
			Integer currIndex = indexSet.get(0);
			for(int i=1;i<indexSet.size();i++){
				dataSet.add(indexSet.get(i));
			}

			for(int i=0;i<=currSt.size();i++){
				ArrayList<ArrayList<Integer>> initSt = deepCloneArrayList(currSt);
				ArrayList<Integer> _bin = null;
				if(i == currSt.size()){
					_bin = new ArrayList<>();
					initSt.add(_bin);
				}else{
					_bin = initSt.get(i);
				}
				if(_bin == null) continue;

				_bin.add(currIndex);
				childs.add( createNode(initSt, dataSet));
			}
		}

		Treex.Node.State st = new Treex.Node.State(currSt);
		st.childs = childs;

		return st;
	}

	public static String printNode(Treex.Node.State node, int lvl){
		String out = "Level " + lvl + ": \t";

		if(node.data.getClass().isArray()){
			Object[] d = (Object[]) node.data;
			out += "( " + Arrays.toString(d) + " )";
		}else{
			out += "( " + node.data.toString() + " )";
		}
		
		out += " --> { ";
		for(int i=0; i<node.childs.size();i++){
			Treex.Node.State ch = node.childs.get(i);
			
			if(ch.data.getClass().isArray()){
				Object[] d = (Object[]) ch.data;
				out += Arrays.toString(d) + " ";
			}else{
				out += ch.data.toString() + " ";
			}
			if(i < node.childs.size()-1) out += ", ";
		}
		out += "} ";
		if(node.childs.size() > 0){
			out += "\n";
		}
		for(Treex.Node.State ch:node.childs){
			out += printNode(ch, lvl+1) + "\n";
		}

		return out;
	}

}
