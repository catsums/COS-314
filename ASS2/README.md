### How to run the program

- Find the .jar file in this folder
- Run the .jar file using
	```bash
	java -jar main.jar
	```
	- Alternatively, you may run the code instead if the .jar file does not work using:
	```bash
		make && make run
	```
	or if you do not have make installed, you may run using the JVM directly
	```bash
		javac -g *.java && java myMain
	```
- Enter the parameters based on the algorithm you need
	- ColonySize is for ACO algorithm
	- PopulationSize, mutationRate, CrossOverRate and Iterations are for GA algorithm
- Once you enter the parameters, the files will be read and you wait for the program to finish running
- A folder called myLogs will be created if it does not exist and .txt file will be created as well containing the log information of each instance and the results