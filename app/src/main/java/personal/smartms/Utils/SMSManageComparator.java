package personal.smartms.Utils;

import java.util.Comparator;
import java.util.Map;

import personal.smartms.Entity.Message;

/**
 * Created by karan on 31/5/15.
 */
public class SMSManageComparator implements Comparator<String> {

    Map<String, Message> base;

    public SMSManageComparator(Map<String, Message> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.
    public int compare(String a, String b) {
        return base.get(a).getDate().compareTo(base.get(b).getDate());
    }
}
