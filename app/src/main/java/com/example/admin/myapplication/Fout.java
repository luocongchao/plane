package com.example.admin.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import java.sql.Time;

public class Fout extends ConstraintLayout {

    private Point mCenterPoint=new Point(75,75);
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

    public Fout(Context context) {
        super(context);
    }


    public Fout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Fout);
        beforeRadius = typedArray.getFloat(R.styleable.Fout_beforeRadius1, 531);
        radius = typedArray.getFloat(R.styleable.Fout_radius1, 75);
        proportion = radius / beforeRadius;
        beforeInRadius = typedArray.getFloat(R.styleable.Fout_beforeInRadius1, 249) * proportion;
    }

    public Fout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//    }
//    @Override
//    public void draw(Canvas canvas) {
//        super.draw(canvas);
//        int measuredWidth = getMeasuredWidth();
//        int measuredHeight = getMeasuredHeight();
//        mCenterPoint = new Point(measuredWidth / 2, measuredHeight / 2);
//    }
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
     * @param centerPoint 中心点
     * @param touchPoint  触摸点
     * @return 摇杆实际显示的位置（点）
     */
    private void getRockerPositionPoint(Point centerPoint, Point touchPoint) {
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

        callBack(angle, (int) lenXY);
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

        mCenterPoint=new Point(getMeasuredWidth()/2,getMeasuredHeight()/2);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                long kk1=  System.currentTimeMillis();
                float moveX1 = event.getX();
                float moveY1 = event.getY();
                getRockerPositionPoint(mCenterPoint, new Point((int) moveX1, (int) moveY1));
                // 回调 开始
                callBackStart();
                long cc1=  System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:// 移动
                long kk=  System.currentTimeMillis();
                float moveX = event.getX();
                float moveY = event.getY();
                getRockerPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY));
                long cc=  System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 移出区域
                // 回调 结束
                callBackFinish();
                if (mOnShakeListener != null) {
                    mOnShakeListener.direction(Direction.DIRECTION_CENTER);
                }
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
