package pro.asdgroup.bizon.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.adapter.ImagePagerAdapter;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.BasePagerFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Article;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Publisher;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.view.FlowLayout;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class ArticleFragment extends BasePagerFragment implements View.OnClickListener {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";

    Article mArticle;
    String fragmentTitle;

    public static ArticleFragment newInstance(Article article){

        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, article.getId());
        args.putString(ARG_TITLE, article.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.publish_date_text) TextView mPublishDateText;
    @InjectView(R.id.publisher_text) TextView mPublisherText;
    @InjectView(R.id.article_body_text) TextView mArticleBodyText;
    @InjectView(R.id.article_description_text) TextView mArticleDescriptionText;
    @InjectView(R.id.tags_layout) FlowLayout mHashtagLayout;

    private ImagePagerAdapter mImageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoeuil.ttf");
        mArticleDescriptionText.setTypeface(font);

        font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
        mPublisherText.setTypeface(font);
        mPublishDateText.setTypeface(font);

        Bundle args = getArguments();
        fragmentTitle = args.getString(ARG_TITLE);

        initLoader();
    }

    private void initLoader(){
        onLoadBegins();
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.getArticle(getArguments().getInt(ARG_ID), new HttpHelper.RestCallback<Article>() {
            @Override
            public void success(Article article, Response response) {
                mArticle = article;
                onLoadSuccess();
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }
        });
    }

    @OnClick(R.id.publisher_text)
    public void openProfilePage() {
        Fragment fragment = CommunityUserFragment.newInstance(mArticle.getPublisher());

        BaseFragment parentFragment = (BaseFragment) getParentFragment();
        parentFragment.addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void onLoadSuccess() {

 /*       if (mArticle.getPictureUrl() == null){
            onLoadFinished();
            return;
        }*/

        if (getView() == null){
            return;
        }

        Fragment fragment = ImagePagerFragment.newInstance(mArticle.getPictureUrls());
        getChildFragmentManager().beginTransaction().add(R.id.imageFrame, fragment).commit();
        onLoadFinished();

        //mImageAdapter = new ImagePagerAdapter()

/*        HttpHelper.getInstance(getActivity()).getImageLoader().get(mArticle.getPictureUrl(), new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onLoadFinished();
            }
`
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                View view = getView();
                if (view != null) {
                    ImageView photo = (ImageView) view.findViewById(R.id.article_photo);
                    photo.setImageBitmap(imageContainer.getBitmap());
                    onLoadFinished();
                }
            }
        });*/
    }

    public void onLoadFinished(){
        updateText();
        super.onLoadFinished();
    }

    private void updateText(){

        mPublishDateText.setText(mArticle.getPublishedDateText());

        Publisher publisher = mArticle.getPublisher();
        if (publisher != null) {
            mPublisherText.setText(publisher.getFirstLastName());
        }

        mArticleBodyText.setText(Html.fromHtml(mArticle.getArticleBody()));
        mArticleDescriptionText.setText(mArticle.getSmallDescription());

        mHashtagLayout.removeAllViews();

        int padding = (int) getActivity().getResources().getDimension(R.dimen.hashtag_padding);

        if (mArticle.getHashTags() == null){
            return;
        }

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GretaTextPro-Bold.otf");

        for (HashTag hashTag: mArticle.getHashTags()){
            TextView hashTagView = new TextView(getActivity());
            hashTagView.setText(hashTag.toString());
            hashTagView.setTag(hashTag);
            hashTagView.setOnClickListener(this);
            hashTagView.setTextColor(getResources().getColor(R.color.textBlue));
            hashTagView.setTypeface(font);
            hashTagView.setPadding(padding, 0, padding, 0);

            mHashtagLayout.addView(hashTagView);
        }
    }

    @Override
    public void onClick(View v) {
        HashTag hashTag = (HashTag) v.getTag();
        List<Integer> hashTagFilters = new ArrayList<>();
        hashTagFilters.add(hashTag.getId());

        ArticleListFragment fragment = ArticleListFragment.newInstance(hashTagFilters);
        BaseFragment parentFragment = (BaseFragment) getParentFragment();
        parentFragment.addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
