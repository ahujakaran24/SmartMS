package personal.smartms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import personal.smartms.Utils.Constants;

/**
 * Created by karan on 2/6/15.
 */
public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    @SuppressLint("NewApi")
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                ContentResolver contentResolver = context.getContentResolver();
                SmsMessage currentMessage=null;

                for (int i = 0; i < pdusObj.length; i++) {

                    currentMessage  = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String message = currentMessage.getDisplayMessageBody();

                   // Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message);

                    /*
                    * SmartMS+ turns shortforms like omg to Oh My God over here*/


                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,"senderNum: "+ phoneNumber + ", message: " + message, duration);
                    toast.show();

                } // end for loop

                final String myPackageName = context.getPackageName();
                if (Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

                    if(currentMessage!=null)
                        /*If its a default app*/
                    putSmsToDatabase(contentResolver, currentMessage);

                }
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

    private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms ) {

        // Create SMS row
        ContentValues values = new ContentValues();
        values.put(Constants.ADDRESS, sms.getOriginatingAddress() );
        values.put( Constants.DATE, sms.getTimestampMillis() );
        values.put( Constants.READ, Constants.MESSAGE_IS_NOT_READ );
        values.put( Constants.STATUS, sms.getStatus() );
        values.put( Constants.TYPE, Constants.MESSAGE_TYPE_INBOX );
        values.put( Constants.SEEN, Constants.MESSAGE_IS_NOT_SEEN );

        try {
            values.put( Constants.BODY, sms.getMessageBody() ); // May need sms.getMessageBody.toString()
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        // Push row into the SMS table
        contentResolver.insert( Uri.parse(Constants.SMS_URI), values );
    }

}
