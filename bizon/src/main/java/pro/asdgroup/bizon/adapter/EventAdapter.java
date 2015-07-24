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
import java.util.Map;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.helper.EventAggregator;
import pro.asdgroup.bizon.model.Event;
import pro.asdgroup.bizon.model.Publisher;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class EventAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private class EventListItem {
        public Event event;
        public EventListItem parent;
    }

    private Drawable mDefaultImage;
    private List<EventListItem> mListItems = new ArrayList<>();
    private EventAggregator aggregator;

    public EventAdapter(){
        init();
    }

    public EventAdapter(List<Event> events){
        init();

        aggregator.addEvents(events);
        processEvents(aggregator.aggregate());
    }

    private void init(){
        mDefaultImage = BizonApp.getAppContext().getResources().getDrawable(R.drawable.no_image);
        aggregator = new EventAggregator();
    }

    public void addEvents(List<Event> events){
        aggregator.addEvents(events);
        processEvents(aggregator.aggregate());
        notifyDataSetChanged();
    }

    public List<Event> getEventList(){
        List<Event> listItems = new ArrayList<>();
        for (EventListItem event : mListItems){
            listItems.add(event.event);
        }

        return listItems;
    }

    private void processEvents(Map<Long, EventAggregator.EventParentItem> data){
        for (Map.Entry<Long, EventAggregator.EventParentItem> eventData: data.entrySet()){
            EventAggregator.EventParentItem parentData = eventData.getValue();
            EventListItem parent = new EventListItem();

            Event parentEvent = new Event();
            parentEvent.setPublishedDate(parentData.date);
            parent.event = parentEvent;

            for (Event event: parentData.eventItemList){
                EventListItem item = new EventListItem();
                item.parent = parent;
                item.event = event;
                mListItems.add(item);
            }
        }
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public EventListItem getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mListItems.get(position).event.getId();
    }

    private class HeaderViewHolder {
        TextView dateText;

        HeaderViewHolder(View view){
            dateText = (TextView)view.findViewById(R.id.date_text);
        }
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup parent) {
        HeaderViewHolder holder;

        if (view == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_event_header, parent, false);
            holder = new HeaderViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        EventListItem item = getItem(position).parent;

        holder.dateText.setText(item.event.getPublishedDateText(true));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).event.getPublishedDate().hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_event, parent, false);
            holder = new ViewHolder();
            holder.initialize(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EventListItem event = getItem(position);

        holder.titleText.setText(event.event.getName());
        holder.descriptionText.setText(event.event.getSmallDescription());
        holder.publishDateText.setText(event.event.getPublishedTime(true));

        Publisher publisher = event.event.getPublisher();
        String publisherText = publisher.getFirstName() + " " + publisher.getLastName();
        holder.publisherNameText.setText(publisherText);

        Picasso.with(parent.getContext())
                .load(event.event.getPictureUrl())
                .placeholder(mDefaultImage)
                .error(mDefaultImage)
                .into(holder.eventImage);

        if (event.event.getIsParticipant()){
            holder.participationStatusImage.setVisibility(View.VISIBLE);
        } else {
            holder.participationStatusImage.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    class ViewHolder {

        TextView titleText;
        TextView descriptionText;
        ImageView eventImage;
        ImageView participationStatusImage;
        TextView publishDateText;
        TextView publisherNameText;

        public void initialize(View view){
            eventImage    = (ImageView) view.findViewById(R.id.event_photo);
            participationStatusImage    = (ImageView) view.findViewById(R.id.participation_status_image);
            titleText       = (TextView) view.findViewById(R.id.name_text);
            descriptionText = (TextView) view.findViewById(R.id.small_description_text);
            publishDateText = (TextView) view.findViewById(R.id.publish_date_text);
            publisherNameText = (TextView) view.findViewById(R.id.publisher_text);

            Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/segoeuil.ttf");
            descriptionText.setTypeface(font);

            font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
            publisherNameText.setTypeface(font);
            publishDateText.setTypeface(font);
        }
    }
}
