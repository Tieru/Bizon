package pro.asdgroup.bizon.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by vvoronov on 03/07/15.
 */
public class DayQuestion implements Serializable {

    @Setter
    @SerializedName("_id")
    Id id;

    @Setter
    @Getter
    String text;

    @Setter
    @Getter
    @SerializedName("picture_url")
    String pictureUrl;

    @Setter
    @Getter
    @SerializedName("author_id")
    String authorId;

    @Setter
    long date;

    @Setter
    @Getter
    @SerializedName("is_day_question")
    int isDayQuestion;

    public String getId(){
        return id.getId();
    }

    public long getDate(){
        return date * 1000;
    }
}
