package personal.smartms.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        mListener = null;
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
                                    messages.remove(numbers.get(position));
                                    numbers.remove(position);
                                    adapter.notifyItemRemoved(position);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    messages.remove(numbers.get(position));
                                    numbers.remove(position);
                                    adapter.notifyItemRemoved(position);
                                }
                                adapter.notifyDataSetChanged();

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

        if(Conversation.haveSent)
        {
            Conversation.haveSent=false;
            mListener.accessStorageInbox();
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
                                    mListener.accessStorageInbox();
                                    mListener.getInbox();
                                    mListener.getNumbers();
                                    update();
                                }
                            }, 4000
                    );
                   // update();
                }
            }
        };
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        getActivity().unregisterReceiver(receiver);   // V V imp to unregister :)
    }
}
