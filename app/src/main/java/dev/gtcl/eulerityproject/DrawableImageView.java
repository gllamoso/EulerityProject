package dev.gtcl.eulerityproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DrawableImageView extends androidx.appcompat.widget.AppCompatImageView {

    private boolean isPainting = false;
    private final List<Path> paths = new ArrayList<>();
    private final List<Paint> paints = new ArrayList<>();
    private final Paint backgroundPaint = new Paint();
    {
        backgroundPaint.setColor(Color.TRANSPARENT);
    }
    private Bitmap bitmap;
    private RectF rectF;
    private StaticLayout textLayout;

    public DrawableImageView(@NonNull @NotNull Context context) {
        super(context);
    }

    public DrawableImageView(@NonNull @NotNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableImageView(@NonNull @NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void stopPainting(){
        this.isPainting = false;
    }

    public void startPainting(@ColorInt int color, int strokeWidth){
        isPainting = true;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paints.add(paint);
        paths.add(new Path());
    }

    public void eraseLastDrawing(){
        if(paths.isEmpty()){
            return;
        }
        int lastIndex = paints.size() - 1;
        paths.remove(lastIndex);
        paints.remove(lastIndex);
        invalidate();
    }

    public void eraseAllDrawings(){
        paths.clear();
        paints.clear();
    }

    public void clearText(){
        textLayout = null;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmap = bm;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, w, h / 2);
        rectF = new RectF(rect);
    }

    public void setText(String text, @ColorInt int color, int sp){
        TextPaint textPaint;
        if(text == null || text.isEmpty()){
            textLayout = null;
        } else {
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(color);
            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics()));
            textLayout = new StaticLayout(text, textPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1F, 1F, false);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isPainting || paths.isEmpty()){
            return super.onTouchEvent(event);
        }
        Path curPath = paths.get(paths.size() - 1);
        final float touchX = event.getX();
        final float touchY = event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                curPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                curPath.lineTo(touchX, touchY);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bitmap != null){
            canvas.drawBitmap(bitmap, 0, 0, backgroundPaint);
            for(int i = 0; i < paths.size(); i++){
                Path touchPath = paths.get(i);
                Paint touchPaint = paints.get(i);
                canvas.drawPath(touchPath, touchPaint);
            }

            if(textLayout != null){
                canvas.save();
                canvas.translate(rectF.left, rectF.top);
                textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    public Bitmap createBitmap(){
        Bitmap res = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(res);
        draw(c);
        return res;
    }
}
