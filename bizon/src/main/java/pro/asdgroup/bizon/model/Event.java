package pro.asdgroup.bizon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/19/2015.
 */
public class Event implements Serializable {
    public final static SimpleDateFormat _dateFormat = new SimpleDateFormat("dd.MM.yy");
    public final static SimpleDateFormat _timeFormat = new SimpleDateFormat("HH:mm");

    @Setter
    @Getter
    int id;

    @Setter
    @Getter
    String name;

    @Setter
    @Getter
    @SerializedName("picture_url")
    String pictureUrl;

    @Setter
    @Getter
    @SerializedName("small_descr")
    String smallDescription;

    @Setter
    @Getter
    String text;

    @Setter
    @Getter
    @SerializedName("is_member")
    int isMember;

    @Setter
    @Getter
    @SerializedName("comments_count")
    int commentsCount;

    @Setter
    @Getter
    @SerializedName("news_count")
    int newsCount;

    @Setter
    @Getter
    Publisher publisher;

    @Setter
    @Getter
    @SerializedName("published_date")
    Date publishedDate;

    @Setter
    @Getter
    @SerializedName("hashtags")
    List<HashTag> hashTags;

    public String getPublishedDateText() {
        return _dateFormat.format(publishedDate);
    }

    public String getPublishTime(){
        return _timeFormat.format(publishedDate);
    }

    public boolean getIsParticipant(){
        return isMember == 1;
    }

    public String getPublishedDateText(boolean setGsmOffset){
        if (!setGsmOffset){
            return getPublishedDateText();
        }

        int gmtOffset = TimeZone.getDefault().getRawOffset();
        Date eventDate = new Date(publishedDate.getTime() + gmtOffset);
        return Event._dateFormat.format(eventDate);
    }

    public String getPublishedTime(boolean setGsmOffset){
        if (!setGsmOffset){
            return getPublishTime();
        }

        int gmtOffset = TimeZone.getDefault().getRawOffset();
        Date eventDate = new Date(publishedDate.getTime() + gmtOffset);
        String timeStr = Event._timeFormat.format(eventDate);

        if (timeStr.charAt(0) == '0') {
            timeStr = timeStr.replaceFirst("0", "");
        }

        return timeStr;
    }
}
