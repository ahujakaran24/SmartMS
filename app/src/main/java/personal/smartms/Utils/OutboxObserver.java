package personal.smartms.Utils;

import android.database.ContentObserver;
import android.os.Handler;

import personal.smartms.Conversation;

/**
 * Created by karan on 31/5/15.
 */

/* Look for
* any newmsg messages sent
* so can update the view
* accordingly*/

public class OutboxObserver extends ContentObserver {


    Conversation c;

    public OutboxObserver(Handler handler, Conversation c) {
        super(handler);
        this.c = c;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        c.getSMS();
        // save the message to the SD card here
    }

}