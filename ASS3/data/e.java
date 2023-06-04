File dataFile = new File("data/breast-cancer.data");

ArrayList<double[]> dataset = new ArrayList<>();
ArrayList<double[]> classSet = new ArrayList<>();

ArrayList<double[]> fixSet = new ArrayList<>();

String[][] attributes = {
	{"no-recurrence-events", "recurrence-events"},
	{"10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99"},
	{"lt40", "ge40", "premeno"},
	{"0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44","45-49", "50-54", "55-59"},
	{"0-2", "3-5", "6-8", "9-11", "12-14", "15-17", "18-20", "21-23", "24-26","27-29", "30-32", "33-35", "36-39"},
	{"yes", "no"},
	{"1", "2", "3"},
	{"left", "right"},
	{"left_up", "left_low", "right_up", "right_low", "central"},
	{"yes", "no"},
};

double ratio = (0.75);

long _seed = 9_876_543_210l;

double acc = 0.0001;
double lRate = 1;
boolean isBipolar = false;

double factor = 1;

ArrayList<String[]> lines = new ArrayList<>();
try{
	Scanner scanner = new Scanner(dataFile);
	int count = 0; int len = 286;
	

	while(scanner.hasNextLine()){
		String line = scanner.nextLine();

		String[] vars = line.split(",");
		lines.add(vars);

		count++;
	}

	My.cout("Counted "+count+" batch instances.");
	
}catch(Exception e){
	e.printStackTrace();
}

String[][] _lines = shuffleArray(lines.toArray(new String[0][]), _seed);
_lines = shuffleArray(_lines, _seed);

for(String[] vars:_lines){
	ArrayList<Double> data = new ArrayList<>();
	ArrayList<Double> classifier = new ArrayList<>();

	boolean dataMissing = false;

	for(int i=0;i<attributes.length;i++){
		double attr = -1;
		for(int a=0;a<attributes[i].length;a++){
			String _var = vars[i].trim().toLowerCase();
			String _att = attributes[i][a].trim().toLowerCase();
			if(_var.compareTo(_att)==0){
				attr = a;
				break;
			}
		}
		if(attr!=-1){
			if(i==0 || i==9){
				double k = ((attr+1) / attributes[i].length);
				k = (k>0.5 ? 1 : 0);
				classifier.add(k);
			}else{
				double k = ((attr+1) / attributes[i].length);
				data.add(My.stepify(k, acc));
			}
		}else{
			dataMissing = true;
			data.add(-1.0);
		}
	}
	double[] dataArr = new double[data.size()];
	for(int i=0;i<dataArr.length;i++) dataArr[i] = data.get(i);
	double[] classArr = new double[classifier.size()];
	for(int i=0;i<classArr.length;i++) classArr[i] = classifier.get(i);

	if(dataMissing){
		fixSet.add(dataArr);
	}else{
		dataset.add(dataArr);
		classSet.add(classArr);
	}
}

ArrayList<double[]> trainData = new ArrayList<>();
ArrayList<double[]> trainOutData = new ArrayList<>();

for(int i=0;i<(int)(ratio * (double)dataset.size()); i++){
	trainData.add(dataset.get(i));
	trainOutData.add(classSet.get(i));
}

ArrayList<double[]> testData = new ArrayList<>();
ArrayList<double[]> testOutData = new ArrayList<>();

for(int i=trainData.size();i<dataset.size(); i++){
	testData.add(dataset.get(i));
	testOutData.add(classSet.get(i));
}

My.cout("Dataset: "+dataset.size());
My.cout("TrainData: "+trainData.size());
My.cout("TrainOutData: "+trainOutData.size());
My.cout("TestData: "+testData.size());
My.cout("TestOutdata: "+testOutData.size());
My.cout("FixData: "+fixSet.size());

int inputSize = trainData.get(0).length;
int outputSize = trainOutData.get(0).length;

My.cout("");
My.cout("InputSize: "+inputSize);
My.cout("OutputSize: "+outputSize);