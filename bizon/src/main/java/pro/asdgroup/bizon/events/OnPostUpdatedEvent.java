package pro.asdgroup.bizon.events;

import lombok.Getter;
import lombok.Setter;
import pro.asdgroup.bizon.model.FeedEntry;

/**
 * Created by Voronov Viacheslav on 08.07.2015.
 */
public class OnPostUpdatedEvent {

    @Setter
    @Getter
    private FeedEntry feedEntry;

    public OnPostUpdatedEvent(FeedEntry feedEntry){
        this.feedEntry = feedEntry;
    }
}
