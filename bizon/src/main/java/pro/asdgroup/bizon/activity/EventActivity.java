package pro.asdgroup.bizon.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.fragment.EventFragment;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Event;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 05.06.2015.
 */
public class EventActivity extends AppCompatActivity {

    public final static String ARG_EVENT_ID = "event_id";
    public final static String ARG_EVENT_NAME = "event_name";

    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);

        eventId = getIntent().getIntExtra(ARG_EVENT_ID, -1);

        if (eventId == -1){
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra(ARG_EVENT_NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        User.authorize(new AuthCallback());
    }

    private class AuthCallback extends HttpHelper.RestCallback<Profile>{

        @Override
        public void failure(Status restError) {
            finish();
        }

        @Override
        public void success(Profile profile, Response response) {
            User.currentUser().setProfile(profile);
            User.currentUser().setUserId(profile.getId());
            RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
            service.getEvent(eventId, new EventCallback());
        }
    }

    private class EventCallback extends HttpHelper.RestCallback<Event>{

        @Override
        public void failure(Status restError) {

        }

        @Override
        public void success(Event event, Response response) {
            Fragment fragment = EventFragment.newInstance(event);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, null)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackNavigationClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onBackNavigationClick();
    }

    private void onBackNavigationClick(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}
