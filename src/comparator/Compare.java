package comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class Compare implements Comparator<Set<String>> {
    Map<Set<String>, Integer> base;

    public Compare(Map<Set<String>, Integer> base) {
        this.base = base;
    }

    public int compare(Set<String> x, Set<String> y) {
        if (base.get(x) >= base.get(y)) {
            return -1;
        } else {
            return 1;
        }
    }
}
