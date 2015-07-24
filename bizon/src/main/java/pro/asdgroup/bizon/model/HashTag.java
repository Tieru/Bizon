package pro.asdgroup.bizon.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/14/2015.
 */
public class HashTag implements Serializable {

    @Setter
    @Getter
    Integer id;

    @Setter
    @Getter
    String name;

    @Override
    public String toString() {
        return "#" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HashTag) {
            return id == ((HashTag) o).id;
        } else {
            return false;
        }
    }
}
