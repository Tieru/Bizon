package pro.asdgroup.bizon.base;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import pro.asdgroup.bizon.R;

/**
 * Created by Voronov Viacheslav on 5/4/2015.
 */

public abstract class BaseFragment extends Fragment implements IDrawerFragment {

    private static final int ACTION_ADD = 0;
    private static final int ACTION_REPLACE = 1;

    public void addFragment(Fragment fragment, boolean storeInBackStack){
        makeFragmentTransaction(ACTION_ADD, fragment, storeInBackStack, -1, -1, null);
    }

    public void addFragment(Fragment fragment, boolean storeInBackStack, int animationIn, int animationOut){
        makeFragmentTransaction(ACTION_ADD, fragment, storeInBackStack, animationIn, animationOut, null);
    }

    public void replaceFragment(Fragment fragment, boolean storeInBackStack){
        makeFragmentTransaction(ACTION_REPLACE, fragment, storeInBackStack, -1, -1, null);
    }

    public void replaceFragment(Fragment fragment, boolean storeInBackStack, int animationIn, int animationOut) {
        makeFragmentTransaction(ACTION_REPLACE, fragment, storeInBackStack, animationIn, animationOut, null);
    }

    private void makeFragmentTransaction(int action, Fragment fragment, boolean storeInBackStack, int animationIn, int animationOut, String tag){
        if (getActivity() == null){
            return;
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (animationIn != -1 && animationOut != -1) {
            transaction.setCustomAnimations(animationIn, animationOut);//(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        switch (action){
            case ACTION_ADD:
                transaction.add(R.id.container, fragment, tag);
                break;
            case ACTION_REPLACE:
                transaction.replace(R.id.container, fragment, tag);
        }

        if (storeInBackStack){
            transaction.addToBackStack(null);
            if (action == ACTION_ADD) {
                fragment.setTargetFragment(this, 101);
            }
        }

        transaction.commit();
    }

    protected void popBackStack(){
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.setMenuVisibility(false);
        }

        Activity activity = getActivity();
        if (activity instanceof MainActivityCallback) {
            ((MainActivityCallback)activity).setActivityTitle(getTitle());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.setMenuVisibility(true);
            Activity activity = getActivity();
            if (activity instanceof MainActivityCallback) {
                ((MainActivityCallback)activity).setActivityTitle(((IDrawerFragment) targetFragment).getTitle());
            }
        }
    }

    public void onLoadBegins(){
        Activity activity = getActivity();
        if (activity instanceof MainActivityCallback){
            ((MainActivityCallback)activity).onLoadBegins();
        }
    }

    public void onLoadFinished(){
        Activity activity = getActivity();
        if (activity instanceof MainActivityCallback){
            ((MainActivityCallback)activity).onLoadFinished();
        }
    }
}
