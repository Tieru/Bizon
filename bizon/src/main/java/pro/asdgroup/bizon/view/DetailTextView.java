package pro.asdgroup.bizon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.BizonApp;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class DetailTextView extends LinearLayout {

    protected static final int DEFAULT_TITLE_TEXT_COLOR = BizonApp.getAppContext().getResources().getColor(R.color.textBlue);    //Color.parseColor("#95B1C7");
    protected static final int DEFAULT_DETAIL_TEXT_COLOR = Color.BLACK;
    protected static final int DEFAULT_DIVIDER_COLOR = Color.GRAY;

    private TextView titleTextView;
    private TextView detailTextView;
    private View dividerView;

    private String titleText;
    private String detailText;
    private float titleTextSize;
    private float detailTextSize;
    private int titleTextColor;
    private int detailTextColor;
    private int dividerColor;
    private float dividerHeight;
    private float contentPadding;


    public DetailTextView(Context context) {
        super(context);
        initialize(context);
    }

    public DetailTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
        setAttributes(attrs);
    }

    public DetailTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context);

        setAttributes(attrs);
    }

    private void initialize(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.detail_text_view_layout, this, true);

        titleTextView = (TextView) findViewById(R.id.title_text);
        LinearLayout.LayoutParams params = (LayoutParams) titleTextView.getLayoutParams();
        params.leftMargin = (int) contentPadding;
        params.rightMargin = (int) contentPadding;
        titleTextView.setLayoutParams(params);

        detailTextView = (TextView) findViewById(R.id.detail_text);
        params = (LayoutParams) detailTextView.getLayoutParams();
        params.leftMargin = (int) contentPadding;
        params.rightMargin = (int) contentPadding;
        detailTextView.setLayoutParams(params);

        dividerView = findViewById(R.id.divider_view);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GretaTextPro-Bold.otf");
        titleTextView.setTypeface(font);

        font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MinionPro-Regular_0.otf");
        detailTextView.setTypeface(font);

    }

    protected void setAttributes(AttributeSet attrs){

        getAttributesValues(attrs);

        applyAttributes();
    }


    protected void getAttributesValues(AttributeSet attrs){
        Context ctx = getContext();

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.DetailTextView);

        try {
            titleText = a.getString(R.styleable.DetailTextView_titleText);
            detailText = a.getString(R.styleable.DetailTextView_detailText);
            titleTextSize = a.getFloat(R.styleable.DetailTextView_titleTextSize,
                    ctx.getResources().getDimension(R.dimen.default_title_text_size));
            detailTextSize = a.getFloat(R.styleable.DetailTextView_detailTextSize,
                    ctx.getResources().getDimension(R.dimen.default_detail_text_size));
            titleTextColor = a.getColor(R.styleable.DetailTextView_titleTextColor, DEFAULT_TITLE_TEXT_COLOR);
            detailTextColor = a.getColor(R.styleable.DetailTextView_detailTextColor, DEFAULT_DETAIL_TEXT_COLOR);
            dividerHeight = a.getFloat(R.styleable.DetailTextView_dividerHeight,
                    ctx.getResources().getDimension(R.dimen.default_divider_height));
            dividerColor = a.getColor(R.styleable.DetailTextView_titleTextColor, DEFAULT_DIVIDER_COLOR);
            contentPadding = a.getFloat(R.styleable.DetailTextView_titleTextColor,
                    ctx.getResources().getDimension(R.dimen.default_content_padding));
        } finally {
            a.recycle();
        }
    }

    protected void applyAttributes(){
        titleTextView.setText(titleText);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        titleTextView.setTextColor(titleTextColor);

        detailTextView.setText(detailText);
        detailTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, detailTextSize);
        detailTextView.setTextColor(detailTextColor);

        dividerView.setBackgroundColor(dividerColor);
        dividerView.getLayoutParams().height = (int) dividerHeight;
    }


    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        titleTextView.setText(titleText);
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
        detailTextView.setText(detailText);
    }

    public float getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        this.titleTextSize = titleTextSize;
        titleTextView.setTextSize(titleTextSize);
    }

    public float getDetailTextSize() {
        return detailTextSize;
    }

    public void setDetailTextSize(float detailTextSize) {
        this.detailTextSize = detailTextSize;
        detailTextView.setTextSize(detailTextSize);
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        titleTextView.setTextColor(titleTextColor);
    }

    public int getDetailTextColor() {
        return detailTextColor;
    }

    public void setDetailTextColor(int detailTextColor) {
        this.detailTextColor = detailTextColor;
        detailTextView.setTextColor(detailTextColor);
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        dividerView.setBackgroundColor(dividerColor);
    }

    public float getDividerHeight() {
        return dividerHeight;
    }

    public void setDividerHeight(float dividerHeight) {
        this.dividerHeight = dividerHeight;
        dividerView.getLayoutParams().height = (int) dividerHeight;
    }

    public void setDetailTextTypeface(Typeface font){
        detailTextView.setTypeface(font);
    }
}
