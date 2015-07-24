package pro.asdgroup.bizon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import pro.asdgroup.bizon.util.HeaderRecyclerViewAdapter;

/**
 * Created by vvoronov on 09/07/15.
 */
public class ContextMenuHeaderRecyclerView extends ContextMenuRecyclerView {

    public ContextMenuHeaderRecyclerView(Context context) {
        super(context);
    }

    public ContextMenuHeaderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContextMenuHeaderRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {

        int longPressPosition;
        if (getAdapter() instanceof HeaderRecyclerViewAdapter) {
            longPressPosition = ((HeaderRecyclerViewAdapter) getAdapter()).getClickedItemPosition();
        } else {
            longPressPosition = getChildAdapterPosition(originalView);
        }

        if (longPressPosition > 0) {
            final long longPressId = getAdapter().getItemId(longPressPosition);
            setContextMenuInfo(new RecyclerViewContextMenuInfo(longPressPosition, longPressId));
            return super.showContextMenuForChild(originalView);
        }
        return false;
    }
}
