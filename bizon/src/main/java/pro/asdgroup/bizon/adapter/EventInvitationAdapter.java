package pro.asdgroup.bizon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.model.InvitationContact;

/**
 * Created by Tieru on 05.06.2015.
 */
public class EventInvitationAdapter extends BaseAdapter {

    public final static int HEADER_TYPE = 1;
    public final static int ITEM_TYPE = 0;

    public final static int COMMUNITY_LIST_TYPE = 0;
    public final static int USER_CONTACTS_LIST_TYPE = 1;

    private List<InvitationContact> mCommunityContacts;
    private List<InvitationContact> mUserContacts;
    private Set<InvitationContact> mSelectedContacts;
    private List<ListItem> mListItems = new ArrayList<>();

    private int listType;

    public EventInvitationAdapter(){
        mCommunityContacts = new ArrayList<>();
        mUserContacts = new ArrayList<>();
        mSelectedContacts = new LinkedHashSet<>();
    }

    public EventInvitationAdapter(List<InvitationContact> contacts){
        mCommunityContacts = contacts;
        mUserContacts = new ArrayList<>();
        mSelectedContacts = new LinkedHashSet<>();
    }

    public void updateList(){
        mListItems.clear();

        ListItem listItem = new ListItem();
        listItem.type = 1;
        listItem.value = BizonApp.getAppContext().getString(R.string.ep_chosen_contacts);
        mListItems.add(listItem);

        for (InvitationContact contact: mSelectedContacts){
            ListItem item = new ListItem();
            item.value = contact;
            mListItems.add(item);
        }

        if (listType == COMMUNITY_LIST_TYPE){
            processCommunityContacts();
        } else {
            processUserContacts();
        }

        notifyDataSetChanged();
    }

    public void processCommunityContacts(){
        ListItem listItem = new ListItem();
        listItem.type = 1;
        listItem.value = BizonApp.getAppContext().getString(R.string.ep_community);
        mListItems.add(listItem);

        for (InvitationContact contact: mCommunityContacts){
            ListItem item = new ListItem();
            item.value = contact;
            mListItems.add(item);
        }
    }

    private void processUserContacts(){
        ListItem listItem = new ListItem();
        listItem.type = 1;
        listItem.value = BizonApp.getAppContext().getString(R.string.ep_community);
        mListItems.add(listItem);

        for (InvitationContact contact: mUserContacts){
            ListItem item = new ListItem();
            item.value = contact;
            mListItems.add(item);
        }
    }

    public void addInvitationContacts(List<InvitationContact> contacts){
        mCommunityContacts.addAll(contacts);
        updateList();
    }

    public void setUserContacts(List<InvitationContact> contacts){
        mUserContacts = contacts;
        if (listType == USER_CONTACTS_LIST_TYPE) {
            updateList();
            notifyDataSetChanged();
        }
    }

    public void setDisplayType(int type){
        listType = type;
        updateList();
    }

    public Set<InvitationContact> getSelectedContacts(){
        return mSelectedContacts;
    }

    public void clearSelected(){
        mSelectedContacts.clear();
        updateList();
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public ListItem getItem(int i) {
        return mListItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void onItemSelection(int position){
        if (position > mSelectedContacts.size() + 1){
            mSelectedContacts.add((InvitationContact) getItem(position).value);
        } else {
            mSelectedContacts.remove((InvitationContact) getItem(position).value);
        }

        updateList();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i) == HEADER_TYPE){
            return getHeaderView(i, view, viewGroup);
        } else {
            return getChildView(i, view, viewGroup);
        }
    }

    private View getHeaderView(int i, View view, ViewGroup viewGroup){
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_header_event_invitation, viewGroup, false);
        }

        TextView titleText = (TextView) view.findViewById(R.id.title);
        titleText.setText((String) getItem(i).value);

        return view;
    }

    private View getChildView(int i, View view, ViewGroup viewGroup){
        ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_event_invitation, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        InvitationContact contact = (InvitationContact) getItem(i).value;
        holder.emailText.setText(contact.getEmail());
        holder.nameText.setText(contact.getFirstLastName());
        Picasso.with(viewGroup.getContext())
                .load(contact.getAvatarUrl())
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .into(holder.image);

        return view;
    }


    private class ViewHolder {
        ImageView image;
        TextView nameText;
        TextView emailText;

        public ViewHolder(View view){
            image = (ImageView) view.findViewById(R.id.contact_image);
            nameText = (TextView) view.findViewById(R.id.name_text);
            emailText = (TextView) view.findViewById(R.id.email_text);
        }
    }

    public static class ListItem {
        public int type = 0;
        public Object value;
    }

}
