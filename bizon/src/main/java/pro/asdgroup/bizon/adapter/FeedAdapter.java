package pro.asdgroup.bizon.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.ActionResult;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.util.TimeManager;
import retrofit.client.Response;

/**
 * Created by vvoronov on 03/07/15.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<FeedEntry> mFeedList;
    private Drawable mDefaultDrawable;
    private FeedAdapterClick mCallback;

    public FeedAdapter(){
        init();
    }

    public FeedAdapter(FeedAdapterClick callback){
        init();
        mCallback = callback;
    }

    private void init(){
        mFeedList = new ArrayList<>();
        mDefaultDrawable = BizonApp.getAppContext().getResources().getDrawable(R.drawable.no_image);
    }

    public void addItems(List<FeedEntry> entries){
        mFeedList.addAll(entries);
        notifyItemRangeInserted(mFeedList.size() - 1 - entries.size(), entries.size());
    }

    public void insertIntoStart(FeedEntry entry){
        mFeedList.add(0, entry);
        notifyItemInserted(0);
    }

    public List<FeedEntry> getItems(){
        return mFeedList;
    }

    public FeedEntry getItem(int position){
        return mFeedList.get(position);
    }

    public void updateItem(FeedEntry feedEntry){
        int index = mFeedList.indexOf(feedEntry);
        if (index == -1){
            return;
        }

        mFeedList.set(index, feedEntry);
        notifyItemChanged(index);
    }

    public void removeItem(FeedEntry feedEntry){
        int index = mFeedList.indexOf(feedEntry);
        if (index == -1){
            return;
        }

        mFeedList.remove(index);
        notifyItemRemoved(index);
    }

    public void reset(){
        mFeedList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_feed, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        FeedEntry entry = mFeedList.get(i);

        String picUrl = entry.getAuthor().getAvatarUrl();
        if (picUrl != null && !picUrl.isEmpty()) {
            Picasso.with(h.context)
                    .load(picUrl)
                    .placeholder(mDefaultDrawable)
                    .error(mDefaultDrawable)
                    .into(h.profilePhoto);
        } else {
            h.profilePhoto.setImageDrawable(mDefaultDrawable);
        }

        h.nameText.setText(entry.getAuthor().getFirstLastName());
        //h.dateText.setText(DateFormat.getDateInstance().format(entry.getDate()));
        h.dateText.setText(TimeManager.getTimeString(h.context, entry.getDate().getTime()));
        h.answerText.setText(entry.getText());
        h.commentsCount.setText(String.valueOf(entry.getCommentsCount()));

        if (entry.getDayQuestion() != null) {
            h.questionText.setVisibility(View.VISIBLE);
            h.questionText.setText(entry.getDayQuestion().getText());
            if (entry.getDayQuestion().getAuthorId() == null){
                String sender = h.context.getString(R.string.app_name);
                SpannableString content = new SpannableString(h.context.getString(R.string.feed_label_question_by) + " " + sender);
                content.setSpan(new UnderlineSpan(), content.length() - sender.length(), content.length(), 0);
                h.senderText.setText(content);
            }
        } else {
            h.questionText.setVisibility(View.GONE);
            h.senderText.setText("");
        }

        if (entry.getLikedBy() != null) {
            h.likesCount.setText(String.valueOf(entry.getLikedBy().size()));

            Profile profile = User.currentUser().getProfile();
            if (profile != null && entry.getLikedBy().contains(profile)){
                Picasso.with(h.context).load(R.drawable.btn_posts_liked).into(h.likeIndicatorImage);
            } else {
                Picasso.with(h.context).load(R.drawable.btn_posts_like).into(h.likeIndicatorImage);
            }

        } else {
            h.likesCount.setText(String.valueOf(0));
            Picasso.with(h.context).load(R.drawable.btn_posts_like).into(h.likeIndicatorImage);
        }

        String imageUrl = entry.getPictureUrl();
        if (imageUrl != null && !imageUrl.isEmpty()){
            h.image.setVisibility(View.VISIBLE);
            Picasso.with(h.image.getContext())
                    .load(imageUrl)
                    .placeholder(mDefaultDrawable)
                    .error(mDefaultDrawable)
                    .into(h.image);
        } else {
            h.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.profilePhoto) ImageView profilePhoto;
        @InjectView(R.id.nameText)TextView nameText;
        @InjectView(R.id.dateText)TextView dateText;
        @InjectView(R.id.questionText)TextView questionText;
        @InjectView(R.id.answerText)TextView answerText;
        @InjectView(R.id.commentsCount)TextView commentsCount;
        @InjectView(R.id.senderText)TextView senderText;
        @InjectView(R.id.likesCount)TextView likesCount;
        @InjectView(R.id.image)ImageView image;
        @InjectView(R.id.likeIndicatorImage)ImageView likeIndicatorImage;
        @InjectView(R.id.likesLayout)View likesLayout;

        Context context;

        public ViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();

            ButterKnife.inject(this, itemView);

            Typeface font = Typeface.createFromAsset(BizonApp.getAppContext().getAssets(), "fonts/segoeuil.ttf");
            dateText.setTypeface(font);
            answerText.setTypeface(font);

            font = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
            nameText.setTypeface(font);

            itemView.setOnClickListener(this);

            likesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLikeButtonClick(getAdapterPosition());
                }
            });
        }

        private void onLikeButtonClick(final int position){
            if (mCallback != null){
                if (!mCallback.onAuthRequest()){
                    return;
                }
            } else if (User.currentUser().getUserId() == null){
                Toast.makeText(context, context.getString(R.string.message_auth_required), Toast.LENGTH_SHORT).show();
                return;
            }

            final FeedEntry entry = mFeedList.get(position);
            if (entry.getLikedBy().contains(User.currentUser().getProfile())){
                entry.getLikedBy().remove(User.currentUser().getProfile());
                notifyItemChanged(position);
                unlikePost(entry, position);
            } else {
                entry.getLikedBy().add(User.currentUser().getProfile());
                notifyItemChanged(position);
                likePost(entry, position);
            }
        }

        private void unlikePost(final FeedEntry entry, final int position){
            RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
            service.unlikePost(entry.getId(), User.currentUser().getUserId(), new HttpHelper.RestCallback<ActionResult>() {
                @Override
                public void success(ActionResult response, Response response1) {
                }

                @Override
                public void failure(Status restError) {
                    entry.getLikedBy().add(User.currentUser().getProfile());
                    notifyItemChanged(position);
                }
            });
        }

        private void likePost(final FeedEntry entry, final int position){
            RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
            service.likePost(entry.getId(), User.currentUser().getUserId(), new HttpHelper.RestCallback<ActionResult>() {
                @Override
                public void success(ActionResult response, Response response1) {}

                @Override
                public void failure(Status restError) {
                    entry.getLikedBy().remove(User.currentUser().getProfile());
                    notifyItemChanged(position);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mCallback != null){
                mCallback.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface FeedAdapterClick{
        boolean onAuthRequest();
        void onItemClick(int position);
    }
}
