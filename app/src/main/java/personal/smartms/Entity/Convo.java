package personal.smartms.Entity;

import java.util.Date;

/**
 * Created by karan on 31/5/15.
 */
public class Convo {

    String number,  body,  date,  inorout, id;

    public Convo(String number, String body, String date, String inorout, String id)
    {
        this.number = number;
        this.body = body;
        this.date = date;
        this.inorout = inorout;
        this.id=id;

    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        Date expiry;
        expiry = new Date(Long.parseLong(date));

        return  expiry;
    }

    public String getInorout() {
        return inorout;
    }
}
