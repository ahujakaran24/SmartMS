package personal.smartms.Entity;

import java.util.*;

/**
 * Created by karan on 31/5/15.
 */
public class Message {

    String Number;
    String seen;
    String Date;
    HashMap<String,String> message;

    public Message( String Number, HashMap<String,String> message, String Date, String seen)
    {
        this.message  = message;
        this.Number = Number;
        this.Date = Date;
        this.seen = seen;
    }

    public String getMessage() {
        return message.get(Number);
    }

    public String getNumber() {
        return Number;
    }

    public Date getDate() {
        Date expiry;
        expiry = new Date(Long.parseLong(Date));
        return expiry;
    }

    public String getSeen() {
        return seen;
    }
}
