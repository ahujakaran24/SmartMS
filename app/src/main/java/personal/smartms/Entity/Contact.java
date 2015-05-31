package personal.smartms.Entity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;

import personal.smartms.MainActivity;

/**
 * Created by karan on 31/5/15.
 */
public class Contact {

    String Name, uri, Number;
    Bitmap bitmap;

    public Contact(String Name, String uri, String Number)
    {
            this.Name = Name;
            this.uri = uri;
             this.Number = Number;
    }


    public String getName() {
        return Name;
    }

    public String getUri() {
        return uri;
    }

    public String getNumber() {
        return Number;
    }

    public Bitmap getContactImage(){

                try {
                    if(uri!=null)
                        bitmap = MediaStore.Images.Media.getBitmap(MainActivity.context.getContentResolver(), Uri.parse(uri));
                    else
                        bitmap = null;
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
        return bitmap;
    }
}
