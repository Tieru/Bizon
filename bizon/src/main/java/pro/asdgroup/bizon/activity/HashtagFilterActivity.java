package pro.asdgroup.bizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.adapter.HashTagListAdapter;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/24/2015.
 */
public class HashtagFilterActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public final static String ARG_ACTIVE_HASHTAGS = "hashtags";

    private HashTagListAdapter mAdapter;

    @InjectView(R.id.hashtags_list) ListView mListView;
    @InjectView(R.id.progress_bar) ProgressBar mProgressBar;
    @InjectView(R.id.toolbar) Toolbar mToolbar;

    private List<Integer> mSelectedHashtags;
    private List<HashTagCheck> mHashtags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hashtag_filters);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mSelectedHashtags = bundle.getIntegerArrayList(ARG_ACTIVE_HASHTAGS);
        } else {
            mSelectedHashtags = new ArrayList<>();
        }

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLoader();
    }


    private void initLoader() {

        mProgressBar.setVisibility(View.VISIBLE);
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.getHashtags(new HttpHelper.RestCallback<List<HashTag>>() {
            @Override
            public void success(List<HashTag> hashTags, Response response) {
                mHashtags = convertToCheckClass(hashTags);
                mAdapter = new HashTagListAdapter(mHashtags);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(HashtagFilterActivity.this);
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(Status restError) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashTagCheck item = mAdapter.getItem(position);
        item.checked = !item.checked;

        mAdapter.notifyDataSetChanged();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.apply_button) {
            applyHashTags();
            Intent intent = this.getIntent();
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(ARG_ACTIVE_HASHTAGS, (ArrayList) mSelectedHashtags);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void applyHashTags() {
        if (mAdapter == null) {
            return;
        }

        Set<Integer> hashTags = new HashSet<>();

        for (HashTagCheck hashtag : mAdapter.getDataSet()) {
            if (hashtag.checked) {
                hashTags.add(hashtag.hashTag.getId());
            }
        }

        mSelectedHashtags = new ArrayList<>(hashTags);

        Toast.makeText(this, getString(R.string.msg_filters_applied), Toast.LENGTH_SHORT).show();
    }

    public class HashTagCheck {
        public HashTag hashTag;
        public boolean checked = false;

        public HashTagCheck(HashTag hashTag) {
            this.hashTag = hashTag;
        }
    }

    private List<HashTagCheck> convertToCheckClass(List<HashTag> hashTags) {
        List<HashTagCheck> items = new ArrayList<>();

        for (HashTag hashTag : hashTags) {
            HashTagCheck hashTagCheck = new HashTagCheck(hashTag);
            if (mSelectedHashtags != null) {
                hashTagCheck.checked = mSelectedHashtags.contains(hashTag.getId());
            }
            items.add(hashTagCheck);
        }

        return items;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hashtags, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clean:
                cleanSelection();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cleanSelection(){
        if (mSelectedHashtags != null) {
            mSelectedHashtags.clear();
        }

        for (HashTagCheck hashTag: mHashtags){
            hashTag.checked = false;
        }

        mAdapter.notifyDataSetChanged();
    }
}
