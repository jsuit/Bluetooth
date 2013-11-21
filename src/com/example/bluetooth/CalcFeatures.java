package com.example.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class CalcFeatures {

	private float MeanAccel;
	private float VarianceAccel;
	private float Energy;
	private float frequency;
	private float Correlation;
	// private float[] data;
	private ArrayList<Float> features;
	private float[] variance;
	private List<Float> datax;
	private List<Float> datay;
	private List<Float> dataz;

	CalcFeatures(List<Float> x, List<Float> y, List<Float> z) {
		datax = x;
		datay = y;
		dataz = z;
		features = new ArrayList<Float>();

	}

	public Float[] CalcMean() {
		float sumX = 0;
		float sumY = 0;
		float sumZ = 0;
		for (int i = 0; i < datax.size(); i++) {
			sumX += datax.get(i);
			sumY += datay.get(i);
			sumZ += dataz.get(i);
		}
		return new Float[] { (float) (sumX / datax.size()),
				sumY / (datay.size()), sumZ / dataz.size() };

	}

	public Float[] CalcVariance(Float[] meanArray) {
		float meanx = meanArray[0];
		float meany = meanArray[1];
		float meanz = meanArray[2];
		float varX = 0;
		float varY = 0;
		float varZ = 0;
		int size = datax.size();
		for (int i = 0; i < size; i++) {

			varX += (datax.get(i) - meanx) * (datax.get(i) - meanx);

			varY += (datay.get(i) - meany) * (datay.get(i) - meany);
			varZ += +(dataz.get(i) - meanz) * (dataz.get(i) - meanz);

		}

		return new Float[] { varX / (size - 1),
				varY / (size -1), varZ / (size-1) };

	}

	

	Float[] calcCorrelation(List<Float> x2,List<Float> y2,
			List<Float> z2, Float[] mean, Float[] variance) {
		// TODO Auto-generated method stub

		if (x2.size() != y2.size() && y2.size() != z2.size()) {
			Log.e("ERROR", "NOT SAME LENGTH");
			return null;
		}
		float sumXY = 0;
		float sumYZ = 0;
		float sumXZ = 0;
		for (int i = 0; i < x2.size(); i++) {
			sumXY = sumXY + (x2.get(i) - mean[0]) * (y2.get(i) - mean[1]);
			sumYZ = sumYZ + (y2.get(i) - mean[1]) * (z2.get(i) - mean[2]);
			sumXZ = sumXZ + (x2.get(i) - mean[0]) * (z2.get(i) - mean[2]);
		}

		return new Float[] { (sumXY * sumXY / (variance[0] * variance[1])),
				(sumYZ * sumYZ / (variance[1] * variance[2])),
				(sumXZ * sumXZ / (variance[0] * variance[2])) };

	}

}
