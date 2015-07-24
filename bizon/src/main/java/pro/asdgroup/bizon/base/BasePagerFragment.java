package pro.asdgroup.bizon.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import pro.asdgroup.bizon.R;

/**
 * Created by Tieru on 15.05.2015.
 */
public abstract class BasePagerFragment extends Fragment {

    @Optional
    @InjectView(R.id.progress_bar)
    FrameLayout mProgressBar;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
    }

    protected void onLoadBegins(){
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void onLoadFinished(){
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
