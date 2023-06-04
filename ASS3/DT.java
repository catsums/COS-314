import java.util.function.Function;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class DT implements Serializable{

	public static class Node implements Serializable{
		public String attr;
		public HashMap<String,Node> childs = new HashMap<>();

		public Node(){
			this.attr = null;
		}
		public Node(String attr){
			this.attr = attr;
		}
		//copy constructor
		public Node(Node other){
			this.attr = other.attr;

			for(String key:other.childs.keySet()){
				Node otherChild = other.getChild(key);

				childs.put(key, new Node(otherChild));
			}
		}

		public int DFSCountNodes(){
			int count = 1;
			if(isLeaf()) return count;

			for(String key:childs.keySet()){
				Node child = childs.get(key);
				if(child==null) continue;

				count += child.DFSCountNodes();
			}

			return count;

		}

		public int DFSDepth(){
			return DFSDepth(0);
		}
		public int DFSDepth(int depth){
			if(isLeaf()){
				return depth;
			}

			int newDepth = depth;

			for(String key:childs.keySet()){
				Node child = childs.get(key);
				if(child==null) continue;

				int d = child.DFSDepth(depth+1);
				if(d>newDepth) newDepth = d;
			}
			
			return newDepth;
		}

		public Node DFSFindNodeByLabel(String att, String label){
			Node parent = DFSFindNodeByAttr(att);

			if(parent==null) return null;

			return parent.getChild(label);
		}

		public Node DFSFindParent(Node root){
			if(root == null || root == this || root.isLeaf()) return null;

			for(String key:root.childs.keySet()){
				Node child = root.childs.get(key);
				if(child == null){
					continue;
				}

				if(child == this || child.hashCode() == this.hashCode()){
					return root;
				}

				if(!child.isLeaf()){
					Node _parent = this.DFSFindParent(child);
					if(_parent!=null){
						return _parent;
					}
				}
			}
			return null;
		}

		public Node DFSFindNodeByAttr(String attr){
			if(this.attr.compareTo(attr)==0) return this;

			for(String key:childs.keySet()){
				Node child = childs.get(key);
				if(child.attr.compareTo(attr)==0){
					return child;
				}
				if(!child.isLeaf()){
					Node _node = child.DFSFindNodeByAttr(attr);
					if(_node==null) continue;
	
					if(_node.attr.compareTo(attr)==0){
						return _node;
					}
				}
			}
			return null;
		}
		public ArrayList<Node> DFSFindNodesByAttr(String attr){
			return DFSFindNodesByAttr(attr, new ArrayList<>());
		}
		public ArrayList<Node> DFSFindNodesByAttr(String attr, ArrayList<Node> list){

			for(String key:childs.keySet()){
				Node child = childs.get(key);
				if(child.attr.compareTo(attr)==0){
					list.add(child);
				}
				if(!child.isLeaf()){
					list = child.DFSFindNodesByAttr(attr, list);
				}
			}
			return list;
		}

		public boolean ownsChild(Node child){
			for(String key:childs.keySet()){
				Node _child = childs.get(key);
				if(child == null) continue;
				if(child == _child || child.hashCode() == _child.hashCode()){
					return true;
				}
			}
			return false;
		}

		public void addChild(String label, Node child){
			if(child==null) return;
			
			childs.put(label, child);
		}

		public boolean hasLabel(String label){
			return childs.containsKey(label);
		}
		
		public String getLabelForChild(Node child){
			if(child==null) return null;

			for(String k:childs.keySet()){
				if(childs.get(k)==child){
					return k;
				}
			}
			return null;
		}

		public Node getChild(String label){
			return childs.get(label);
		}

		public Node removeChild(String label){
			if(!hasLabel(label)) return null;
			return childs.remove(label);
		}

		public Node setChild(String label, Node newNode){
			Node oldNode = null;
			if(hasLabel(label)){
				oldNode = getChild(label);
			}
			addChild(label, newNode);

			return oldNode;
		}

		public boolean isLeaf(){
			return (childs.size() <= 0);
		}

		@Override
		public String toString(){
			return toString(0);
		}
		protected String toString(int depth){
			String out = "";

			for(int i=0;i<depth;i++){
				out += "  ";
				if(i==depth-1) out+="- ";
			}

			out += attr;
			if(!isLeaf()){
				out += " [ ";
				String[] childKeys = childs.keySet().toArray(new String[0]);
				for(int i=0;i<childKeys.length;i++){
					String key = childKeys[i];
					Node child = childs.get(key);
					if(child==null) continue;
					out += key + ":" + child.attr;
					
					if(i<childKeys.length-1) out += " , ";
				}
				out += " ]";
				
				for(String key:childKeys){
					Node child = childs.get(key);
					if(child==null) continue;
					if(!child.isLeaf()){
						out += "\n" + child.toString(depth+1);
					}
				}
			}

			return out;
		}
		
	}

	public HashMap<String, ArrayList<String>> labels = new HashMap<>();
	public String[] attributes;

	public DT(String[] attributes){
		labels = new HashMap<>();
		this.attributes = attributes;
		for(String att:attributes){
			labels.put(att, new ArrayList<>());
		}
	}

	public void addData(String[] data){
		for(int i=0;i<attributes.length;i++){
			if(i>data.length) break;
			ArrayList<String> list = labels.get(attributes[i]);
			list.add(data[i]);
		}
	}

	public ArrayList<String> possibleValuesForAttr(String attr){
		ArrayList<String> vals = new ArrayList<>();

		if(!labels.containsKey(attr)) return null;
		ArrayList<String> labelSet = labels.get(attr);
		
		for(String label:labelSet){
			if(vals.contains(label)) continue;
			vals.add(label);
		}
		return vals;
	}

	public String decideLabel(Node tree, ArrayList<String> data){

		return checkAttr(tree, data, new ArrayList<>(Arrays.asList(attributes)));
	}

	protected String checkAttr(Node node, ArrayList<String> data, ArrayList<String> attrs){
		String attr = node.attr;
		int indexOfData = -1;
		for(int i=0;i<attrs.size();i++){
			if(attr.compareTo(attrs.get(i))==0){
				indexOfData = i;
				break;
			}
		}

		if(indexOfData<0){
			// My.cout("nullable Node: "+node.attr);
			return null;
		}
		
		String val = data.get(indexOfData);
		
		if(node.hasLabel(val)){
			Node child = node.getChild(val);
			if(child.isLeaf()) return child.attr;
			
			return checkAttr(child, data, attrs);
		}
		// My.cout("mid nullable");
		// My.cout("Node: \n"+node);
		// My.cout("Node childs: "+Arrays.toString(node.childs.keySet().toArray()));
		// My.cout("label: "+val);
		// My.cout("indexOfData: "+indexOfData);
		// My.cout("data: "+Arrays.toString(data.toArray()));
		return null;
	}

	public Node ID3(String targetAttr){
		ArrayList<String> newAttrs = new ArrayList<>();

		for(String att:attributes){
			newAttrs.add(att);
		}
		newAttrs.remove(targetAttr);

		return ID3(labels, targetAttr, newAttrs);
	}
	public Node ID3(HashMap<String,ArrayList<String>> labelSet, String targetAttr, ArrayList<String> attrs){
		Node newNode = new Node();
		
		//check if labelSet for targetAttr have the same labels
		HashMap<String, Integer> labelCount = new HashMap<>();
		String onlyLabel = null; boolean onlyOneLabel = true;
		String[] list = labelSet.get(targetAttr).toArray(new String[0]);
		for(String label:list){
			int count = 0;
			if(labelCount.containsKey(label)){
				count = labelCount.get(label);
			}
			count++;
			labelCount.put(label, count);
			if(onlyLabel==null) onlyLabel = label;
			if(onlyLabel.compareTo(label)!=0) onlyOneLabel = false;
		}
		if(onlyOneLabel){
			newNode.attr = onlyLabel;
			return newNode;
		}
		//check if attributeSet is empty
		String mostCommon = null;
		for(String label:list){
			if(mostCommon==null) mostCommon = label;
			else if(labelCount.get(label) > labelCount.get(mostCommon)){
				mostCommon = label;
			}
		}
		
		if(attrs.size()<=0){
			newNode.attr = mostCommon;
			return newNode;
		}
		//get attr with highest gain and set it as attr for node
		String highGainAttr = null; double highGainVal = Double.NEGATIVE_INFINITY;
		for(String attr:attrs){
			double gain = calculateGain(targetAttr, attr);
			if(gain>highGainVal){
				highGainAttr = attr;
				highGainVal = gain;
			}
		}
		
		newNode.attr = highGainAttr;
		
		String[] labelList = labelSet.get(newNode.attr).toArray(new String[0]);
		String[] setKeys = labelSet.keySet().toArray(new String[0]);
		
		// ArrayList<String> newAttrs = (ArrayList) attrs.clone();
		ArrayList<String> newAttrs = attrs;
		newAttrs.remove(newNode.attr);
		// My.cout(newNode.attr + " removed.");
		
		ArrayList<String> counted = new ArrayList<>();
		
		for(String label:labelList){

			if(counted.contains(label)) continue;
			counted.add(label);

			Node childNode;
			//create subset of new attrs
			
			//create subset of new labelset
			HashMap<String,ArrayList<String>> newLabelSet = new HashMap<>();
			for(String key:labelSet.keySet()){
				newLabelSet.put(key, new ArrayList<>());
			}
			for(int i=0;i<labelList.length;i++){
				if(labelList[i].compareTo(label)==0){
					for(String key:labelSet.keySet()){
						newLabelSet.get(key).add( labelSet.get(key).get(i) );
					}
				}
			}

			if(newLabelSet.get(newNode.attr).size()<=0){
				childNode = new Node(mostCommon);
			}else{
				childNode = ID3(newLabelSet, targetAttr, newAttrs);
			}
			// My.cout(newNode.attr+" added "+childNode.attr+" for "+label);

			newNode.addChild(label, childNode);
		}

		return newNode;
	}

	public Node RandomID3(String targetAttr, long seed){
		ArrayList<String> newAttrs = new ArrayList<>();

		for(String att:attributes){
			newAttrs.add(att);
		}
		newAttrs.remove(targetAttr);

		return RandomID3(labels, targetAttr, newAttrs, seed, 2);
	}

	public Node RandomID3(HashMap<String,ArrayList<String>> labelSet, String targetAttr, ArrayList<String> attrs, long seed, int iter){
		Node newNode = new Node();

		//check if labelSet for targetAttr have the same labels
		HashMap<String, Integer> labelCount = new HashMap<>();
		String onlyLabel = null; boolean onlyOneLabel = true;
		String[] list = labelSet.get(targetAttr).toArray(new String[0]);
		for(String label:list){
			int count = 0;
			if(labelCount.containsKey(label)){
				count = labelCount.get(label);
			}
			count++;
			labelCount.put(label, count);
			if(onlyLabel==null) onlyLabel = label;
			if(onlyLabel.compareTo(label)!=0) onlyOneLabel = false;
		}
		if(onlyOneLabel){
			newNode.attr = onlyLabel;
			return newNode;
		}
		//check if attributeSet is empty
		int rndIndex = My.rndInt(0, list.length-1, (long)My.rndInt(123456789,987654321, seed)*iter);
		String randomLabel = list[rndIndex];
		if(attrs.size()<=0){
			newNode.attr = randomLabel;
			return newNode;
		}

		//get attr by random
		rndIndex = My.rndInt(0, attrs.size()-1, seed*12345*labelSet.size());
		String highGainAttr = attrs.get(rndIndex);
		newNode.attr = highGainAttr;
		
		String[] labelList = labelSet.get(newNode.attr).toArray(new String[0]);

		ArrayList<String> newAttrs = attrs;
		newAttrs.remove(newNode.attr);

		ArrayList<String> counted = new ArrayList<>();
		
		for(String label:labelList){
			if(counted.contains(label)) continue;
			counted.add(label);

			Node childNode;
			//create subset of new attrs
			
			//create subset of new labelset
			HashMap<String,ArrayList<String>> newLabelSet = new HashMap<>();

			for(String key:labelSet.keySet()){
				newLabelSet.put(key, new ArrayList<>());
			}
			for(int i=0;i<labelList.length;i++){
				if(labelList[i].compareTo(label)==0){
					for(String key:labelSet.keySet()){
						newLabelSet.get(key).add( labelSet.get(key).get(i) );
					}
				}
			}

			if(newLabelSet.get(newNode.attr).size()<=0){
				childNode = new Node(randomLabel);
			}else{
				childNode = RandomID3(newLabelSet, targetAttr, newAttrs, seed*123456789, iter*(iter+1));
			}
			// My.cout(newNode.attr+" added "+childNode.attr+" for "+label);

			newNode.addChild(label, childNode);
		}

		return newNode;

	}

	public double calculateGain(String attr, String condAttr){

		double sumOfEds = 0;

		if(!labels.containsKey(attr)) return 0;
		if(!labels.containsKey(condAttr)) return 0;

		String[] list = possibleValuesForAttr(attr).toArray(new String[0]);
		String[] condList = possibleValuesForAttr(condAttr).toArray(new String[0]);
		// ArrayList<String> condCounted =  new ArrayList<>();

		double eD = calculateEntropy(attr);

		
		for(String condSamp:condList){

			double p = instanceRatio(condSamp, condAttr);
			double Ent = calculateEntropy(attr, condSamp, condAttr);
			
			sumOfEds += (p*Ent);

			// My.cout("p: "+p);
			// My.cout("ent: "+Ent);
			
		}
		
		double gainz = (eD - sumOfEds);
		
		// My.cout("ED: "+eD);
		// My.cout("sumOfEds: "+sumOfEds);

		return gainz;

	}

	public double calculateEntropy(String attr){
		double En = 0;

		if(!labels.containsKey(attr)) return -1;

		String[] list = possibleValuesForAttr(attr).toArray(new String[0]);

		for(String samp:list){

			double p = instanceRatio(samp, attr);

			if(p!=0) En += (-p * My.log(2, p));

		}

		return En;

	}
	public double calculateEntropy(String attr, String condLabel, String condAttr){
		double En = 0;

		if(!labels.containsKey(attr)) return 0;
		if(!labels.containsKey(condAttr)) return 0;

		String[] list = possibleValuesForAttr(attr).toArray(new String[0]);

		for(String samp:list){
			double p = instanceRatio(new String[]{samp, attr}, new String[]{condLabel, condAttr});

			if(p!=0) En += (-p * My.log(2, p));

		}

		return En;

	}
	protected double instanceRatio(String label, String attr){
		//p(i) = numOfInst i class is for / total instances
		double p = 0;
		double numOfInst = 0;

		if(!labels.containsKey(attr)) return 0;

		String[] list = labels.get(attr).toArray(new String[0]);

		for(int i=0;i<list.length;i++){
			String samp = list[i];
			if(samp.compareTo(label)==0){
				numOfInst ++;
			}
		}

		p = (numOfInst / list.length);

		return p;
	}
	protected double instanceRatio(String[] labelPair, String[] condPair){
		//p(i) = numOfInst i class is for / total instances
		double p = 0;
		double numOfInst = 0;
		double numOfCondInst = 0;

		String label = labelPair[0];
		String attr = labelPair[1];
		String condLabel = condPair[0];
		String condAttr = condPair[1];

		if(!labels.containsKey(attr)) return 0;
		if(!labels.containsKey(condAttr)) return 0;

		String[] list = labels.get(attr).toArray(new String[0]);
		String[] condList = labels.get(condAttr).toArray(new String[0]);

		for(int i=0;i<list.length;i++){
			String samp = list[i];
			String condSamp = condList[i];

			if(condSamp.compareTo(condLabel)==0){
				numOfCondInst ++;
				if(samp.compareTo(label)==0){
					numOfInst++;
				}
			}
			
		}

		if(numOfCondInst<=0) return 0;
		if(numOfInst<=0) return 0;

		p = (numOfInst / numOfCondInst);

		return p;
	}
}
