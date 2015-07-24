package pro.asdgroup.bizon.util;

import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;

import pro.asdgroup.bizon.R;

/**
 * Created by vvoronov on 20/07/15.
 */
public class TimeManager {

    public final static int SECONDS_LIMIT = 60 * 1000;
    public final static int MINUTES_LIMIT = 60 * 60 * 1000;
    public final static int HOURS_LIMIT = 12 * 60 * 60 * 1000;
    public final static int YESTERDAY_LIMIT = 36 * 60 * 60 * 1000;
    public final static int DAYS_LIMIT = 3 * 24 * 60 * 60 * 1000;


    public static String getTimeString(Context context, long time){

        long timeDiff = Calendar.getInstance().getTimeInMillis() - time;

        if (time == 0 || timeDiff <= 0){
            return context.getString(R.string.feed_post_label_just_now);
        }

        if (timeDiff > DAYS_LIMIT){
            return DateFormat.getDateInstance().format(time);
        }

        if (timeDiff < SECONDS_LIMIT){
            int seconds = (int)timeDiff/1000;
            return context.getString(R.string.feed_post_label_seconds_ago, seconds);
        }

        if (timeDiff < MINUTES_LIMIT){
            int minutes = (int)timeDiff/60/1000;
            return context.getString(R.string.feed_post_label_minutes_ago, minutes);
        }

        if (timeDiff < HOURS_LIMIT){
            int hours = (int)timeDiff/60/60/1000;
            return context.getString(R.string.feed_post_label_hours_ago, hours);
        }

        if (timeDiff < YESTERDAY_LIMIT){
            return context.getString(R.string.feed_post_label_yesterday);
        }

        if (timeDiff < DAYS_LIMIT){
            int days = (int)timeDiff/24/60/60/1000;
            return context.getString(R.string.feed_post_label_days_ago, days);
        }

        return "";
    }
}
