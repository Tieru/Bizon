package pro.asdgroup.bizon.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class Status {

    public Status(){}

    public Status(String error){}

    @Setter
    @Getter
    int code;

    @Setter
    @Getter
    String error;
}
