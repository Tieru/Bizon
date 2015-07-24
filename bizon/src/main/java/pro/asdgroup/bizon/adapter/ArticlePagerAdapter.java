package pro.asdgroup.bizon.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import pro.asdgroup.bizon.fragment.ArticleFragment;
import pro.asdgroup.bizon.model.Article;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class ArticlePagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Article> articleList;

    public ArticlePagerAdapter(FragmentManager fm, ArrayList<Article> articles){
        super(fm);

        articleList = articles;
    }

    public void setArticles(ArrayList<Article> articles){
        this.articleList = articles;
    }

    @Override
    public Fragment getItem(int position) {
        return ArticleFragment.newInstance(articleList.get(position));
    }

    @Override
    public int getCount() {
        return articleList.size();
    }
}
