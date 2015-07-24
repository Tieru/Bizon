package pro.asdgroup.bizon.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.data.HttpHelper;
import pro.asdgroup.bizon.data.RestHelper;
import pro.asdgroup.bizon.model.Company;
import pro.asdgroup.bizon.model.Status;
import retrofit.client.Response;

/**
 * Created by Voronov Viacheslav on 5/3/2015.
 */
public class CompanyEditFragment extends BaseFragment {

    private final static String ARG_INDEX = "userid";

    @InjectView(R.id.name_edit) EditText mNameEdit;
    @InjectView(R.id.activities_edit) EditText mBusinessEdit;
    @InjectView(R.id.website_edit) EditText mWebsiteEdit;
    @InjectView(R.id.about_edit) EditText mAboutEdit;

    private int companyIndex = -1;
    private Company mCompany;

    public CompanyEditFragment() {}

    public static CompanyEditFragment newInstance() {
        return new CompanyEditFragment();
    }

    public static CompanyEditFragment newInstance(int companyIndex) {
        CompanyEditFragment fragment = new CompanyEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, companyIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company_edit, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Bundle bundle = getArguments();

        if (bundle != null){
            companyIndex = bundle.getInt(ARG_INDEX, -1);
            mCompany = User.currentUser().getProfile().getCompanies().get(companyIndex);
        } ////TODO: else Toast

        if (mCompany == null){
            mCompany = new Company();
            //TODO: Toast
            return;
        }

        mNameEdit.setText(mCompany.getName());
        mBusinessEdit.setText(mCompany.getBusiness());
        mWebsiteEdit.setText(mCompany.getSiteUrl());
        mAboutEdit.setText(mCompany.getAbout());
    }

    @OnClick(R.id.save_btn)
    void onSaveClick(){
        if (validateInputs()){
            updateCompany();
        }
    }

    private boolean validateInputs(){
        String name = mNameEdit.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(getActivity(), getString(R.string.cp_validation_fail_name), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateCompany(){

        onLoadBegins();

        RestHelper service = HttpHelper.getRestAdapter().create(RestHelper.class);

        if (companyIndex == -1) {
            service.createCompany(
                    User.currentUser().getUserId(),
                    mNameEdit.getText().toString(),
                    mAboutEdit.getText().toString(),
                    mBusinessEdit.getText().toString(),
                    mWebsiteEdit.getText().toString(),
                    new LoaderCallback());
        } else {
            service.updateCompany(
                    User.currentUser().getUserId(),
                    mCompany.getId(),
                    mNameEdit.getText().toString(),
                    mAboutEdit.getText().toString(),
                    mBusinessEdit.getText().toString(),
                    mWebsiteEdit.getText().toString(),
                    new LoaderCallback());
        }
    }

    private class LoaderCallback extends HttpHelper.RestCallback<Company> {
        @Override
        public void success(Company company, Response response) {
            mCompany.setId(company.getId());
            mCompany.setName(mNameEdit.getText().toString());
            mCompany.setAbout(mAboutEdit.getText().toString());
            mCompany.setBusiness(mBusinessEdit.getText().toString());
            mCompany.setSiteUrl(mWebsiteEdit.getText().toString());

            onLoadSuccess();
        }

        @Override
        public void failure(Status restError) {
            onLoadFinished();
        }
    }

    private void onLoadSuccess(){
        if (companyIndex == -1){
            User.currentUser().getProfile().getCompanies().add(mCompany);
        } else {
            User.currentUser().getProfile().getCompanies().set(companyIndex, mCompany);
        }

        onLoadFinished();
        Toast.makeText(getActivity(), getString(R.string.cp_update_success), Toast.LENGTH_SHORT).show();

        CompanyListFragment fragment = (CompanyListFragment)getTargetFragment();
        if (fragment != null) {
            fragment.updateCompaniesList();
        }

        getFragmentManager().popBackStack();
    }

    @Override
    public String getTitle() {
        return getString(R.string.company_edit_title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }


}