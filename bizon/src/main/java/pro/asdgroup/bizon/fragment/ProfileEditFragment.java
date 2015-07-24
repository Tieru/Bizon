package pro.asdgroup.bizon.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.City;
import pro.asdgroup.bizon.model.HashTag;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.model.UserAvatar;
import pro.asdgroup.bizon.util.BitmapResizeTransformation;
import pro.asdgroup.bizon.util.DocumentExifTransformation;
import pro.asdgroup.bizon.view.DetailTextView;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/30/2015.
 */
public class ProfileEditFragment extends BaseFragment {

    private static final int GALLERY_RES_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    public static ProfileEditFragment newInstance() {
        return new ProfileEditFragment();
    }

    public ProfileEditFragment() {
    }

    Profile mProfile;
    private boolean imageIsLoaded;

    @InjectView(R.id.name_edit) EditText mNameEdit;
    @InjectView(R.id.mid_name_edit) EditText mMidNameEdit;
    @InjectView(R.id.surname_edit) EditText mSurnameEdit;
    @InjectView(R.id.email_edit) EditText mEmailEdit;
    @InjectView(R.id.phone_edit) EditText mPhoneEdit;
    @InjectView(R.id.about_user_edit) EditText mAboutEdit;
    @InjectView(R.id.vk_edit) EditText mVkEdit;
    @InjectView(R.id.skype_edit) EditText mSkypeEdit;
    @InjectView(R.id.city_edit) EditText mCityEdit;
    @InjectView(R.id.tags_edit) EditText mTagsEdit;
    @InjectView(R.id.company_text) DetailTextView mCompaniesText;
    @InjectView(R.id.profile_photo) ImageView mProfileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        mProfile = (Profile) User.currentUser().getProfile().clone();

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        getTargetFragment().setMenuVisibility(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void initUI() {
        if (getView() == null){
            return;
        }

        registerForContextMenu(mProfileImage);

        Profile profile = User.currentUser().getProfile();

        mNameEdit.setText(profile.getFirstName());
        mSurnameEdit.setText(profile.getLastName());
        mMidNameEdit.setText(profile.getMiddleName());
        mEmailEdit.setText(profile.getEmail());
        mAboutEdit.setText(profile.getAbout());
        mPhoneEdit.setText(profile.getPhone());
        mVkEdit.setText(profile.getVk());
        mSkypeEdit.setText(profile.getSkype());
        if (profile.getCity() != null) {
            mCityEdit.setText(profile.getCity().getName());
        }

        mCompaniesText.setDetailText(profile.getCompanyNames(true));

        if (profile.getHashTags() != null) {
            StringBuilder hashTags = new StringBuilder();
            for (HashTag hashTag : profile.getHashTags()) {
                if (hashTags.length() != 0) {
                    hashTags.append(" ");
                }

                hashTags.append("#");
                hashTags.append(hashTag.getName());
            }

            mTagsEdit.setText(hashTags.toString());
        }


        if (profile.getAvatarUrl() != null && (profile.getAvatarUrl().length() > 0)){
            Picasso.with(getActivity()).load(profile.getAvatarUrl())
                    .error(R.drawable.no_image)
                    .into(mProfileImage);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.profile_photo) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.photo_chooser, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_camera:
                launchCameraIntent();
                return true;
            case R.id.action_gallery:
                launchGalleryIntent();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @OnClick(R.id.save_btn)
    void onSaveButtonClick() {
        if (validateInputsAndSetValues(mProfile)) {
            updateProfile();
        }
    }

    @OnClick(R.id.profile_photo)
    void onProfilePhotoClick(View view) {
        getActivity().openContextMenu(view);
    }

    private void updateProfile() {
        onLoadBegins();
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.updateProfile(mProfile, new HttpHelper.RestCallback<Profile>() {
            @Override
            public void success(Profile profile, Response response) {
                User.currentUser().setProfile(mProfile);
                Toast.makeText(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show();
                if (imageIsLoaded) {
                    uploadProfilePhoto();
                } else {
                    ((ProfileFragment) getTargetFragment()).initLoader();
                    getFragmentManager().popBackStack();
                    onLoadFinished();
                }
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
            }
        });
    }

    @OnClick(R.id.company_text)
    void onCompanyItemClick() {
        CompanyListFragment fragment = CompanyListFragment.newInstance(CompanyListFragment.EDIT_MODE,
                mProfile.getCompanies());

        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private boolean validateInputsAndSetValues(Profile profile) {
        String firstName = mNameEdit.getText().toString();
        String lastName = mSurnameEdit.getText().toString();
        String email = mEmailEdit.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) { //TODO: validate separately
            Toast.makeText(getActivity(), getString(R.string.profile_update_validation_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        profile.setFirstName(capFirstLetter(firstName));
        profile.setLastName(capFirstLetter(lastName));
        profile.setEmail(email);

        profile.setMiddleName(capFirstLetter(mMidNameEdit.getText().toString()));
        profile.setAbout(mAboutEdit.getText().toString());
        profile.setPhone(mPhoneEdit.getText().toString());
        profile.setVk(mVkEdit.getText().toString());
        profile.setSkype(mSkypeEdit.getText().toString());
        City city = new City();
        city.setName(mCityEdit.getText().toString());
        profile.setCity(city);

        List<HashTag> hashTags = new ArrayList<>();
        String hashTagsString = mTagsEdit.getText().toString();
        String[] hashTagArray = hashTagsString.split("#");
        for (String hashTagValue : hashTagArray) {
            if (hashTagValue.trim().isEmpty()){
                continue;
            }
            HashTag tag = new HashTag();
            tag.setName(hashTagValue.replace("#", ""));
            hashTags.add(tag);
        }

        profile.setHashTags(hashTags);

        return true;
    }

    private String capFirstLetter(String value){
        if (value == null || value.length() < 1){
            return "";
        }
        StringBuilder rackingSystemSb = new StringBuilder(value);
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        return rackingSystemSb.toString();
    }

    public void launchGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.pp_choose_image)), GALLERY_RES_CODE);
    }

    public void launchCameraIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case GALLERY_RES_CODE:
                    try {
                        processGalleryImage(intent);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.photo_couldnt_open_photo, Toast.LENGTH_SHORT).show();
                    }
                    return;
                case REQUEST_IMAGE_CAPTURE:
                    processCameraImage(intent);
                    return;
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void processCameraImage(Intent intent){
        Uri uri = intent.getData();
        Picasso.with(getActivity()).load(uri)
                .transform(new BitmapResizeTransformation())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(mProfileImage);
        imageIsLoaded = true;
//        Bitmap bitmap = ((BitmapDrawable)mProfileImage.getDrawable()).getBitmap();
//        processImage(bitmap);
    }

    private void processGalleryImage(Intent intent) throws FileNotFoundException {
        Uri uri = intent.getData();
        Picasso.with(getActivity()).load(uri)
                .placeholder(R.drawable.no_image)
                .transform(new DocumentExifTransformation(getActivity(), uri))
                .transform(new BitmapResizeTransformation())
                .error(R.drawable.no_image)
                .into(mProfileImage);
        imageIsLoaded = true;

//        Bitmap bitmap = ((BitmapDrawable)mProfileImage.getDrawable()).getBitmap();
//        processImage(bitmap);
    }

    private String getEncodedImage() {
        Bitmap bitmap = ((BitmapDrawable)mProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, byteStream);
        byte[] byteArray = byteStream.toByteArray();
        return Base64.encodeToString(byteArray, 0);
    }

    private void uploadProfilePhoto(){
        UserAvatar avatar = new UserAvatar();
        avatar.setId(User.currentUser().getUserId());
        String encodedImage = getEncodedImage();
        avatar.setAvatar(encodedImage);
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.uploadAvatar(avatar,
                new HttpHelper.RestCallback<Response>() {
                    @Override
                    public void success(Response status, Response response) {
                        ((ProfileFragment) getTargetFragment()).initLoader();

                        getFragmentManager().popBackStack();
                        onLoadFinished();
                    }

                    @Override
                    public void failure(Status restError) {
                        getFragmentManager().popBackStack();
                        onLoadFinished();
                        Toast.makeText(getActivity(), getString(R.string.photo_wasnt_uploaded), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateCompanyText(){
        mCompaniesText.setDetailText(User.currentUser().getProfile().getCompanyNames(true));
    }

    @Override
    public void onStop() {
        super.onStop();
        getTargetFragment().setMenuVisibility(true);

        ((ProfileFragment)getTargetFragment()).updateUI(User.currentUser().getProfile());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @Override
    public String getTitle() {
        return getString(R.string.profile_edit_title);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null){
            ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 2);
        }
    }
}
