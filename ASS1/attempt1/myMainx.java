import java.util.ArrayList;
import java.util.Arrays;

import java.util.*;
import java.io.*;

public class myMainx{

	public static void main(String[] args) throws Exception {
		Myx.cout("| MAIN START |"); Myx.cout("---------------");
		
		m2();
		
		Myx.cout("---------------"); Myx.cout("| MAIN END |");
        return;
    }

	public static void m1(){
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int[] packsizes = new int[]{3,5,1,7};
		ArrayList<Binx.Pack> packs = new ArrayList<>();
		
		int cap = 10;

		for(int i=0; i<packsizes.length; i++){
			int p = packsizes[i];

			packs.add(new Binx.Pack(p));
		}

		int size = packs.size() - 1;
		ArrayList<Integer> indexSet = new ArrayList<>();
		for(int i=0;i<size;i++) indexSet.add(i+1);

		ArrayList<ArrayList<Integer>> initData = new ArrayList<>();
		initData.add(new ArrayList<>(Arrays.asList(new Integer[]{0})));

		Treex.Node.State rootSt = BPPx.createNode(initData, indexSet);

		Treex.Node root = new Treex.Node(rootSt,(data)->{
			Binx.Set set = new Binx.Set(cap);
			boolean succ = set.setState((ArrayList<ArrayList<Integer>>) data, packs, cap);
			
			if(succ) return set;
			return null;
		});

		Treex.Node.State _rootSt = root.getState((data)->{
			Binx.Set set = (Binx.Set) data;
			ArrayList<ArrayList<Integer>> st = set.getState(packs);
			return st;
		});

		
		
	}

	public static void m2(){
		try{
			File file = new File("Falkenauer/Falkenauer_T/Falkenauer_t60_00.txt");
			Scanner scanner = new Scanner(file);

			int num = Integer.parseInt(scanner.nextLine());
			int cap = Integer.parseInt(scanner.nextLine());

			ArrayList<Binx.Pack> packs = new ArrayList<>();

			while(scanner.hasNextLine() && packs.size() < 10){
				int packsize = Integer.parseInt(scanner.nextLine());

				Binx.Pack pack = new Binx.Pack(packsize);

				// My.cout(pack);

				packs.add(pack);
			}

			int len = packs.size();

			ArrayList<Integer> indexSet = new ArrayList<>();
			for(int i=0;i<len;i++) indexSet.add(i+1);

			// for(Integer index:indexSet){
			// 	My.cout(index);
			// }

			ArrayList<ArrayList<Integer>> initData = new ArrayList<>();
			initData.add(new ArrayList<>(Arrays.asList(new Integer[]{0})));

			Treex.Node.State rootSt = BPPx.createNode(initData, indexSet);

			Myx.cout(BPPx.printNode(rootSt, 1));

			// Tree.Node root = new Tree.Node(rootSt,(data)->{
			// 	Bin.Set set = new Bin.Set(cap);
			// 	boolean succ = set.setState((ArrayList<ArrayList<Integer>>) data, packs, cap);
				
			// 	if(succ) return set;
			// 	return null;
			// });

			// Tree.Node.State _rootSt = root.getState((data)->{
			// 	Bin.Set set = (Bin.Set) data;
			// 	ArrayList<ArrayList<Integer>> st = set.getState(packs);
			// 	return st;
			// });

		}catch(Exception deezNuts){
			
		}
	}
}