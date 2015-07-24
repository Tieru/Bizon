package pro.asdgroup.bizon.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.activity.CommentEditActivity;
import pro.asdgroup.bizon.adapter.FeedAdapter;
import pro.asdgroup.bizon.adapter.listener.RecyclerViewScrollListener;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.events.OnPostRemoved;
import pro.asdgroup.bizon.events.OnPostUpdatedEvent;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.Id;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.model.dto.PostDTO;
import pro.asdgroup.bizon.util.DividerItemDecoration;
import retrofit.client.Response;

/**
 * Created by vvoronov on 03/07/15.
 */
public class FeedFragment extends BaseFragment {

    private final static int REQ_CODE_NEW_POST = 100;

    @InjectView(R.id.feedList) RecyclerView mList;
    @InjectView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeToRefreshLayout;

    private FeedAdapter mAdapter;
    private RestHelper mRestService;
    private FeedLoaderCallback mLoaderCallback;
    private RecyclerViewScrollListener mScrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);
        mList.addItemDecoration(new DividerItemDecoration());
        //mList.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new FeedAdapter(new FeedAdapter.FeedAdapterClick() {
            @Override
            public boolean onAuthRequest() {
                Activity activity = getActivity();
                if (activity instanceof MainActivityCallback){
                    if (!((MainActivityCallback) activity).onAuthRequest()){
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void onItemClick(int position) {
                FeedEntry entry = mAdapter.getItem(position);
                addFragment(FeedEntryFragment.newInstance(entry), true,
                        R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        mList.setAdapter(mAdapter);

        mLoaderCallback = new FeedLoaderCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);

        loadPage(0);

        mScrollListener = new RecyclerViewScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadPage(page);
            }
        };

        mList.addOnScrollListener(mScrollListener);

        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        setHasOptionsMenu(true);
    }

    private void loadPage(int page){
        mRestService.getFeed(page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
    }

    private class FeedLoaderCallback extends HttpHelper.RestCallback<List<FeedEntry>> {

        @Override
        public void failure(Status restError) {
            onLoadFinished();
            mSwipeToRefreshLayout.setRefreshing(false);
        }

        @Override
        public void success(List<FeedEntry> data, Response response) {
            onLoadFinished();
            if (getView() == null){
                return;
            }

            mSwipeToRefreshLayout.setRefreshing(false);
            mAdapter.addItems(data);
        }
    }

    private void refreshItems(){
        mAdapter.reset();
        mScrollListener.reset();
        loadPage(0);
    }

    public void onEvent(OnPostUpdatedEvent event){
        mAdapter.updateItem(event.getFeedEntry());
    }

    public void onEvent(OnPostRemoved event){
        mAdapter.removeItem(event.getFeedEntry());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.add, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Activity activity = getActivity();
            if (activity instanceof MainActivityCallback){
                if (((MainActivityCallback) activity).onAuthRequest()){
                    Intent intent = new Intent(getActivity(), CommentEditActivity.class);
                    intent.putExtra(CommentEditActivity.ARG_TEXT_LIMIT, CommentEditActivity.POST_MAX_CHARS);
                    intent.putExtra(CommentEditActivity.ARG_HAS_ADD_CONTENT, true);

                    startActivityForResult(intent, REQ_CODE_NEW_POST);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_NEW_POST && resultCode == Activity.RESULT_OK){
            onPostCreate(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onPostCreate(Intent intent){
        String text = intent.getStringExtra(CommentEditActivity.ARG_TEXT);
        String additionalContent = intent.getStringExtra(CommentEditActivity.ARG_ADD_CONTENT);

        final FeedEntry entry = new FeedEntry();
        entry.setAuthor(User.currentUser().getProfile());
        entry.setText(text);
        entry.setCity(User.currentUser().getProfile().getCity().getName());
        if (additionalContent != null && !additionalContent.trim().isEmpty()){
            //String image64 = getImage(additionalContent);
            entry.setPictureUrl(additionalContent);
        }
        onLoadBegins();
        mRestService.createPost(entry, new HttpHelper.RestCallback<PostDTO>() {

            @Override
            public void success(PostDTO feedEntry, Response response) {
                onLoadFinished();
                entry.setText(feedEntry.getText());
                entry.setCity(feedEntry.getCity());
                entry.setPictureUrl(feedEntry.getPictureUrl());
                entry.setId(feedEntry.getId());
                mAdapter.insertIntoStart(entry);
                mList.scrollToPosition(0);
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }
        });
    }
/*
    private String getImage(String uri){

    }*/

    public static Fragment newInstance(){
        return new FeedFragment();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.reset(this);
    }

    @Override
    public String getTitle() {
        return BizonApp.getAppContext().getString(R.string.feed_title);
    }
}
