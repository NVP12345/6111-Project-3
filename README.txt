COMS E6111 ADVANCED DATABASE SYSTEMS
PROJECT 3

a)TEAM:  NICOLO PIZZOFERRATO(nvp2015)
         NATASHA S KENKRE(nsk2141)

b)The following is a list of all the files that we are submitting:
> source files :
                 AprioriFinder.java (main file)
                 domain/RuleObject.java
		 domain/DoubleValidatorUtil.java
                 comparator/Compare.java
> libraries (jars):
                 lib/guava-18.0.jar
> build files:
                 build.xml
> script files:
                 dataset_gen.py
> text files:
                 README.txt
                 dataset_source.csv
                 INTEGRATED-DATASET.csv
                 example-run.txt


c) We used the Stop & Frisk data from the NYPD for all five boroughs from 2014 as the source. To keep the size of the data reasonable, we limited the data to a single month (May) and selected only somewhat interesting fields to try and find some meaningful correlation (the 19 columns specified in dataset_gen.py). We used the script dataset_gen.py to transform the source data into the desired CSV for the integrated dataset. This script parses the original data into row objects with only the desired columns, which we then print based on the condition that the "datestop" begins with "5"
(indicating that the incident occurred in May). You can generate the integrated dataset using our script as follows:

        python dataset_gen.py dataset_source.csv > INTEGRATED-DATASET.csv

d) Usage:

        $ ant
        $ java -cp "AprioriFinder.jar:lib/*" AprioriFinder <integrated dataset csv> <min_sup> <min_conf>

e)Internal design of the project
  We have followed the original implementation of Apriori Algorithm for generating association rules as given in Section 2.1 of the Agrawal and Srikant paper in VLDB 1994.

The program starts off by checking that min_support and min_confidence provided is a decimal value lying between 0-1 as mentioned.
The calculateApriori function is mainly where the basic Apriori Algorithm is implemented.
Every line from the INTEGRATED-DATASET file is read and using the hash map individual items and their counts are calculated. Large Itemsets of size 1 are then generated using the min_support condition and added to the frequentItemSetList.  
 

f) java -cp "AprioriFinder.jar:lib/*" AprioriFinder INTEGRATED-DATASET.csv
