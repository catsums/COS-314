import java.util.ArrayList;

public class Treex{

	public Node root = null;

	public static class Node{
		public Object data;
		public ArrayList<Node> childs = new ArrayList<Node>();
		public int hv = Integer.MAX_VALUE;

		public static class State{
			public Object data = null;
			public ArrayList<State> childs = new ArrayList<State>();

			public State(){}
			public State(Object d){
				this.data = d;
			}
			public State(Node node){
				State st = node.getState();
				this.data = st.data;
				this.childs = st.childs;
			}public State(Node node, ProcessFunction func){
				State st = node.getState(func);
				this.data = st.data;
				this.childs = st.childs;
			}
		}

		public Node(){
			this.data = null;
		}public Node(Object _data){
			this.data = _data;
		}public Node(Object _data, int _hv){
			this.data = _data;
			this.hv = _hv;
		}public Node(Object _data, Node[] childs){
			this.data = _data;
			for(Node ch:childs){
				this.childs.add(ch);
			}
		}public Node(Object _data, ArrayList<Node> childs){
			this.data = _data;
			for(Node ch:childs){
				this.childs.add(ch);
			}
		}public Node(Object _data, Node[] childs, int _hv){
			this.data = _data;
			for(Node ch:childs){
				this.childs.add(ch);
			}
			this.hv = _hv;
		}public Node(Object _data, ArrayList<Node> childs, int _hv){
			this.data = _data;
			for(Node ch:childs){
				this.childs.add(ch);
			}
			this.hv = _hv;
		}public Node(State st){
			this.data = null;
			this.setState(st);
		}public Node(State st, ProcessFunction func){
			this.data = null;
			this.setState(st,func);
		}
		

		public State getState(){
			State st = new State();
			st.data = this.data;
			for(Node ch:this.childs){
				State chSt = ch.getState();
				st.childs.add(chSt);
			}
			return st;
		}public State getState(ProcessFunction func){
			State st = new State();
			st.data = func.call(this.data);
			for(Node ch:this.childs){
				State chSt = ch.getState(func);
				st.childs.add(chSt);
			}
			return st;
		}

		public boolean setState(State st){
			if(st == null) return false;

			Object data = st.data;
			if(data == null) return false;

			ArrayList<Node> childs = new ArrayList<Node>();
			for(State ch:st.childs){
				Node chNode = new Node(null);
				chNode.setState(ch);
				childs.add(chNode);
			}

			this.data = data;
			this.childs = childs;

			return true;
		}

		interface ProcessFunction{
			Object call(Object obj);
		}
		interface ProcessFunctionArgs{
			Object call(Object obj, Object[] args);
		}
		public boolean setState(State st, ProcessFunction func){
			if(st == null) return false;
			
			Object data = func.call(st.data);
			if(data == null) return false;

			ArrayList<Node> childs = new ArrayList<Node>();
			for(State ch:st.childs){
				Node chNode = new Node(null);
				boolean succ = chNode.setState(ch, func);
				if(succ) childs.add(chNode);
			}

			this.data = data;
			this.childs = childs;

			return true;
		}

	}

	public Treex(){
		this.root = null;
	}
	public Treex(Node root){
		this.root = root;
	}

	public ArrayList<Node.State> getPathToNode(Node.State node, Node.State root){
		ArrayList<Node.State> path = new ArrayList<>();
		_getPathToNode(node, root, path);

		return path;
	}
	protected boolean _getPathToNode(Node.State node, Node.State root, ArrayList<Node.State> path){
		if(node == null || root == null) return false;

		for(Node.State ch:root.childs){
			if(ch.equals(node)){
				path.add(ch);
				return true;
			}else{
				if(_getPathToNode(node, ch, path)){
					return true;
				}
			}
		}

		return false;
	}

	public ArrayList<Node> getPathToNode(Node node){
		ArrayList<Node> path = new ArrayList<>();
		_getPathToNode(node, this.root, path);

		return path;
	}public ArrayList<Node> getPathToNode(Node node, Node root){
		ArrayList<Node> path = new ArrayList<>();
		_getPathToNode(node, root, path);

		return path;
	}

	protected boolean _getPathToNode(Node node, Node root, ArrayList<Node> path){
		if(node == null || root == null) return false;

		for(Node ch:root.childs){
			if(ch.equals(node)){
				path.add(ch);
				return true;
			}else{
				if(_getPathToNode(node, ch, path)){
					return true;
				}
			}
		}

		return false;
	}

}