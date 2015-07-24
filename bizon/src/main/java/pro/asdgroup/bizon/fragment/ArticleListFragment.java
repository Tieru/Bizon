package pro.asdgroup.bizon.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.activity.HashtagFilterActivity;
import pro.asdgroup.bizon.adapter.ArticleAdapter;
import pro.asdgroup.bizon.adapter.listener.EndlessScrollListener;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Article;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class ArticleListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static String ARG_HASHTAGS = "hashtags";
    private static String ARG_AUTHOR_ID = "expert_id";
    private static int ON_FILTER_ACTIVITY_RESULT = 200;

    static final String LOGS = "BizonLogs";

    @InjectView(R.id.articles_list) ListView mArticlesListView;

    private ArticleAdapter mAdapter;
    private ScrollListener mScrollListener;

    private List<Integer> mHashTags;
    private int mCurrentPage = 0;
    private RestHelper mRestService;
    private OnArticleListLoadedCallback mLoaderCallback;
    private String mAuthorId;

    public static ArticleListFragment newInstance() {
        return new ArticleListFragment();
    }

    public static ArticleListFragment newInstance(List<Integer> hashTags) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ARG_HASHTAGS, (ArrayList)hashTags);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ArticleListFragment newInstance(String authorId) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AUTHOR_ID, authorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ArticleListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_articles_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        mAdapter = new ArticleAdapter();
        mScrollListener = new ScrollListener();
        mArticlesListView.setAdapter(mAdapter);
        mArticlesListView.setOnScrollListener(mScrollListener);
        mArticlesListView.setOnItemClickListener(this);

        mLoaderCallback = new OnArticleListLoadedCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);

        Bundle args = getArguments();
        if (args != null){
            mHashTags = args.getIntegerArrayList(ARG_HASHTAGS);
            mAuthorId = args.getString(ARG_AUTHOR_ID);
        } else {
            setHasOptionsMenu(true);
        }

        onLoadBegins();
        loadArticles(mCurrentPage);
    }

    private void loadArticles(int page) {
        if (mHashTags != null && mHashTags.size() > 0) {
            Integer[] hashtags = new Integer[mHashTags.size()];
            hashtags = mHashTags.toArray(hashtags);
            mRestService.getArticles(page, HttpHelper.DEFAULT_COUNT, hashtags, mLoaderCallback);
        } else if (mAuthorId != null && !mAuthorId.isEmpty()){
            mRestService.getArticles(mAuthorId, page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
        } else {
            mRestService.getArticles(page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
        }
    }

    private class OnArticleListLoadedCallback extends HttpHelper.RestCallback<List<Article>> {

        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }

        @Override
        public void success(List<Article> data, Response response) {
            onLoadFinished();
            if (getView() == null){
                return;
            }

            mAdapter.addArticles(data);
            mAdapter.notifyDataSetChanged();
        }
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.article, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            onFilterMenuClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onFilterMenuClick() {
        Intent intent = new Intent(getActivity(), HashtagFilterActivity.class);

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(HashtagFilterActivity.ARG_ACTIVE_HASHTAGS, (ArrayList) mHashTags);

        intent.putExtras(bundle);
        startActivityForResult(intent, ON_FILTER_ACTIVITY_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ON_FILTER_ACTIVITY_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                onLoadBegins();
                mAdapter.resetData();
                mScrollListener.reset();
                //mRestService.getArticles(0, HttpHelper.DEFAULT_COUNT, mHashTags, mLoaderCallback);
                Bundle bundle = data.getExtras();
                if (bundle != null){
                    mHashTags = bundle.getIntegerArrayList(HashtagFilterActivity.ARG_ACTIVE_HASHTAGS);
                }
                loadArticles(0);
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ArticlePagerFragment.newInstance(mAdapter.getArticlesList(), position);
        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    class ScrollListener extends EndlessScrollListener {

        @Override
        public void onLoadMore(int page) {
            mCurrentPage = page;
            loadArticles(page);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        mAdapter = null;
        setHasOptionsMenu(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public String getTitle() {
        return getString(R.string.articles_title);
    }
}
