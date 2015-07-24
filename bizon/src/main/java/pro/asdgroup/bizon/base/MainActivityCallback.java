package pro.asdgroup.bizon.base;

/**
 * Created by Voronov Viacheslav on 10/05/15.
 */
public interface MainActivityCallback {
    void onUserStatusChange();
    void setActivityTitle(String title);

    void onUserLogout();

    boolean onAuthRequest();

    void onLoadBegins();
    void onLoadFinished();
}
