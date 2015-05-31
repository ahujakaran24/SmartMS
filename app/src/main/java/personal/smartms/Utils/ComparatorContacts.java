package personal.smartms.Utils;

import java.util.Comparator;

import personal.smartms.Entity.Contact;

/**
 * Created by karan on 31/5/15.
 */
public class ComparatorContacts implements Comparator<Contact> {

    public int compare(Contact object1, Contact object2) {
        return object1.getName().compareTo(object2.getName());
    }
}
