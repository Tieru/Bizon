package pro.asdgroup.bizon.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.adapter.CommunityAdapter;
import pro.asdgroup.bizon.adapter.listener.EndlessScrollListener;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.util.KeyboardUtils;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class CommunityListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private final static String ARG_EVENT = "event_id";
    private final static String ARG_PROFILES = "profiles";

    public static CommunityListFragment newInstance() {
        return new CommunityListFragment();
    }

    public static Fragment newInstance(int eventId) {
        CommunityListFragment fragment = new CommunityListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_EVENT, eventId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(List<Profile> profiles) {
        CommunityListFragment fragment = new CommunityListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_PROFILES, (ArrayList)profiles);
        fragment.setArguments(bundle);
        return fragment;
    }

    @InjectView(R.id.profile_list) StickyListHeadersListView mProfileListView;
    @InjectView(R.id.clear_text_btn) Button mSearchClearButton;
    @InjectView(R.id.search_edit) EditText mSearchEdit;
    CommunityAdapter mAdapter;
    RestHelper mRestService;
    private ScrollListener mScrollListener;
    private ProfileListLoadedCallback mLoaderCallback;
    private int eventId = -1;
    private List<Profile> mProfiles;
    private int mPage = 0;

    private Handler mSearchHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLoader();
        ButterKnife.inject(this, view);

        mAdapter = new CommunityAdapter();
        mProfileListView.setAdapter(mAdapter);
        mProfileListView.setOnItemClickListener(this);

        Bundle args = getArguments();
        if (args != null){
            eventId = args.getInt(ARG_EVENT, -1);
            mProfiles = args.getParcelableArrayList(ARG_PROFILES);
        }

        if (mProfiles == null){
            onLoadBegins();
            loadProfiles(0);

            mScrollListener = new ScrollListener();
            mProfileListView.setOnScrollListener(mScrollListener);

            setOnSearchTextChangeListener();
            KeyboardUtils.setupUI(getActivity(), view);
        } else {
            mAdapter.addProfiles(mProfiles);
            mSearchEdit.setVisibility(View.GONE);
        }
    }

    private void initLoader() {
        mLoaderCallback = new ProfileListLoadedCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);
    }

    private void loadProfiles(int page) {
        if (eventId == -1) {
            mRestService.getUsers(mSearchEdit.getText().toString(), page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
        } else {
            mRestService.getEventParticipants(mSearchEdit.getText().toString(), page,
                    HttpHelper.DEFAULT_COUNT, eventId, mLoaderCallback);
        }
    }

    private class ProfileListLoadedCallback extends HttpHelper.RestCallback<List<Profile>> {

        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }

        @Override
        public void success(List<Profile> data, Response response) {
            if (getView() == null){
                return;
            }

            onLoadFinished();

            if (mPage == 0){
                mAdapter.reset();
                mScrollListener.reset();
            }

            mAdapter.addProfiles(data);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = CommunityUserFragment.newInstance(mAdapter.getItem(position));
        addFragment(fragment, true);
    }

    private void setOnSearchTextChangeListener(){
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0){
                    mSearchClearButton.setVisibility(View.GONE);
                } else {
                    mSearchClearButton.setVisibility(View.VISIBLE);
                }

                mSearchHandler.removeCallbacksAndMessages(null);
                mSearchHandler.postDelayed(mSearchRequestTask, 750);
            }
        });
    }

    private Runnable mSearchRequestTask = new Runnable(){

        @Override
        public void run() {
            if (getView() == null){
                return;
            }

            loadProfiles(0);
            mPage = 0;
        }
    };

    @OnClick(R.id.clear_text_btn)
    void onClearSearchClick(){
        mSearchEdit.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        ButterKnife.reset(this);
    }

    class ScrollListener extends EndlessScrollListener {

        @Override
        public void onLoadMore(int page) {
            loadProfiles(page);
            mPage = page;
        }
    }

    @Override
    public String getTitle() {
        if (eventId == -1) {
            return getString(R.string.community_title);
        } else {
            return getString(R.string.participants_title);
        }
    }
}

