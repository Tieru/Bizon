package pro.asdgroup.bizon.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tieru on 05.06.2015.
 */
public class ActionResult {

    public static final int SUCCESS = 1;
    public static final int FAIL = 0;

    @Setter
    @Getter
    int success;
}
