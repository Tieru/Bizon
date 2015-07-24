package pro.asdgroup.bizon.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.model.Article;
import pro.asdgroup.bizon.model.Publisher;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class ArticleAdapter extends BaseAdapter{

    private List<Article> mArticles;
    private Drawable mDefaultDrawable;

    public ArticleAdapter(List<Article> articles){
        mArticles = articles;
        initialize();
    }

    public ArticleAdapter(){
        mArticles = new ArrayList<>();
        initialize();
    }

    private void initialize(){
        mDefaultDrawable = BizonApp.getAppContext().getResources().getDrawable(R.drawable.no_image);
    }

    public List<Article> getArticlesList(){
        return mArticles;
    }

    public void addArticles(List<Article> articles){
        mArticles.addAll(articles);
        notifyDataSetChanged();
    }

    public void resetData(){
        mArticles.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public Article getItem(int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mArticles.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_article, parent, false);
            holder = new ViewHolder();
            holder.initialize(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article article = getItem(position);

        holder.titleText.setText(article.getName());
        holder.descriptionText.setText(article.getSmallDescription());
        holder.publishDateText.setText(article.getPublishedDateText());

        Publisher publisher = article.getPublisher();
        if (publisher != null) {
            String publisherText = publisher.getFirstLastName();
            holder.publisherNameText.setText(publisherText);
        }

        String picUrl = article.getPictureUrl();
        if (picUrl != null && !picUrl.isEmpty()) {
            Picasso.with(parent.getContext())
                    .load(article.getPictureUrl())
                    .placeholder(mDefaultDrawable)
                    .error(mDefaultDrawable)
                    .into(holder.articleImage);
        } else {
            holder.articleImage.setImageDrawable(mDefaultDrawable);
        }

        return convertView;
    }

    class ViewHolder {

        TextView titleText;
        TextView descriptionText;
        ImageView articleImage;
        TextView publishDateText;
        TextView publisherNameText;


        public void initialize(View view){
            articleImage    = (ImageView) view.findViewById(R.id.article_photo);
            titleText       = (TextView) view.findViewById(R.id.name_text);
            descriptionText = (TextView) view.findViewById(R.id.small_description_text);
            publishDateText = (TextView) view.findViewById(R.id.publish_date_text);
            publisherNameText = (TextView) view.findViewById(R.id.publisher_text);

            Typeface font = Typeface.createFromAsset(BizonApp.getAppContext().getAssets(), "fonts/segoeuil.ttf");
            descriptionText.setTypeface(font);

            font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
            publisherNameText.setTypeface(font);
            publishDateText.setTypeface(font);
        }
    }
}
