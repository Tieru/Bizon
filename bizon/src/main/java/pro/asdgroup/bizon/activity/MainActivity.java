package pro.asdgroup.bizon.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vk.sdk.VKUIHelper;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.adapter.DrawerAdapter;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.fragment.ArticleListFragment;
import pro.asdgroup.bizon.fragment.CommunityListFragment;
import pro.asdgroup.bizon.fragment.EventListFragment;
import pro.asdgroup.bizon.fragment.ExpertsFragment;
import pro.asdgroup.bizon.fragment.FeedFragment;
import pro.asdgroup.bizon.fragment.MainFragment;
import pro.asdgroup.bizon.fragment.ProfileFragment;
import pro.asdgroup.bizon.fragment.RegisterFragment;
import pro.asdgroup.bizon.gcm.RegisterGsmIdTask;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.util.KeyboardUtils;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MainActivityCallback, FragmentManager.OnBackStackChangedListener {

    public static final String ARG_BASE_FRAGMENT = "nav_signin";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final int FRAGMENT_LOGIN_INDEX = 0;
    public static final int FRAGMENT_MAIN_PAGE_INDEX = 1;
    public static final int FRAGMENT_ARTICLES_INDEX = 2;
    public static final int FRAGMENT_EVENTS_INDEX = 3;
    public static final int FRAGMENT_COMMUNITIES_INDEX = 4;
    public static final int FRAGMENT_EXPERTS_INDEX = 5;
    public static final int FRAGMENT_FEED_INDEX = 6;

    public static final int FRAGMENT_REGISTER = 11;

    public static final int FRAGMENT_EVENT = 31;

    public static final int LOGIN_ACTIVITY_REQUEST_CODE = 111;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer) ListView mLeftDrawerList;
    @InjectView(R.id.progress_bar) FrameLayout mProgressBar;
    @InjectView(R.id.container) FrameLayout mFragmentLayout;
    private TextView mTitleText;

    GoogleCloudMessaging gcm;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mNavigationDrawerAdapter;

    private boolean initialized;
    private CharSequence mTitle;

    private long mLastBackButtonClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initDrawer();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        initStartupFragment();

        initGsm();
    }

    private void initView() {
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitleText = (TextView) mToolbar.findViewById(R.id.tv_toolbar);

        mNavigationDrawerAdapter = new DrawerAdapter();
        mLeftDrawerList.setAdapter(mNavigationDrawerAdapter);
        mLeftDrawerList.setOnItemClickListener(this);
    }

    private void init(){
        initialized = true;
        onUserStatusChange();
    }

    private void updateDrawerItems(){
        mNavigationDrawerAdapter.notifyDataSetChanged();
    }

    private void initDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                KeyboardUtils.hideKeyboard(MainActivity.this);
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 || mDrawerLayout.isDrawerOpen(Gravity.START)) {
                    onBackPressed();
                } else {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initStartupFragment(){
        Bundle bundle = getIntent().getExtras();
        int arg_index = 1;

        if (bundle != null){
            arg_index = bundle.getInt(ARG_BASE_FRAGMENT);
        }

        switch (arg_index){
            case FRAGMENT_LOGIN_INDEX:
                openFragment(FRAGMENT_LOGIN_INDEX);
                break;
            case FRAGMENT_REGISTER:
                Fragment fragment = RegisterFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, null)
                        .commit();
                break;
            default:
                openFragment(FRAGMENT_MAIN_PAGE_INDEX);
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        openFragment(position);
        mLeftDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void openFragment(int position) {
        Fragment fragment;
        String tag = null;

        switch (position){
            case FRAGMENT_LOGIN_INDEX:
                String userId = User.currentUser().getUserId();
                if (userId == null){
                    startLoginActivity();
                    return;
                }

                fragment = new ProfileFragment();
                tag = "PROFIlE";
                break;
            case FRAGMENT_MAIN_PAGE_INDEX:
                fragment = MainFragment.newInstance();
                break;
            case FRAGMENT_ARTICLES_INDEX:
                fragment = ArticleListFragment.newInstance();
                break;
            case FRAGMENT_EVENTS_INDEX:
                fragment = EventListFragment.newInstance();
                break;
            case FRAGMENT_COMMUNITIES_INDEX:
                fragment = CommunityListFragment.newInstance();
                break;
            case FRAGMENT_EXPERTS_INDEX:
                fragment = ExpertsFragment.newInstance();
                break;
            case FRAGMENT_FEED_INDEX:
                fragment = FeedFragment.newInstance();
                break;
            default:
                fragment = null;
        }

        onLoadFinished(); //in case of one of callbacks hang on

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
        invalidateOptionsMenu();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    public void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.ARG_ACTIVITY_TYPE, LoginActivity.LOGIN_SCREEN_NAVIGATION);
        startActivityForResult(intent, LOGIN_ACTIVITY_REQUEST_CODE);
    }

    @Deprecated
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null){
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 2);
        }
    }


    @Override
    public void onBackPressed() {
        KeyboardUtils.hideKeyboard(this);

        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            onLoadFinished(); //TODO: this shouldn't be here
            getSupportFragmentManager().popBackStack();
        } else if (!KeyboardUtils.hideKeyboard(this)){
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - mLastBackButtonClickTime > 1500){
                Toast.makeText(this, R.string.message_click_once_more_to_exit, Toast.LENGTH_SHORT).show();
                mLastBackButtonClickTime = currentTime;
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onAuthRequest() {
        if (User.currentUser().getUserId() == null){
            startLoginActivity();
            return false;
        }

        return true;
    }

    @Override
    public void onUserStatusChange() {
        updateDrawerItems();
        openFragment(FRAGMENT_MAIN_PAGE_INDEX);
    }

    @Override
    public void setActivityTitle(String title) {
        mTitle = title;
        setTitle(mTitle);
        mTitleText.setText(title);
    }

    @Override
    public void onUserLogout() {
        onUserStatusChange();
    }

    @Override
    public void onLoadBegins() {
        mFragmentLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished() {
        mFragmentLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackStackChanged() {
        syncActionBarArrowState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        if (!initialized && User.currentUser().getProfile() != null){
            init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            updateDrawerItems();
        }
    }

    private void syncActionBarArrowState() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        mDrawerToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(backStackEntryCount > 0);
        mDrawerToggle.syncState();
    }

    private void initGsm(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            String regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                User.gcmToken = regid;
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
       /* if (resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            Toast.makeText(this, "This device is not supported. Error: " + resultCode, Toast.LENGTH_SHORT).show();
        } else*/  if (resultCode != ConnectionResult.SUCCESS) {
            //Toast.makeText(this, "This device is not supported. Error: " + resultCode, Toast.LENGTH_SHORT).show();
            Log.i("BizonGSMService", "This device is not supported.");
            return false;
        }
        return true;
    }

    private String getRegistrationId() {
        String registrationId = User.getGcmRegistrationIdFromPrefs();
        if (registrationId == null || registrationId.isEmpty()) {
            Log.i("BizonGSMService", "Registration not found.");
            return "";
        }

        int registeredVersion = User.getAppVersionFromPrefs();
        int currentVersion = BizonApp.getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i("BizonGSMService", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new RegisterGsmIdTask(this).execute(null, null, null);
    }
}
