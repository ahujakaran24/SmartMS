package personal.smartms.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.TreeMap;

import personal.smartms.Adapter.InboxAdapter;
import personal.smartms.Conversation;
import personal.smartms.Entity.Message;
import personal.smartms.Entity.MyRecyclerView;
import personal.smartms.Interfaces.ActivityFragmentInterface;
import personal.smartms.Interfaces.UpdateSMSManager;
import personal.smartms.R;
import personal.smartms.Utils.Constants;


public class SMSManager extends Fragment implements UpdateSMSManager {

    private MyRecyclerView rv;
    private TreeMap<String,Message> messages;
    private ArrayList<String> numbers;
    private LinearLayoutManager llm;
    private BroadcastReceiver receiver;
     InboxAdapter adapter;
    private ActivityFragmentInterface mListener;


    public SMSManager() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_smsmanager, container, false);
        rv = (MyRecyclerView)view.findViewById(R.id.rv);
        update();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ActivityFragmentInterface) activity;
            messages = mListener.getInbox();
            numbers = mListener.getNumbers();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActivityFragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void update() {
        adapter = new InboxAdapter(messages,numbers,getActivity());
        llm= new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(rv,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    //No Left Swipe
                                    //Left swipe is fragment change
                                }
                            }

                            @SuppressLint("NewApi") //Default 4.4+
                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    final String myPackageName = getActivity().getPackageName();

                                   /** If App Is not default*/
                                    if (!Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(myPackageName)) {
                                            // App is not default.
                                            // Show the "not currently set as the default SMS app" interface
                                            final AlertDialog alertDialog = new AlertDialog.Builder(
                                                    getActivity()).create();
                                            alertDialog.setTitle("Make SmartMS default ap");
                                            alertDialog.setMessage("Your app is not a default Sms app. Please click ok to make SmartMs default app");
                                            alertDialog.setIcon(R.drawable.disputes);
                                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                                // Write your code here to execute after dialog closed
                                                                Intent intent =new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,myPackageName);
                                                                getActivity().startActivity(intent);
                                                                alertDialog.dismiss();
                                                }
                                            });
                                            alertDialog.show();
                                    } else {

                                    //Is the DEfault app
                                                if (!Constants.contactExists(getActivity(), messages.get(numbers.get(numbers.size()-position-1)).getNumber())) {

                                                    /*Contact
                                                    * is not in phone
                                                    * so delete it*/

                                                    deleteSMSThread(getActivity(), messages.get(numbers.get(numbers.size()-position-1)).getMessage(), messages.get(numbers.get(numbers.size()-position-1)).getNumber());
                                                    messages.remove(numbers.get(numbers.size()-position-1));
                                                    numbers.remove(numbers.size()-position-1);
                                                    update();
                                                } else {
                                                     /*Contact
                                                    * is in phone
                                                    * so mark it*/
                                                    //Mark as seen in db
                                                    markMessageRead(getActivity(), messages.get(numbers.get(numbers.size()-position-1)).getNumber(), messages.get(numbers.get(numbers.size()-position-1)).getMessage());
                                                    //Mark seen locally
                                                    messages.get(numbers.get(numbers.size()-position-1)).setSeen();
                                                    Toast.makeText(getActivity(), "Marked seen", Toast.LENGTH_LONG).show();
                                                    update();

                                                }
                                    }
                                }
                            }
                        });

        rv.addOnItemTouchListener(swipeTouchListener);
    }

    /*Listen to incoming sms to have
 * latest list*/
    @Override
    public void onResume()
    {
        super.onResume();

        if(Conversation.haveSent&&mListener!=null)
        {
            Conversation.haveSent=false;
            mListener.startFetchTask();
            mListener.getInbox();
            mListener.getNumbers();
            update();
        }

        //receive newmsg sms then..
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                    //Refresh
                    //Give native database  a second to update
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if(mListener!=null) {
                                        mListener.startFetchTask();
                                        mListener.getInbox();
                                        mListener.getNumbers();
                                        update();
                                    }
                                }
                            }, 4000
                    );
                }
            }
        };
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        try {
            getActivity().unregisterReceiver(receiver);   // V V imp to unregister :)
        }catch(Exception e) //Gotta catchem all :P
        {
            //No check if receiver isRegistered :o hence try-catch :(
        }
    }


    /*Cant delte sms from android 4.4 + if this app
    * is not default app*/




    public void deleteSMSThread(Context ctx, String message, String number) {
        try {
            Uri uriSms = Uri.parse("content://sms");
            Cursor c = ctx.getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address",
                            "person", "date", "body" }, null, null, null);

            Log.i("msg delete tag", "c count......"+c.getCount());
            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    if (message.equals(body) && address.equals(number)) {
                        int rows = ctx.getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null,null);
                        Toast.makeText(ctx,"Message thread from "+address+ " Deleted",Toast.LENGTH_LONG).show();
                    }
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            if(e!=null)
                Toast.makeText(ctx,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void markMessageRead(Context context, String number, String body) {

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try{

            while (cursor.moveToNext()) {
                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                        return;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.e("Mark Read", "Error in Read: " + e.toString());
        }
    }






}
