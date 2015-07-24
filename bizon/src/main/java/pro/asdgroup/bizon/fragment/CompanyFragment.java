package pro.asdgroup.bizon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.base.BaseFragment;
import pro.asdgroup.bizon.model.Company;
import pro.asdgroup.bizon.view.DetailTextView;

/**
 * Created by Voronov Viacheslav on 5/3/2015.
 */
public class CompanyFragment extends BaseFragment {

    private final static String ARG_COMPANY = "company";

    public CompanyFragment() {
    }

    public static CompanyFragment newInstance(Company company) {
        CompanyFragment fragment = new CompanyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    private void initUI() {
        View view = getView();
        if (view == null) {
            return;
        }

        Company company = (Company) getArguments().getSerializable(ARG_COMPANY);

        if (company == null) {
            return;
        }

        ((DetailTextView) view.findViewById(R.id.name_text)).setDetailText(company.getName());
        ((DetailTextView) view.findViewById(R.id.business_text)).setDetailText(company.getBusiness());
        ((DetailTextView) view.findViewById(R.id.website_text)).setDetailText(company.getSiteUrl());
        ((DetailTextView) view.findViewById(R.id.about_text)).setDetailText(company.getAbout());
    }

    @Override
    public String getTitle() {
        return getString(R.string.company_title);
    }
}
