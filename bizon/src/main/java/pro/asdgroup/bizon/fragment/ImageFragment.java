package pro.asdgroup.bizon.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.base.IImagePagerHolder;
import pro.asdgroup.bizon.BizonApp;

/**
 * Created by Voronov Viacheslav on 01.07.2015.
 */
public class ImageFragment extends Fragment {

    public static final String ARG_IMAGE_URL = "url";
    public static final String ARG_POSITION = "pos";
    public static final Drawable mDefaultDrawable = BizonApp.getAppContext().getResources().getDrawable(R.drawable.no_image);

    @InjectView(R.id.image) ImageView mImageView;

    private int mFragmentPosition;

    public static Fragment newInstance(String imageUrl, int position){
        Fragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ImageFragment.ARG_IMAGE_URL, imageUrl);
        args.putInt(ImageFragment.ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        Bundle args = getArguments();
        String imageUrl = args.getString(ARG_IMAGE_URL);
        mFragmentPosition = args.getInt(ARG_POSITION, -1);

        if (imageUrl == null || imageUrl.isEmpty()) {
            ((IImagePagerHolder) getParentFragment()).onImageLoadFailed(mFragmentPosition);
            return;
        }

        Picasso.with(getActivity())
            .load(imageUrl)
            .into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    if (getParentFragment() instanceof IImagePagerHolder) {
                        ((IImagePagerHolder) getParentFragment()).onImageLoaded();
                    }
                }

                @Override
                public void onError() {
                    if (getParentFragment() instanceof IImagePagerHolder) {
                        ((IImagePagerHolder) getParentFragment()).onImageLoadFailed(mFragmentPosition);
                    }
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
