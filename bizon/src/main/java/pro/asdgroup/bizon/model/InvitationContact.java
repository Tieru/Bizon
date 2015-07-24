package pro.asdgroup.bizon.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tieru on 05.06.2015.
 */
public class InvitationContact {

    @Setter
    @Getter
    String id;

    @Setter
    @Getter
    @SerializedName("firstname")
    String firstName;

    @Setter
    @Getter
    @SerializedName("lastname")
    String lastName;

    @Setter
    @Getter
    String email;

    @Setter
    @Getter
    @SerializedName("avatar_url")
    String avatarUrl;

    public String getFirstLastName(){
        return firstName + (lastName == null? "": " " + lastName);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof InvitationContact) {
             if (email.equals(((InvitationContact)o).email)){
                return true;
             }
        }

        return false;
    }
}
