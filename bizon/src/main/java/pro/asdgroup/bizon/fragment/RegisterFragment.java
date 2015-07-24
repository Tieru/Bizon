package pro.asdgroup.bizon.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.adapter.autocomplete.CompanyAutoCompleteAdapter;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Company;
import pro.asdgroup.bizon.model.Profile;
import pro.asdgroup.bizon.model.Status;
import pro.asdgroup.bizon.model.UserAvatar;
import pro.asdgroup.bizon.util.BitmapResizeTransformation;
import pro.asdgroup.bizon.util.DocumentExifTransformation;
import pro.asdgroup.bizon.view.DelayAutoCompleteTextView;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class RegisterFragment extends BaseFragment {

    private static final String ARG_PROFILE = "profile";
    private static final int GALLERY_RES_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    public static RegisterFragment newInstance(Profile profile){
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROFILE, profile);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterFragment() {
    }

    @InjectView(R.id.name_edit) EditText mNameEdit;
    @InjectView(R.id.mid_name_edit) EditText mMidNameEdit;
    @InjectView(R.id.surname_edit) EditText mSurnameEdit;
    @InjectView(R.id.city_edit) EditText mCityEdit;
    @InjectView(R.id.company_edit) DelayAutoCompleteTextView mCompanyEdit;
    @InjectView(R.id.email_edit) EditText mEmailEdit;
    @InjectView(R.id.password_edit) EditText mPasswordEdit;
    @InjectView(R.id.pass_again_edit) EditText mPassCopyEdit;
    @InjectView(R.id.profile_photo) ImageView mProfileImage;
    @InjectView(R.id.auto_complete_progress_bar) ProgressBar mAutoCompleteProgressBar;

    private Profile mProfile;
    private boolean imageIsLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Bundle bundle = getArguments();
        if (bundle != null){
            mProfile = bundle.getParcelable(ARG_PROFILE);
            initValues();
        }

        mCompanyEdit.setThreshold(2);
        mCompanyEdit.setAdapter(new CompanyAutoCompleteAdapter());
        mCompanyEdit.setLoadingIndicator(mAutoCompleteProgressBar);
        mCompanyEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Company company = (Company) adapterView.getItemAtPosition(position);
                mCompanyEdit.setText(company.getName());
            }
        });

        registerForContextMenu(mProfileImage);
    }

    private void initValues(){
        mNameEdit.setText(mProfile.getFirstName());
        mSurnameEdit.setText(mProfile.getLastName());
        mEmailEdit.setText(mProfile.getEmail());
        if (mProfile.getCity() != null) {
            mCityEdit.setText(mProfile.getCity().getName());
        }

        if (mProfile.getAvatarUrl() != null && (mProfile.getAvatarUrl().length() > 0)){
            Picasso.with(getActivity()).load(mProfile.getAvatarUrl())
                    .error(R.drawable.no_image)
                    .into(mProfileImage);
            imageIsLoaded = true;
        }
    }

    @OnClick(R.id.signup_button)
    protected void sendRegisterRequest() {

        String password = mPasswordEdit.getText().toString().trim();
        String passwordCopy = mPassCopyEdit.getText().toString().trim();
        String email = mEmailEdit.getText().toString().trim();
        String name = mNameEdit.getText().toString().trim();
        String surname = mSurnameEdit.getText().toString().trim();
        String company = mCompanyEdit.getText().toString().trim();
        String city = mCityEdit.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.rp_name_surname_required_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (company.isEmpty() || city.isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.rp_city_company_required_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || !password.equals(passwordCopy)) {
            Toast.makeText(getActivity(), getString(R.string.rp_passwords_arent_equal), Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.rp_email_required_error), Toast.LENGTH_SHORT).show();
            return;
        }

        onLoadBegins();
        if (User.currentUser().getVkToken() == null && User.currentUser().getFbToken() == null){
            credentialsLogin();
        } else {
            socialLogin();
        }
    }

    private void socialLogin(){
        String method = "";
        String userId = "";
        String token = "";

        if (User.currentUser().getFbToken() != null){
            method = "FB";
            userId = User.currentUser().getFbToken().getUserId();
            token = User.currentUser().getFbToken().getToken();
        } else if (User.currentUser().getVkToken() != null){
            method = "VK";
            userId = User.currentUser().getVkToken().userId;
            token = User.currentUser().getVkToken().accessToken;
        }

        if (method.isEmpty() || userId.isEmpty() || token.isEmpty()){
            credentialsLogin();
            Log.e("pro.asdgroup.bizon.BizonApp", "No token submitted for social net sign up");
            return;
        }

        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.register(mNameEdit.getText().toString(),
                mMidNameEdit.getText().toString(),
                mSurnameEdit.getText().toString(),
                mCityEdit.getText().toString(),
                mCompanyEdit.getText().toString(),
                mPasswordEdit.getText().toString(),
                mEmailEdit.getText().toString(),
                method,
                userId,
                token,
                "ANDROID",
                User.getGcmToken(),
                new LoginCallback());
    }

    private void credentialsLogin(){
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.register(capFirstLetter(mNameEdit.getText().toString()),
                capFirstLetter(mMidNameEdit.getText().toString()),
                capFirstLetter(mSurnameEdit.getText().toString()),
                mCityEdit.getText().toString(),
                mCompanyEdit.getText().toString(),
                mPasswordEdit.getText().toString(),
                mEmailEdit.getText().toString(),
                "ANDROID",
                User.getGcmToken(),
                new LoginCallback());
    }

    private String capFirstLetter(String value){
        if (value == null || value.length() < 1){
            return "";
        }

        StringBuilder rackingSystemSb = new StringBuilder(value);
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        return rackingSystemSb.toString();
    }

    private class LoginCallback extends HttpHelper.RestCallback<Profile> {
        @Override
        public void success(Profile profile, Response response) {
            User.currentUser().setUserId(profile.getId());

            if (imageIsLoaded) {
                uploadProfilePhoto();

            } else {
                onRegistrationComplete();
            }
        }

        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }
    }

    private void onRegistrationComplete() {
        User.saveAuthInfo(mEmailEdit.getText().toString(), mPasswordEdit.getText().toString());

        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.getUser(User.currentUser().getUserId(), new HttpHelper.RestCallback<Profile>() {
            @Override
            public void success(Profile profile, Response response) {
                User.currentUser().setProfile(profile);
                onLoadFinished();

                Activity activity = getActivity();
                if (activity instanceof MainActivityCallback){
                    ((MainActivityCallback) activity).onUserStatusChange();
                }
            }

            @Override
            public void failure(Status restError) {
                onLoadFinished();
                Toast.makeText(getActivity(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
            }
        });
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
                        onRegistrationComplete();
                    }

                    @Override
                    public void failure(Status restError) {
                        onRegistrationComplete();
                        Toast.makeText(getActivity(), getString(R.string.photo_wasnt_uploaded), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.profile_photo)
    void onProfilePhotoClick(View view){
        getActivity().openContextMenu(view);
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
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
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .transform(new BitmapResizeTransformation())
                .into(mProfileImage);
        imageIsLoaded = true;
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
    }

    private String getEncodedImage() {
        Bitmap bitmap = ((BitmapDrawable)mProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, byteStream);
        byte[] byteArray = byteStream.toByteArray();
        return Base64.encodeToString(byteArray, 0);
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

    @Override
    public String getTitle() {
        return getString(R.string.signup_title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }
}
