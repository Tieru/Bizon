package pro.asdgroup.bizon.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.activity.CommentEditActivity;
import pro.asdgroup.bizon.activity.PhotoActivity;
import pro.asdgroup.bizon.adapter.FeedCommentsAdapter;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.events.OnPostRemoved;
import pro.asdgroup.bizon.events.OnPostUpdatedEvent;
import pro.asdgroup.bizon.model.ActionResult;
import pro.asdgroup.bizon.model.FeedComment;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.model.dto.PostDTO;
import pro.asdgroup.bizon.util.KeyboardUtils;
import pro.asdgroup.bizon.view.ContextMenuRecyclerView;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 08.07.2015.
 */
public class FeedEntryFragment extends BaseFragment {

    private final static String ARG_ENTRY = "feed_entry";
    private final static int REQ_CODE_COMMENT_EDIT = 101;
    private final static int REQ_CODE_POST_EDIT = 102;


    @InjectView(R.id.commentsList) RecyclerView mList;
    @InjectView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeToRefreshLayout;

    @InjectView(R.id.messageEdit) EditText mMessageEdit;

    private FeedEntry mFeedEntry;
    private FeedCommentsAdapter mAdapter;
    RestHelper mRestService;

    private FeedComment mCurrentlyEditedComment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);

        mFeedEntry = (FeedEntry) getArguments().getSerializable(ARG_ENTRY);
        mAdapter = new FeedCommentsAdapter(mFeedEntry);
        mList.setAdapter(mAdapter);

        setAdapterClickListener();

        loadPost();

        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        setGlobalLayoutListener(view);
        registerForContextMenu(mList);
        setHasOptionsMenu(true);
    }

    private void loadPost(){
        onLoadBegins();

        mRestService.getPost(mFeedEntry.getId(), new HttpHelper.RestCallback<FeedEntry>() {
            @Override
            public void success(FeedEntry feedEntry, Response response) {
                mFeedEntry = feedEntry;
                if (getView() == null) {
                    return;
                }

                mAdapter.setFeedEntry(feedEntry);
                onLoadFinished();
            }

            @Override
            public void failure(Status restError) {
                if (getView() == null) {
                    return;
                }
                onLoadFinished();
            }
        });
    }

    private void refreshItems(){
        mAdapter.onLoadingBegins();
        loadPost();
    }

    private void setAdapterClickListener(){
        mAdapter.setCallback(new FeedCommentsAdapter.FeedCommentsAdapterClicks() {
            @Override
            public void onLikeButtonClick() {
                FeedEntryFragment.this.onLikeButtonClick();
            }

            @Override
            public void onProfileClick(Profile profile) {
                addFragment(CommunityUserFragment.newInstance(profile), true,
                        R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void onUserPhotosClick() {
                Fragment fragment = CommunityListFragment.newInstance(mFeedEntry.getLikedBy());
                addFragment(fragment, true,
                        R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void onPostImageClick() {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra(PhotoActivity.ARG_IMAGE, mFeedEntry.getPictureUrl());
                startActivity(intent);
            }

            @Override
            public void onSenderClick() {
                addFragment(QuestionListFragment.newInstance(), true, R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void onLoadCommentsClick() {
                loadComments();
            }

            @Override
            public void onLikedByClick() {
                openCommunityListByLikes();
            }
        });
    }

    private void onLikeButtonClick(){
        Activity activity = getActivity();
        if (activity instanceof MainActivityCallback){
            if (!((MainActivityCallback) activity).onAuthRequest()){
                return;
            }
        }

        if (mFeedEntry.getLikedBy().contains(User.currentUser().getProfile())){
            mFeedEntry.getLikedBy().remove(User.currentUser().getProfile());
            mAdapter.updateLikeLayout();
            unlikePost();
        } else {
            mFeedEntry.getLikedBy().add(User.currentUser().getProfile());
            mAdapter.updateLikeLayout();
            likePost();
        }
    }

    private void unlikePost(){
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.unlikePost(mFeedEntry.getId(), User.currentUser().getUserId(), new HttpHelper.RestCallback<ActionResult>() {
            @Override
            public void success(ActionResult response, Response response1) {
                EventBus.getDefault().post(new OnPostUpdatedEvent(mFeedEntry));
            }

            @Override
            public void failure(Status restError) {
                mFeedEntry.getLikedBy().add(User.currentUser().getProfile());
                mAdapter.updateLikeLayout();
            }
        });
    }

    private void likePost(){
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.likePost(mFeedEntry.getId(), User.currentUser().getUserId(), new HttpHelper.RestCallback<ActionResult>() {
            @Override
            public void success(ActionResult response, Response response1) {
                EventBus.getDefault().post(new OnPostUpdatedEvent(mFeedEntry));
            }

            @Override
            public void failure(Status restError) {
                mFeedEntry.getLikedBy().remove(User.currentUser().getProfile());
                mAdapter.updateLikeLayout();
            }
        });
    }

    @Override
    public void onLoadBegins() {
        mAdapter.onLoadingBegins();
    }

    @Override
    public void onLoadFinished() {
        mSwipeToRefreshLayout.setRefreshing(false);
        mAdapter.onLoadingEnds();
    }

    //This listener is used to scroll to the last ListView position on keyboard open
    public void setGlobalLayoutListener(final View view){
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    mList.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });
    }

    @OnClick(R.id.messageEditLayout)
    void onMessageLayoutClick(){
        mMessageEdit.requestFocus();
    }

    @OnClick(R.id.sendButton)
    void onSendMessageButtonClick(){
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.commentPost(mFeedEntry.getId(), mMessageEdit.getText().toString(), User.currentUser().getUserId(),
                new HttpHelper.RestCallback<FeedComment>() {
                    @Override
                    public void success(FeedComment comment, Response response) {
                        KeyboardUtils.hideKeyboard(getActivity());
                        mFeedEntry.incCommentsCount(1);
                        mAdapter.addItem(comment);
                        mMessageEdit.setText("");
                        mList.scrollToPosition(mAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void failure(Status restError) {
                        Toast.makeText(getActivity(), R.string.feed_comments_message_error_send, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.comment, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerViewContextMenuInfo info
                = (ContextMenuRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()) {
            case R.id.action_remove:
                removeComment(info);
                return true;
            case R.id.action_edit:
                editComment(info);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void removeComment(final ContextMenuRecyclerView.RecyclerViewContextMenuInfo info){
        onLoadBegins();
        final FeedComment comment = mAdapter.getItem(info.position);
        mRestService.removePostComment(mFeedEntry.getId(), User.currentUser().getUserId(), comment.getId(),
                new HttpHelper.RestCallback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {

                        if (getView() == null) {
                            return;
                        }
                        mFeedEntry.incCommentsCount(-1);
                        mAdapter.removeItem(info.position);
                        EventBus.getDefault().post(new OnPostUpdatedEvent(mFeedEntry));
                        onLoadFinished();
                    }

                    @Override
                    public void failure(Status restError) {
                        if (getView() == null) {
                            return;
                        }
                        onLoadFinished();
                    }
                });
    }

    private void editComment(ContextMenuRecyclerView.RecyclerViewContextMenuInfo info){
        mCurrentlyEditedComment = mAdapter.getItem(info.position);
        Intent intent = new Intent(getActivity(), CommentEditActivity.class);
        intent.putExtra(CommentEditActivity.ARG_TEXT, mCurrentlyEditedComment.getText());
        startActivityForResult(intent, REQ_CODE_COMMENT_EDIT);
    }

    private void updateComment(String text){
        final String oldText = mCurrentlyEditedComment.getText();
        mCurrentlyEditedComment.setText(text);
        mAdapter.updateComment(mCurrentlyEditedComment);
        mRestService.editPostComment(mFeedEntry.getId(), text, User.currentUser().getUserId(), mCurrentlyEditedComment.getId(),
                new HttpHelper.RestCallback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {

                        if (getView() == null) {
                            return;
                        }
                        mCurrentlyEditedComment = null;
                        EventBus.getDefault().post(new OnPostUpdatedEvent(mFeedEntry));
                        onLoadFinished();
                    }

                    @Override
                    public void failure(Status restError) {
                        if (getView() == null) {
                            return;
                        }
                        mCurrentlyEditedComment.setText(oldText);
                        mAdapter.updateComment(mCurrentlyEditedComment);
                        mCurrentlyEditedComment = null;
                        onLoadFinished();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQ_CODE_COMMENT_EDIT){
                String text = data.getStringExtra(CommentEditActivity.ARG_TEXT);
                updateComment(text);
            } else if (requestCode == REQ_CODE_POST_EDIT){
                updatePost(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadComments(){
        final int itemCount = mAdapter.getChildItemCount();
        mRestService.getPostComments(mFeedEntry.getId(), 5, itemCount,
                new HttpHelper.RestCallback<List<FeedComment>>() {
                    @Override
                    public void success(List<FeedComment> comments, Response response1) {

                        if (getView() == null) {
                            return;
                        }


                        mFeedEntry.getComments().addAll(0, comments);
                        mAdapter.notifyItemRangeInserted(0, comments.size() + 1); //header + item count
                        mList.scrollToPosition(mAdapter.getChildItemCount());
                        onLoadFinished();
                    }

                    @Override
                    public void failure(Status restError) {
                        if (getView() == null) {
                            return;
                        }

                        Toast.makeText(getActivity(), getString(R.string.feed_comments_message_error_load), Toast.LENGTH_SHORT).show();
                        onLoadFinished();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feedpost, menu);

        if (!mFeedEntry.getAuthor().equals(User.currentUser().getProfile())){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_remove:
                removePost();
                return true;
            case R.id.action_edit:
                editPost();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removePost(){
        onLoadBegins();
        mRestService.removePost(mFeedEntry.getId(), User.currentUser().getUserId(), new HttpHelper.RestCallback<ActionResult>() {
            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }

            @Override
            public void success(ActionResult actionResult, Response response) {
                EventBus.getDefault().post(new OnPostRemoved(mFeedEntry));

                if (getView() == null) {
                    return;
                }

                onLoadFinished();
                popBackStack();
            }
        });
    }


    private void editPost(){
        Intent intent = new Intent(getActivity(), CommentEditActivity.class);
        intent.putExtra(CommentEditActivity.ARG_TEXT, mFeedEntry.getText());
        intent.putExtra(CommentEditActivity.ARG_QUESTION_OF_DAY, mFeedEntry.getDayQuestion());
        intent.putExtra(CommentEditActivity.ARG_TEXT_LIMIT, CommentEditActivity.POST_MAX_CHARS);
        intent.putExtra(CommentEditActivity.ARG_ADD_CONTENT, mFeedEntry.getPictureUrl());
        intent.putExtra(CommentEditActivity.ARG_HAS_ADD_CONTENT, true);
        startActivityForResult(intent, REQ_CODE_POST_EDIT);
    }

    private void updatePost(Intent intent){
        mFeedEntry.setText(intent.getStringExtra(CommentEditActivity.ARG_TEXT));
        String postImage = intent.getStringExtra(CommentEditActivity.ARG_ADD_CONTENT);
        if (postImage != null && !postImage.isEmpty()) {
            mFeedEntry.setPictureUrl(postImage);
        } else {
            mFeedEntry.setPictureUrl(null);
        }

        mFeedEntry.setCity(User.currentUser().getProfile().getCity().getName());

        onLoadBegins();
        mRestService.editPost(mFeedEntry, new HttpHelper.RestCallback<PostDTO>() {
                    @Override
                    public void success(PostDTO response, Response response1) {
                        if (getView() == null) {
                            return;
                        }
                        mFeedEntry.setText(response.getText());
                        mFeedEntry.setPictureUrl(response.getPictureUrl());
                        mAdapter.notifyItemChanged(0);
                        EventBus.getDefault().post(new OnPostUpdatedEvent(mFeedEntry));
                        onLoadFinished();
                    }

                    @Override
                    public void failure(Status restError) {
                        onLoadFinished();
                    }
                });
    }

    private void openCommunityListByLikes(){
        //TODO:
        Fragment fragment = CommunityListFragment.newInstance(mFeedEntry.getLikedBy());
        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static Fragment newInstance(FeedEntry entry){
        Fragment fragment = new FeedEntryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, entry);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public String getTitle() {
        return BizonApp.getAppContext().getString(R.string.feed_entry_title);
    }
}
