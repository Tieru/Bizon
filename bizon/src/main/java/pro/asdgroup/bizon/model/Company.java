package pro.asdgroup.bizon.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/14/2015.
 */
public class Company implements Serializable {

    @Setter
    @Getter
    int id;

    @Setter
    @Getter
    String name;

    @Setter
    @Getter
    String about;

    @Setter
    @Getter
    String siteUrl;

    @Setter
    @Getter
    String business;

}
