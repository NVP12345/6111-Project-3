package comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class Compare implements Comparator<Set<String>> {
    Map<Set<String>, Integer> base;

    public Compare(Map<Set<String>, Integer> base) {
        this.base = base;
    }

    public int compare(Set<String> a, Set<String> b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
