package pro.asdgroup.bizon.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.adapter.EventInvitationAdapter;
import pro.asdgroup.bizon.adapter.listener.EndlessScrollListener;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.loader.ContactsLoader;
import pro.asdgroup.bizon.model.ActionResult;
import pro.asdgroup.bizon.model.EventHtml;
import pro.asdgroup.bizon.model.InvitationContact;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;

/**
 * Created by Tieru on 04.06.2015.
 */
public class EventInvitationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<InvitationContact>> {

    public static final String ARG_EVENT_ID = "event_id";

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.contact_list) ListView mContactsList;
    @InjectView(R.id.send_btn) Button mSendButton;

    private RestHelper mRestService;
    private ProfileListLoadedCallback mLoaderCallback;
    private EventInvitationAdapter mAdapter;
    private ScrollListener mScrollListener;
    private int eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invitation);

        eventId = getIntent().getIntExtra(ARG_EVENT_ID, -1);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoaderCallback = new ProfileListLoadedCallback();
        mRestService = HttpHelper.getRestAdapter().create(RestHelper.class);
        mAdapter = new EventInvitationAdapter();
        mContactsList.setAdapter(mAdapter);
        mContactsList.setOnItemClickListener(this);
        mScrollListener = new ScrollListener();
        mContactsList.setOnScrollListener(mScrollListener);
        loadPage(0);

        SegmentedGroup radioGroup = (SegmentedGroup) findViewById(R.id.segmented_control);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.all_radio_button:
                        mAdapter.setDisplayType(EventInvitationAdapter.COMMUNITY_LIST_TYPE);
                        mContactsList.setOnScrollListener(mScrollListener);
                        break;
                    case R.id.my_radio_button:
                        mAdapter.setDisplayType(EventInvitationAdapter.USER_CONTACTS_LIST_TYPE);
                        mContactsList.setOnScrollListener(null);
                        break;
                }
            }
        });

        initContactLoader();
    }

    private void loadPage(int page){
        mRestService.getUserContacts(page, HttpHelper.DEFAULT_COUNT, mLoaderCallback);
    }

    private void initContactLoader(){
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mAdapter.getItem(i).type == EventInvitationAdapter.HEADER_TYPE){
            return;
        }

        mAdapter.onItemSelection(i);
    }

    @Override
    public Loader<List<InvitationContact>> onCreateLoader(int i, Bundle bundle) {
        return new ContactsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<InvitationContact>> loader, List<InvitationContact> invitationContacts) {
        mAdapter.setUserContacts(invitationContacts);
    }

    @Override
    public void onLoaderReset(Loader<List<InvitationContact>> loader) {

    }

    private class ProfileListLoadedCallback extends HttpHelper.RestCallback<List<InvitationContact>> {

        @Override
        public void failure(Status restError) {
        }

        @Override
        public void success(List<InvitationContact> data, Response response) {
            mAdapter.addInvitationContacts(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.send_btn)
    void onSendButtonClick(){
        List<InvitationContact> communityUsers = new ArrayList<>();
        List<InvitationContact> phoneContacts = new ArrayList<>();

        for (InvitationContact contact: mAdapter.getSelectedContacts()){
            if (contact.getId() != null && !contact.getId().isEmpty()){
                communityUsers.add(contact);
            } else {
                phoneContacts.add(contact);
            }
        }

        if (communityUsers.size() > 0){
            sendEmails(communityUsers);
        }

        if (phoneContacts.size() > 0){
            openEmailIntent(phoneContacts);
        }
    }

    private void sendEmails(List<InvitationContact> contacts){
        String[] iDs = new String[contacts.size()];
        for(int i = 0; i<contacts.size(); i++){
            iDs[i] = contacts.get(i).getId();
        }
        mRestService.eventInvite(User.currentUser().getUserId(), eventId, iDs,
                new HttpHelper.RestCallback<ActionResult>() {
                    @Override
                    public void success(ActionResult actionResult, Response response) {
                        Toast.makeText(EventInvitationActivity.this, R.string.ep_invitations_are_sent, Toast.LENGTH_SHORT).show();
                        mAdapter.clearSelected();
                    }

                    @Override
                    public void failure(Status restError) {
                        Toast.makeText(EventInvitationActivity.this, R.string.ep_invitations_arent_sent_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openEmailIntent(final List<InvitationContact> contacts){
        mRestService.getEventInviteText(eventId,
                new HttpHelper.RestCallback<EventHtml>() {
                    @Override
                    public void success(EventHtml result, Response response) {
                        String[] emails = new String[contacts.size()];
                        for(int i = 0; i <contacts.size(); i++){
                            emails[i] = contacts.get(i).getEmail();
                        }

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, emails);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.ep_invitation_email_subject));
                        intent.putExtra(Intent.EXTRA_TEXT, result.getHtmlString());

                        startActivity(Intent.createChooser(intent, getString(R.string.ep_invitation_email_intent_title)));
                        mAdapter.clearSelected();
                    }

                    @Override
                    public void failure(Status restError) {
                        Toast.makeText(EventInvitationActivity.this, R.string.ep_invitations_arent_sent_error, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    class ScrollListener extends EndlessScrollListener {

        @Override
        public void onLoadMore(int page) {
            loadPage(page);
        }
    }
}
