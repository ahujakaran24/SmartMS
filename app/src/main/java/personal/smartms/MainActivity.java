package personal.smartms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import personal.smartms.Adapter.SwipeAdapter;
import personal.smartms.Entity.Message;
import personal.smartms.Entity.MyViewPager;
import personal.smartms.Interfaces.ActivityFragmentInterface;
import personal.smartms.Utils.SMSManageComparator;


public class MainActivity extends ActionBarActivity implements ActivityFragmentInterface {

    private SwipeAdapter swipeAdapter;
    public static MyViewPager mViewPager;
    private String bodyInbox[], numberInbox[], dateInbox[], seen[], thread_id[];
     private ProgressBar pb;
    private HashMap<String,String> messages= new HashMap<String,String> ();
    private HashMap<String,Message> inbox = new HashMap<String,Message>();
    private ArrayList<String> numbers = new ArrayList<String>();
    TreeMap<String,Message> sorted_map;
    Integer count = 0;
    private Message tempValue = null;
    public static Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        startFetchTask();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
        mViewPager = (MyViewPager) findViewById(R.id.pager);
        pb = (ProgressBar)findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /*if swiped then change*/
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        /* if page is clicked*/
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });

            //Add tabs
        for (int i = 0; i < 2; i++) {
            if(i==0)
            getSupportActionBar().addTab(getSupportActionBar().newTab().setText("Messages").setTabListener(tabListener));
            else
                getSupportActionBar().addTab(getSupportActionBar().newTab().setText("Contacts").setTabListener(tabListener));
        }

        /*******Finished layout*/

        /******Start SMS storage access*/
        }




    //Called from Activity or fragment
    @Override
    public void startFetchTask(){
        new FetchInbox().execute();
    }


    //Called in worker thread from asynctask
    public void accessStorageInbox(){
        Uri uri = Uri.parse("content://sms");
        Cursor c= getContentResolver().query(uri, null, null ,null,null);

         bodyInbox = new String[c.getCount()];
         numberInbox = new String[c.getCount()];
         dateInbox = new String[c.getCount()];
         seen  = new String[c.getCount()];
        thread_id = new String[c.getCount()];

        /*Reverse parsing since latest sms is in the top of the table
        * and using HashMap with unique key as number
        * hence latest sms will be the latest
        * overriden value!!*/
         if(c.moveToLast()){
                     for(int i=0;i<c.getCount();i++){
                         bodyInbox[i]= c.getString(c.getColumnIndexOrThrow("body")).toString();
                         numberInbox[i]=c.getString(c.getColumnIndexOrThrow("address")).toString();
                         dateInbox[i]=c.getString(c.getColumnIndexOrThrow("date")).toString();
                         seen[i] = c.getString(c.getColumnIndexOrThrow("read")).toString();
                         thread_id[i] = c.getString(c.getColumnIndexOrThrow("thread_id")).toString();

                         //Temporary object of this iteration
                             tempValue = new Message(numberInbox[i],bodyInbox[i], dateInbox[i], seen[i], thread_id[i]);

                        /* to avoid duplicate entries :
                         +91000000(inbox) will be different
                         from 00000(outbox) in key.

                         If this app is used in a country where country code is more than 4 letters, and numbers
                         get mixed up like +9123 - 55555 and 55555 then user might see
                         dual entries.

                         A risk taken for greater good ( converting  such numbers to contact list is expensive
                         and can't gaurntee number will be in contacts!
                         */
                         if(numberInbox[i].length()>=10) {
                             numberInbox[i] = numberInbox[i].substring(numberInbox[i].length()-4);
                         }else{
                             //Keep going
                         }
                         //Final HashMap Key : Number, Value : temp Object
                             inbox.put(numberInbox[i], tempValue);

                         c.moveToPrevious();
                         }
             }
         c.close();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    public TreeMap<String,Message> getInbox() {
        return sorted_map;
    }
    @Override
    public ArrayList<String> getNumbers() {
        return numbers;
    }

    // Async Task Class
    class FetchInbox extends AsyncTask<Void, Void, Void> {

        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {

        }

        // Download Music File from Internet
        @Override
        protected Void doInBackground(Void... no) {

            accessStorageInbox();


            return null;
        }

        @Override
        protected void onPostExecute(Void no) {

            Log.d("Unsorted list", inbox.toString());

        /*Use a comparator to sort
         via Date (We want latest
          Message on top */
            SMSManageComparator bvc =  new SMSManageComparator(inbox);

            sorted_map = new TreeMap<String,Message>(bvc); //gotta <3 the Structured data

            sorted_map.putAll(inbox); //Our newmsg hero

            Log.d("sorted list", sorted_map.toString());

            // inbox.clear(); //Go ahead gc

       /* Store the sorted Keys
         in an arraylist for easy
         retrieval in adapter*/

            int j =0;

            for (String key: sorted_map.keySet()) {
                numbers.add(j,key);   //The sidekick
                j++;
            }
            pb.setVisibility(View.GONE); //sho


            if(mViewPager!=null&&swipeAdapter!=null)
            //Set fragment after cursor has data
            mViewPager.setAdapter(swipeAdapter);



        }
    }

}
