package com.liuxiaozhu.taiwanmapsvg.bean;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Author：Created by liuxiaozhu on 2018/2/3.
 * Email: chenhuixueba@163.com
 * 台湾省的一个地区（javaBean）
 */

public class AreaItem {
    private Path mPath;
    private int drawColor;

    public AreaItem(Path mPath) {
        this.mPath = mPath;
    }

    /**
     * 绘制方法
     *
     * @param canvas
     * @param paint
     * @param isSelect 是否选择
     */
    public void draw(Canvas canvas, Paint paint, boolean isSelect) {
        if (isSelect) {
            //被选择
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.FILL);
            //1.绘制背景
            paint.setColor(Color.BLACK);
            paint.setShadowLayer(8, 0, 0, Color.WHITE);
            canvas.drawPath(mPath, paint);

            //绘制区域
            paint.clearShadowLayer();
            paint.setColor(drawColor);
            canvas.drawPath(mPath, paint);
        } else {
            //未被选择
            //绘制内容
            paint.clearShadowLayer();
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(mPath, paint);
            //绘制边界
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            canvas.drawPath(mPath, paint);
        }
    }

    /**
     * 点击区域是否在台湾省的这（当前区县）个区域上
     *
     * @param x
     * @param y
     */
    public boolean isTouch(int x, int y) {
        RectF rect = new RectF();
        //计算mPath的矩形边界
        mPath.computeBounds(rect, true);
        Region region = new Region();
        region.setPath(mPath, new Region((int) rect.left, (int) rect.top,
                (int) rect.right, (int) rect.bottom));
        return region.contains(x, y);
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }
}
