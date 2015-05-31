package personal.smartms.Interfaces;

import java.util.ArrayList;
import java.util.TreeMap;

import personal.smartms.Entity.Message;

/**
 * Created by karan on 30/5/15.
 */
public interface ActivityFragmentInterface {

    public TreeMap<String,Message> getInbox();
    public ArrayList<String> getNumbers();
    public void accessStorageInbox();


}
