package personal.smartms;

import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import personal.smartms.Adapter.ConverseAdapter;
import personal.smartms.Entity.Convo;
import personal.smartms.Utils.CustomComparator;
import personal.smartms.Utils.OutboxObserver;


public class Conversation extends ActionBarActivity {


    private RecyclerView rv;
    public static boolean haveSent = false;
    private  String number;
    private SmsManager smsManager;
    private ArrayList<Convo> text;
    private Convo tempValue;
    private TextView tv;
    private EditText editText;
    private ImageView send;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        pb=(ProgressBar)findViewById(R.id.pb);
        pb.getIndeterminateDrawable().setColorFilter(0xFFcc0000,android.graphics.PorterDuff.Mode.MULTIPLY);
        send = (ImageView)findViewById(R.id.send);
        editText = (EditText)findViewById(R.id.editText);
        text = new ArrayList<Convo>();
        smsManager = SmsManager.getDefault();
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            number = extras.getString("number");
        }

       // ContentResolver contentResolver = this.getContentResolver();
        getSMS();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().equals("")) {
                    smsManager.sendTextMessage(number, null, editText.getText().toString(), null, null);
                    pb.setVisibility(View.VISIBLE);
                    editText.setText("");
                    haveSent = true;

                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    getSMS();
                                }
                            },2000
                    );
                }
                else
                    Toast.makeText(getApplicationContext(),"Can't send empty text",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getSMS()
    {
        if(pb.isShown())
        pb.setVisibility(View.GONE);
        String[] selectionArgs = {number};
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Uri uriSMSURI2 = Uri.parse("content://sms/sent");

       // Cursor cursor = getContentResolver().query(uriSMSURI, null, "address = ?", selectionArgs, "date desc");
        CursorLoader cursorLoader = new CursorLoader(this,uriSMSURI, null,"address = ?",selectionArgs, null);
        CursorLoader cursorLoader2 = new CursorLoader(this,uriSMSURI2, null,"address = ?",selectionArgs, null);

        Cursor cursor = cursorLoader.loadInBackground();
        Cursor cursor2 = cursorLoader2.loadInBackground();

        getContentResolver().registerContentObserver(Uri.parse("content://sms/out"), true, (new OutboxObserver(new Handler(), Conversation.this)));

        Log.d("Query", getContentResolver().query(uriSMSURI, null, "address = ?", selectionArgs, "date desc").toString());


        /*Query inbox*/
        try{
          /* if(!cursor.isFirst())
               cursor.moveToFirst();*/

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                //to fetch the contact name of the conversation
                String contactName = address;
                Uri Nameuri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
                Cursor cs = getContentResolver().query(Nameuri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, ContactsContract.PhoneLookup.NUMBER + "='" + address + "'", null, null);

                if (cs.getCount() > 0) {
                    cs.moveToFirst();
                    contactName = cs.getString(cs.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }

                tempValue = new Convo(contactName, body, date, "in");
                text.add(tempValue);
            }


    } catch(Exception ex) {
        // Log the exception's message or whatever you like
    } finally {
        try {
            if( cursor != null && !cursor.isClosed() )
                cursor.close();
        } catch(Exception ex) {}
    }

        /*Query Sent sms*/

        try{
          /* if(!cursor.isFirst())
               cursor.moveToFirst();*/
            cursor2.moveToPosition(-1);
            while (cursor2.moveToNext()) {
              //  String address = cursor2.getString(cursor2.getColumnIndex("address"));
                String body = cursor2.getString(cursor2.getColumnIndexOrThrow("body"));
                String date = cursor2.getString(cursor2.getColumnIndexOrThrow("date"));

                //to fetch the contact name of the conversation

                tempValue = new Convo("Me", body, date, "out");
                text.add(tempValue);

            }
        } catch(Exception ex) {
            // Log the exception's message or whatever you like
        } finally {
            try {
                if( cursor2 != null && !cursor2.isClosed() )
                    cursor2.close();
            } catch(Exception ex) {}
        }



        /*Sort all sms based on date*/

        Collections.sort(text, new CustomComparator());

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        llm.setReverseLayout(true);
        rv.setLayoutManager(llm);
        ConverseAdapter adapter = new ConverseAdapter(text,this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onStop()
    {
        text.clear();
        super.onStop();
    }

    @Override
    public void onResume()
    {
        text = new ArrayList<Convo>();
        getSMS();
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
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
}
