package com.tuoming.mes.strategy.util;

import java.math.BigDecimal;
import java.util.Arrays;

public class BigDataForecastUtil {
	public static double OutlierPrediction(Double[] data1){
		double[] data = new double[data1.length];
		double	q1,q3;
		int q1_idx, q3_idx;
		double	iqr, iqr_1p5;
		double q1_1p5_iqr, q3_1p5_iqr;
		double total = 0;
		int	total_cnt = 0;
		double mean = 0;

		for(int loop = 0; loop < data1.length; loop++){
			data[loop] = data1[loop];
		}
		Arrays.sort(data);
		q1 = data.length * 0.25;
		q3 = data.length * 0.75;
		q1_idx = new BigDecimal(q1).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() - 1;
		q3_idx = new BigDecimal(q3).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() - 1;
		iqr = data[q3_idx] - data[q1_idx];
		iqr_1p5 = iqr * 1.5;
		q1_1p5_iqr = data[q1_idx] - iqr_1p5;
		q3_1p5_iqr = data[q3_idx] + iqr_1p5;

		for(int loop = 0; loop < data.length; loop++){
			if(data[loop] < q1_1p5_iqr || data[loop] > q3_1p5_iqr){
//				System.out.println(data[loop] + " is outlier");
			}else{
				total = total + data[loop];
				total_cnt++;
			}
		}
		mean = total / total_cnt;	
		return mean;
	}
	public static int JudgeOutlier(double[] data1, double pre){
		double[] data = new double[data1.length];
		double	q1,q3;
		int q1_idx, q3_idx;
		double	iqr, iqr_1p5, iqr_3;
		double q1_1p5_iqr, q3_1p5_iqr;
		//double q1_3iqr, q3_3iqr;
		int flag = 0;

		for(int loop = 0; loop < data1.length; loop++){
			data[loop] = data1[loop];
		}
		Arrays.sort(data);
		q1 = data.length * 0.25;
		q3 = data.length * 0.75;
		q1_idx = new BigDecimal(q1).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() - 1;
		q3_idx = new BigDecimal(q3).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() - 1;
		iqr = data[q3_idx] - data[q1_idx];
		iqr_1p5 = iqr * 1.5;
		iqr_3 = iqr * 3;
		q1_1p5_iqr = data[q1_idx] - iqr_1p5;
		q3_1p5_iqr = data[q3_idx] + iqr_1p5;
//		q1_3iqr = data[q1_idx] - iqr_3;
//		q3_3iqr = data[q3_idx] + iqr_3;

		if(pre < q1_1p5_iqr || pre > q3_1p5_iqr){
			flag = 1;
		}else{
			flag = 0;
		}
		
		return flag;
	}

}
