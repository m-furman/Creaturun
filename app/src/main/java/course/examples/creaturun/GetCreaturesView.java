package course.examples.creaturun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Jon on 11/29/16.
 */

public class GetCreaturesView extends RelativeLayout {
    static float scaleFactor = .125f;
    static float chestVerticalAlignment = .3f;
    static float keyVerticalAlignment = .8f;
    static RectF keyUnlockZone = new RectF(.4f, .25f, .6f, .4f);
    static float creatureVerticalAlignment = .8f;

    Context context;
    public enum AnimationState {
        STATIC_CLOSED,
        DRAGGING_CLOSED,
        OPENING,
        OPENED
    }
    AnimationState animationState = AnimationState.STATIC_CLOSED;

    Bitmap chestClosed = null;
    Bitmap chestOpen = null;
    Bitmap key = null;

    ///////////////////state variables for:
    //STATIC_CLOSED
    Rect keyRect = null;
    //DRAGGING_CLOSED
    Point keyOffset = null;
    Point currentKeyPoint = null;
    //OPENING
    Bitmap[] creatures = null;
    //OPENED

    public GetCreaturesView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public GetCreaturesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GetCreaturesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.context = context;
        invalidate();
        setWillNotDraw(false);
        chestClosed = loadScaledBitmap(R.drawable.chestclosed);
        chestOpen = loadScaledBitmap(R.drawable.chestopen);
        key = loadScaledBitmap(R.drawable.key);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Point chestCenter = new Point((int)(canvas.getWidth() / 2), (int)(chestVerticalAlignment * canvas.getHeight()));
        switch (animationState) {
            case STATIC_CLOSED:
                drawBitmapCentered(canvas, chestClosed, chestCenter);
                Point keyCenter = new Point((int)(canvas.getWidth() / 2), (int)(keyVerticalAlignment * canvas.getHeight()));
                keyRect = drawBitmapCentered(canvas, key, keyCenter);
                return;
            case DRAGGING_CLOSED:
                drawBitmapCentered(canvas, chestClosed, chestCenter);
                drawBitmapCentered(canvas, key, currentKeyPoint);
                return;
            case OPENING:
                //add animation here
                return;
            case OPENED:
                drawBitmapCentered(canvas, chestOpen, chestCenter);
                for (int i = 0; i < creatures.length; i++) {
                    Point creaturePoint = new Point((int)(canvas.getWidth() * (i + .5f) / creatures.length),
                            (int)(canvas.getHeight() * creatureVerticalAlignment));
                    drawBitmapCentered(canvas, creatures[i], creaturePoint);
                }
                return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int touchX = (int)motionEvent.getX();
        int touchY = (int)motionEvent.getY();
        switch (animationState) {
            case STATIC_CLOSED:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (insideMask(key, keyRect, new Point(touchX, touchY))) {
                        animationState = AnimationState.DRAGGING_CLOSED;
                        keyOffset = new Point (touchX - keyRect.centerX(), touchY - keyRect.centerY());
                    }
                }
                return true;
            case DRAGGING_CLOSED:
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    if (keyUnlockZone.contains((float)touchX / getWidth(), (float)touchY / getHeight())) {
                        animationState = AnimationState.OPENING;
                        startOpening();
                    } else {
                        currentKeyPoint = new Point(touchX - keyOffset.x, touchY - keyOffset.y);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    animationState = AnimationState.STATIC_CLOSED;
                }
                invalidate();
                return true;
            case OPENED:
                return true;
            default:
                return true;
        }
    }

    private void startOpening() {
        //add some animation here instead of just setting it
        animationState = AnimationState.OPENED;
        //add some generation of random creatures or something here
        creatures = new Bitmap[] {loadScaledBitmap(R.drawable.pumpkin),loadScaledBitmap(R.drawable.sanic)};
        Button button = new Button(context);
        button.setText("BACK TO MENU");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //doesn't do anything right now, add function to go back to menu
            }
        });
        addView(button);
        invalidate();
    }

    //laods a bitmap that is scaled down
    private Bitmap loadScaledBitmap(int resource) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap unscaled = ((BitmapDrawable) drawable).getBitmap();
        return Bitmap.createScaledBitmap(unscaled, (int)(unscaled.getWidth()*scaleFactor),
                (int)(unscaled.getHeight()*scaleFactor), true);
    }

    //draws a bitmap centered at a point instead of from top left corner
    private Rect drawBitmapCentered(Canvas canvas, Bitmap bitmap, Point center) {
        int left = center.x - bitmap.getWidth() / 2;
        int top = center.y - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, left, top, null);
        return new Rect(left, top, left + bitmap.getWidth(), top + bitmap.getHeight());
    }

    //checks if the click is on a nontransparent pixel of the bitmap
    private boolean insideMask(Bitmap bitmap, Rect rect, Point point) {
        if (!rect.contains(point.x, point.y)) {
            return false;
        } else {
            int inBitmapX = point.x - rect.left;
            int inBitmapY = point.y - rect.top;
            return Color.alpha(bitmap.getPixel(inBitmapX, inBitmapY)) > 0;
        }
    }
}
