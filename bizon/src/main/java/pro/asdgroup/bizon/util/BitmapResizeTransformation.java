package pro.asdgroup.bizon.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by Tieru on 30.05.2015.
 */
public class BitmapResizeTransformation implements Transformation {

    @Override public Bitmap transform(Bitmap source) {

        if (source.getHeight() <= 600 && source.getHeight() <= 600){
            return source;
        }

        float height = source.getHeight();
        float width = source.getWidth();

        if (height >= width){
            float proportion = width / height;
            height = 600;
            width = proportion * (float)600;
        } else {
            float proportion = height / width;
            width = 600;
            height = proportion * (float)600;
        }

        //return Bitmap.createScaledBitmap(source, (int)width, (int)height, true);

        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, source.getWidth(), source.getHeight()),
                new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);

        Bitmap resultBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, true);

        if (resultBitmap != source) {
            source.recycle();
        }

        return resultBitmap;



        /*int x = (source.getWidth() - size);
        int y = (source.getHeight() - size);

        Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
        if (result != source) {
            source.recycle();
        }
        return result;*/
    }

    @Override public String key() { return "resized"; }
}
