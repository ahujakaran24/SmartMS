package personal.smartms.Entity;

import java.util.*;

/**
 * Created by karan on 31/5/15.
 */
public class Message {

    String Number;
    String seen;
    String Date;
    String ThreadId;
    String message;

    public Message( String Number, String message, String Date, String seen, String ThreadId)
    {
        this.message  = message;
        this.Number = Number;
        this.Date = Date;
        this.seen = seen;
        this.ThreadId = ThreadId;
    }

    //Temp set seen when marking read in datbase. Dont wanna load the whole content again
    public void setSeen()
    {
        seen ="1";
    }

    public String getMessage() {
        return message;
    }

    public String getThreadId() {
        return ThreadId;
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
