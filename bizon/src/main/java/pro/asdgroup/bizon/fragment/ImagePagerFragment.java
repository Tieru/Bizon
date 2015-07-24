package pro.asdgroup.bizon.fragment;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.adapter.ImagePagerAdapter;
import pro.asdgroup.bizon.base.IImagePagerHolder;

/**
 * Created by Voronov Viacheslav on 01.07.2015.
 */
public class ImagePagerFragment extends Fragment implements IImagePagerHolder {

    private final static String ARG_URLS = "image_urls";

    @InjectView(R.id.viewPager) ViewPager mViewPager;
    @InjectView(R.id.imageIndicator) CirclePageIndicator mPagerIndicator;

    public static Fragment newInstance(List<String> urls){
        Fragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_URLS, (ArrayList<String>) urls);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        List<String> imageUrlList = getArguments().getStringArrayList(ARG_URLS);

        ImagePagerAdapter instructionPagerAdapter = new ImagePagerAdapter(getChildFragmentManager(), imageUrlList);
        mViewPager.setAdapter(instructionPagerAdapter);

        assert imageUrlList != null;
        if (imageUrlList.size() == 1){
            mPagerIndicator.setVisibility(View.GONE);
        } else {
            mPagerIndicator.setViewPager(mViewPager);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onImageLoadFailed(int position) {
        //TODO:
    }

    @Override
    public void onImageLoaded() {
        //TODO:
    }
}



