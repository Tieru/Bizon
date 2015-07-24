package pro.asdgroup.bizon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.model.EventNew;

/**
 * Created by Voronov Viacheslav on 11/05/15.
 */
public class EventNewsAdapter extends BaseAdapter {

    List<EventNew> mEventNews;

    public EventNewsAdapter(List<EventNew> eventNews) {
        mEventNews = eventNews;
    }

    public EventNewsAdapter() {
        mEventNews = new ArrayList<>();
    }

    public void addNews(List<EventNew> eventNews) {
        mEventNews.addAll(eventNews);
        notifyDataSetChanged();
    }

    public void resetData(){
        mEventNews.clear();
    }

    @Override
    public int getCount() {
        return mEventNews.size();
    }

    @Override
    public EventNew getItem(int i) {
        return mEventNews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_event_news, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        EventNew item = getItem(i);

        holder.publisherText.setText(item.getPublisher().getFirstLastName());
        holder.publishDateText.setText(item.getDateText());
        holder.newsText.setText(item.getText());

        return view;
    }

    private class ViewHolder {
        TextView publisherText;
        TextView publishDateText;
        TextView newsText;

        public ViewHolder(View view) {
            publisherText = (TextView) view.findViewById(R.id.publisher_text);
            publishDateText = (TextView) view.findViewById(R.id.publish_date_text);
            newsText = (TextView) view.findViewById(R.id.news_text);
        }

    }
}
