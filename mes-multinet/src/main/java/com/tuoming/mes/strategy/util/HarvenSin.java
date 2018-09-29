package com.tuoming.mes.strategy.util;

/**
 * 用户计算地球上两个点的距离
 * 计算公式为: HarvenSin(d/R) = HarvenSin(a1-a2) + cos(a1)*cos(a2)*HarvenSin(b1-b2)
 * 其中 HarvenSin(a) = sin(a/2) * sin(a/2)
 * R: 为地球半径，可取平均值 6371km
 * a1,a2 表示两点的纬度；
 * b1,b2 表示两点的经度；
 * a1,a2,b1,b2 都需要使用弧度，角度转换为弧度公式为 a1 = 经度|纬度 * pi/180
 * 所以 d = 2R*Math.asin(Math.sqrt(HarvenSin(a1-a2) + cos(a1)*cos(a2)*HarvenSin(b1-b2)))
 * Copyright © 2008   卓望公司
 * package: com.tuoming.mes.strategy.util
 * fileName: HarvenSin.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/15 15:54
 */
public class HarvenSin {

    /**
     * 距离推导式中基本元素计算（Math.pow(Math.sin(theta/2), 2）
     * @param theta 弧度
     * @return 返回计算结果
     */
    private static double harvenSin(double theta) {
        return Math.pow(Math.sin(theta / 2), 2);
    }

    /**
     * 将经纬度转换成弧度(PI*角度/180)
     *
     * @param degrees 经纬度
     * @return 经纬度对应的弧度
     */
    private static double degreesToRadians(double degrees) {
        double radians = 180;
        return Math.PI * degrees / radians;
    }

    /**
     * 计算经度|纬度的差值（绝对值）
     *
     * @param degrees1 源经度(纬度)
     * @param degrees2 目标经度(纬度)
     * @return 经度|纬度的差值（绝对值）
     */
    private static double radiansDistance(double degrees1, double degrees2) {
        return degreesToRadians(degrees1) - degreesToRadians(degrees2);
    }

    /**
     *
     * @param srcLongitude 源经度
     * @param srcLatitude  源纬度
     * @param destLongitude 邻区经度
     * @param destLatitude 邻区纬度
     * @return 返回2个点的距离
     */
    public static double distance(double srcLongitude, double srcLatitude, double destLongitude, double destLatitude) {
        double earthRadius = 6378137.0;
        double vLat = radiansDistance(srcLatitude, destLatitude);
        double vLon = radiansDistance(srcLongitude, destLongitude);
        double h = harvenSin(vLat)
                + Math.cos(degreesToRadians(srcLatitude)) * Math.cos(degreesToRadians(destLatitude)) * harvenSin(vLon);
        return 2 * earthRadius * Math.asin(Math.sqrt(h));
    }

}