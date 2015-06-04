package personal.smartms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import personal.smartms.Conversation;
import personal.smartms.Entity.Message;
import personal.smartms.R;
import personal.smartms.Utils.Constants;

/**
 * Created by karan on 31/5/15.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder>{

    TreeMap<String,Message> messages;
    ArrayList<String> numbers;
    Context context;


    public InboxAdapter(TreeMap<String,Message> messages, ArrayList<String> numbers, Context context){
        this.messages = messages;
        this.numbers = numbers;
        this.context = context;
    }

    public static class InboxViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView number;
        TextView message;
        TextView date;
        ImageView newmsg;
        LinearLayout background;


       // ImageView personPhoto;

        InboxViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            number = (TextView)itemView.findViewById(R.id.number);
            message = (TextView)itemView.findViewById(R.id.message);
            date = (TextView)itemView.findViewById(R.id.date);
            newmsg = (ImageView)itemView.findViewById(R.id.newmsg);
            background = (LinearLayout)itemView.findViewById(R.id.l1);
        }
    }

    @Override
    public InboxViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_message, viewGroup, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //Toast.makeText(context, messages.get(numbers.get(messages.size()-i-1)).getNumber().toString(),Toast.LENGTH_LONG).show();

                String number = messages.get(numbers.get(messages.size()-i-1)).getNumber().toString();
                Intent i = new Intent(context, Conversation.class);
                i.putExtra("number", number);
                context.startActivity(i);

            }
        });

        InboxViewHolder pvh = new InboxViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /*In Main Activity since we parsed in revers, we need to display again in reverse to get it right*/
    @Override
    public void onBindViewHolder(InboxViewHolder inboxViewHolder, int i) {

        //HashMap.get ( key-->Arraylist.get(size - pos -1)). getter();
        if(inboxViewHolder!=null&&messages.get(numbers.get(messages.size() - i - 1))!=null) {
            inboxViewHolder.number.setText(Constants.getContact(context, messages.get(numbers.get(messages.size() - i - 1)).getNumber()));
            inboxViewHolder.message.setText(messages.get(numbers.get(messages.size() - i - 1)).getMessage());
            inboxViewHolder.date.setText(messages.get(numbers.get(messages.size() - i - 1)).getDate().toString().substring(0, 16));

            /*Mark it if its unread*/
            if ((messages.get(numbers.get(messages.size() - i - 1)).getSeen().equals("0")))
                inboxViewHolder.newmsg.setVisibility(View.VISIBLE);
            else
                inboxViewHolder.newmsg.setVisibility(View.GONE);

            /*Color it if its from a contact*/
            if(Constants.contactExists(context,messages.get(numbers.get(messages.size() - i - 1)).getNumber()))
            {
                inboxViewHolder.background.setBackgroundColor(Color.parseColor("#A4C639"));
            }else{
                inboxViewHolder.background.setBackgroundColor(Color.parseColor("#FBCEB1"));
            }

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}