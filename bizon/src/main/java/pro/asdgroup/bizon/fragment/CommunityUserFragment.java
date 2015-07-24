package pro.asdgroup.bizon.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.activity.PhotoActivity;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Publisher;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.view.DetailTextView;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/21/2015.
 */

//TODO: probably should be merged with "ProfileFragment"
public class CommunityUserFragment extends BaseFragment {

    private static final String ARG_PROFILE = "profile";

    @InjectView(R.id.name_text) DetailTextView mFirstNameText;
    @InjectView(R.id.mid_name_text) DetailTextView mMiddleNameText;
    @InjectView(R.id.surname_text) DetailTextView mLastNameText;
    @InjectView(R.id.about_user_text) DetailTextView mAboutText;
    @InjectView(R.id.city_text) DetailTextView mCityText;
    @InjectView(R.id.email_text) DetailTextView mEmailText;
    @InjectView(R.id.phone_text) DetailTextView mPhoneText;
    @InjectView(R.id.vk_text) DetailTextView mVkText;
    @InjectView(R.id.skype_text) DetailTextView mSkypeText;
    @InjectView(R.id.company_text) DetailTextView mCompanyText;
    @InjectView(R.id.tags_text) DetailTextView mHashTags;
    @InjectView(R.id.profile_photo) ImageView mProfileImage;
    @InjectView(R.id.expertArticlesBtn) Button mExpertArticlesButton;
    @InjectView(R.id.expertArticlesDivider) View mExpertArticlesDivider;

    public CommunityUserFragment() {
    }

    public static CommunityUserFragment newInstance(Profile profile) {
        CommunityUserFragment fragment = new CommunityUserFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PROFILE, profile);
        fragment.setArguments(args);

        return fragment;
    }

    public static CommunityUserFragment newInstance(Publisher publisher) {
        Profile profile = new Profile(publisher);
        return newInstance(profile);
    }

    private Profile mProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        mProfile = getArguments().getParcelable(ARG_PROFILE);

        initLoader();
    }

    private void initLoader() {
        onLoadBegins();
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.getUser(mProfile.getId(), new HttpHelper.RestCallback<Profile>() {
            @Override
            public void success(Profile profile, Response response) {
                mProfile = profile;
                onLoadSuccess();
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }
        });
    }

    private void onLoadSuccess() {
        updateUI(mProfile);
        onLoadFinished();
    }

    protected void updateUI(Profile userInfo) {

        if (getView() == null){
            return;
        }

        if (userInfo == null) {
            Toast.makeText(getActivity(), getString(R.string.error_no_data), Toast.LENGTH_SHORT).show();
            return;
        }

        mProfile = userInfo;

        mFirstNameText.setDetailText(userInfo.getFirstName());
        mLastNameText.setDetailText(userInfo.getLastName());

        if (userInfo.getMiddleName() == null || userInfo.getMiddleName().trim().isEmpty()){
            mMiddleNameText.setVisibility(View.GONE);
        } else {
            mMiddleNameText.setDetailText(userInfo.getMiddleName());
        }

        if (userInfo.getAbout() == null || userInfo.getAbout().trim().isEmpty()){
            mAboutText.setVisibility(View.GONE);
        } else {
            mAboutText.setDetailText(userInfo.getAbout());
        }

        if (userInfo.getCity() == null || userInfo.getCity().getName().trim().isEmpty()) {
            mCityText.setVisibility(View.GONE);
        } else {
            mCityText.setDetailText(userInfo.getCity().getName());
        }

        if (userInfo.getEmail() == null || userInfo.getEmail().trim().isEmpty()) {
            mEmailText.setVisibility(View.GONE);
        } else {
            mEmailText.setDetailText(userInfo.getEmail());
        }

        if (userInfo.getPhone() == null || userInfo.getPhone().trim().isEmpty()) {
            mPhoneText.setVisibility(View.GONE);
        } else {
            mPhoneText.setDetailText(userInfo.getPhone());
        }

        if (userInfo.getVk() == null || userInfo.getVk().trim().isEmpty()) {
            mVkText.setVisibility(View.GONE);
        } else {
            mVkText.setDetailText(userInfo.getVk());
        }

        if (userInfo.getSkype() == null || userInfo.getSkype().trim().isEmpty()) {
            mSkypeText.setVisibility(View.GONE);
        } else {
            mSkypeText.setDetailText(userInfo.getSkype());
        }

        if (userInfo.getCompanies().size() != 0) {
            mCompanyText.setDetailText(userInfo.getCompanyNames(true));
        } else {
            mCompanyText.setVisibility(View.GONE);
        }

        if (userInfo.getAvatarUrl() != null && (userInfo.getAvatarUrl().length() > 0)) {

            Picasso.with(getActivity())
                    .load(userInfo.getAvatarUrl())
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .into(mProfileImage);

            mProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PhotoActivity.class);
                    intent.putExtra(PhotoActivity.ARG_IMAGE, mProfile.getAvatarUrl());
                    startActivity(intent);
                }
            });
        }

        if (userInfo.getHashTags() != null && userInfo.getHashTags().size() != 0) {
            StringBuilder hashTags = new StringBuilder();
            for (HashTag hashTag : userInfo.getHashTags()) {
                if (hashTags.length() != 0) {
                    hashTags.append(" ");
                }

                hashTags.append("#");
                hashTags.append(hashTag.getName());
            }

            mHashTags.setDetailText(hashTags.toString());
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GretaTextPro-Bold.otf");
            mHashTags.setDetailTextTypeface(font);
        } else {
            mHashTags.setVisibility(View.GONE);
        }

        if ("EXPERT".equals(userInfo.getRole())){ //fixme hardcoded string
            mExpertArticlesButton.setVisibility(View.VISIBLE);
            mExpertArticlesDivider.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.vk_text)
    void openVkProfile() {
        String profileUrl = mVkText.getDetailText();
        if (profileUrl == null || profileUrl.isEmpty()){
            return;
        }

        if (!profileUrl.contains("http") && !profileUrl.contains("https")) {
            profileUrl = (new StringBuilder()).append("http://").append(profileUrl).toString();
        }
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(profileUrl)));
        }
        catch (ActivityNotFoundException activitynotfoundexception) {
            activitynotfoundexception.printStackTrace();
            //Toast.makeText(getActivity(), 0x7f060051, 0).show(); //TODO:
        }
    }

    @OnClick(R.id.email_text)
    void openEmailIntent() {
        String s = mEmailText.getDetailText();

        if (s.isEmpty()){
            return;
        }

        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.EMAIL", new String[]{s});
        startActivity(intent);
    }

    @OnClick(R.id.phone_text)
    void callProfileNumber() {
        String s = mPhoneText.getDetailText();

        if (s == null || s.isEmpty()){
            return;
        }

        startActivity(new Intent("android.intent.action.CALL",
                Uri.parse((new StringBuilder()).append("tel:").append(s).toString())));
    }

    @OnClick(R.id.company_text)
    void moveToCompanyFragment() {
        if (mProfile.getCompanies() == null || mProfile.getCompanies().size() == 0){
            return;
        }

        Fragment fragment;

        if (mProfile.getCompanies().size() == 1 ){
            fragment = CompanyFragment.newInstance(mProfile.getCompanies().get(0));
        } else {
            fragment = CompanyListFragment.newInstance(CompanyListFragment.VIEW_MODE,
                    mProfile.getCompanies());
        }

        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @OnClick(R.id.expertArticlesBtn)
    void onExpertArticlesClick(){
        Fragment fragment = ArticleListFragment.newInstance(mProfile.getId());
        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public String getTitle() {
        Profile profile = getArguments().getParcelable(ARG_PROFILE);

        return profile.getFirstLastName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }
}
