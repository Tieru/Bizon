package pro.asdgroup.bizon.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by vvoronov on 03/07/15.
 */
public class FeedComment {

    @Setter
    Id id;

    @Setter
    long date;

    @Getter
    @Setter
    String text;

    @Setter
    @Getter
    Profile author;

    public String getId(){
        return id.getId();
    }

    public void setDateInMillis(long date){
        this.date = date / 1000;
    }

    public long getDate(){
        return date * 1000;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FeedComment){
            if (((FeedComment)o).getId().equals(getId())){
                return true;
            }
        }

        return false;
    }
}
