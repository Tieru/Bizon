package pro.asdgroup.bizon.model.dto;

import lombok.Getter;
import lombok.Setter;
import pro.asdgroup.bizon.model.Profile;

/**
 * Created by Tieru on 21.05.2015.
 */
public class ProfileDTO extends Profile {

    @Setter
    @Getter
    int success = -1;
}
