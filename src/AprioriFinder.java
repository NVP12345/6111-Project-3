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
import domain.RuleObject;

public class AprioriFinder {

    public static final String OUTPUT = "output.txt";

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
            System.out.println("ERROR - give 3 args");
            System.exit(1);
        }
        AprioriFinder obj = new AprioriFinder(new File(args[0]), Double.valueOf(args[1]), Double.valueOf(args[2]));
        obj.calculateApriori();
    }

    public void calculateApriori() {
        List<Set<Set<String>>> largeItemSetList = new ArrayList<Set<Set<String>>>();

        //Getting individual Items and their counts
        Map<String, Integer> i_supp_Map = new HashMap<String, Integer>();
        Map<Set<String>, Integer> largeItemSupportMap = new HashMap<Set<String>, Integer>();
        Compare bvc = new Compare(largeItemSupportMap);
        TreeMap<Set<String>, Integer> sortedMap = new TreeMap<Set<String>, Integer>(bvc);
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                List<String> tx = new ArrayList<String>();
                for (String i : items) {
                    i = i.trim();
                    if (i_supp_Map.containsKey(i)) {
                        int newCount = (i_supp_Map.get(i)) + 1;
                        i_supp_Map.remove(i);
                        i_supp_Map.put(i, newCount);
                    } else {
                        i_supp_Map.put(i, 1);
                    }
                    tx.add(i);
                }
                transactionList.add(tx);
            }
            br.close();

            //Enter all level 0 large item sets in largeItemSetList
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
            Set<Set<String>> setString = new HashSet<Set<String>>();
            setString.add(temp);
            largeItemSetList.add(setString);

            int k = 1;
            while (largeItemSetList.get(k - 1).size() != 0) {
                Set<Set<String>> addToLargeItemSetList = new HashSet<Set<String>>(); //APRIORI-GEN---new candidates

                Set<String> entries = Sets.newHashSet();
                for (Set<String> s : largeItemSetList.get(k - 1)) {
                    for (String string : s) {
                        entries.add(string);
                    }
                }

                Set<Set<String>> preCkList = Sets.powerSet(entries);

                //pruning candidates not included in previous level
                Set<Set<String>> ckList = pruneCklist(preCkList, k);
                ckList = eliminatePreviIS(largeItemSetList, ckList, k);

                //candidates contained in transactions
                for (Set<String> ck : ckList) {
                    int count = 0;
                    for (List<String> transaction : transactionList) {
                        if (transaction.containsAll(ck))
                            count++;
                    }
                    if ((double) count / transactionList.size() > minSupport) {
                        addToLargeItemSetList.add(ck);
                    }
                }
                k++;
                largeItemSetList.add(addToLargeItemSetList);
            }

            largeItemSetList.remove(largeItemSetList.size() - 1);
            Set<Set<String>> tempSet = largeItemSetList.get(0);
            Iterator<Set<String>> tempIter = tempSet.iterator();
            Set<String> tempTempSet = tempIter.next();
            for (String enterStringInSet : tempTempSet) {
                Set<String> newEntry = new HashSet<String>();
                newEntry.add(enterStringInSet);
                Integer newEntryCount = -1;

                for (Entry<String, Integer> entry : i_supp_Map.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    if (key.equals(enterStringInSet)) {
                        newEntryCount = value;
                    }
                }
                largeItemSupportMap.put(newEntry, newEntryCount);
            }

            //populate largeItemSupportMap with k > 1
            getlargeItemSet_SupportMap(largeItemSetList, largeItemSupportMap);
            //generating confidences:
            //get all rule possibilities in tempRuleList
            List<RuleObject> tempRuleList = getRulePossibilities(largeItemSupportMap);

            //Removing rules that have rHS in LHS and has conf. < minConf.
            removeLessConfSuppRules(tempRuleList);
            sortedMap.putAll(largeItemSupportMap);

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

    private void removeLessConfSuppRules(List<RuleObject> tempRuleList) {
        for (int i = 0; i < tempRuleList.size(); i++) {
            RuleObject currentRule = tempRuleList.get(i);
            if (currentRule.getLhs().containsAll(currentRule.getRhs())) {
                tempRuleList.remove(currentRule);
                continue;
            }
            if (currentRule.findConfidence() < minConfidence) {
                tempRuleList.remove(currentRule);
                continue;
            }
            ruleList.add(currentRule);
        }
    }

    private Set<Set<String>> eliminatePreviIS(List<Set<Set<String>>> largeItemSetList, Set<Set<String>> ckList, int k) {
        Set<Set<String>> prunedSet = Sets.newHashSet(ckList);
        for (Set<String> ck : ckList) {
            //ELIMINATING ONES OCCURRING IN PREVIOUS LARGE ITEM SET LIST (k-1)
            if (!largeItemSetList.get(k - 1).containsAll(ck)) {
                prunedSet.remove(ck);
            }
        }
        return prunedSet;
    }

    private Set<Set<String>> pruneCklist(Set<Set<String>> preCkList, int k) {
        Set<Set<String>> ckList = Sets.newHashSet();
        for (Set<String> preCk : preCkList) {
            if (preCk.size() == k + 1) {
                ckList.add(preCk);
            }
        }
        return ckList;
    }

    private List<RuleObject> getRulePossibilities(Map<Set<String>, Integer> largeItemSupportMap) {
        List<RuleObject> tempRuleList = new ArrayList<RuleObject>();
        for (Entry<Set<String>, Integer> entry : largeItemSupportMap.entrySet()) {
            Set<String> key = entry.getKey();
            Integer value = entry.getValue();
            for (Entry<Set<String>, Integer> innerEntry : largeItemSupportMap.entrySet()) {
                Set<String> innerKey = innerEntry.getKey();
                Integer innerValue = innerEntry.getValue();

                if (union(key, innerKey).size() != 0 && innerKey.size() == 1) {
                    RuleObject rule = new RuleObject(transactionList);
                    rule.setLhs(key);
                    rule.setRhs(innerKey);
                    rule.setLhsCount(value);
                    rule.setRhsCount(innerValue);
                    tempRuleList.add(rule);
                }
            }

        }
        return tempRuleList;
    }

    public void getlargeItemSet_SupportMap(
            List<Set<Set<String>>> largeItemSetList,
            Map<Set<String>, Integer> largeItemSupportMap
    ) {
        for (int i = 1; i < largeItemSetList.size(); i++) {
            Set<Set<String>> setSetString = largeItemSetList.get(i);
            for (Set<String> keySet : setSetString) {
                Integer supportValue = getSupportCount(keySet);
                largeItemSupportMap.put(keySet, supportValue);
            }
        }
    }

    public void printOutput(TreeMap<Set<String>, Integer> sortedMap) throws IOException {
        File outputFile = new File(OUTPUT);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        printAndWriteMessage(String.format("==Frequent itemsets (min_sup=%f%%)\n", minSupport * 100), bw);
        for (Entry<Set<String>, Integer> entry : sortedMap.entrySet()) {
            Set<String> key = entry.getKey();
            Integer value = entry.getValue();
            double support = (value / (double) transactionList.size()) * 100.0;
            printAndWriteMessage(Arrays.toString(key.toArray()) + ", " + support + "%\n", bw);
        }
        printAndWriteMessage(String.format("\nHigh-confidence association rules (min_conf=%f%%)\n", minConfidence * 100), bw);
        for (RuleObject rule : ruleList) {
            if (rule.findSupport() >= minSupport) {
                printAndWriteMessage(rule + "\n", bw);
            }
        }
        bw.close();
    }

    private void printAndWriteMessage(String message, BufferedWriter bw) throws IOException {
        bw.write(message);
        System.out.print(message);
    }

    public <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new HashSet<T>(setA);
        tmp.addAll(setB);
        return tmp;
    }

    private int getSupportCount(Set<String> set) {
        int count = 0;
        for (List<String> transaction : transactionList) {
            if (transaction.containsAll(set)) {
                count++;
            }
        }
        return count;
    }
}
