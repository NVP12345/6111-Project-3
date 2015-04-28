package pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class RuleObject {
    Set<String> leftSideItemSet;
    Set<String> rightSideItemSet;

    public double conff;
    double supp;

    int lhs_value;
    int rhs_value;

    List<List<String>> transactionList = new ArrayList<List<String>>();

    public RuleObject(List<List<String>> tl) {
        transactionList = tl;
        leftSideItemSet = new HashSet<String>();
        rightSideItemSet = new HashSet<String>();
    }

    private int getSupportCount(Set<String> set) {
        int count = 0;
        for (int i = 0; i < transactionList.size(); i++) {
            if (transactionList.get(i).containsAll(set)) {
                count++;
            }
        }
        return count;
    }

    public void setLhs(Set<String> lhs) {
        this.leftSideItemSet = lhs;
    }

    public Set<String> getLhs() {
        return leftSideItemSet;
    }

    @Override
    public String toString() {
        return Arrays.toString(leftSideItemSet.toArray()) + " ==> " + Arrays.toString(rightSideItemSet.toArray()) + " (Conf: " + conff * 100 + "%, Supp: " + findSupport() * 100 + "%)";
    }

    public int getLhsCount() {
        return lhs_value;
    }

    public Set<String> getRhs() {
        return rightSideItemSet;
    }

    public int getRhsCount() {
        return rhs_value;
    }

    public void setLhsCount(int lhsSupport) {
        this.lhs_value = lhsSupport;
    }

    public void setRhs(Set<String> rhs) {
        this.rightSideItemSet = rhs;
    }

    public void setRhsCount(int rhsSupport) {
        this.rhs_value = rhsSupport;
    }


    public double findConfidence() {
        conff = getSupportCount(Sets.union(leftSideItemSet, rightSideItemSet));
        supp = (int) conff;
        conff /= getSupportCount(leftSideItemSet);
        return conff;
    }

    public double findSupport() {
        return getSupportCount(Sets.union(leftSideItemSet, rightSideItemSet)) / (double) transactionList.size();
    }

}
