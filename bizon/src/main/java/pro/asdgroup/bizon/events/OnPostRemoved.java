package pro.asdgroup.bizon.events;

import lombok.Getter;
import lombok.Setter;
import pro.asdgroup.bizon.model.FeedEntry;

/**
 * Created by Voronov Viacheslav on 19.07.2015.
 */
public class OnPostRemoved {

    @Setter
    @Getter
    private FeedEntry feedEntry;

    public OnPostRemoved(FeedEntry feedEntry){
        this.feedEntry = feedEntry;
    }

}
