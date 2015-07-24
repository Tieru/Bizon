package pro.asdgroup.bizon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class Publisher implements Serializable{

    @Getter
    @Setter
    int id;

    @Getter
    @Setter
    @SerializedName("firstname")
    String firstName;

    @Getter
    @Setter
    @SerializedName("lastname")
    String lastName;

    public Publisher(){}

    public static Publisher fromJSON(JSONObject data) throws JSONException {

        if (data == null){
            return null;
        }

        Publisher publisher = new Publisher();
        publisher.setId(data.getInt("id"));
        publisher.setFirstName(data.optString("firstname"));
        publisher.setLastName(data.optString("lastname"));

        return publisher;
    }
    public String getFirstLastName(){
        return firstName + " " + lastName;
    }

}
