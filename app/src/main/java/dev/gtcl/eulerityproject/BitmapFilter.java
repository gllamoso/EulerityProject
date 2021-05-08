package dev.gtcl.eulerityproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.annotation.ColorInt;

public class BitmapFilter {

    public static Bitmap applyGrayscale(Bitmap src){
        final int width = src.getWidth();
        final int height = src.getHeight();

        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());
        final Canvas c = new Canvas(res);
        final Paint p = new Paint();
        final ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        final ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        p.setColorFilter(f);
        c.drawBitmap(src, 0, 0, p);
        src.recycle();
        return res;
    }

    public static Bitmap applyVignette(Bitmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final Bitmap res = src.copy(src.getConfig(), true);

        float radius = (float) (width/1.2);
        int[] colors = new int[] { 0, 0x55000000, 0xFF000000 };
        float[] positions = new float[] { 0.0F, 0.5F, 1.0F };

        final RadialGradient gradient = new RadialGradient(width / 2F, height / 2F, radius, colors, positions, Shader.TileMode.CLAMP);

        final Canvas c = new Canvas(res);
        c.drawARGB(1, 0, 0, 0);

        final Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.BLACK);
        p.setShader(gradient);

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectf = new RectF(rect);

        c.drawRect(rectf, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(res, rect, rect, p);
        src.recycle();
        return res;
    }

    public static Bitmap applySepia(Bitmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                final int pixel = src.getPixel(x, y);

                final int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                blue = green = red = (int)(0.3 * red + 0.59 * green + 0.11 * blue);

                red += 110;
                red = Math.min(255, red);

                green += 65;
                green = Math.min(255, green);

                blue += 20;
                blue = Math.min(255, blue);

                res.setPixel(x, y, Color.argb(alpha, red, green, blue));
            }
        }
        src.recycle();
        return res;
    }

    public static Bitmap applyColorInversion(Bitmap src) {
        final int height = src.getHeight();
        final int width = src.getWidth();
        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pixelColor = src.getPixel(x, y);
                final int alpha = Color.alpha(pixelColor);
                final int red = 255 - Color.red(pixelColor);
                final int green = 255 - Color.green(pixelColor);
                final int blue = 255 - Color.blue(pixelColor);

                res.setPixel(x, y, Color.argb(alpha, red, green, blue));
            }
        }
        src.recycle();
        return res;
    }

    // value = [-255, 255]
    public static Bitmap applyBrightness(Bitmap src, int value) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                final int pixel = src.getPixel(x, y);
                final int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // increase/decrease each channel
                red += value;
                red = Math.max(red, 0);
                red = Math.min(red, 255);

                green += value;
                green = Math.max(green, 0);
                green = Math.min(green, 255);

                blue += value;
                blue = Math.max(blue, 0);
                blue = Math.min(blue, 255);

                // apply new pixel color to output bitmap
                res.setPixel(x, y, Color.argb(alpha, red, green, blue));
            }
        }
        src.recycle();
        return res;
    }

    // value = [-100, +100]
    public static Bitmap applyContrast(Bitmap src, double value) {
        final int width = src.getWidth();
        final int height = src.getHeight();

        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());
        final Canvas c = new Canvas();
        c.setBitmap(res);
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));

        final double contrast = Math.pow((100 + value) / 100, 2);

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                final int pixel = src.getPixel(x, y);
                final int alpha = Color.alpha(pixel);

                int red = Color.red(pixel);
                red = (int)(((((red / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                red = Math.max(red, 0);
                red = Math.min(red, 255);

                int green = Color.green(pixel);
                green = (int)(((((green / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                green = Math.max(green, 0);
                green = Math.min(green, 255);

                int blue = Color.blue(pixel);
                blue = (int)(((((blue / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                blue = Math.max(blue, 0);
                blue = Math.min(blue, 255);

                res.setPixel(x, y, Color.argb(alpha, red, green, blue));
            }
        }
        src.recycle();
        return res;
    }

    // value = [0, 200]
    public static Bitmap applySaturation(Bitmap src, int value) {
        final int width = src.getWidth();
        final int height = src.getHeight();

        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());
        final Canvas c = new Canvas(res);
        final Paint p = new Paint();
        final ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation((float)(value / 100.0));
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        p.setColorFilter(filter);
        c.drawBitmap(src, 0, 0, p);
        src.recycle();
        return res;
    }

    // hue = [0, 360]
    public static Bitmap applyHue(Bitmap src, float hue) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final Bitmap res = src.copy(src.getConfig(), true);
        float [] hsv = new float[3];

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                final int pixel = res.getPixel(x,y);
                Color.colorToHSV(pixel,hsv);
                hsv[0] = hue;
                res.setPixel(x,y,Color.HSVToColor(Color.alpha(pixel),hsv));
            }
        }
        src.recycle();
        return res;
    }

    public static Bitmap applyTint(Bitmap src, @ColorInt int color) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final Bitmap res = Bitmap.createBitmap(width, height, src.getConfig());

        final Paint p = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        final Canvas c = new Canvas(res);
        c.drawBitmap(src, 0, 0, p);
        src.recycle();
        return res;
    }

    public static Bitmap applyRotation(Bitmap src, float degrees) {
        final Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap res = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        src.recycle();
        return res;
    }

    public static Bitmap applyFlip(Bitmap src, boolean horizontal, boolean vertical) {
        final Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        Bitmap res = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        src.recycle();
        return res;
    }

    public static Bitmap addText(Context context, Bitmap src, String text, @ColorInt int color, int textSize){
        final float scale = context.getResources().getDisplayMetrics().density;
        final int height = src.getHeight();
        final int width = src.getWidth();
        final Bitmap res = src.copy(src.getConfig(), true);

        final Rect rect = new Rect(0, 0, width, height/2); // place text on top
        final RectF rectf = new RectF(rect);

        final Canvas c = new Canvas(res);
        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(color);
        textPaint.setTextSize((int) (textSize * scale));
        final StaticLayout textLayout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_CENTER, 1F, 1F, false);

        c.save();
        c.translate(rectf.left, rectf.top);
        textLayout.draw(c);
        c.restore();
        src.recycle();
        return res;
    }
}
