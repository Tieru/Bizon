package pro.asdgroup.bizon.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.asdgroup.bizon.R;
import pro.asdgroup.bizon.model.DayQuestion;
import pro.asdgroup.bizon.util.BitmapResizeTransformation;
import pro.asdgroup.bizon.util.DocumentExifTransformation;

/**
 * Created by vvoronov on 09/07/15.
 */
public class CommentEditActivity extends AppCompatActivity {

    public final static String ARG_TEXT_LIMIT = "text_limit";
    public final static String ARG_HAS_ADD_CONTENT = "has_additional_content";
    public final static String ARG_TEXT = "text";
    public final static String ARG_ADD_CONTENT = "additional_content";
    public final static String ARG_QUESTION_OF_DAY = "question_of_day";
    //public final static String ARG_ADD_CONTENT_UPDATED = "add_content_updated";

    public final static int POST_MAX_CHARS = 140;

    private static final int GALLERY_RES_CODE = 100;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.commentEdit) EditText mCommentEdit;
    @InjectView(R.id.addContentLayout) FrameLayout mAddContentLayout;
    @InjectView(R.id.symbolCountText) TextView mSymbolCountText;
    @InjectView(R.id.questionText) TextView mQuestionText;
    @InjectView(R.id.postImageView) ImageView mPostImage;
    //@InjectView(R.id.removeImageButton) View mPostImageRemoveButton;

    private boolean mHasAdditionalContent;
    private boolean mImageIsPicked;
    //private boolean mImageIsUpdated;

    private DayQuestion mDayQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment_edit);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();

    }

    private void initUI(){
        Intent intent = getIntent();
        mHasAdditionalContent = intent.getBooleanExtra(ARG_HAS_ADD_CONTENT, false);
        String text = intent.getStringExtra(ARG_TEXT);
        mCommentEdit.setText(text);

        if (mHasAdditionalContent){
            String imageUrl = intent.getStringExtra(ARG_ADD_CONTENT);
            if (imageUrl != null && !imageUrl.trim().isEmpty()){
                mImageIsPicked = true;
                mAddContentLayout.setVisibility(View.VISIBLE);
                Picasso.with(this).load(imageUrl)
                        .placeholder(R.drawable.no_image)
                        .transform(new BitmapResizeTransformation())
                        .error(R.drawable.no_image)
                        .into(mPostImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                mImageIsPicked = false;
                                invalidateOptionsMenu();
                                mAddContentLayout.setVisibility(View.GONE);
                            }
                        });

            }
        }

        mDayQuestion = (DayQuestion) intent.getSerializableExtra(ARG_QUESTION_OF_DAY);
        if (mDayQuestion != null){
            String questionPrefix = getString(R.string.feed_post_create_label_question) + " ";
            SpannableStringBuilder sb = new SpannableStringBuilder(questionPrefix + mDayQuestion.getText());
            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(bss, questionPrefix.length(), sb.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mQuestionText.setText(sb);
        }

        final int textLimit = intent.getIntExtra(ARG_TEXT_LIMIT, -1);

        if (textLimit != -1){
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(textLimit);
            mCommentEdit.setFilters(filterArray);

            mCommentEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String countValue = s.length() + "/" + textLimit;
                    mSymbolCountText.setText(countValue);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit, menu);

        if (mHasAdditionalContent && !mImageIsPicked){
            getMenuInflater().inflate(R.menu.attach, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.submit:
                onSubmitClick();
                return true;
            case R.id.action_attach:
                onAttachClicked();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSubmitClick(){
        Intent intent = new Intent();
        intent.putExtra(ARG_TEXT, mCommentEdit.getText().toString());
        if (mImageIsPicked){
            intent.putExtra(ARG_ADD_CONTENT, getEncodedImage());
        }
/*        if (mImageIsUpdated) {
            intent.putExtra(ARG_ADD_CONTENT_UPDATED, true);
        }*/

        if (mDayQuestion != null){
            intent.putExtra(ARG_QUESTION_OF_DAY, mDayQuestion);
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void onAttachClicked(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.pp_choose_image)), GALLERY_RES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_RES_CODE:
                    processGalleryImage(data);
                    return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processGalleryImage(Intent intent){
        Uri uri = intent.getData();

        Picasso.with(this).load(uri)
                .placeholder(R.drawable.no_image)
                .transform(new DocumentExifTransformation(this, uri))
                .transform(new BitmapResizeTransformation())
                .error(R.drawable.no_image)
                .into(mPostImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        mImageIsPicked = true;
                        mAddContentLayout.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @OnClick(R.id.removeImageButton)
    void onRemoveImageButtonClick(){
        mImageIsPicked = false;
        invalidateOptionsMenu();
        mAddContentLayout.setVisibility(View.GONE);
    }

    private String getEncodedImage() {
        Bitmap bitmap = ((BitmapDrawable) mPostImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, byteStream);
        byte[] byteArray = byteStream.toByteArray();
        return Base64.encodeToString(byteArray, 0);
    }
}
