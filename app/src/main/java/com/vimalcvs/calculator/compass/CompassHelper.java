package com.vimalcvs.calculator.compass;

import java.util.GregorianCalendar;


public class CompassHelper {

    public static final float ALPHA = 0.15f;

    public static float calculateHeading(float[] accelerometerReading, float[] magnetometerReading) {
        float ax = accelerometerReading[0];
        float ay = accelerometerReading[1];
        float az = accelerometerReading[2];

        float ex = magnetometerReading[0];
        float ey = magnetometerReading[1];
        float ez = magnetometerReading[2];


        float hx = ey * az - ez * ay;
        float hy = ez * ax - ex * az;
        float hz = ex * ay - ey * ax;


        final float invH = 1.0f / (float) Math.sqrt(hx * hx + hy * hy + hz * hz);
        hx *= invH;
        hy *= invH;
        hz *= invH;


        final float invA = 1.0f / (float) Math.sqrt(ax * ax + ay * ay + az * az);
        ax *= invA;
        //ay *= invA;
        az *= invA;

        // 重力向量和新向量H的叉积
        //final float mx = ay * hz - az * hy;
        final float my = az * hx - ax * hz;
        //final float mz = ax * hy - ay * hx;

        // 使用反正切函数获取弧度表示的方向
        return (float) Math.atan2(hy, my);
    }

    public static float calculateMagneticDeclination(double latitude, double longitude, double altitude) {
        TSAGeoMag geoMag = new TSAGeoMag();
        return (float) geoMag
                .getDeclination(latitude, longitude, geoMag.decimalYear(new GregorianCalendar()), altitude);
    }

    public static float convertRadioDeg(float rad) {
        return (float) (rad / Math.PI) * 180;
    }

    /**
     * 将角度从[-180,180]范围映射到[0,360]范围
     */
    public static float map180to360(float angle) {
        return (angle + 360) % 360;
    }

    public static void lowPassFilter(float[] input, float[] output) {
        if (output == null) {
            return;
        }
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
    }
}
