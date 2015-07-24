package pro.asdgroup.bizon.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.adapter.EventNewsAdapter;
import pro.asdgroup.bizon.adapter.listener.EndlessScrollListener;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Event;
import pro.asdgroup.bizon.model.EventNew;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;


/**
 * Created by Voronov Viacheslav on 11/05/15.
 */
public class EventNewsFragment extends BaseFragment {

    private static final String ARG_EVENT = "event";

    public static EventNewsFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        EventNewsFragment fragment = new EventNewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ListView mListView;
    private EventNewsAdapter mAdapter;
    private Event mEvent;
    RestHelper mRestService;
    private EventNewsLoaderCallback mLoaderCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setClickable(true);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        mEvent = (Event) bundle.getSerializable(ARG_EVENT);

        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setOnScrollListener(new ScrollListener());
        mAdapter = new EventNewsAdapter();
        mListView.setAdapter(mAdapter);
        mLoaderCallback = new EventNewsLoaderCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);

        Profile profile = User.currentUser().getProfile();
        if (profile != null && profile.getIsAdmin() == 1) {
            setHasOptionsMenu(true);
            registerForContextMenu(mListView);
        }

        onLoadBegins();
        loadEventNews(0);
    }

    private void loadEventNews(int page) {
        mRestService.getEventNews(mEvent.getId(), page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
    }

    private class EventNewsLoaderCallback extends HttpHelper.RestCallback<List<EventNew>> {

        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }

        @Override
        public void success(List<EventNew> data, Response response) {
            mAdapter.addNews(data);
            mAdapter.notifyDataSetChanged();
            onLoadFinished();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.news, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_event_add_news) {
            onNewsMenuClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onNewsMenuClick() {
        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.enf_add_news_dialog_title));
        alertDialog.setView(input);

        alertDialog.setPositiveButton(getString(R.string.enf_add_news_dialog_submit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newsMessage = input.getText().toString();
                        if (newsMessage.isEmpty()){
                            Toast.makeText(getActivity(), R.string.enf_add_news_dialog_empty_error, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sendNews(newsMessage);
                    }
                });

        alertDialog.setNegativeButton(getString(R.string.enf_add_news_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void sendNews(String newsMessage){
        onLoadBegins();
        mRestService.addEventNews(User.currentUser().getUserId(),
                mEvent.getId(),
                newsMessage,
                new HttpHelper.RestCallback<Status>() {
                    @Override
                    public void failure(Status restError) {
                        onLoadFinished();
                    }

                    @Override
                    public void success(Status status, Response response) {
                        Toast.makeText(getActivity(), R.string.enf_new_added_success, Toast.LENGTH_SHORT).show();

                        mAdapter.resetData();
                        loadEventNews(0);

                        onLoadFinished();
                    }
                });
    }

    public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.news_listitem, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.remove:
                removeNew(info);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void removeNew(AdapterView.AdapterContextMenuInfo info){
        EventNew news = mAdapter.getItem(info.position);


        onLoadBegins();
        mRestService.removeEventNews(User.currentUser().getUserId(),
                news.getId(),
                new HttpHelper.RestCallback<Status>() {
                    @Override
                    public void failure(Status restError) {
                        onLoadFinished();
                    }

                    @Override
                    public void success(Status status, Response response) {
                        Toast.makeText(getActivity(), R.string.enf_new_removed_success, Toast.LENGTH_SHORT).show();

                        mAdapter.resetData();
                        loadEventNews(0);
                        onLoadFinished();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHasOptionsMenu(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public String getTitle() {
        return mEvent.getName();
    }

    class ScrollListener extends EndlessScrollListener {

        @Override
        public void onLoadMore(int page) {
            loadEventNews(page);
        }
    }
}
