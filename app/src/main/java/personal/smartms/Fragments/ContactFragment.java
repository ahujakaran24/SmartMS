package personal.smartms.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;

import personal.smartms.Adapter.ContactAdapter;
import personal.smartms.Entity.Contact;
import personal.smartms.R;
import personal.smartms.Utils.ComparatorContacts;


public class ContactFragment extends Fragment {


    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private RecyclerView rv;
    private Contact temp;
    private ProgressBar pb;
    String phone = null;
    String image_uri = "";



  public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchContacts().execute();

     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contact, container, false);

        pb= (ProgressBar)view.findViewById(R.id.pb1);
        pb.getIndeterminateDrawable().setColorFilter(0xFFcc0000,
                android.graphics.PorterDuff.Mode.MULTIPLY);

        rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setVisibility(View.GONE);


        return  view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    // Async Task Class
    class FetchContacts extends AsyncTask<Void, Void, Void> {

        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Download Music File from Internet
        @Override
        protected Void doInBackground(Void... no) {

            ContentResolver cr = getActivity().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    phone = null;
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        pCur.close();
                    }
                   // cur.close();
                    if(phone!=null) {
                        temp = new Contact(name, image_uri, phone);
                        contacts.add(temp);
                    }
                }
            }
            cur.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void no) {

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);

        /*sort by name*/
            Collections.sort(contacts, new ComparatorContacts());
            ContactAdapter adapter = new ContactAdapter(contacts,getActivity());
            rv.setAdapter(adapter);
            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

        }
    }



}
