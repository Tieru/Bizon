package pro.asdgroup.bizon.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.activity.PhotoActivity;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.data.DataProvider;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.view.DetailTextView;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class ProfileFragment extends BaseFragment {

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public ProfileFragment() {
    }

    @InjectView(R.id.name_text) DetailTextView mNameText;
    @InjectView(R.id.mid_name_text) DetailTextView mMidNameText;
    @InjectView(R.id.surname_text) DetailTextView mSurnameText;
    @InjectView(R.id.about_user_text) DetailTextView mAboutText;
    @InjectView(R.id.city_text) DetailTextView mCityText;
    @InjectView(R.id.email_text) DetailTextView mEmailText;
    @InjectView(R.id.phone_text) DetailTextView mPhoneText;
    @InjectView(R.id.vk_text) DetailTextView mVkText;
    @InjectView(R.id.skype_text) DetailTextView mSkypeText;
    @InjectView(R.id.company_text) DetailTextView mCompanyText;
    @InjectView(R.id.tags_text) DetailTextView mTagsText;
    @InjectView(R.id.profile_photo) ImageView mProfileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Profile userProfile = User.currentUser().getProfile();
        if (userProfile == null) {
            initLoader();
        } else {
            updateUI(userProfile);
        }

        setHasOptionsMenu(true);

        createLogoutButton(view);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            onEditMenuClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void onEditMenuClick() {
        Fragment fragment = ProfileEditFragment.newInstance();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack("PROFILE")
                .commit();

        fragment.setTargetFragment(this, 10);
    }

    public void initLoader() {
        onLoadBegins();
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.getUser(User.currentUser().getUserId(), new HttpHelper.RestCallback<Profile>() {
            @Override
            public void success(Profile profile, Response response) {
                User.currentUser().setProfile(profile);

                ((MainActivityCallback) getActivity()).onUserStatusChange();

                updateUI(profile);
                onLoadFinished();
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }
        });
    }

    protected void updateUI(final Profile profile) {

        mNameText.setDetailText(profile.getFirstName());
        mMidNameText.setDetailText(profile.getMiddleName());
        mSurnameText.setDetailText(profile.getLastName());
        mAboutText.setDetailText(profile.getAbout());
        if (profile.getCity() != null) {
            mCityText.setDetailText(profile.getCity().getName());
        }
        mEmailText.setDetailText(profile.getEmail());
        mPhoneText.setDetailText(profile.getPhone());
        mVkText.setDetailText(profile.getVk());
        mSkypeText.setDetailText(profile.getSkype());

        if (profile.getCompanies() != null) {
            mCompanyText.setDetailText(profile.getCompanyNames(true));
        }

        if (profile.getAvatarUrl() != null && (profile.getAvatarUrl().length() > 0)){
            Picasso.with(getActivity()).load(profile.getAvatarUrl())
                    .error(R.drawable.no_image)
                    .into(mProfileImage);

            mProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PhotoActivity.class);
                    intent.putExtra(PhotoActivity.ARG_IMAGE, profile.getAvatarUrl());
                    startActivity(intent);
                }
            });
        }


        if (profile.getHashTags() != null) {
            StringBuilder hashTags = new StringBuilder();
            for (HashTag hashTag : profile.getHashTags()) {
                if (hashTags.length() != 0) {
                    hashTags.append(" ");
                }

                hashTags.append("#");
                hashTags.append(hashTag.getName());
            }

            mTagsText.setDetailText(hashTags.toString());
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GretaTextPro-Bold.otf");
            mTagsText.setDetailTextTypeface(font);
        }
    }

    public void moveToLoginScreen() {
        Fragment fragment = LoginFragment.newInstance();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void createLogoutButton(View view) {

        Button logoutButton = new Button(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logoutButton.setLayoutParams(params);

        logoutButton.setText(getActivity().getString(R.string.pp_logout));
        logoutButton.setOnClickListener(new OnLogoutClickListener());
        logoutButton.setTextColor(getResources().getColor(android.R.color.white));
        logoutButton.setBackgroundColor(getResources().getColor(R.color.dark_red));

        ((LinearLayout) view.findViewById(R.id.root_layout)).addView(logoutButton);
    }

    @OnClick(R.id.company_text)
    void moveToCompanyFragment() {
        Fragment fragment = CompanyListFragment.newInstance(CompanyListFragment.VIEW_MODE,
                User.currentUser().getProfile().getCompanies());

        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);

        fragment.setTargetFragment(this, 10);
    }

    @Override
    public String getTitle() {
        return getString(R.string.profile_title);
    }

    class OnLogoutClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            HashMap<String, String> params = new HashMap<>();

            params.put("user_id", User.currentUser().getUserId());
            params.put("push_token", User.getGcmToken());

            DataProvider dataProvider = new DataProvider();
            dataProvider.getAsync("logout", params);

            User.currentUser().setProfile(null);
            User.currentUser().setUserId(null);
            User.clearUserCredentials();

            if (getActivity() instanceof MainActivityCallback){
                ((MainActivityCallback)getActivity()).onUserLogout();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }
}
