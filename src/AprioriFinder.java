import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Sets;
import comparator.Compare;
import domain.DoubleValidatorUtil;
import domain.RuleObject;

public class AprioriFinder {

    public static final String OUTPUT = "output.single_transactiont";

    private List<List<String>> transactionList;
    private List<RuleObject> ruleList;

    private final File inputFile;
    private final double minSupport;
    private final double minConfidence;

    public AprioriFinder(File inputFile, double minSupport, double minConfidence) {
        this.inputFile = inputFile;
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
        this.transactionList = new ArrayList<List<String>>();
        this.ruleList = new ArrayList<RuleObject>();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java AprioriFinder Integrated-Dataset.csv <min_sup> <min_conf>");
            System.exit(1);
        }
        
        double minSupport = -1;
        if (DoubleValidatorUtil.isStringParsableToDouble(args[1])) {
            double value = Double.parseDouble(args[1]);
            if (value >= 0 && value <= 1) {
                minSupport = value;
            }
        }
        if (minSupport == -1) {
            System.out.println("minimum support must be a decimal between 0 and 1, inclusive" + args[0]);
            System.exit(1);
        }
        
        double minConfidence = -1;
        if (DoubleValidatorUtil.isStringParsableToDouble(args[2])) {
            double value = Double.parseDouble(args[2]);
            if (value >= 0 && value <= 1) {
                minConfidence = value;
            }
        }
        if (minConfidence == -1) {
            System.out.println("minimum confidence must be a decimal between 0 and 1, inclusive" + args[0]);
            System.exit(1);
        }
        
        AprioriFinder obj = new AprioriFinder(new File(args[0]), Double.valueOf(args[1]), Double.valueOf(args[2]));
        obj.calculateApriori();
    }

    public void calculateApriori() {
        List<Set<Set<String>>> frequentItemSetList = new ArrayList<Set<Set<String>>>();

        //Getting individual Items and their counts
        Map<String, Integer> i_supp_Map = new HashMap<String, Integer>();
        Map<Set<String>, Integer> freqItem_supp_Map = new HashMap<Set<String>, Integer>();
        Compare bvc = new Compare(freqItem_supp_Map);
        TreeMap<Set<String>, Integer> sortedMap = new TreeMap<Set<String>, Integer>(bvc);
        try {
            BufferedReader stdin = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = stdin.readLine()) != null) {
                String[] items = line.split(",");
                List<String> single_transaction = new ArrayList<String>();
                for (String num : items) {
                    num = num.trim();
                    if (i_supp_Map.containsKey(num)) {
                        int increaseCount = (i_supp_Map.get(i)) + 1;
                        i_supp_Map.remove(num);
                        i_supp_Map.put(num, increaseCount);
                    } else {
                        i_supp_Map.put(num, 1);
                    }
                    single_transaction.add(num);
                }
                transactionList.add(single_transaction);
            }
            stdin.close();

            //Putting in all the level 0 L item sets in the frequentItemSetList
            Iterator<Entry<String, Integer>> iterator = i_supp_Map.entrySet().iterator();
            Set<String> temp = new HashSet<String>();
            while (iterator.hasNext()) {
                Entry<String, Integer> entry = iterator.next();
                String key = entry.getKey();
                Integer value = entry.getValue();
                if ((double) (value) / transactionList.size() >= minSupport) {
                    temp.add(key);
                }
            }
            Set<Set<String>> formStringSet = new HashSet<Set<String>>();
            formStringSet.add(temp);
            frequentItemSetList.add(formStringSet);

            int k = 1;
            while (frequentItemSetList.get(k - 1).size() != 0) {
                Set<Set<String>> addTofrequentItemSetList = new HashSet<Set<String>>(); //APRIORI-GEN---new candidates

                Set<String> entries = Sets.newHashSet();
                for (Set<String> s : frequentItemSetList.get(k - 1)) {
                    for (String string : s) {
                        entries.add(string);
                    }
                }

                Set<Set<String>> preCandidateItemsetList = Sets.powerSet(entries);

                //removing(pruning) candidates that are not present in the level before
                Set<Set<String>> candidateItemsetList = prunecandidateItemsetList(preCandidateItemsetList, k);
                candidateItemsetList = eliminatePreviIS(frequentItemSetList, candidateItemsetList, k);

                //candidates present in the transactions
                for (Set<String> ck : candidateItemsetList) {
                    int count = 0;
                    for (List<String> transaction : transactionList) {
                        if (transaction.containsAll(ck))
                            count++;
                    }
                    if ((double) count / transactionList.size() > minSupport) {
                        addTofrequentItemSetList.add(ck);
                    }
                }
                k++;
                frequentItemSetList.add(addTofrequentItemSetList);
            }

            frequentItemSetList.remove(frequentItemSetList.size() - 1);
            Set<Set<String>> tempSet = frequentItemSetList.get(0);
            Iterator<Set<String>> tempIter = tempSet.iterator();
            Set<String> tempTempSet = tempIter.next();
            for (String enterStringInSet : tempTempSet) {
                Set<String> addEntry = new HashSet<String>();
                addEntry.add(enterStringInSet);
                Integer addEntryCount = -1;

                for (Entry<String, Integer> entry : i_supp_Map.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    if (key.equals(enterStringInSet)) {
                        addEntryCount = value;
                    }
                }
                freqItem_supp_Map.put(addEntry, addEntryCount);
            }

            //populate freqItem_supp_Map with k > 1
            findFreqItemset_supp_Map(frequentItemSetList, freqItem_supp_Map);
            //getting confidences :-
            //checking for all possible rules in tmpListOfRules
            List<RuleObject> tmpListOfRules = findPossibleRules(freqItem_supp_Map);

            //Elimination of rules that have rightHside in leftHside and having conf < minConf
            eliminateConfRules(tmpListOfRules);
            sortedMap.putAll(freqItem_supp_Map);

            Comparator<RuleObject> comparator = new Comparator<RuleObject>() {
                public int compare(RuleObject o1, RuleObject o2) {
                    if (o1.findConfidence() <= o2.findConfidence())
                        return 1;
                    return -1;
                }
            };

            Collections.sort(ruleList, comparator);
            printOutput(sortedMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminateConfRules(List<RuleObject> tmpListOfRules) {
        for (int i = 0; i < tmpListOfRules.size(); i++) {
            RuleObject currentRule = tmpListOfRules.get(i);
            if (currentRule.getLhs().containsAll(currentRule.getRhs())) {
                tmpListOfRules.remove(currentRule);
                continue;
            }
            if (currentRule.findConfidence() < minConfidence) {
                tmpListOfRules.remove(currentRule);
                continue;
            }
            ruleList.add(currentRule);
        }
    }

    private Set<Set<String>> eliminatePreviIS(List<Set<Set<String>>> frequentItemSetList, Set<Set<String>> candidateItemsetList, int k) {
        Set<Set<String>> prunedSet = Sets.newHashSet(candidateItemsetList);
        for (Set<String> ck : candidateItemsetList) {
            if (!frequentItemSetList.get(k - 1).containsAll(ck)) {
                prunedSet.remove(ck);
            }
        }
        return prunedSet;
    }

    private Set<Set<String>> prunecandidateItemsetList(Set<Set<String>> preCandidateItemsetList, int k) {
        Set<Set<String>> candidateItemsetList = Sets.newHashSet();
        for (Set<String> preCk : preCandidateItemsetList) {
            if (preCk.size() == k + 1) {
                candidateItemsetList.add(preCk);
            }
        }
        return candidateItemsetList;
    }

    private List<RuleObject> findPossibleRules(Map<Set<String>, Integer> freqItem_supp_Map) {
        List<RuleObject> tmpListOfRules = new ArrayList<RuleObject>();
        for (Entry<Set<String>, Integer> entry : freqItem_supp_Map.entrySet()) {
            Set<String> key = entry.getKey();
            Integer value = entry.getValue();
            for (Entry<Set<String>, Integer> innerEntry : freqItem_supp_Map.entrySet()) {
                Set<String> innerKey = innerEntry.getKey();
                Integer innerValue = innerEntry.getValue();

                if (union(key, innerKey).size() != 0 && innerKey.size() == 1) {
                    RuleObject rule = new RuleObject(transactionList);
                    rule.setLhs(key);
                    rule.setRhs(innerKey);
                    rule.setLhsCount(value);
                    rule.setRhsCount(innerValue);
                    tmpListOfRules.add(rule);
                }
            }

        }
        return tmpListOfRules;
    }

    public void findFreqItemset_supp_Map(
            List<Set<Set<String>>> frequentItemSetList,
            Map<Set<String>, Integer> freqItem_supp_Map
    ) {
        for (int i = 1; i < frequentItemSetList.size(); i++) {
            Set<Set<String>> setSetString = frequentItemSetList.get(i);
            for (Set<String> keySet : setSetString) {
                Integer supportValue = calculateSupport(keySet);
                freqItem_supp_Map.put(keySet, supportValue);
            }
        }
    }

    public void printOutput(TreeMap<Set<String>, Integer> sortedMap) throws IOException {
        File outputFile = new File(OUTPUT);
        BufferedWriter buff_write = new BufferedWriter(new FileWriter(outputFile));
        printToConsole(String.format("==Frequent itemsets (min_sup=%f%%)\n", minSupport * 100), buff_write);
        for (Entry<Set<String>, Integer> entry : sortedMap.entrySet()) {
            Set<String> key = entry.getKey();
            Integer value = entry.getValue();
            double support = (value / (double) transactionList.size()) * 100.0;
            printToConsole(Arrays.toString(key.toArray()) + ", " + support + "%\n", buff_write);
        }
        printToConsole(String.format("\nHigh-confidence association rules (min_conf=%f%%)\n", minConfidence * 100), buff_write);
        for (RuleObject rule : ruleList) {
            if (rule.findSupport() >= minSupport) {
                printToConsole(rule + "\n", buff_write);
            }
        }
        buff_write.close();
    }

    private void printToConsole(String message, BufferedWriter buff_write) throws IOException {
        buff_write.write(message);
        System.out.print(message);
    }

    public <T> Set<T> union(Set<T> setX, Set<T> setY) {
        Set<T> tmp = new HashSet<T>(setX);
        tmp.addAll(setY);
        return tmp;
    }

    private int calculateSupport(Set<String> set) {
        int count = 0;
        for (List<String> transaction : transactionList) {
            if (transaction.containsAll(set)) {
                count++;
            }
        }
        return count;
    }
}
