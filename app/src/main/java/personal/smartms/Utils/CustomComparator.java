package personal.smartms.Utils;

import java.util.Comparator;

import personal.smartms.Entity.Convo;

/**
 * Created by karan on 31/5/15.
 */
public class CustomComparator implements Comparator<Convo> {
    public int compare(Convo object1, Convo object2) {
        return object1.getDate().compareTo(object2.getDate());
    }
}
