package pro.asdgroup.bizon.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by vvoronov on 03/07/15.
 */
public class FeedEntry implements Serializable {

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
    List<Profile> likedBy;

    @Setter
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

    public void incCommentsCount(int change){
        commentsCount += change;
    }

    public String getId(){
        return id.getId();
    }

    public Date getDate(){
        return new Date(date * 1000);
    }


    @Override
    public int hashCode() {
        if (id != null){
            return getId().hashCode();
        }

        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FeedEntry){
            FeedEntry entry = (FeedEntry)o;
            if (entry.id != null && id != null && entry.getId().equals(getId())){
                return true;
            }
        }

        return false;
    }

    public static class FeedEntrySerializer implements JsonSerializer<FeedEntry> {

        @Override
        public JsonElement serialize(FeedEntry src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();

            if (src.id != null){
                jsonObject.addProperty("id", src.getId());
            }

            jsonObject.addProperty("author_id", src.getAuthor().getId());
            jsonObject.addProperty("city", src.getCity());

            if (src.getPictureUrl() != null && !src.getPictureUrl().isEmpty()) {
                jsonObject.addProperty("picture", src.getPictureUrl());
            }

            if (src.getDayQuestion() != null) {
                jsonObject.addProperty("question_id", src.getDayQuestion().getId());
            }
            jsonObject.addProperty("text", src.getText());
            return jsonObject;
        }
    }
}
