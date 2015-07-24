package pro.asdgroup.bizon.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.activity.CommentEditActivity;
import pro.asdgroup.bizon.adapter.QuestionAdapter;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.DayQuestion;
import pro.asdgroup.bizon.model.FeedEntry;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.model.dto.PostDTO;
import pro.asdgroup.bizon.util.RecyclerViewClicks;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 06.07.2015.
 */
public class QuestionListFragment extends BaseFragment {

    private final static int REQ_CODE_NEW_POST = 100;

    @InjectView(R.id.list) RecyclerView mList;
    private QuestionAdapter mAdapter;
    private RestHelper mRestService;
    private QuestionLoaderCallback mLoaderCallback;

    private boolean mMoreQuestionsButtonEnabled = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);
        mAdapter = new QuestionAdapter();
        mList.setAdapter(mAdapter);
        mLoaderCallback = new QuestionLoaderCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);

        mList.addOnItemTouchListener(new RecyclerViewClicks(mList, new RecyclerViewClicks.ClickListener(){

            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), CommentEditActivity.class);
                intent.putExtra(CommentEditActivity.ARG_TEXT_LIMIT, CommentEditActivity.POST_MAX_CHARS);
                intent.putExtra(CommentEditActivity.ARG_HAS_ADD_CONTENT, true);
                intent.putExtra(CommentEditActivity.ARG_QUESTION_OF_DAY, mAdapter.getItem(position));
                startActivityForResult(intent, REQ_CODE_NEW_POST);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        setHasOptionsMenu(true);

        initQuestionList();

        onLoadBegins();
    }

    private void initQuestionList(){
        mRestService.getDailyQuestions(User.currentUser().getUserId(), new HttpHelper.RestCallback<List<DayQuestion>>() {

            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }

            @Override
            public void success(List<DayQuestion> data, Response response) {
                if (getView() == null) {
                    return;
                }
                onLoadFinished();
                mAdapter.addItems(data);
            }
        });
    }

    private void loadQuestions(){
        String[] ids = new String[mAdapter.getItemCount()];
        for (int i = 0; i < ids.length; i++){
            ids[i] = mAdapter.getItem(i).getId();
        }
        HttpHelper.silienceMod = true;
        mRestService.getDailyQuestion(User.currentUser().getUserId(), ids, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_NEW_POST && resultCode == Activity.RESULT_OK){
            createNewPost(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createNewPost(Intent intent){
        String text = intent.getStringExtra(CommentEditActivity.ARG_TEXT);
        String image = intent.getStringExtra(CommentEditActivity.ARG_ADD_CONTENT);
        final DayQuestion question = (DayQuestion) intent.getSerializableExtra(CommentEditActivity.ARG_QUESTION_OF_DAY);
        final FeedEntry entry = new FeedEntry();
        entry.setText(text);
        if (image != null && !image.isEmpty()){
            entry.setPictureUrl(image);
        }
        entry.setAuthor(User.currentUser().getProfile());
        entry.setDayQuestion(question);

        onLoadBegins();
        mRestService.createPost(entry, new HttpHelper.RestCallback<PostDTO>() {

            @Override
            public void success(PostDTO feedEntry, Response response) {
                onLoadFinished();
                entry.setDayQuestion(question);
                entry.setId(feedEntry.getId());
                entry.setText(feedEntry.getText());
                entry.setPictureUrl(feedEntry.getPictureUrl());
                entry.setCity(feedEntry.getCity());

                Fragment fragment = FeedEntryFragment.newInstance(entry);
                addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
                Toast.makeText(getActivity(), R.string.feed_post_create_message_error_send, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.question, menu);

        if (!mMoreQuestionsButtonEnabled){
            menu.getItem(0).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.questions_get_more){
            loadQuestions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class QuestionLoaderCallback extends HttpHelper.RestCallback<DayQuestion> {

        @Override
        public void failure(Status restError) {
            Toast.makeText(getActivity(), R.string.questions_message_no_more_questions, Toast.LENGTH_SHORT).show();
            mMoreQuestionsButtonEnabled = false;
            getActivity().invalidateOptionsMenu();
            HttpHelper.silienceMod = false;
        }

        @Override
        public void success(DayQuestion data, Response response) {
            if (getView() == null) {
                return;
            }
            mAdapter.addItem(data);
            mList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            HttpHelper.silienceMod = false;
        }
    }

    public static Fragment newInstance(){
        return new QuestionListFragment();
    }

    @Override
    public String getTitle() {
        return BizonApp.getAppContext().getString(R.string.questions_title);
    }

}
