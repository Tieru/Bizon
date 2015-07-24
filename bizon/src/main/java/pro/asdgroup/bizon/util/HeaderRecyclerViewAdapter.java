package pro.asdgroup.bizon.util;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by vvoronov on 08/07/15.
 */
public abstract class HeaderRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected static final int TYPE_HEADER = 111;
    protected static final int TYPE_ITEM = 333;

    private boolean hasHeader = false;

    private int mClickedItemPosition = -1;

    public abstract int getChildItemCount();

    @Override
    public final int getItemCount() {
        int count = getChildItemCount();
        if (hasHeader){
            count++;
        }

        return count;
    }

    protected abstract T onCreateItemViewHolder(ViewGroup viewGroup, int type);

    protected abstract T onCreateHeaderViewHolder(ViewGroup viewGroup, int type);

    @Override
    public T onCreateViewHolder(ViewGroup viewGroup, int type) {
        if(type == TYPE_ITEM) {
            return onCreateItemViewHolder(viewGroup, type);
        } else if (type == TYPE_HEADER){
            return onCreateHeaderViewHolder(viewGroup, type);
        }

        return null;
    }

    public int getItemPositionOffset(){
        return hasHeader? 1: 0;
    }

    protected abstract void onBindHeaderViewHolder(T vh, int position);

    protected abstract void onBindItemViewHolder(T vh, int position);

    @Override
    public final void onBindViewHolder(T vh, int position) {

        if (hasHeader && position == 0){
            onBindHeaderViewHolder(vh, position);
        } else {
            onBindItemViewHolder(vh, position);
        }
    }

    protected void setHasHeader(boolean hasHeader){
        this.hasHeader = hasHeader;
    }

    @Override
    public int getItemViewType(int position) {
        if(hasHeader && position == 0){
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    public int getClickedItemPosition() {
        return mClickedItemPosition;
    }

    public void setClickedItemPosition(int clickedItemPosition) {
        this.mClickedItemPosition = clickedItemPosition;
    }
}
