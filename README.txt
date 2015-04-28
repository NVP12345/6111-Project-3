COMS E6111 ADVANCED DATABASE SYSTEMS
PROJECT 3

a)TEAM:  NICOLO PIZZOFERRATO(nvp2015)
         NATASHA S KENKRE(nsk2141)

b)The following is a list of all the files that we are submitting:
> source files :
                 Project3Main.java (main file)
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


c) We used the Stop & Frisk data from the NYPD for all five boroughs from 2014 as the source. To keep the size of the
   data reasonable, we limited the data to a single month (May) and selected only somewhat interesting fields to
   try and find some meaningful correlation (the 19 columns specified in dataset_gen.py). We used the script dataset_gen.py
   to transform the source data into the desired CSV for the integrated dataset. This script parses the original data into
   row objects with only the desired columns, which we then print based on the condition that the "datestop" begins with "5"
   (indicating that the incident occurred in May). You can generate the integrated dataset using our script as follows:

        python dataset_gen.py dataset_source.csv > INTEGRATED-DATASET.csv

d) Usage:

        $ ant
        $ java -cp "AprioriFinder.jar:lib/*" AprioriFinder <integrated dataset csv> <min_sup> <min_conf>

e)

f) java -cp "AprioriFinder.jar:lib/*" AprioriFinder INTEGRATED-DATASET.csv
