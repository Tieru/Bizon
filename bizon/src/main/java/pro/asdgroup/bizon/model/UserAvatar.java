package pro.asdgroup.bizon.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class UserAvatar {

    @Setter
    @Getter
    @SerializedName("user_id")
    private String id;

    @Setter
    @Getter
    private String avatar;
}
