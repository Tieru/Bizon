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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
 * Created by Tieru on 24.05.2015.
 */
public class ExpertsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static ExpertsFragment newInstance() {
        return new ExpertsFragment();
    }

    public ExpertsFragment() {
    }

    @InjectView(R.id.profile_list) StickyListHeadersListView mProfileListView;
    @InjectView(R.id.search_edit) EditText mSearchEdit;
    @InjectView(R.id.clear_text_btn) Button mSearchClearButton;
    private CommunityAdapter mAdapter;
    private RestHelper mRestService;
    private ScrollListener mScrollListener;
    private ProfileListLoadedCallback mLoaderCallback;

    private Handler mSearchHandler = new Handler();

    private int mPage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLoader();
        ButterKnife.inject(this, view);

        mScrollListener = new ScrollListener();
        mProfileListView.setOnScrollListener(mScrollListener);
        mAdapter = new CommunityAdapter();
        mProfileListView.setAdapter(mAdapter);
        mProfileListView.setOnItemClickListener(this);

        onLoadBegins();
        loadProfiles(0);

        setOnSearchTextChangeListener();
        KeyboardUtils.setupUI(getActivity(), view);
    }

    private void initLoader() {
        mLoaderCallback = new ProfileListLoadedCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);
    }

    private void loadProfiles(int page) {
        mRestService.getExperts(mSearchEdit.getText().toString(), page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        ButterKnife.reset(this);
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
            loadProfiles(0);
            mPage = 0;
        }
    };

    class ScrollListener extends EndlessScrollListener {

        @Override
        public void onLoadMore(int page) {
            loadProfiles(page);
            mPage = page;
        }
    }


    @Override
    public String getTitle() {
        return getString(R.string.experts_title);
    }
}

