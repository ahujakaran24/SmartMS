package personal.smartms.Adapter;

import android.content.Context;
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

import personal.smartms.Entity.Convo;
import personal.smartms.R;

/**
 * Created by karan on 31/5/15.
 */
public class ConverseAdapter extends RecyclerView.Adapter<ConverseAdapter.ConverseViewHolder>
{

    ArrayList<Convo> convos;
    Context context;
    public ConverseAdapter(ArrayList<Convo> convos, Context context){
        this.convos = convos;
        this.context = context;
    }

    public static class ConverseViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView number;
        TextView message;
        TextView date;
        ImageView out;
        ImageView in;
        LinearLayout background;

        // ImageView personPhoto;

        ConverseViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            number = (TextView)itemView.findViewById(R.id.number);
            message = (TextView)itemView.findViewById(R.id.message);
            date = (TextView)itemView.findViewById(R.id.date);
            out = (ImageView)itemView.findViewById(R.id.imageView);
            in = (ImageView)itemView.findViewById(R.id.in);
            background=(LinearLayout)itemView.findViewById(R.id.l1);
        }
    }

    @Override
    public ConverseViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_message, viewGroup, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        ConverseViewHolder pvh = new ConverseViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        return convos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /*In Main Activity since we parsed in revers, we need to display again in reverse to get it right*/
    @Override
    public void onBindViewHolder(ConverseViewHolder convosViewHolder, int i) {
        convosViewHolder.number.setText(convos.get(convos.size()-i-1).getNumber());
        convosViewHolder.message.setText(convos.get(convos.size()-i-1).getBody());
        convosViewHolder.date.setText(convos.get(convos.size() - i - 1).getDate().toString().substring(0, 16));
        if(convos.get(convos.size()-i-1).getInorout().equals("in"))
        {
            convosViewHolder.in.setImageResource(R.drawable.in);
            convosViewHolder.background.setBackgroundColor(Color.parseColor("#FFC285"));
        }
        else {
            convosViewHolder.out.setImageResource(R.drawable.out);
            convosViewHolder.background.setBackgroundColor(Color.parseColor("#D6EBFF"));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
