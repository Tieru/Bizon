package pro.asdgroup.bizon.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.activity.MainActivity;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.activity.LoginActivity;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;

/**
 * Created by vvoronov on 29/06/15.
 */
public class WelcomeFragment extends Fragment {


    public static Fragment newInstance(int type){
        Fragment fragment = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LoginActivity.ARG_ACTIVITY_TYPE, type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @InjectView(R.id.skip_auth_button) ImageView mSkipButton;
    @InjectView(R.id.cancel_button) TextView mCancelButton;
    @InjectView(R.id.progressBar) View mProgressBar;
    @InjectView(R.id.signin_button) ImageView mSignInButton;
    @InjectView(R.id.signup_button) ImageView mSignUpButton;

    private int mNavigationType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Bundle args = getArguments();

        mNavigationType = args.getInt(LoginActivity.ARG_ACTIVITY_TYPE, LoginActivity.WELCOME_SCREEN_NAVIGATION);

        if (mNavigationType == LoginActivity.WELCOME_SCREEN_NAVIGATION) {
            HttpHelper.silienceMod = true;  //hide notifications in case of authorization failure
            User.authorize(new SignInCallback());
            Activity activity = getActivity();
            if (activity instanceof MainActivityCallback){
                ((MainActivityCallback) activity).onLoadBegins();
            }
        } else {
            displayNavigationButtons();
        }
    }

    @OnClick(R.id.signin_button)
    void onSignInClick(){
        openFragment(LoginFragment.newInstance());
    }

    @OnClick(R.id.signup_button)
    void onSignUpClick(){
        openFragment(RegisterFragment.newInstance());
    }

    @OnClick(R.id.skip_auth_button)
    void onSkipAuthClick(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.cancel_button)
    void onCancelClick(){
        getActivity().finish();
    }

    public void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .commit();
    }

    public class SignInCallback extends HttpHelper.RestCallback<Profile> {
        @Override
        public void failure(Status restError) {
            User.clearUserCredentials();
            HttpHelper.silienceMod = false;
            Activity activity = getActivity();
            if (activity instanceof MainActivityCallback){
                ((MainActivityCallback) activity).onLoadFinished();
            }
            displayNavigationButtons();
        }

        @Override
        public void success(Profile profile, Response response) {
            User.currentUser().setUserId(profile.getId());
            User.currentUser().setProfile(profile);
            HttpHelper.silienceMod = false;

            if (getView() == null){
                return;
            }

            Activity activity = getActivity();
            if (activity instanceof MainActivityCallback){
                ((MainActivityCallback) activity).onUserStatusChange();
            }
        }
    }

    private void displayNavigationButtons(){
        mProgressBar.setVisibility(View.GONE);
        if (mNavigationType == LoginActivity.WELCOME_SCREEN_NAVIGATION) {
            mSkipButton.setVisibility(View.VISIBLE);
        } else {
            mCancelButton.setVisibility(View.VISIBLE);
        }

        mSignInButton.setVisibility(View.VISIBLE);
        mSignUpButton.setVisibility(View.VISIBLE);
    }
}
