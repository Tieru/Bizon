package pro.asdgroup.bizon.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.base.BaseFragment;


/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public String getTitle() {
        return getString(R.string.main_title);
    }

    @OnClick({R.id.community_btn, R.id.dailyQuestionBtn, R.id.events_btn, R.id.feedButton})
    public void onClick(View view) {
        Fragment fragment = null;

        switch (view.getId()) {
            case R.id.community_btn:
                fragment = CommunityListFragment.newInstance();
                break;
            case R.id.dailyQuestionBtn:
                fragment = QuestionListFragment.newInstance();
                break;
            case R.id.events_btn:
                fragment = EventListFragment.newInstance();
                break;
            case R.id.feedButton:
                fragment = FeedFragment.newInstance();
                break;
        }

        addFragment(fragment, true, R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
