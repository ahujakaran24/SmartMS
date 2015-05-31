package personal.smartms.Entity;

import java.util.Date;

/**
 * Created by karan on 31/5/15.
 */
public class Convo {

    String number,  body,  date,  inorout;

    public Convo(String number, String body, String date, String inorout)
    {
        this.number = number;
        this.body = body;
        this.date = date;
        this.inorout = inorout;

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
