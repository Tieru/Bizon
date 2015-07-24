package pro.asdgroup.bizon.util;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Voronov Viacheslav on 07.07.2015.
 */
public class RecyclerViewClicks implements RecyclerView.OnItemTouchListener {

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    private GestureDetector mGestureDetector;
    private ClickListener mListener;
    private RecyclerView mList;

    public RecyclerViewClicks (RecyclerView list, ClickListener listener){
        mListener = listener;
        mList = list;
        mGestureDetector = new GestureDetector(list.getContext(), new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if (mListener != null) {
                    View view = mList.findChildViewUnder(e.getX(), e.getY());
                    int position = mList.getChildAdapterPosition(view);
                    mListener.onLongClick(view, position);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View view = rv.findChildViewUnder(e.getX(), e.getY());
        if (view != null && mListener != null && mGestureDetector.onTouchEvent(e)){
            mListener.onClick(view, mList.getChildAdapterPosition(view));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}