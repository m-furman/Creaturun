package course.examples.creaturun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jon on 11/29/16.
 */

public class GetCreaturesView extends RelativeLayout {
    static float scaleFactor = .125f;
    static float chestVerticalAlignment = .3f;
    static float keyVerticalAlignment = .8f;
    static RectF keyUnlockZone = new RectF(.4f, .25f, .6f, .4f);
    static float creatureVerticalAlignment = .8f;
    static int frameRate = 30;
    static long totalOpeningTime = 500;
    static int poofCount = 15;
    static float poofRadius = .4f;

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
    Bitmap[] poofs;
    Point[] finalPoofPositions;
    float currentOpenTime;
    Paint alphaPaint = new Paint();
    //OPENED
    boolean bitmapsDrawn = false;
    Creature[] creatures = null;
    Rect[] creatureRects = null;
    Bitmap[] creatureBitmaps = null;
    int selectedCreature = -1;

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
        chestClosed = loadScaledBitmap(R.drawable.chestclosed, 1);
        chestOpen = loadScaledBitmap(R.drawable.chestopen, 1);
        key = loadScaledBitmap(R.drawable.key, 1);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Point chestCenter = new Point((int)(canvas.getWidth() / 2), (int)(chestVerticalAlignment * canvas.getHeight()));
        switch (animationState) {
            case STATIC_CLOSED:
                drawBitmapCentered(canvas, chestClosed, chestCenter, null);
                Point keyCenter = new Point((int)(canvas.getWidth() / 2), (int)(keyVerticalAlignment * canvas.getHeight()));
                keyRect = drawBitmapCentered(canvas, key, keyCenter, null);
                return;
            case DRAGGING_CLOSED:
                drawBitmapCentered(canvas, chestClosed, chestCenter, null);
                drawBitmapCentered(canvas, key, currentKeyPoint, null);
                return;
            case OPENING:
                drawBitmapCentered(canvas, chestOpen, chestCenter, null);
                if (poofs != null) {
                    float lerp = currentOpenTime / totalOpeningTime;
                    for (int i = 0; i < creatures.length; i++) {
                        Point creatureFinalPoint = new Point((int) (canvas.getWidth() * (i + .5f) / creatures.length),
                                (int) (canvas.getHeight() * creatureVerticalAlignment));
                        Point creaturePoint = new Point((int) (lerp * creatureFinalPoint.x + (1-lerp)*chestCenter.x),
                                (int) (lerp * creatureFinalPoint.y + (1-lerp)*chestCenter.y));
                        drawBitmapCentered(canvas, creatureBitmaps[i], creaturePoint, null);
                    }

                    float easing = 2 * (float) Math.sqrt(currentOpenTime / totalOpeningTime);
                    int alpha = (int) (255f * (1 - Math.pow (2 * currentOpenTime / totalOpeningTime - 1, 2)));
                    alphaPaint.setAlpha(alpha);
                    for (int i = 0; i < poofs.length; i++) {
                        Point centerPoint = new Point(chestCenter.x + (int) (finalPoofPositions[i].x * easing),
                                chestCenter.y + (int) (finalPoofPositions[i].y * easing));
                        drawBitmapCentered(canvas, poofs[i], centerPoint, alphaPaint);
                    }
                }
                return;
            case OPENED:
                drawBitmapCentered(canvas, chestOpen, chestCenter, null);
                for (int i = 0; i < creatures.length; i++) {
                    Point creaturePoint = new Point((int)(canvas.getWidth() * (i + .5f) / creatures.length),
                            (int)(canvas.getHeight() * creatureVerticalAlignment));
                    creatureRects[i] = drawBitmapCentered(canvas, creatureBitmaps[i], creaturePoint, null);
                }
                bitmapsDrawn = true;
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
                        invalidate();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    animationState = AnimationState.STATIC_CLOSED;
                    invalidate();
                }
                return true;
            case OPENED:
                if (bitmapsDrawn) {
                    for (int i = 0; i < creatures.length; i++) {
                        if (insideMask(creatureBitmaps[i], creatureRects[i], new Point(touchX, touchY))) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                selectedCreature = i;
                            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                if (i == selectedCreature) {
                                    CreaturePopupWindow popup = new CreaturePopupWindow(creatures[i], context);
                                    popup.showPopup();
                                }
                                selectedCreature = -1;
                            }
                        }
                    }
                }
                return true;
            default:
                return true;
        }
    }

    //starts opening animation
    private void startOpening() {
        //add some generation of random creatures or something here
        creatures = new Creature[] {new Creature(Creature.CreatureType.CAT), new Creature(Creature.CreatureType.SANIC)};
        creatureBitmaps = new Bitmap[creatures.length];
        creatureRects = new Rect[creatures.length];
        for (int i = 0; i < creatures.length; i++) {
            creatureBitmaps[i] = loadScaledBitmap(creatures[i].getImageResource(), 1);
        }

        Timer timer = new Timer();
        timer.schedule(new OpeningTimerTask(), 0, 1000 / frameRate);
    }

    //sets up for final state
    private void fullOpen () {
        Button button = new Button(context);
        button.setText("BACK TO MENU");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
        addView(button);

        invalidate();
        animationState = AnimationState.OPENED;
    }

    //loads a bitmap that is scaled down
    private Bitmap loadScaledBitmap(int resource, float rescale) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap unscaled = ((BitmapDrawable) drawable).getBitmap();
        return Bitmap.createScaledBitmap(unscaled, (int)(unscaled.getWidth()*scaleFactor*rescale),
                (int)(unscaled.getHeight()*scaleFactor*rescale), true);
    }

    //draws a bitmap centered at a point instead of from top left corner
    private Rect drawBitmapCentered(Canvas canvas, Bitmap bitmap, Point center, Paint paint) {
        int left = center.x - bitmap.getWidth() / 2;
        int top = center.y - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, left, top, paint);
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

    //opening animation handler timer task
    public class OpeningTimerTask extends TimerTask {
        long startTime;

        public OpeningTimerTask () {
            startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();

            if (currentTime - startTime > totalOpeningTime) {
                GetCreaturesView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        fullOpen();
                    }
                });
                cancel();
            } else {
                currentOpenTime = currentTime - startTime;

                if (poofs == null) {
                    poofs = new Bitmap[poofCount];
                    finalPoofPositions = new Point[poofCount];
                    for (int i = 0; i < poofCount; i++) {
                        double r = Math.random();
                        float size = 2f + (float)Math.random();
                        if (r > .5f) poofs[i] = loadScaledBitmap(R.drawable.poof1, size);
                        else poofs[i] = loadScaledBitmap(R.drawable.poof2, size);
                        double radius = .75f + .25f * Math.random();
                        double angle = Math.random() * Math.PI * 2;
                        finalPoofPositions[i] = new Point((int)(radius * Math.cos(angle) * poofRadius * GetCreaturesView.this.getWidth()),
                                (int)(radius * Math.sin(angle) * poofRadius * GetCreaturesView.this.getWidth()));
                    }
                }
            }

            GetCreaturesView.this.post(new Runnable () {
                @Override
                public void run() {
                    invalidate();
                }
            });
        }
    }
}
