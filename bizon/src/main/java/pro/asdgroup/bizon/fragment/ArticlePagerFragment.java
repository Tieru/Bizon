package pro.asdgroup.bizon.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.adapter.ArticlePagerAdapter;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.model.Article;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class ArticlePagerFragment extends BaseFragment {

    ArticlePagerAdapter mArticleAdapter;
    private static final String ARG_ARTICLES = "articles";
    private static final String ARG_INDEX = "index";

    private String mActivityTitle;

    public static ArticlePagerFragment newInstance(List<Article> articles, int index){
        ArticlePagerFragment fragment = new ArticlePagerFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ARTICLES, new ArrayList(articles));
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);

        return fragment;
    }

    public ArticlePagerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_article_pager, container, false);

        ViewPager pager = (ViewPager) rootView.findViewById(R.id.article_pager);

        Bundle args = getArguments();
        final ArrayList<Article> articles = args.getParcelableArrayList(ARG_ARTICLES);
        int index = args.getInt(ARG_INDEX, 0);

        mArticleAdapter = new ArticlePagerAdapter(getChildFragmentManager(), articles);

        pager.setAdapter(mArticleAdapter);
        pager.setCurrentItem(index);

        mActivityTitle = articles.get(index).getName();

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActivityTitle = articles.get(position).getName();
                ((MainActivityCallback)getActivity()).setActivityTitle(mActivityTitle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTargetFragment().setMenuVisibility(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        getTargetFragment().setMenuVisibility(true);
    }

    @Override
    public String getTitle() {
        return mActivityTitle;
    }
}
