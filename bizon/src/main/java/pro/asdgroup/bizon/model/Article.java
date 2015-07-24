package pro.asdgroup.bizon.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class Article implements Parcelable {

    protected final static SimpleDateFormat _dateFormat = new SimpleDateFormat("dd.MM.yy", new Locale("ru", "RU"));

    @Setter
    @Getter
    int id;

    @Setter
    @Getter
    String name;

    @Setter
    @Getter
    @SerializedName("small_descr")
    String smallDescription;

    @Setter
    @Getter
    @SerializedName("body")
    String articleBody;

    @Setter
    @Getter
    @SerializedName("picture_url")
    String pictureUrl;

    @Setter
    @Getter
    @SerializedName("pictures_urls")
    List<String> pictureUrls;

    @Setter
    @Getter
    @SerializedName("published_date")
    Date publishedDate;

    @Setter
    @Getter
    Publisher publisher;

    @Setter
    @Getter
    @SerializedName("hashtags")
    List<HashTag> hashTags;

    public String getPublishedDateText() {
        return _dateFormat.format(publishedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(smallDescription);
        dest.writeLong(publishedDate.getTime());
        dest.writeString(pictureUrl);
        dest.writeSerializable(publisher);
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    private Article (Parcel in) {
        id = in.readInt();
        name = in.readString();
        smallDescription = in.readString();
        publishedDate = new Date(in.readInt());
        pictureUrl = in.readString();
        publisher = in.readParcelable(Publisher.class.getClassLoader());
    }
}
