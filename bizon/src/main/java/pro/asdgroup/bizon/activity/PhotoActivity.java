package pro.asdgroup.bizon.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.asdgroup.bizon.R;

/**
 * Created by vvoronov on 08/07/15.
 */
public class PhotoActivity extends AppCompatActivity {

    public final static String ARG_IMAGE = "image";

    @InjectView(R.id.image) ImageView mImageView;
    @InjectView(R.id.progressBar) View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo);

        ButterKnife.inject(this);

        String imageUrl = getIntent().getStringExtra(ARG_IMAGE);

        if (imageUrl == null || imageUrl.isEmpty()){
            onFailImageDownload();
            return;
        }

        Picasso.with(this)
                .load(imageUrl)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        onFailImageDownload();
                    }
                });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onFailImageDownload(){
        Picasso.with(this)
                .load(R.drawable.no_image)
                .into(mImageView);
        mProgressBar.setVisibility(View.GONE);
    }
}
