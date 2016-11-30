package course.examples.creaturun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jon on 11/29/16.
 */

public class GetCreaturesView extends View {
    public enum AnimationState {
        STATIC_CLOSED,
        DRAGGING_CLOSED,
        OPENING,
        OPENED
    }
    AnimationState animationState = AnimationState.STATIC_CLOSED;

    public GetCreaturesView(Context context) {
        super(context);
        init(null, 0);
    }

    public GetCreaturesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GetCreaturesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        switch (animationState) {
            case STATIC_CLOSED:
                Drawable chestClosedDrawable = getResources().getDrawable(R.drawable.chestclosed);
                Bitmap chestClosed = ((BitmapDrawable) chestClosedDrawable).getBitmap();
                canvas.drawBitmap(chestClosed, 0, 0, null);
                return;
            case DRAGGING_CLOSED:
                return;
            case OPENING:
                return;
            case OPENED:
                return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return  true;
    }
}
