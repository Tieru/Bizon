package pro.asdgroup.bizon.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pro.asdgroup.bizon.model.dto.ProfileDTO;

/**
 * Created by Voronov Viacheslav on 4/14/2015.
 */
public class Profile implements Parcelable, Serializable, Cloneable {

    public Profile(){}

    public Profile(Publisher publisher){
        id = String.valueOf(publisher.getId());
        firstName = publisher.getFirstName();
        lastName = publisher.getLastName();
    }

    public Profile(ProfileDTO profile){
        id = profile.getId();
        firstName = profile.getFirstName();
        lastName = profile.getLastName();
        middleName = profile.getMiddleName();
        about = profile.getAbout();
        city = profile.getCity();
        region = profile.getRegion();
        companies = profile.getCompanies();
        vk = profile.getVk();
        skype = profile.getSkype();
        phone = profile.getPhone();
        email = profile.getEmail();
        hashTags = profile.getHashTags();
        avatarUrl = profile.getAvatarUrl();
        isAdmin = profile.getIsAdmin();
    }

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
    @SerializedName("middlename")
    String middleName;

    @Setter
    @Getter
    @SerializedName("about")
    String about;

    @Setter
    @Getter
    City city;

    @Setter
    @Getter
    Region region;

    @Setter
    @Getter
    List<Company> companies;

    @Setter
    @Getter
    String vk;

    @Setter
    @Getter
    String skype;

    @Setter
    @Getter
    String phone;

    @Setter
    @Getter
    String email;

    @Setter
    @Getter
    String role;

    @Setter
    @Getter
    @SerializedName("hashtags")
    List<HashTag> hashTags;

    @Setter
    @Getter
    Business business;

    @Setter
    @Getter
    @SerializedName("avatar_url")
    String avatarUrl;

    @Setter
    @Getter
    @SerializedName("is_admin")
    int isAdmin;

    @Setter
    @Getter
    @SerializedName("answered_questions")
    String answeredQuestions;

    @Setter
    @Getter
    @SerializedName("ignored_questions")
    String ignoredQuestions;

    public String getFirstLastName(){
        return firstName + " " + lastName;
    }

    public String getLastFirstName(){
        return lastName + " " + firstName;
    }

    public String getCompanyNames(boolean eachOnNewString){

        if (companies == null){
            return "";
        }

        StringBuilder companies = new StringBuilder();

        for (Company company: this.companies){
            if (companies.length() != 0){
                if (eachOnNewString){
                    companies.append("\n");
                } else {
                    companies.append(" ");
                }
            }

            companies.append(company.getName());
        }

        return companies.toString();
    }


    public String getTagsString(){
        if (hashTags == null){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (HashTag hashTag: hashTags){
            sb.append("#");
            sb.append(hashTag.getName());
            sb.append(" ");
        }

        if (sb.length() == 0){
            return "";
        }

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(middleName);
        dest.writeString(avatarUrl);
        if (companies == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(companies);
        }
        if (hashTags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hashTags);
        }
    }

    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    private Profile (Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        middleName = in.readString();
        avatarUrl = in.readString();
        if (in.readByte() == 0x01) {
            companies = new ArrayList<>();
            in.readList(companies, Company.class.getClassLoader());
        } else {
            companies = null;
        }

        if (in.readByte() == 0x01) {
            hashTags = new ArrayList<>();
            in.readList(hashTags, HashTag.class.getClassLoader());
        } else {
            hashTags = null;
        }
    }

    public static class ProfileSerializer implements JsonSerializer<Profile>{

        @Override
        public JsonElement serialize(Profile src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", src.getId());
            jsonObject.addProperty("firstname", src.getFirstName());
            jsonObject.addProperty("lastname", src.getLastName());
            jsonObject.addProperty("middlename", src.getMiddleName());
            jsonObject.addProperty("about", src.getAbout());
            jsonObject.addProperty("phone", src.getPhone());
            jsonObject.addProperty("email", src.getEmail());
            jsonObject.addProperty("vk", src.getVk());
            jsonObject.addProperty("skype", src.getSkype());

            if (src.getCity() != null) {
                jsonObject.addProperty("city_name", src.getCity().getName());
            }

            if (src.getCompanies() != null) {
                JsonArray companies = new JsonArray();
                for (final Company companyObject : src.getCompanies()) {
                    JsonObject company = new JsonObject();
                    company.addProperty("id", companyObject.getId());
                    company.addProperty("name", companyObject.getName());
                    company.addProperty("about", companyObject.getAbout());
                    company.addProperty("site", companyObject.getSiteUrl());
                    company.addProperty("business", companyObject.getBusiness());
                    companies.add(company);
                }
                jsonObject.add("companies", companies);
            }

            if (src.getBusiness() != null){
                JsonObject business = new JsonObject();
                business.addProperty("name", src.getBusiness().getName());

                if (src.getBusiness().getHashTags() != null){
                    JsonArray hashTags = new JsonArray();
                    for (final HashTag hashTagObject : src.getBusiness().getHashTags()) {
                        JsonObject hashTag = new JsonObject();
                        hashTag.addProperty("id", hashTagObject.getId());
                        hashTag.addProperty("name", hashTagObject.getName());
                        hashTags.add(hashTag);
                    }
                    business.add("hashtags", business);
                }
                jsonObject.add("business", business);
            }

            if(src.getHashTags() != null){
                JsonArray hashTags = new JsonArray();
                for (final HashTag hashTagObject : src.getHashTags()) {
                    String name = hashTagObject.getName().replace("#", "").trim();
                    JsonPrimitive nameObject = new JsonPrimitive(name);
                    hashTags.add(nameObject);
                }

                jsonObject.add("hashtags", hashTags);
            }

            return jsonObject;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Profile){
            String id = ((Profile) o).getId();
            if (id != null && id.equals(this.id)){
                return true;
            }
        }

        return false;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
