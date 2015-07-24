package pro.asdgroup.bizon.loader;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.model.InvitationContact;

/**
 * Created by Tieru on 05.06.2015.
 */
public class ContactsLoader extends AsyncTaskLoader<List<InvitationContact>> {
    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        this.forceLoad();
    }

    @Override
    public List<InvitationContact> loadInBackground() {

        List<InvitationContact> contacts = new ArrayList<>();

        String name;

        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (!cur.moveToFirst()) {
            return contacts;
        }

        while (cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            Cursor emails = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);

            while (emails.moveToNext()) {
                String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                if ((!emailAddress.equalsIgnoreCase("")) && (emailAddress.contains("@"))) {

                    InvitationContact contact = new InvitationContact();
                    contact.setFirstName(name);
                    contact.setEmail(emailAddress);
                    contact.setAvatarUrl(image_uri);
                    contacts.add(contact);
                }
            }
            emails.close();
        }

        return contacts;
    }
}
