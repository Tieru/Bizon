package pro.asdgroup.bizon.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.model.FeedComment;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.util.HeaderRecyclerViewAdapter;
import pro.asdgroup.bizon.view.CircleImageView;

/**
 * Created by vvoronov on 08/07/15.
 */
public class FeedCommentsAdapter extends HeaderRecyclerViewAdapter<RecyclerView.ViewHolder> {


    private FeedEntry mFeedEntry;
    private List<FeedComment> mComments;
    private HeaderViewHolder mHeaderViewHolder;
    private FeedCommentsAdapterClicks mCallback;

    public FeedCommentsAdapter(FeedEntry feedEntry){
        mFeedEntry = feedEntry;
        if (mFeedEntry.getComments() != null) {
            mComments = mFeedEntry.getComments();
        } else {
            mComments = new ArrayList<>();
        }

        init();
    }

    public void setCallback(FeedCommentsAdapterClicks callback){
        mCallback = callback;
    }

    private void init(){
        setHasHeader(true);
    }

    public void setFeedEntry(FeedEntry entry){
        mFeedEntry = entry;

        if (entry.getComments() != null){
            if (mComments.size() <= entry.getComments().size()){
                mComments = entry.getComments();
                notifyItemRangeChanged(0, mComments.size() + 1);
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public void addItem(FeedComment comment){
        mComments.add(comment);
        notifyItemInserted(mComments.size() + getItemPositionOffset() - 1);

        mHeaderViewHolder.mCommentsCount.setText(String.valueOf(mComments.size()));
    }

    public void addItems(List<FeedComment> comments){
        mComments.addAll(comments);
    }

    public FeedComment getItem(int position){
        return mComments.get(position - getItemPositionOffset());
    }

    public void updateComment(FeedComment comment){
        int position = mComments.indexOf(comment) + getItemPositionOffset();
        notifyItemChanged(position);
    }

    public void removeItem(int position){
        mComments.remove(position - getItemPositionOffset());
        notifyItemRemoved(position);
    }

    public void onLoadingBegins(){
        if (mHeaderViewHolder != null) {
            mHeaderViewHolder.progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void onLoadingEnds(){
        if (mHeaderViewHolder != null) {
            mHeaderViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    public void updateLikeLayout(){
        mHeaderViewHolder.updateLikeLayout();
    }

    @Override
    public int getChildItemCount() {
        return mComments.size();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int type) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_feedentry_comment, viewGroup, false));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup, int type) {
        mHeaderViewHolder = new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_feed_entry_header, viewGroup, false));
        return mHeaderViewHolder;
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder vh, int position) {
        mHeaderViewHolder = (HeaderViewHolder)vh;
        mHeaderViewHolder.updateUI();
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder vh, int position) {
        ItemViewHolder h = (ItemViewHolder)vh;
        FeedComment comment = mComments.get(position - getItemPositionOffset());
        String avatar = comment.getAuthor().getAvatarUrl();
        if (avatar != null && !avatar.isEmpty()) {
            Picasso.with(h.context)
                    .load(avatar)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .into(h.profilePhoto);
        } else {
            Picasso.with(h.context).load(R.drawable.no_image).into(h.profilePhoto);
        }

        h.nameText.setText(comment.getAuthor().getFirstLastName());
        h.dateText.setText(DateFormat.getDateInstance().format(comment.getDate()));
        h.commentText.setText(comment.getText());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {//implements View.OnCreateContextMenuListener {

        @InjectView(R.id.profilePhoto) ImageView profilePhoto;
        @InjectView(R.id.nameText) TextView nameText;
        @InjectView(R.id.dateText) TextView dateText;
        @InjectView(R.id.commentText) TextView commentText;
        @InjectView(R.id.commentLayout) View commentLayout;
        View mView;
        Context context;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            mView = itemView;
            context = itemView.getContext();

            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/segoeuil.ttf");
            dateText.setTypeface(font);
            commentText.setTypeface(font);

            font = Typeface.createFromAsset(context.getAssets(), "fonts/GretaTextPro-Bold.otf");
            nameText.setTypeface(font);


            profilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        FeedComment comment = mComments.get(getAdapterPosition() - getItemPositionOffset());
                        mCallback.onProfileClick(comment.getAuthor());
                    }
                }
            });

            View.OnClickListener layoutListener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (getItem(getAdapterPosition()).getAuthor().equals(User.currentUser().getProfile())) {
                        setClickedItemPosition(getAdapterPosition());
                        mView.showContextMenu();
                    }
                }
            };
            itemView.setOnClickListener(layoutListener); //TODO: refactor this
            nameText.setOnClickListener(layoutListener);
            dateText.setOnClickListener(layoutListener);
            commentText.setOnClickListener(layoutListener);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.profilePhoto) ImageView mProfilePhoto;
        @InjectView(R.id.nameText)TextView mNameText;
        @InjectView(R.id.dateText)TextView mDateText;
        @InjectView(R.id.questionText)TextView mQuestionText;
        @InjectView(R.id.answerText)TextView mAnswerText;
        @InjectView(R.id.commentsCount)TextView mCommentsCount;
        @InjectView(R.id.senderText)TextView mSenderText;
        @InjectView(R.id.likesCount)TextView mLikesCount;
        @InjectView(R.id.image)ImageView mPostImage;
        @InjectView(R.id.likeIndicatorImage)ImageView mLikeIndicatorImage;
        @InjectView(R.id.likesLayout)View mLikesLayout;
        @InjectView(R.id.profileLayout) View mProfileLayout;
        @InjectView(R.id.progressBar) View progressBar;
        @InjectView(R.id.userPhotosLayout) LinearLayout mUserPhotosLayout;
        @InjectView(R.id.loadMoreCommentsButton) View mLoadMoreCommentsButton;
        @InjectView(R.id.commentTextCount) TextView mCommentTextCount;

        Context context;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            context = itemView.getContext();
            initUI(itemView);
        }

        private void initUI(View view){

            mCommentTextCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null){
                        mCallback.onLoadCommentsClick();
                    }
                }
            });

            mLikesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onLikeButtonClick();
                    }
                }
            });

            mProfileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onProfileClick(mFeedEntry.getAuthor());
                    }
                }
            });

            mUserPhotosLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onUserPhotosClick();
                    }
                }
            });

            mPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onPostImageClick();
                    }
                }
            });

            mSenderText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onSenderClick();
                    }
                }
            });

            mUserPhotosLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onLikedByClick();
                    }
                }
            });

            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/segoeuil.ttf");
            mDateText.setTypeface(font);
            mAnswerText.setTypeface(font);

            font = Typeface.createFromAsset(context.getAssets(), "fonts/GretaTextPro-Bold.otf");
            mNameText.setTypeface(font);

            mAnswerText.setTextIsSelectable(true);
            mAnswerText.setAutoLinkMask(Linkify.WEB_URLS);
        }


        private void updateUI(){
            String picUrl = mFeedEntry.getAuthor().getAvatarUrl();
            if (picUrl != null && !picUrl.isEmpty()) {
                Picasso.with(context)
                        .load(picUrl)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(mProfilePhoto);
            } else {
                Picasso.with(context)
                        .load(R.drawable.no_image)
                        .into(mProfilePhoto);
            }

            if (mComments.size() >= mFeedEntry.getCommentsCount()){
                mLoadMoreCommentsButton.setVisibility(View.GONE);
            } else {
                mCommentTextCount.setText(context.getString(R.string.feed_comments_label_count,
                        mComments.size(), mFeedEntry.getCommentsCount() - mComments.size()));
            }


            mNameText.setText(mFeedEntry.getAuthor().getFirstLastName());
            mDateText.setText(DateFormat.getDateInstance().format(mFeedEntry.getDate()));
            //mAnswerText.setText(Html.fromHtml(mFeedEntry.getText()));
            mAnswerText.setText(mFeedEntry.getText());
            mCommentsCount.setText(String.valueOf(mFeedEntry.getCommentsCount()));

            if (mFeedEntry.getDayQuestion() != null) {
                mQuestionText.setVisibility(View.VISIBLE);
                mQuestionText.setText(mFeedEntry.getDayQuestion().getText());
                if (mFeedEntry.getDayQuestion().getAuthorId() == null){
                    String sender = context.getString(R.string.app_name);
                    SpannableString content = new SpannableString(context.getString(R.string.feed_label_question_by) + " " + sender);
                    content.setSpan(new UnderlineSpan(), content.length() - sender.length(), content.length(), 0);
                    mSenderText.setText(content);
                }
            } else {
                mQuestionText.setVisibility(View.GONE);
                mSenderText.setText("");
            }

            if (mFeedEntry.getLikedBy() != null) {
                mLikesCount.setText(String.valueOf(mFeedEntry.getLikedBy().size()));

                Profile profile = User.currentUser().getProfile();
                if (profile != null && mFeedEntry.getLikedBy().contains(profile)){
                    Picasso.with(context).load(R.drawable.btn_posts_liked).into(mLikeIndicatorImage);
                } else {
                    Picasso.with(context).load(R.drawable.btn_posts_like).into(mLikeIndicatorImage);
                }

            } else {
                mLikesCount.setText(String.valueOf(0));
                Picasso.with(context).load(R.drawable.btn_posts_like).into(mLikeIndicatorImage);
            }

            String imageUrl = mFeedEntry.getPictureUrl();
            if (imageUrl != null && !imageUrl.isEmpty()){
                mPostImage.setVisibility(View.VISIBLE);
                Picasso.with(mPostImage.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(mPostImage);
            } else {
                mPostImage.setVisibility(View.GONE);
            }

            mUserPhotosLayout.removeAllViews();
            mUserPhotosLayout.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = dpToPx(25);
            params.width = dpToPx(25);

            if (mFeedEntry.getLikedBy() == null){
                return;
            }

            for (Profile profile: mFeedEntry.getLikedBy()){
                final ImageView profilePhoto = new CircleImageView(mUserPhotosLayout.getContext());
                profilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                profilePhoto.setLayoutParams(params);
                String avatar = profile.getAvatarUrl();
                if (avatar == null || avatar.isEmpty()){
                    continue;
                }

                Picasso.with(mUserPhotosLayout.getContext())
                        .load(avatar)
                        .into(profilePhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                mUserPhotosLayout.addView(profilePhoto);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        }

        void updateLikeLayout(){
            mLikesCount.setText(String.valueOf(mFeedEntry.getLikedBy().size()));
            int drawableRes = mFeedEntry.getLikedBy().contains(User.currentUser().getProfile())?
                    R.drawable.btn_posts_liked: R.drawable.btn_posts_like;
            Picasso.with(context)
                    .load(drawableRes)
                    .into(mLikeIndicatorImage);
        }
    }

    public int dpToPx(int dp) {
        Resources r = BizonApp.getAppContext().getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static class RecyclerContextMenuInfo implements ContextMenu.ContextMenuInfo {

        public RecyclerContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }

        final public int position;
        final public long id;
    }

    public interface FeedCommentsAdapterClicks {
        void onLikeButtonClick();
        void onProfileClick(Profile profile);
        void onUserPhotosClick();
        void onPostImageClick();
        void onSenderClick();
        void onLoadCommentsClick();
        void onLikedByClick();
    }

}
