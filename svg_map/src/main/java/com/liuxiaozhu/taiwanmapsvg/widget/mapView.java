package com.liuxiaozhu.taiwanmapsvg.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.liuxiaozhu.taiwanmapsvg.R;
import com.liuxiaozhu.taiwanmapsvg.bean.AreaItem;
import com.liuxiaozhu.taiwanmapsvg.utils.PathParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Author：Created by liuxiaozhu on 2018/2/3.
 * Email: chenhuixueba@163.com
 * 绘制Map
 */

public class mapView extends View {
    private final Context context;
    private ArrayList<AreaItem> areaItemArrayList = new ArrayList<>();
    //每一块区域随机选一个颜色
    private int[] mapColor = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    //被选择的区域
    private AreaItem areaItem;
    private Paint mPaint;
    private Paint mTextPaint;
    //缩放系数
    private float scale = 1.3f;

    private GestureDetectorCompat gestureDetectorCompat;

    public mapView(Context context) {
        this(context, null);
    }

    public mapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public mapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.RED);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(30);

        gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                handlerOnTouch(event.getX(), event.getY());
                return true;
            }
        });
        //启动线程加载数据
        loadDataThread.start();
    }

    /**
     * 处理触摸事件
     *
     * @param x
     * @param y
     */
    private void handlerOnTouch(float x, float y) {
        if (areaItemArrayList.size() != 0) {
            AreaItem areaItems = null;
            for (AreaItem item : areaItemArrayList) {
                //除以放大系数
                if (item.isTouch((int) (x / scale), (int) (y / scale))) {
                    areaItems = item;
                    break;
                }
            }
            if (areaItems != null) {
                areaItem = areaItems;
                postInvalidate();
            }
        }
    }

    /**
     * 解析SVG数据
     */
    Thread loadDataThread = new Thread() {
        @Override
        public void run() {
            DocumentBuilderFactory factory
                    = DocumentBuilderFactory.newInstance();//获取DocumentBuilderFactory实例
            DocumentBuilder builder = null;
            //从xml中读取输入流
            InputStream inputStream = context.getResources().openRawResource(R.raw.ic_taiwan);
            try {
                builder = factory.newDocumentBuilder();
                //解析输入流
                Document document = builder.parse(inputStream);
                Element element = document.getDocumentElement();
                //获取path对应的Path数据
                NodeList nodeList = element.getElementsByTagName("path");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element elements = (Element) nodeList.item(i);
                    String stringPath = elements.getAttribute("android:pathData");
                    Path path = PathParser.createPathFromPathData(stringPath);
                    AreaItem item = new AreaItem(path);
                    //颜色设置
                    item.setDrawColor(mapColor[i % 4]);
                    areaItemArrayList.add(item);
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                postInvalidate();
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (areaItemArrayList.size() != 0) {
            canvas.save();
            //画布放大1.3倍
            canvas.scale(scale, scale);
            for (AreaItem item : areaItemArrayList) {
                //绘制没有被选择的
                if (areaItem != item) {
                    item.draw(canvas, mPaint, false);
                }
            }
            if (areaItem != null) {
                //绘制选择的
                areaItem.draw(canvas, mPaint, true);
            }
            canvas.restore();
            canvas.drawText("中国台湾省",100,50,mTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将点击事件交由GestureDetectorCompat处理
        return gestureDetectorCompat.onTouchEvent(event);
    }

}
