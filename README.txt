COMS E6111 ADVANCED DATABASE SYSTEMS
PROJECT 3

a)TEAM:  NICOLO PIZZOFERRATO(nvp2015)
         NATASHA S KENKRE(nsk2141)

b)The following is a list of all the files that we are submitting:
> source files :
                 AprioriFinder.java (main file)
                 comparator/Compare.java
                 domain/RuleObject.java
                 util/DoubleValidatorUtil.java
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

e)Internal design of the project
    We have followed the original implementation of Apriori Algorithm for generating association rules as given in Section 2.1 of the Agrawal and Srikant paper in VLDB 1994.

    The program starts off by checking that min_support and min_confidence provided is a decimal value lying between 0-1 as mentioned.
    The calculateApriori function is mainly where the basic Apriori Algorithm is implemented.
    Every line from the INTEGRATED-DATASET file is read and using the hash map individual items and their counts are calculated. Large Itemsets of size 1 are then generated using the min_support condition and added to the frequentItemSetList.

f) java -cp "AprioriFinder.jar:lib/*" AprioriFinder INTEGRATED-DATASET.csv .17 .6

   This dataset can be used to determine correlations between grade, ethnicity, and scores. Since the scores are scaled
   according to grade, the grades alone should not be a significant factor in the outcome of the grades; this is
   supported by the data since no rules have a left hand side of "grade=7" or "grade=8". However, ethnicity has a strong
   correlation to grades, as indicated by the output of the following rules:

   [category=Black] ==> [meanscore=<675] (Conf: 93.75%, Supp: 23.4375%)
   [category=Hispanic] ==> [meanscore=<675] (Conf: 92.1875%, Supp: 23.046875%)
   [category=Asian] ==> [meanscore=675+] (Conf: 84.375%, Supp: 21.09375%)
   [category=White] ==> [meanscore=675+] (Conf: 68.75%, Supp: 17.1875%)

   As these rules show, according to the data Black and Hispanic test takers score under 675 over 90% of the time, while
   Asian and White test takers score above 675 an overwhelming majority of the time.

   The full result of this run can be seen in "example-run.txt".
