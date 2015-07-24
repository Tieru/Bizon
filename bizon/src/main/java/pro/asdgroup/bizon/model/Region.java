package pro.asdgroup.bizon.model;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/14/2015.
 */
public class Region implements Serializable {

    @Setter
    @Getter
    int id;

    @Setter
    @Getter
    String name;

    @Setter
    @Getter
    List<City> cities;

}
