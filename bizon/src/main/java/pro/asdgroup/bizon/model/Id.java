package pro.asdgroup.bizon.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by vvoronov on 03/07/15.
 */
public class Id implements Serializable {

    public Id(){}

    public Id(String id){
        this.id = id;
    }

    @Setter
    @Getter
    @SerializedName("$id")
    String id;
}
