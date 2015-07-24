package pro.asdgroup.bizon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Status;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tieru on 21.05.2015.
 */
public class RestorePasswordFragment extends BaseFragment {

    @InjectView(R.id.email_edit) EditText mEmailEdit;


    public static RestorePasswordFragment newInstance(){
        return new RestorePasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restore_password, container, false );
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
    }

    @Override
    public String getTitle() {
        return BizonApp.getAppContext().getString(R.string.restore_password_title);
    }

    @OnClick(R.id.restore_btn)
    void onRestoreButtonClick(){
        String email = mEmailEdit.getText().toString();

        if (email.isEmpty()){
            Toast.makeText(getActivity(), R.string.lp_retore_password_empty_email_error, Toast.LENGTH_SHORT).show();
            return;
        }

        onLoadBegins();
        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);
        service.restorePassword(email, new Callback<Status>() {
            @Override
            public void success(Status status, Response response) {
                onLoadFinished();
                Toast.makeText(getActivity(), R.string.lp_retore_password_success, Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void failure(RetrofitError error) {
                onLoadFinished();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }
}
