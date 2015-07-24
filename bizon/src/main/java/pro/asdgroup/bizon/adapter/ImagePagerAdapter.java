package pro.asdgroup.bizon.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import pro.asdgroup.bizon.fragment.ImageFragment;

/**
 * Created by Voronov Viacheslav on 01.07.2015.
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mImageUrlList;

    public ImagePagerAdapter(FragmentManager fm, List<String> imageList) {
        super(fm);
        mImageUrlList = imageList;
    }

    @Override
    public Fragment getItem(int i) {
        return ImageFragment.newInstance(mImageUrlList.get(i), i);
    }

    @Override
    public int getCount() {
        return mImageUrlList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position);
    }
}