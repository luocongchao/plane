package com.example.admin.myapplication;

import static com.example.admin.myapplication.ProtocolUtil.getHighBitLowBit;

/**
 * @author zhuoguangming
 * 封装了和无人机的通信协议,用于生成符合协议格式的各种无人机的控制数据报文
 */
public class Protocol {
    private static final char PROTOCOL_HEADER_FIRST = 0xAA;
    private static final char PROTOCOL_HEADER_SECOND = 0x1c;
    private static final char CR = 0x0D;
    private static final char LF = 0x0A;

    public enum Command {

        W_STATUS(0xC0),//写状态
        LOOP_RETURN_STATUS(0x30),//定时返回状态数据
        CONNECTED(0x50);//连接成功
        public char v;

        Command(int i) {
            v = (char) i;
        }
    }

    public enum Gesture {
        W_GESTURE(0x11),//写姿态
        R_GESTURE(0x21),;//读姿态
        public final char v;

        Gesture(int i) {
            this.v = (char) i;
        }
    }

    public enum PID {
        R_OUTER_PID(0x20),// 读外环 PID
        W_OUTER_PID(0x10),//写外环 PID
        W_INNER_PID(0x14),//写内环 PID
        R_INNER_PID(0x24),;//读内环 PID
        public final char v;

        PID(int i) {
            this.v = (char) i;
        }
    }

    public static char[] initial_data() {
        char[] chars = new char[34];
        chars[0] = PROTOCOL_HEADER_FIRST;
        chars[2] = PROTOCOL_HEADER_SECOND;
        chars[31] = PROTOCOL_HEADER_SECOND;
        chars[32] = CR;
        chars[33] = LF;
        return chars;
    }

    /**
     * 无人机状态控制函数
     *
     * @param power    油门
     * @param roll     横滚
     * @param course   航向
     * @param pitching 俯仰
     */
    public static char[] WriteStatus(char power, char roll, char course, char pitching) {
        char[] p = initial_data();
        BitData bitData = getHighBitLowBit(power);
        p[3] = bitData.H;
        p[4] = bitData.L;
        bitData = getHighBitLowBit(course);
        p[5] = bitData.H;
        p[6] = bitData.L;
        bitData = getHighBitLowBit(roll);
        p[7] = bitData.H;
        p[8] = bitData.L;
        bitData = getHighBitLowBit(pitching);
        p[9] = bitData.H;
        p[10] = bitData.L;
        return p;
    }

    /**
     * PID 控制函数，可读可写
     *
     * @param pid        pid
     * @param roll_p     横滚 p
     * @param roll_i     横滚 i
     * @param roll_d     横滚 d
     * @param course_p   航向 p
     * @param course_i   航向 i
     * @param course_d   航向 d
     * @param pitching_p 俯仰 p
     * @param pitching_i 俯仰 i
     * @param pitching_d 俯仰 d
     * @return 根据给定的 PID 和控制数值生成控制无人机 PID 的数据报文，如果只是读取无人机的 PID 数据只需指定 PID 其他参数为 0
     */
    public static char[] PID_Control(PID pid,
                                     char roll_p, char roll_i, char roll_d,
                                     char course_p, char course_i, char course_d,
                                     char pitching_p, char pitching_i, char pitching_d) {
        switch (pid) {
            case R_INNER_PID: {
                roll_p *= 10;
                roll_i *= 10;
                roll_d *= 10;

                course_p *= 10;
                course_i *= 10;
                course_d *= 10;

                pitching_p *= 10;
                pitching_i *= 10;
                pitching_d *= 10;
            }
            break;
            case R_OUTER_PID: {
                roll_p *= 100;
                roll_i *= 100;
                roll_d *= 100;

                course_p *= 100;
                course_i *= 100;
                course_d *= 100;

                pitching_p *= 100;
                pitching_i *= 100;
                pitching_d *= 100;
            }
            break;
        }
        char[] p = initial_data();
        p[1] = pid.v;
        BitData bitData = getHighBitLowBit(roll_p);
        p[3] = bitData.H;
        p[4] = bitData.L;

        bitData = getHighBitLowBit(roll_i);
        p[5] = bitData.H;
        p[6] = bitData.L;

        bitData = getHighBitLowBit(roll_d);
        p[7] = bitData.H;
        p[8] = bitData.L;

        bitData = getHighBitLowBit(pitching_p);
        p[9] = bitData.H;
        p[10] = bitData.L;

        bitData = getHighBitLowBit(pitching_i);
        p[11] = bitData.H;
        p[12] = bitData.L;

        bitData = getHighBitLowBit(pitching_d);
        p[13] = bitData.H;
        p[14] = bitData.L;
        bitData = getHighBitLowBit(course_p);
        p[15] = bitData.H;
        p[16] = bitData.L;
        bitData = getHighBitLowBit(course_i);
        p[17] = bitData.H;
        p[18] = bitData.L;
        bitData = getHighBitLowBit(course_d);
        p[19] = bitData.H;
        p[20] = bitData.L;
        return p;
    }

    /**
     * @param gesture        姿态控制函数，可读可写
     * @param acceleration_x 加速度 x
     * @param acceleration_y 加速度 y
     * @param acceleration_z 加速度 z
     * @param gyroscope_x    陀螺仪 x
     * @param gyroscope_y    陀螺仪 y
     * @param gyroscope_z    陀螺仪 z
     * @return 根据给定的命令和数值生成控制无人机姿态的数据报文，如果只是读取无人机的姿态数据只需指定命令为读取姿态数据，其他参数为 0
     */
    public static char[] GestureControl(Gesture gesture,
                                        char acceleration_x, char acceleration_y, char acceleration_z,
                                        char gyroscope_x, char gyroscope_y, char gyroscope_z) {
        char[] p = initial_data();
        p[1] = gesture.v;
        BitData bitData = getHighBitLowBit(acceleration_x);
        p[3] = bitData.H;
        p[4] = bitData.L;

        bitData = getHighBitLowBit(acceleration_y);
        p[5] = bitData.H;
        p[6] = bitData.L;

        bitData = getHighBitLowBit(acceleration_z);
        p[7] = bitData.H;
        p[8] = bitData.L;

        bitData = getHighBitLowBit(gyroscope_x);
        p[9] = bitData.H;
        p[10] = bitData.L;

        bitData = getHighBitLowBit(gyroscope_y);
        p[11] = bitData.H;
        p[12] = bitData.L;

        bitData = getHighBitLowBit(gyroscope_z);
        p[13] = bitData.H;
        p[14] = bitData.L;
        return p;
    }
}
