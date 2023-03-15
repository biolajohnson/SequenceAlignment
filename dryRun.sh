FILES="./datapoints/in*.txt";

# compiling programs
javac Basic.java
javac EfficientSolutionBiola.java 

# generating results from basic algorithm
for f in $FILES
do
java Basic "$f" "basic_results.csv"
done

# generating result from efficient algorithm
for f in $FILES
do
java EfficientSolutionBiola "$f" "efficient_results.csv"
done


python3 clean_up.py basic_results.csv efficient_results.csv