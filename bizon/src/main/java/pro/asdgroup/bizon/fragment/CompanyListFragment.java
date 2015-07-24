package pro.asdgroup.bizon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.adapter.CompanyAdapter;
import pro.asdgroup.bizon.base.IDrawerFragment;
import pro.asdgroup.bizon.base.MainActivityCallback;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.model.Company;

/**
 * Created by Voronov Viacheslav on 5/2/2015.
 */


public class CompanyListFragment extends ListFragment implements IDrawerFragment {

    public final static int VIEW_MODE = 0;
    public final static int EDIT_MODE = 1;

    private final static String ARG_MODE = "mode";
    private final static String ARG_COMPANIES = "companies";

    private CompanyAdapter mAdapter;

    int mode = 0;

    public static CompanyListFragment newInstance(int mode, List<Company> companies) {
        CompanyListFragment fragment = new CompanyListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);
        args.putSerializable(ARG_COMPANIES, (ArrayList) companies);
        fragment.setArguments(args);
        return fragment;
    }

    public CompanyListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        ButterKnife.inject(this, view);

        mode = getArguments().getInt(ARG_MODE, VIEW_MODE);

        if (mode == VIEW_MODE) {
            view.findViewById(R.id.add_company).setVisibility(View.GONE);
        }

        List<Company> companies = (ArrayList) args.getSerializable(ARG_COMPANIES);

        mAdapter = new CompanyAdapter(companies);
        setListAdapter(mAdapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment fragment = null;

        switch (mode) {
            case VIEW_MODE:
                Company company = (Company) l.getAdapter().getItem(position);
                fragment = CompanyFragment.newInstance(company);
                break;
            case EDIT_MODE:
                fragment = CompanyEditFragment.newInstance(position);
                break;
        }

        moveToAnotherFragment(fragment);
    }

    private void moveToAnotherFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.setTargetFragment(this, 0);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void updateCompaniesList(){
        mAdapter.setCompanies(User.currentUser().getProfile().getCompanies());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivityCallback) getActivity()).setActivityTitle(getTitle());

        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.setMenuVisibility(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.setMenuVisibility(true);
            ((MainActivityCallback) getActivity())
                    .setActivityTitle(((IDrawerFragment) targetFragment).getTitle());

            if (targetFragment instanceof ProfileEditFragment){
                ((ProfileEditFragment) targetFragment).updateCompanyText();
            }
        }

    }

    @OnClick(R.id.add_company)
    public void addCompany() {
        Fragment fragment = CompanyEditFragment.newInstance();
        moveToAnotherFragment(fragment);
    }

    @Override
    public String getTitle() {
        return BizonApp.getAppContext().getString(R.string.companies_title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }
}
