package pro.asdgroup.bizon.helper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pro.asdgroup.bizon.model.Event;

/**
 * Created by Voronov Viacheslav on 5/4/2015.
 */
public class EventAggregator {

    public static class EventParentItem implements Comparable<EventParentItem>{
        public Date date;
        public List<Event> eventItemList = new ArrayList<>();

        @Override
        public int hashCode() {
            return date.hashCode();
        }


        @Override
        public int compareTo(@NonNull EventParentItem another) {
            return date.compareTo(another.date);
        }
    }

    private Map<Long, EventParentItem> parentItems = new TreeMap<>();
    private List<Event> mEvents = new ArrayList<>();

    public void addEvents(List<Event> events){
        mEvents.addAll(events);
    }

    public Map<Long, EventParentItem> aggregate(){

        for (Event event: mEvents){
            EventParentItem parentItem = getOrCreateParent(event.getPublishedDate().getTime());
            parentItem.eventItemList.add(event);
        }

        mEvents.clear();

        return parentItems;
    }

    public void clear(){
        parentItems.clear();
        mEvents.clear();
    }

    private EventParentItem getOrCreateParent(long date){
        EventParentItem item = parentItems.get(date);

        if (item == null){
            item = new EventParentItem();
            item.date = new Date(date);
            parentItems.put(date, item);
        }

        return item;
    }
}
