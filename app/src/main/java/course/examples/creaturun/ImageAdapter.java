package course.examples.creaturun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Mark on 11/30/2016.
 * Adapted from: https://developer.android.com/guide/topics/ui/layout/gridview.html
 */


public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            // below is where we set the size of the images to be loaded in
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Bitmap bitmap = ((BitmapDrawable)ContextCompat.getDrawable(mContext,mThumbIds[position])).getBitmap();
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        imageView.setImageBitmap(scaled);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.cat,
            R.drawable.fish,
            R.drawable.pig,
            R.drawable.pumpkin,
            R.drawable.sanic,
            R.drawable.snail,
            R.drawable.brown_creature_centered,
            R.drawable.blue_cat_thing,
            R.drawable.weird_green_blob
    };
}
