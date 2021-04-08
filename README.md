# The New Maps Project Redistricting Algorithm v1.2

## Updates: v2.0
Effective April 7 2021
No breaking changes to original algorithm methodology
  
**Changes:**
1. New point-based redistricting model: treats data as a collection of single points, and precincts may be divided amongst different districts. No threshold support as of version 2.0. Read more in algorithm documentation.
2. Auto runs point based-based redistricting model when input threshold is "0"


## How to use:

### Run Locally 

**Must have Java installed, preferably JDK 11 or higher**

1. Download the repository onto your local system
2. Put an input file in "inputfiles" directory, see below "Input File Format" for the required format of this file.    
3. Open the command line to compile the code, "cd" into the folder with all the code and run "javac \*.java". (This compiles all .java files)   
4. Run the program's Main class with 4 command line arguments: state name, input file name (in the "inputfiles" directory), # of districts, threshold (as a decimal percent).   
**Example Execution:** java Main "Rhode Island" "rhodeisland.txt" "2" "0.95"
5. When the program has finished running and it prints "Successfully written to results.txt", open the result.txt file and retrieve the output data. See below "Retrieve Output Data" for the file format. 

### Prints during the Algorithm:  
Check the console for lines of print during the algorithm

1. Version of the Algorithm
2. Calculated total population
3. Population threshold (total population / districts * threshold %). Input "0" to use the point-based redistricting model
4. "Starting Algorithm"
5. Results (will print line count if over 100 lines)
6. "Successfully Written", with file name in the "results" folder for output, OR "Error".


## File Formats

### Input File Format: 
**Will not work if not in this format:** .txt files with Comma separated values. Each line: town name, population, latitude, longitude (latitude and longitude are numerical values, do not put N/S/E/W).


**Example:**  
  
Providence,179883,41.8239891,-71.4128343  
Cranston,81456,41.7798226,-71.4372796  
Warwick,81004,41.7001009,-71.4161671  
Pawtucket,72117,41.878711,-71.38255579999999  
East Providence,47618,41.8137116,-71.3700545  
Woonsocket,41751,42.00287609999999,-71.51478390000001  
...   


### Retrieve Output Data: 
After the algorithm is complete, it will write to a new file in the "results" folder located in the root directory. The Name of this file will be printed in the console if run successfully. (Name of file in the format: State-Threshold---Day of Week-Month-Day-Hours-Minutes-Seconds-Timezone-Year)  
  
The content of the output file is in the following format: First Line: State name,threshold (as a decimal). Following lines: town name  (% of population if using point-based model), district #, latitude, longitude, population

**Example:**  
    
Massachusetts,0.95  
Boston,1,42.361799621354926,-71.05746810205792,692600  
Chelsea,1,42.3917638,-71.0328284,39690,   
Worcester,2,42.29045342556963,-71.70299110368317,185428  
Auburn,2,42.1945385,-71.8356271,16766  
Millbury,2,42.19207189999999,-71.761522,13947  
Shrewsbury,2,42.2959267,-71.71284709999999,38526  
Leicester,2,42.245926,-71.90868429999999,11341  
...  

### Sample Input Files
In the folder "inputfiles", there are text files that have data corresponding to the US state name (Ex: westviginia.txt contains data from West Virginian towns). These files are already in the specified CSV format for input files
