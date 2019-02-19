package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by ASUS on 6/5/2017.
 */

public class BitmapBluetooth {
    public Canvas canvas = null;
    public Paint paint = null;
    public Bitmap bm = null;
    public int width;
    public float length = 0.0F;
    public byte[] bitbuf = null;

    public BitmapBluetooth() {
    }

    public int getLength() {
        return (int) this.length + 20;
    }

    public void initCanvas(int w) {
        int h = 10 * w;
        try {
            this.bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (bm != null) {
            this.canvas = new Canvas(this.bm);
            this.canvas.drawColor(-1);
            this.width = w;
            this.bitbuf = new byte[this.width / 8];
        }
    }

    public void initCanvas(int w, String path, String text) {
        Bitmap e = BitmapFactory.decodeFile(path);
        Paint paint = new Paint();
        paint.setTextSize(25);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        try {
            this.bm = Bitmap.createBitmap((e.getWidth() * 2) + rect.width(), e.getHeight() + 20, Bitmap.Config.ARGB_4444);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (bm != null) {
            this.canvas = new Canvas(this.bm);
            this.canvas.drawColor(-1);
            this.width = w;
            this.bitbuf = new byte[this.width / 8];
        }
    }

    public void initCanvas(int w, int h, String path) {
        try {
            this.bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bm != null) {
            this.canvas = new Canvas(this.bm);
            this.canvas.drawColor(-1);
            this.width = w;
            this.bitbuf = new byte[this.width / 8];
        }
    }

    public void initPaint() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(-16777216);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setTextAlign(Paint.Align.CENTER);
    }

    public void drawImageToBitmap(float x, float y, String path) {
        if (canvas != null)
            try {
                Bitmap e = BitmapFactory.decodeFile(path);
                this.canvas.drawBitmap(bitmapConvertMonochrome(e), x, y, null);
                if (this.length < y + (float) e.getHeight()) {
                    this.length = y + (float) e.getHeight();
                }

            } catch (Exception var5) {
                var5.printStackTrace();
            }

    }

    public void drawImage(float x, float y, String path) {
        if (canvas != null)
            try {
                Bitmap e = BitmapFactory.decodeFile(path);
                this.canvas.drawBitmap(e, x, y, null);
                if (this.length < y + (float) e.getHeight()) {
                    this.length = y + (float) e.getHeight();
                }

            } catch (Exception var5) {
                var5.printStackTrace();
            }

    }

    public void drawImageSunmi(String path) {
        if (canvas != null)
            try {
                Bitmap e = BitmapFactory.decodeFile(path);
                float y = (canvas.getHeight() / e.getHeight()) / 2;
                this.canvas.drawBitmap(bitmapConvertMonochrome(e), ((canvas.getWidth() / 2) - ((e.getWidth() / 2) + (e.getWidth() / 4))),
                        y, null);
                if (this.length < y + (float) e.getHeight()) {
                    this.length = y + (float) e.getHeight();
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }

    }

    public void drawImage(String path) {
        if (canvas != null)
            try {
                Bitmap e = BitmapFactory.decodeFile(path);
                float y = (canvas.getHeight() / e.getHeight()) / 2;
                this.canvas.drawBitmap(e, (canvas.getWidth() - e.getWidth()) / 2,
                        y, null);
                if (this.length < y + (float) e.getHeight()) {
                    this.length = y + (float) e.getHeight();
                }

            } catch (Exception var5) {
                var5.printStackTrace();
            }

    }

    public void drawImage(float x, float y, String path, String text, Context context) {
        text = " " + text;
        if (canvas != null)
            try {
                Paint paint = new Paint();
                paint.setTextSize(25);
                Rect rect = new Rect();
                paint.getTextBounds(text, 0, text.length(), rect);
                Paint.FontMetrics metric = paint.getFontMetrics();
                int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
                int yText = (int) (textHeight - metric.descent);
                Bitmap e = BitmapFactory.decodeFile(path);

                int divide = textHeight - e.getHeight();
                this.canvas.drawBitmap(e, x, divide / 2, null);
                if (this.length < y + (float) e.getHeight()) {
                    this.length = y + (float) e.getHeight();
                }

                this.canvas.drawText(text, e.getWidth(), yText, paint);

                ImageView view = new ImageView(context);
                view.setImageBitmap(bm);

                bm = ((BitmapDrawable) view.getDrawable()).getBitmap();

            } catch (Exception var5) {
                var5.printStackTrace();
            }
    }

    public void drawImageBluetooth(float x, float y, String path, String text, Context context) {
        text = " " + text;
        if (canvas != null)
            try {
                Paint paint = new Paint();
                paint.setTextSize(25);
                Rect rect = new Rect();
                paint.getTextBounds(text, 0, text.length(), rect);
                Paint.FontMetrics metric = paint.getFontMetrics();
                int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
                int yText = (int) (textHeight - metric.descent);
                Bitmap e = BitmapFactory.decodeFile(path);

                int divide = textHeight - e.getHeight();
                int totalWidthComponent = rect.width() + e.getWidth();
                int xImage = (canvas.getWidth() / 2) - (totalWidthComponent / 2);
                int xText = xImage + e.getWidth();
                this.canvas.drawBitmap(e, xImage, divide / 2, null);
                if (this.length < y + (float) e.getHeight()) {
                    this.length = y + (float) e.getHeight();
                }

                this.canvas.drawText(text, xText, yText, paint);

                ImageView view = new ImageView(context);
                view.setImageBitmap(bm);

                bm = ((BitmapDrawable) view.getDrawable()).getBitmap();

            } catch (Exception var5) {
                var5.printStackTrace();
            }

    }

    public void printPng() {
        File f = new File("/mnt/sdcard/0.png");
        FileOutputStream fos = null;
        Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, this.getLength());

        try {
            fos = new FileOutputStream(f);
            nbm.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        }

    }

    public byte[] printDraw() {
        byte[] imgbuf = new byte[0];
        if (bm != null) {
            Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, this.getLength());
            imgbuf = new byte[this.width / 8 * this.getLength() + 8];
            boolean s = false;
            imgbuf[0] = 29;
            imgbuf[1] = 118;
            imgbuf[2] = 48;
            imgbuf[3] = 0;
            imgbuf[4] = (byte) (this.width / 8);
            imgbuf[5] = 0;
            imgbuf[6] = (byte) (this.getLength() % 256);
            imgbuf[7] = (byte) (this.getLength() / 256);
            int var23 = 7;

            for (int i = 0; i < this.getLength(); ++i) {
                int t;
                for (t = 0; t < this.width / 8; ++t) {
                    int c0 = nbm.getPixel(t * 8 + 0, i);
                    byte p0;
                    if (c0 == -1) {
                        p0 = 0;
                    } else {
                        p0 = 1;
                    }

                    int c1 = nbm.getPixel(t * 8 + 1, i);
                    byte p1;
                    if (c1 == -1) {
                        p1 = 0;
                    } else {
                        p1 = 1;
                    }

                    int c2 = nbm.getPixel(t * 8 + 2, i);
                    byte p2;
                    if (c2 == -1) {
                        p2 = 0;
                    } else {
                        p2 = 1;
                    }

                    int c3 = nbm.getPixel(t * 8 + 3, i);
                    byte p3;
                    if (c3 == -1) {
                        p3 = 0;
                    } else {
                        p3 = 1;
                    }

                    int c4 = nbm.getPixel(t * 8 + 4, i);
                    byte p4;
                    if (c4 == -1) {
                        p4 = 0;
                    } else {
                        p4 = 1;
                    }

                    int c5 = nbm.getPixel(t * 8 + 5, i);
                    byte p5;
                    if (c5 == -1) {
                        p5 = 0;
                    } else {
                        p5 = 1;
                    }

                    int c6 = nbm.getPixel(t * 8 + 6, i);
                    byte p6;
                    if (c6 == -1) {
                        p6 = 0;
                    } else {
                        p6 = 1;
                    }

                    int c7 = nbm.getPixel(t * 8 + 7, i);
                    byte p7;
                    if (c7 == -1) {
                        p7 = 0;
                    } else {
                        p7 = 1;
                    }

                    int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7;
                    this.bitbuf[t] = (byte) value;
                }

                for (t = 0; t < this.width / 8; ++t) {
                    ++var23;
                    imgbuf[var23] = this.bitbuf[t];
                }
            }

        }
        return imgbuf;
    }

    public static Bitmap bitmapConvertMonochrome(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                // use 128 as threshold, above -> white, below -> black
                if (gray > 128) {
                    gray = 255;
                } else {
                    gray = 0;
                }
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }

}
