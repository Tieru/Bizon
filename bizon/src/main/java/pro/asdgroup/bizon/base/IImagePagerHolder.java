package pro.asdgroup.bizon.base;

/**
 * Created by Voronov Viacheslav on 01.07.2015.
 */
public interface IImagePagerHolder {

    void onImageLoadFailed(int position);
    void onImageLoaded();
}
