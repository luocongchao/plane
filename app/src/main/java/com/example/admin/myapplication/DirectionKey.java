package com.example.admin.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DirectionKey extends View {


    private Point mCenterPoint;
    private Paint mRockerPaint;

    private OnShakeListener mOnShakeListener;

    private Direction tempDirection = Direction.DIRECTION_CENTER;


    // 角度
    private static final double ANGLE_0 = 0;
    private static final double ANGLE_360 = 360;
    // 360°平分8份的边缘角度
    private static final double ANGLE_8D_OF_0P = 30;
    private static final double ANGLE_8D_OF_1P = 60;
    private static final double ANGLE_8D_OF_2P = 120;
    private static final double ANGLE_8D_OF_3P = 150;
    private static final double ANGLE_8D_OF_4P = 210;
    private static final double ANGLE_8D_OF_5P = 240;
    private static final double ANGLE_8D_OF_6P = 300;
    private static final double ANGLE_8D_OF_7P = 330;


    //大图内圆半径
    private float beforeRadius;
    //大图外圆半径
    private float beforeInRadius;
    //缩放比例
    private float proportion;
    //半径
    private float radius;

    //控件属性集合
    private TypedArray typedArray;

    public DirectionKey(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.DirectionKey);
        beforeRadius = typedArray.getFloat(R.styleable.DirectionKey_beforeRadius, 1062);
        radius = typedArray.getFloat(R.styleable.DirectionKey_radius, 75);
        proportion = radius / beforeRadius;
        beforeInRadius = typedArray.getFloat(R.styleable.DirectionKey_beforeInRadius, 249) * proportion;

        mRockerPaint = new Paint();
        mRockerPaint.setAntiAlias(true);
    }


    private Bitmap getBitmap(Direction direction) {
        Drawable drawable = null;
        switch (direction) {
            case DIRECTION_UP:
                drawable = typedArray.getDrawable(R.styleable.DirectionKey_top_Background);
                break;
            case DIRECTION_LEFT:
                drawable = typedArray.getDrawable(R.styleable.DirectionKey_left_Background);
                break;
            case DIRECTION_DOWN:
                drawable = typedArray.getDrawable(R.styleable.DirectionKey_bottom_Background);
                break;
            case DIRECTION_RIGHT:
                drawable = typedArray.getDrawable(R.styleable.DirectionKey_right_Background);
                break;
            default:
                return null;
        }

        if (null != drawable) {
            if (drawable instanceof BitmapDrawable) {
                // 获取图片数据
                return  ((BitmapDrawable) drawable).getBitmap();
            }
        }
        return null;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        mCenterPoint = new Point(measuredWidth / 2, measuredHeight / 2);

        if (tempDirection == Direction.DIRECTION_LEFT || tempDirection == Direction.DIRECTION_DOWN_LEFT || tempDirection == Direction.DIRECTION_UP_LEFT) {
            Bitmap bitmap=getBitmap(Direction.DIRECTION_LEFT);
            float realH = proportion * bitmap.getHeight() / 2;
            float realW = proportion * bitmap.getWidth() / 2;
            Rect src = new Rect(0, 0, (int) bitmap.getWidth(), (int) bitmap.getHeight());
            RectF dst = new RectF(0, mCenterPoint.y - realH, realW * 2, mCenterPoint.y + realH);
            canvas.drawBitmap(bitmap, src, dst, mRockerPaint);
        }
        if (tempDirection == Direction.DIRECTION_UP ||tempDirection == Direction.DIRECTION_UP_LEFT||tempDirection == Direction.DIRECTION_UP_RIGHT) {
            Bitmap bitmap=getBitmap(Direction.DIRECTION_UP);
            float realH = proportion * bitmap.getHeight() / 2;
            float realW = proportion * bitmap.getWidth() / 2;
            Rect src = new Rect(0, 0, (int) bitmap.getWidth(), (int) bitmap.getHeight());
            RectF dst = new RectF(mCenterPoint.x-realW, 0, mCenterPoint.x+realW, realH*2);
            canvas.drawBitmap(bitmap, src, dst, mRockerPaint);
        }
        if (tempDirection == Direction.DIRECTION_RIGHT||tempDirection == Direction.DIRECTION_UP_RIGHT||tempDirection == Direction.DIRECTION_DOWN_RIGHT) {
            Bitmap bitmap=getBitmap(Direction.DIRECTION_RIGHT);
            float realH = proportion * bitmap.getHeight() / 2;
            float realW = proportion * bitmap.getWidth() / 2;
            Rect src = new Rect(0, 0, (int) bitmap.getWidth(), (int) bitmap.getHeight());
           RectF dst = new RectF(mCenterPoint.x*2-(realW*2), mCenterPoint.y -realH, mCenterPoint.x*2, mCenterPoint.y + realH);
            canvas.drawBitmap(bitmap, src, dst, mRockerPaint);
        }
        if (tempDirection == Direction.DIRECTION_DOWN||tempDirection == Direction.DIRECTION_DOWN_RIGHT||tempDirection == Direction.DIRECTION_DOWN_LEFT) {
            Bitmap bitmap=getBitmap(Direction.DIRECTION_DOWN);
            float realH = proportion * bitmap.getHeight() / 2;
            float realW = proportion * bitmap.getWidth() / 2;
            Rect src = new Rect(0, 0, (int) bitmap.getWidth(), (int) bitmap.getHeight());
            RectF dst = new RectF(mCenterPoint.x-realW, mCenterPoint.y*2-(realH*2), mCenterPoint.x+realW,mCenterPoint.y*2 );
            canvas.drawBitmap(bitmap, src, dst, mRockerPaint);
        }
    }

    /**
     * 弧度转角度
     *
     * @param radian 弧度
     * @return 角度[0, 360)
     */
    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    /**
     * 回调
     * 返回参数
     *
     * @param angle 摇动角度
     */
    private void callBack(double angle, float distance) {
        Log.d("distance", distance + "");
        if (distance < beforeInRadius) {
            tempDirection = Direction.DIRECTION_CENTER;
            return;
        }
        if (null != mOnShakeListener) {
            if ((ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                // 右
                tempDirection = Direction.DIRECTION_RIGHT;
                mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
            } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN_RIGHT) {
                // 右下
                tempDirection = Direction.DIRECTION_DOWN_RIGHT;
                mOnShakeListener.direction(Direction.DIRECTION_DOWN_RIGHT);
            } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                // 下
                tempDirection = Direction.DIRECTION_DOWN;
                mOnShakeListener.direction(Direction.DIRECTION_DOWN);
            } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle && tempDirection != Direction.DIRECTION_DOWN_LEFT) {
                // 左下
                tempDirection = Direction.DIRECTION_DOWN_LEFT;
                mOnShakeListener.direction(Direction.DIRECTION_DOWN_LEFT);
            } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                // 左
                tempDirection = Direction.DIRECTION_LEFT;
                mOnShakeListener.direction(Direction.DIRECTION_LEFT);
            } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle && tempDirection != Direction.DIRECTION_UP_LEFT) {
                // 左上
                tempDirection = Direction.DIRECTION_UP_LEFT;
                mOnShakeListener.direction(Direction.DIRECTION_UP_LEFT);
            } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle && tempDirection != Direction.DIRECTION_UP) {
                // 上
                tempDirection = Direction.DIRECTION_UP;
                mOnShakeListener.direction(Direction.DIRECTION_UP);
            } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle && tempDirection != Direction.DIRECTION_UP_RIGHT) {
                // 右上
                tempDirection = Direction.DIRECTION_UP_RIGHT;
                mOnShakeListener.direction(Direction.DIRECTION_UP_RIGHT);
            }
        }
    }

    /**
     * 获取摇杆实际要显示的位置（点）
     *
     * @param centerPoint  中心点
     * @param touchPoint   触摸点
     * @param regionRadius 摇杆可活动区域半径
     * @param rockerRadius 摇杆半径
     * @return 摇杆实际显示的位置（点）
     */
    private Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float regionRadius, float rockerRadius) {
        // 两点在X轴的距离
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // 两点在Y轴距离
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // 两点距离
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        // 计算弧度
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);
        // 计算角度
        double angle = radian2Angle(radian);

        if (lenXY + rockerRadius <= regionRadius) { // 触摸位置在可活动范围内
            // 回调 返回参数
            callBack(angle, (int) lenXY);
            return touchPoint;
        } else { // 触摸位置在可活动范围以外
            // 计算要显示的位置
            int showPointX = (int) (centerPoint.x + (regionRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (regionRadius - rockerRadius) * Math.sin(radian));

            callBack(angle, (int) Math.sqrt((showPointX - centerPoint.x) * (showPointX - centerPoint.x) + (showPointY - centerPoint.y) * (showPointY - centerPoint.y)));
            return new Point(showPointX, showPointY);
        }
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 回调
     * 开始
     */
    private void callBackStart() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnShakeListener) {
            mOnShakeListener.onStart();
        }
    }

    /**
     * 回调
     * 结束
     */
    private void callBackFinish() {
        tempDirection = Direction.DIRECTION_CENTER;

        if (null != mOnShakeListener) {
            mOnShakeListener.onFinish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                // 回调 开始
                callBackStart();
            case MotionEvent.ACTION_MOVE:// 移动
                float moveX = event.getX();
                float moveY = event.getY();
                getRockerPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY), 75 + 50, 50);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 移出区域
                // 回调 结束
                callBackFinish();
                if (mOnShakeListener != null) {
                    mOnShakeListener.direction(Direction.DIRECTION_CENTER);
                }
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 方向值为：
     * DIRECTION_LEFT, 	// 左
     * DIRECTION_RIGHT, 	// 右
     * DIRECTION_UP, 		// 上
     * DIRECTION_DOWN, 	// 下
     * DIRECTION_UP_LEFT,  // 左上
     * DIRECTION_UP_RIGHT, // 右上
     * DIRECTION_DOWN_LEFT, // 左下
     * DIRECTION_DOWN_RIGHT, // 右下
     * DIRECTION_CENTER 	// 中间
     */
    public enum Direction {
        DIRECTION_LEFT,    // 左
        DIRECTION_RIGHT,    // 右
        DIRECTION_UP,        // 上
        DIRECTION_DOWN,    // 下
        DIRECTION_UP_LEFT,  // 左上
        DIRECTION_UP_RIGHT, // 右上
        DIRECTION_DOWN_LEFT, // 左下
        DIRECTION_DOWN_RIGHT, // 右下
        DIRECTION_CENTER    // 中间
    }

    /**
     * 添加摇动的监听
     *
     * @param listener 监听的方向，值有：
     *                 横向 左右两个方向:DIRECTION_2_HORIZONTAL,
     *                 纵向 上下两个方向:DIRECTION_2_VERTICAL,
     *                 四个方向: DIRECTION_4_ROTATE_0,
     *                 四个方向 旋转45度:DIRECTION_4_ROTATE_45,
     *                 八个方向: DIRECTION_8 。
     * @param listener 回调
     */
    public void setOnShakeListener(OnShakeListener listener) {
        mOnShakeListener = listener;
    }

    /**
     * 摇动方向监听接口
     */
    public interface OnShakeListener {
        // 开始
        void onStart();

        /**
         * 摇动方向
         *
         * @param direction 方向
         */
        void direction(Direction direction);

        // 结束
        void onFinish();
    }
}
