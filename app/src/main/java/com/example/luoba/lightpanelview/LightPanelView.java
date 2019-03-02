package com.example.luoba.lightpanelview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 自定义光线表盘
 * @author crain
 */
public class LightPanelView extends View {

    /**
     * 外圆画笔
     */
    private Paint outerPaint;

    /**
     * 外圆边框宽度
     */
    private float outerStrokeWith;

    /**
     * 外圆半径
     */
    private float outerRadius;

    /**
     * 和colors关联，取值0-1
     */
    private float[] positions;

    /**
     * 和positions关联
     */
    private int[] colors;

    /**
     * 控件宽度
     */
    private int width;

    /**
     * 控件高度
     */
    private int height;

    /**
     * 渐变shader
     */
    private SweepGradient sweepGradient;

    /**
     * 灯光画笔
     */
    private Paint lightPaint;

    /**
     * 灯光半径
     */
    private int lightRadius;

    /**
     * 灯光粗细
     */
    private int lightStrokeWith;

    /**
     * 色值比率
     */
    private float radio;

    /**
     * 颜色取色器
     */
    private LinearGradientGetter sectionOne;
    private LinearGradientGetter sectionTwo;

    /**
     * 外圆
     */
    private RectF outerOval;

    /**
     * 光线圆
     */
    private RectF lightOval;

    /**
     * 开始角度
     */
    private int startAngle;

    /**
     * 扫过角度
     */
    private int sweepAngle;

    /**
     * shader 变换矩阵
     */
    private Matrix shaderMatrix;

    /**
     * 中心圆点x坐标
     */
    private int centerX;

    /**
     * 中心原点y坐标
     */
    private int centerY;

    /**
     * 灯泡画笔
     */
    private Paint bulbPaint;
    private RectF bulbR1;
    private RectF bulbR2;
    private RectF bulbR3;

    /**
     * 和positions关联
     */
    private int[] colors2;

    public LightPanelView(Context context) {
        this(context, null);
    }

    public LightPanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LightPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        outerStrokeWith = dp2px(6);
        outerRadius = dp2px(120);
        lightRadius = dp2px(80);
        lightStrokeWith = dp2px(1);
        startAngle = 150;
        sweepAngle = 240;

        // outer ring paint
        outerPaint = new Paint();
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setAntiAlias(true);
        outerPaint.setStrokeWidth(outerStrokeWith);
        outerPaint.setStrokeCap(Paint.Cap.ROUND);

        positions = new float[3];
        positions[0] = 0.25f;
        positions[1] = 0.625f;
        positions[2] = 1f;

        colors = new int[3];
        colors[0] = 0xFFFFF8D8;
        colors[1] = 0xFFFFD401;
        colors[2] = 0xFF675600;

        colors2 = new int[3];
        colors2[0] = 0xFF675600;
        colors2[1] = 0xFFFFD401;
        colors2[2] = 0xFFFFF8D8;

        // light paint
        lightPaint = new Paint();
        lightPaint.setStyle(Paint.Style.FILL);
        lightPaint.setAntiAlias(true);
        lightPaint.setStrokeWidth(lightStrokeWith);
        lightPaint.setStrokeCap(Paint.Cap.SQUARE);

        // bulb paint
        bulbPaint = new Paint();
        bulbPaint.setStyle(Paint.Style.FILL);
        bulbPaint.setAntiAlias(true);

        // 第一部分线性渐变取色
        sectionOne = new LinearGradientGetter(colors2[0], colors2[1]);

        // 第二部分线性渐变取色
        sectionTwo = new LinearGradientGetter(colors2[1], colors2[2]);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = (int) ((outerRadius + outerStrokeWith) * 2);
        height = (int) ((outerRadius + outerStrokeWith) * (1 + 0.5)); // 这里的0.5最终要变成根据角度计算的sin值

        centerX = (int) (outerRadius + outerStrokeWith);
        centerY = (int) (outerRadius + outerStrokeWith);

        setMeasuredDimension(width, height);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        if (outerOval == null) {
            outerOval = new RectF(
                    outerStrokeWith,
                    outerStrokeWith,
                    outerRadius * 2 + outerStrokeWith,
                    outerRadius * 2 + outerStrokeWith);
        }

        if (lightOval == null) {
            lightOval = new RectF(
                    centerX - lightRadius,
                    centerY - lightRadius,
                    centerX + lightRadius,
                    centerY + lightRadius);
        }

        if (shaderMatrix == null) {
            // 设置shader旋转角度
            shaderMatrix = new Matrix();
            shaderMatrix.postRotate(45f, outerRadius + outerStrokeWith, outerRadius + outerStrokeWith);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (sweepGradient == null) {
            sweepGradient = new SweepGradient(width / 2, width / 2, colors, positions);
        }

        sweepGradient.setLocalMatrix(shaderMatrix);
        outerPaint.setShader(sweepGradient);

        // 画最外层颜色渐变的扇形
        canvas.drawArc(outerOval, startAngle, sweepAngle, false, outerPaint);

        // 根据不同的比率设置光线颜色
        if (radio < 0.5) {
            int color = sectionOne.getColor(radio * 2f);
            lightPaint.setColor(color);
            bulbPaint.setColor(color);
        } else {
            int color = sectionTwo.getColor((radio - 0.5f) * 2f);
            lightPaint.setColor(color);
            bulbPaint.setColor(color);
        }

        // 画内层光线
        drawLight(canvas);

        // 画灯泡
        drawBulbs(canvas);
    }

    /*
     * 画灯泡的两种方案：
     * 1. 使用canvas：分别画出中心圆，底部三个圆角矩形
     * 2. 使用灯泡的png图片，imageView.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
     *
     * 这里面使用的是第一种方案
     */

    /**
     * draw bulbs
     * @param canvas canvas
     */
    private void drawBulbs(Canvas canvas) {
        int bulbRadius = dp2px(30);
        canvas.drawCircle(centerX, centerY, bulbRadius, bulbPaint);

        if (bulbR1 == null) {
            bulbR1 = new RectF(
                    centerX - 40,
                    centerY + bulbRadius - 10,
                    centerX + 40,
                    centerY + bulbRadius + 15);
        }

        if (bulbR2 == null) {
            bulbR2 = new RectF(
                    centerX - 30,
                    centerY + bulbRadius + 20,
                    centerX + 30,
                    centerY + bulbRadius + 35
            );
        }
        if (bulbR3 == null) {
            bulbR3 = new RectF(
                    centerX - 30,
                    centerY + bulbRadius + 40,
                    centerX + 30,
                    centerY + bulbRadius + 55);
        }
        canvas.drawRoundRect(bulbR1, 5,5, bulbPaint);
        canvas.drawRoundRect(bulbR2, 5,5, bulbPaint);
        canvas.drawRoundRect(bulbR3, 5,5, bulbPaint);
    }

    public void setRadio(float radio) {
        this.radio = radio;
        postInvalidate();
    }

    /**
     * draw light
     * @param canvas canvas
     */
    private void drawLight(Canvas canvas) {
        int angle = 0;
        while (angle <= sweepAngle) {
            canvas.save();
            canvas.rotate(startAngle + angle, centerX, centerY);
            canvas.drawLine(
                    centerX + lightRadius, centerY,
                    centerY + lightRadius - 15, centerY, lightPaint);
            canvas.restore();
            angle += 8;
        }
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 颜色取值器
     */
    public class LinearGradientGetter {
        private int mStartColor;
        private int mEndColor;

        private LinearGradientGetter(int startColor, int endColor) {
            this.mStartColor = startColor;
            this.mEndColor = endColor;
        }

        public void setStartColor(int startColor) {
            this.mStartColor = startColor;
        }

        public void setEndColor(int endColor) {
            this.mEndColor = endColor;
        }

        /**
         * 获取某一个百分比间的颜色,radio取值[0,1]
         * @param radio 色值比率
         * @return 色值比率对应的颜色
         */
        private int getColor(float radio) {
            int redStart = Color.red(mStartColor);
            int blueStart = Color.blue(mStartColor);
            int greenStart = Color.green(mStartColor);
            int redEnd = Color.red(mEndColor);
            int blueEnd = Color.blue(mEndColor);
            int greenEnd = Color.green(mEndColor);

            int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
            int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
            int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
            return Color.argb(255, red, greed, blue);
        }
    }
}
