package pro.asdgroup.bizon.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.adapter.EventPagerAdapter;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.model.Event;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class EventPagerFragment extends BaseFragment {

    private static final String ARG_ARTICLES = "articles";
    private static final String ARG_INDEX = "index";

    EventPagerAdapter mEventAdapter;
    ViewPager mPager;
    private String mActivityTitle;

    public static EventPagerFragment newInstance(List<Event> articles, int index){
        EventPagerFragment fragment = new EventPagerFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ARTICLES, (ArrayList<Event>) articles);
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);

        return fragment;
    }

    public EventPagerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_article_pager, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.article_pager);

        Bundle args = getArguments();
        final List<Event> events = (ArrayList<Event>)args.getSerializable(ARG_ARTICLES);
        int index = args.getInt(ARG_INDEX, 0);

        mEventAdapter = new EventPagerAdapter(getChildFragmentManager(), events);

        mPager.setAdapter(mEventAdapter);
        mPager.setCurrentItem(index);
        mActivityTitle = events.get(index).getName();

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActivityTitle = events.get(position).getName();
                ((MainActivityCallback) getActivity()).setActivityTitle(mActivityTitle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHasOptionsMenu(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public String getTitle() {
        return mActivityTitle;
    }
}
