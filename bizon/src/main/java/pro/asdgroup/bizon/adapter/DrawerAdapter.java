package pro.asdgroup.bizon.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.User;
import pro.asdgroup.bizon.BizonApp;
import pro.asdgroup.bizon.model.Profile;

/**
 * Created by Voronov Viacheslav on 10/05/15.
 */
public class DrawerAdapter extends BaseAdapter {

    private Context context;
    private List<DrawerItem> mDrawerItems;
    private int currentItem = -1;
    private String currentUserId = "";


    public DrawerAdapter(){
        context = BizonApp.getAppContext();
        mDrawerItems = initMenuItems();
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public DrawerItem getItem(int i) {
        return mDrawerItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_navigation_drawer, viewGroup, false);
        }

        DrawerItem item = getItem(i);

        ((TextView)view.findViewById(R.id.title)).setText(item.title);

        if (currentItem == i){
            ((ImageView)view.findViewById(R.id.icon)).setImageResource(item.drawableActive);
        } else {
            ((ImageView)view.findViewById(R.id.icon)).setImageResource(item.drawableInactive);
        }

        return view;
    }

    public static class DrawerItem {
        String title;
        int drawableActive;
        int drawableInactive;
    }

    private List<DrawerItem> initMenuItems(){
        List<DrawerItem> menuItems = new ArrayList<>();
        String[] mLeftSliderData = context.getResources().getStringArray(R.array.drawer_titles);
        TypedArray activeItemIcons = context.getResources().obtainTypedArray(R.array.drawer_icon_active);
        TypedArray inactiveItemIcons = context.getResources().obtainTypedArray(R.array.drawer_icon_inactive);

        for (int i = 0; i < mLeftSliderData.length; i++){
            DrawerItem item = new DrawerItem();
            item.title = mLeftSliderData[i];
            item.drawableActive = activeItemIcons.getResourceId(i, -1);
            item.drawableInactive = inactiveItemIcons.getResourceId(i, -1);
            menuItems.add(item);
        }

        activeItemIcons.recycle();
        inactiveItemIcons.recycle();

        return menuItems;
    }


    private void updateDrawerItems(){
        Profile profile = User.currentUser().getProfile();
        if (profile != null){
            DrawerItem item = new DrawerItem();
            String title;
            title = profile.getFirstLastName();

            item.title = title;
            item.drawableActive = R.drawable.ic_user_active;
            item.drawableInactive = R.drawable.ic_user_inactive;
            mDrawerItems.set(0, item);
        } else {
            DrawerItem item = new DrawerItem();
            item.title = context.getString(R.string.signin_title);
            item.drawableActive = R.drawable.ic_nouser_active;
            item.drawableInactive = R.drawable.ic_nouser_inactive;
            mDrawerItems.set(0, item);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        String userId = User.currentUser().getUserId();
        if (!currentUserId.equals(userId)){

            if (userId == null){
                currentUserId = "";
            } else {
                currentUserId = userId;
            }

            updateDrawerItems();
        }

        super.notifyDataSetChanged();
    }


}
