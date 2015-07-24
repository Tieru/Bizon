package pro.asdgroup.bizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vk.sdk.VKUIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.fragment.WelcomeFragment;

/**
 * Created by Tieru on 24.05.2015.
 */
public class LoginActivity extends AppCompatActivity implements MainActivityCallback{

    public final static String ARG_ACTIVITY_TYPE = "type";

    public final static int WELCOME_SCREEN_NAVIGATION = 0;
    public final static int LOGIN_SCREEN_NAVIGATION = 1;

    @InjectView(R.id.progress_bar) View mProgressBar;

    private int mNavType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_container);
        ButterKnife.inject(this);

        mNavType = getIntent().getIntExtra(ARG_ACTIVITY_TYPE, WELCOME_SCREEN_NAVIGATION);

        Fragment fragment = WelcomeFragment.newInstance(mNavType);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, fragment).commit();
    }

    @Override
    public void onUserStatusChange() {
        if (mNavType == WELCOME_SCREEN_NAVIGATION){
            startActivity(new Intent(this, MainActivity.class));
        } else {
            setResult(RESULT_OK);
        }

        finish();
    }

    @Override
    public void setActivityTitle(String title) {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onAuthRequest() {
        return false;
    }

    @Override
    public void onLoadBegins() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

}
