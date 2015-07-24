package pro.asdgroup.bizon.util;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Voronov Viacheslav on 02.07.2015.
 */
public class KeyboardUtils {

    public static boolean hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null && view instanceof EditText){
            ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 2);
            view.clearFocus();
            return true;
        }

        return false;
    }


    public static void setupUI(final Activity activity, View view) {
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return event.getAction() == MotionEvent.ACTION_UP && hideKeyboard(activity);
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
        }
    }
}
