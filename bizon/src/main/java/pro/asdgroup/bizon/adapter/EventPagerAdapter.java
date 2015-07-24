package pro.asdgroup.bizon.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import pro.asdgroup.bizon.fragment.EventFragment;
import pro.asdgroup.bizon.model.Event;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class EventPagerAdapter extends FragmentStatePagerAdapter {

    List<Event> mEvents;

    public EventPagerAdapter(FragmentManager fm, List<Event> events) {
        super(fm);

        mEvents = events;
    }

    @Override
    public Fragment getItem(int position) {
        return EventFragment.newInstance(mEvents.get(position));
    }

    public Event getItemEvent(int position) {
        return mEvents.get(position);
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }
}
