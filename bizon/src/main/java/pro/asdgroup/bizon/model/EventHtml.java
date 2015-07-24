package pro.asdgroup.bizon.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tieru on 05.06.2015.
 */
public class EventHtml {
    @Getter
    @Setter
    @SerializedName("html_string")
    String htmlString;
}
