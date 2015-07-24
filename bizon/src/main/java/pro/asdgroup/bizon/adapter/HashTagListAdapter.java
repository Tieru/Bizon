package pro.asdgroup.bizon.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.activity.HashtagFilterActivity.HashTagCheck;
import pro.asdgroup.bizon.BizonApp;

/**
 * Created by Voronov Viacheslav on 4/25/2015.
 */
public class HashTagListAdapter extends BaseAdapter {

    List<HashTagCheck> mHashTags;

    public HashTagListAdapter(List<HashTagCheck> tags){
        mHashTags = tags;
    }

    public List<HashTagCheck> getDataSet(){
        return mHashTags;
    }

    @Override
    public int getCount() {
        return mHashTags.size();
    }

    @Override
    public HashTagCheck getItem(int position) {
        return mHashTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mHashTags.get(position).hashTag.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_hashtag, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        HashTagCheck hashTagCheck = getItem(position);
        holder.hashTagText.setText(hashTagCheck.hashTag.getName());

        Resources res = BizonApp.getAppContext().getResources();
        if (hashTagCheck.checked){
            holder.hashTagIcon.setImageDrawable(res.getDrawable(R.drawable.filter_cell_selected));
            holder.hashTagText.setTextColor(res.getColor(R.color.dark_green));
            holder.hashTagCheckbox.setImageDrawable(res.getDrawable(R.drawable.filter_checkmark_selected));
        } else {
            holder.hashTagIcon.setImageDrawable(res.getDrawable(R.drawable.filter_cell_default));
            holder.hashTagText.setTextColor(Color.BLACK);
            holder.hashTagCheckbox.setImageDrawable(res.getDrawable(R.drawable.filter_checkmark_default));
        }

        return convertView;
    }

    class ViewHolder {

        ImageView hashTagIcon;
        TextView hashTagText;
        ImageView hashTagCheckbox;

        ViewHolder(View view){

            hashTagIcon = (ImageView) view.findViewById(R.id.hashtag_icon);
            hashTagText = (TextView) view.findViewById(R.id.hashtag_text);
            hashTagCheckbox = (ImageView) view.findViewById(R.id.checkbox_image);

            Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/MinionPro-Regular_0.otf");
            hashTagText.setTypeface(font);
        }

    }
}
