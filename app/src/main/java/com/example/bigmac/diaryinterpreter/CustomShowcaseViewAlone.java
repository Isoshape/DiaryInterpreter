package com.example.bigmac.diaryinterpreter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.github.amlcurran.showcaseview.ShowcaseDrawer;

/**
 * Created by BigMac on 24/12/16.
 */
public class CustomShowcaseViewAlone implements ShowcaseDrawer {

    private final float width;
    private final float height;
    private final Paint eraserPaint;
    private final Paint basicPaint;
    private final int eraseColour;
    private final RectF renderRect;

    public CustomShowcaseViewAlone(Resources resources) {
        width = resources.getDimension(R.dimen.custom_showcase_width);
        height = resources.getDimension(R.dimen.custom_showcase_height);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        eraseColour = resources.getColor(R.color.custom_showcase_bg);
        basicPaint = new Paint();
        renderRect = new RectF();
    }

    @Override
    public void setShowcaseColour(int color) {
        eraserPaint.setColor(color);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        bufferCanvas.getWidth();

        renderRect.left = x - bufferCanvas.getWidth() / 2f;
        renderRect.right = x + bufferCanvas.getWidth() / 2f;
        renderRect.top = y - height;
        renderRect.bottom = y + height;
        bufferCanvas.drawRect(renderRect, eraserPaint);
    }

    @Override
    public int getShowcaseWidth() {
        return (int) width;
    }

    @Override
    public int getShowcaseHeight() {
        return (int) height;
    }

    @Override
    public float getBlockedRadius() {
        return width;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {

    }

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(eraseColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }

}

