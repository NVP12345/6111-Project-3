COMS E6111 ADVANCED DATABASE SYSTEMS
PROJECT 3

a)TEAM:  NICOLO PIZZOFERRATO(nvp2015)
         NATASHA S KENKRE(nsk2141)

b)The following is a list of all the files that we are submitting:
> source files :
                 AprioriFinder.java (main file)
                 domain/RuleObject.java
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


c) We used the NYS math test results by year, grade, district, and ethnicity as the source. To keep the size of the
   data reasonable, we limited the data to a single year (2011) and grades (7 and 8) and selected only somewhat interesting
   fields to try and find some meaningful correlation (the 9 columns specified in dataset_gen.py). We used the script
   dataset_gen.py to transform the source data into the desired CSV for the integrated dataset. This script parses the
   original data into row objects with only the desired columns, which we then print based on the condition that the
   year is 2011 and the grade is 7 or 8. You can generate the integrated dataset using our script as follows:

        python dataset_gen.py dataset_source.csv > INTEGRATED-DATASET.csv

d) Usage:

        $ ant
        $ java -cp "AprioriFinder.jar:lib/*" AprioriFinder <integrated dataset csv> <min_sup> <min_conf>

e)

f) java -cp "AprioriFinder.jar:lib/*" AprioriFinder INTEGRATED-DATASET.csv
