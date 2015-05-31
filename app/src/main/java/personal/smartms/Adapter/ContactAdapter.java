package personal.smartms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import personal.smartms.Conversation;
import personal.smartms.Entity.Contact;
import personal.smartms.R;

/**
 * Created by karan on 31/5/15.
 */
public class ContactAdapter  extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
{

    ArrayList<Contact> contacts;
    Context context;

    public ContactAdapter(ArrayList<Contact> contacts, Context context){
        this.contacts = contacts;
        this.context = context;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        ImageView photo;

        // ImageView personPhoto;

        ContactViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            name = (TextView)itemView.findViewById(R.id.mText);
            photo = (ImageView)itemView.findViewById(R.id.mImage);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_card, viewGroup, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = contacts.get(i).getNumber();
                number = number.replaceAll("\\s","");
                Intent i = new Intent(context, Conversation.class);
                i.putExtra("number", number);
                context.startActivity(i);


            }
        });

        ContactViewHolder pvh = new ContactViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /*In Main Activity since we parsed in revers, we need to display again in reverse to get it right*/
    @Override
    public void onBindViewHolder(ContactViewHolder convosViewHolder, int i) {

        convosViewHolder.name.setText(contacts.get(i).getName());

        if(contacts.get(i).getUri()!=null) {
            convosViewHolder.photo.invalidate();
            convosViewHolder.photo.setImageBitmap(contacts.get(i).getContactImage());
        }else{
            convosViewHolder.photo.invalidate();
            convosViewHolder.photo.setImageResource(R.drawable.contacts);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

