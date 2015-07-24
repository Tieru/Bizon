package pro.asdgroup.bizon.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 11/05/15.
 */
public class EventNew {

    protected final static SimpleDateFormat _dateFormat = new SimpleDateFormat("dd.MM.yy");

    @Setter
    @Getter
    String id;

    @Setter
    @Getter
    Date date;

    @Setter
    @Getter
    @SerializedName("sender")
    Publisher publisher;

    @Setter
    @Getter
    String text;

    public String getDateText() {
        return _dateFormat.format(date);
    }
}
