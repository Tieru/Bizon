package pro.asdgroup.bizon.model.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.asdgroup.bizon.model.DayQuestion;
import pro.asdgroup.bizon.model.FeedComment;
import pro.asdgroup.bizon.model.Id;
import pro.asdgroup.bizon.model.Profile;

/**
 * Created by Voronov Viacheslav on 19.07.2015.
 */
public class PostDTO implements Serializable {

    @Setter
    long date;

    @Setter
    @Getter
    String text;

    @Getter
    @Setter
    @SerializedName("picture_url")
    String pictureUrl;

    @Getter
    @Setter
    String url;

    @Setter
    @Getter
    @SerializedName("who_likes")
    String likedBy;

    @Setter
    @Getter
    @SerializedName("_id")
    Id id;

    @Setter
    @Getter
    String city;

    @Setter
    @Getter
    @SerializedName("day_question")
    DayQuestion dayQuestion;

    @Setter
    @Getter
    Profile author;

    @Setter
    @Getter
    List<FeedComment> comments;

    @Setter
    @Getter
    @SerializedName("comments_count")
    int commentsCount;
}
