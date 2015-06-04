package personal.smartms;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import personal.smartms.Utils.Constants;


public class PopUpActivity extends ActionBarActivity {


    SmsManager smsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_incoming_sms);

        TextView number = (TextView) findViewById(R.id.number);
        TextView body = (TextView) findViewById(R.id.body);
        final EditText response = (EditText) findViewById(R.id.reply);
        ImageView send = (ImageView) findViewById(R.id.send);

        Bundle bundle = this.getIntent().getBundleExtra("myBundle");



        final String phoneNumber =  bundle.getString("number");


        body.setText(bundle.getString("message"));
        number.setText(Constants.getContact(getApplicationContext(), phoneNumber));
        send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                if (response.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Response is empty", Toast.LENGTH_LONG).show();
                } else {
                    smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, response.getText().toString(), null, null);

                    //Add outgoing sms to db
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(myPackageName))
                    {
                        ContentResolver contentResolver = getApplicationContext().getContentResolver();
                        putSmsToDatabase(contentResolver,phoneNumber,response.getText().toString());


                    }

                    finish();


                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pop_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void putSmsToDatabase( ContentResolver contentResolver, String number, String message) {

        // Create SMS row
        ContentValues values = new ContentValues();
        values.put(Constants.ADDRESS, number );
        values.put( Constants.DATE, System.currentTimeMillis() );
        values.put( Constants.TYPE, Constants.MESSAGE_TYPE_SENT );
        values.put( Constants.BODY, message ); // May need sms.getMessageBody.toString()
        // Push row into the SMS table
        contentResolver.insert(Uri.parse("content://sms/sent"), values );
    }
}
