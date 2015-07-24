package pro.asdgroup.bizon.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.adapter.EventAdapter;
import pro.asdgroup.bizon.adapter.listener.EndlessScrollListener;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Event;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class EventListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private final static String ARG_HASHTAG = "hashtags";

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    public static EventListFragment newInstance(HashTag hashTag) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_HASHTAG, hashTag);
        fragment.setArguments(args);
        return fragment;
    }

    public EventListFragment() {
    }

    private StickyListHeadersListView mEventListView;
    private EventAdapter mAdapter;
    private RestHelper mRestService;
    private EventLoaderCallback mLoaderCallback;
    private HashTag mHashTag;

    private boolean loadMyEventsOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEventListView = (StickyListHeadersListView) view.findViewById(R.id.list);
        mEventListView.setOnScrollListener(new ScrollListener());
        mEventListView.setOnItemClickListener(this);

        mLoaderCallback = new EventLoaderCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);

        Bundle args = getArguments();
        if (args != null){
            mHashTag = (HashTag) args.getSerializable(ARG_HASHTAG);
        }

        SegmentedGroup radioGroup = (SegmentedGroup) view.findViewById(R.id.segmented_control);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.all_radio_button:
                            loadMyEventsOnly = false;
                        break;
                    case R.id.my_radio_button:
                            loadMyEventsOnly = true;
                        break;
                }

                reloadEvents();
            }
        });

        if (User.currentUser().getProfile() == null || mHashTag != null){
            radioGroup.setVisibility(View.GONE);
        }

        onLoadBegins();
        loadEvents(0);
    }

    private void reloadEvents(){
        mAdapter = null;
        onLoadBegins();
        loadEvents(0);
    }

    private void loadEvents(int page){
        if (loadMyEventsOnly){
            loadEvents(page, 1);
        } else {
            loadEvents(page, 0);
        }
    }

    private void loadEvents(int page, int participationStatus){
        if (mHashTag == null) {
            mRestService.getEvents(page, HttpHelper.DEFAULT_COUNT, participationStatus, User.currentUser().getUserId(), mLoaderCallback);
        } else {
            mRestService.getEvents(page, HttpHelper.DEFAULT_COUNT, participationStatus, User.currentUser().getUserId(), mHashTag.getId(), mLoaderCallback);
        }
    }

    private class EventLoaderCallback extends HttpHelper.RestCallback<List<Event>> {

        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }

        @Override
        public void success(List<Event> data, Response response) {
            onLoadFinished();
            if (getView() == null){
                return;
            }

            if(mAdapter == null) {
                mAdapter = new EventAdapter(data);
                mEventListView.setAdapter(mAdapter);
            }
/*            mAdapter.addEvents(data);
            mAdapter.notifyDataSetChanged();*/
        }
    }

    class ScrollListener extends EndlessScrollListener {

        @Override
        public void onLoadMore(int page) {
            loadEvents(page);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = EventPagerFragment.newInstance(mAdapter.getEventList(), position);
        addFragment(fragment, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEventListView = null;
    }

    @Override
    public String getTitle() {
        Bundle args = getArguments();
        if (args != null){
            return ((HashTag) args.getSerializable(ARG_HASHTAG)).toString();
        }
        return getString(R.string.events_title);
    }
}
