package personal.smartms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
    private String bodyInbox[], numberInbox[], dateInbox[], seen[];
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

        accessStorageInbox();
        //Set fragment after cursor has data
        mViewPager.setAdapter(swipeAdapter);
        }



    @Override
    public void accessStorageInbox(){
        Uri uri = Uri.parse("content://sms");
        Cursor c= getContentResolver().query(uri, null, null ,null,null);

         bodyInbox = new String[c.getCount()];
         numberInbox = new String[c.getCount()];
         dateInbox = new String[c.getCount()];
         seen  = new String[c.getCount()];

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

                         //Messages is hashmap of number Key and body value (THis is to keeep one latest copy of message)
                             messages.put(numberInbox[i], bodyInbox[i]);

                         //Temporary object of this iteration
                             tempValue = new Message(numberInbox[i],messages, dateInbox[i], seen[i]);

                         //Final HashMap Key : Number, Value : temp Object
                             inbox.put(numberInbox[i], tempValue);

                         //Final Key (Number) ArrayList
                             /*if(!numbers.contains(numberInbox[i]))
                                 numbers.add(numberInbox[i]);
*/
                         c.moveToPrevious();
                         }
             }
         c.close();

        /*Use a comparator to sort
         via Date (We want latest
          Message on top */
        SMSManageComparator bvc =  new SMSManageComparator(inbox);

        sorted_map = new TreeMap<String,Message>(bvc); //gotta <3 the Structured data

        sorted_map.putAll(inbox); //Our newmsg hero

        // inbox.clear(); //Go ahead gc

       /* Store the sorted Keys
         in an arraylist for easy
         retrieval in adapter*/

        for (String key: sorted_map.keySet()) {
            numbers.add(key);   //The sidekick
        }

        pb.setVisibility(View.GONE); //sho

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

}
