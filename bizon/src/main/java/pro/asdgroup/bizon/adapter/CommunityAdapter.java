package pro.asdgroup.bizon.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.model.Profile;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Voronov Viacheslav on 4/20/2015.
 */
public class CommunityAdapter  extends BaseAdapter implements StickyListHeadersAdapter {

    private List<Profile> mProfiles;
    private Drawable mDefaultDrawable;
/*
    HashMap<String, Integer> mSectionIndexes;
    private String[] mSections;
*/

    public CommunityAdapter(){
        mProfiles = new ArrayList<>();
        init();
    }

    public CommunityAdapter(List<Profile> profiles){
        mProfiles = profiles;
        init();
    }

    private void init(){
        mDefaultDrawable = BizonApp.getAppContext().getResources().getDrawable(R.drawable.no_image);
    }

/*
    private void updateSections(){
        mSectionIndexes = new LinkedHashMap<>();

        for (int i = 0; i < mProfiles.size(); i++) {
            String surname = mProfiles.get(i).getLastName();
            String ch = surname.substring(0, 1);
            ch = ch.toUpperCase();
            mSectionIndexes.put(ch, i);
        }

        Set<String> sectionLetters = mSectionIndexes.keySet();

        mSections = new String[sectionLetters.size()];
        sectionLetters.toArray(mSections);
    }
*/

    public void addProfiles(List<Profile> profiles){
        mProfiles.addAll(profiles);
        //updateSections();
        notifyDataSetChanged();
    }

    public void reset(){
        mProfiles.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProfiles.size();
    }

    @Override
    public Profile getItem(int position) {
        return mProfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile, parent, false);
            holder = new ViewHolder();
            holder.initialize(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Profile profile = getItem(position);

        holder.nameText.setText(profile.getLastFirstName());
        String companyText = profile.getCompanyNames(false);
        holder.companyText.setText(companyText);
        holder.tagsText.setText(profile.getTagsString());

        Picasso.with(parent.getContext())
                .load(profile.getAvatarUrl())
                .placeholder(mDefaultDrawable)
                .error(mDefaultDrawable)
                .into(holder.profileImage);

        return convertView;
    }

    private class HeaderViewHolder {
        TextView dateText;

        HeaderViewHolder(View view){
            dateText = (TextView)view.findViewById(R.id.date_text);
        }
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;

        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_event_header, viewGroup, false);
            holder = new HeaderViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        holder.dateText.setText(getItem(i).getLastName().substring(0, 1));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).getLastName().substring(0, 1).hashCode();
    }

/*    @Override
    public Object[] getSections() {
        return mSections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
*//*        for (int i = 0; i < getCount(); i++) {
            String lastName = mProfiles.get(i).getLastName();
            char firstChar = lastName.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;*//*
        //Log.d("asda", "asdasda");
        //return mSectionIndexes.get(mSections[sectionIndex]);

        String sectionValue = mSections[sectionIndex];
*//*        for (int i=0; i < this.getCount(); i++) {
            String item = this.getItem(i).getLastName().toLowerCase();
            if (item.substring(0, 1).equals(sectionValue)) {
                return i;
            }
        }*//*
        return mSectionIndexes.get(mSections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        //return mProfiles.get(position).getLastName().charAt(0);
        return 0;
    }*/

    class ViewHolder {

        TextView nameText;
        TextView companyText;
        TextView tagsText;
        ImageView profileImage;


        public void initialize(View view){
            profileImage    = (ImageView) view.findViewById(R.id.profile_photo);
            nameText       = (TextView) view.findViewById(R.id.name_text);
            companyText = (TextView) view.findViewById(R.id.company_text);
            tagsText = (TextView) view.findViewById(R.id.tags_text);

            Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
            tagsText.setTypeface(font);

            font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/MinionPro-Regular_0.otf");
            companyText.setTypeface(font);
        }
    }
}
