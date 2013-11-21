package com.example.bluetooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

public class TestExample implements Runnable {

	private float data[];
	private CalcFeatures calcFeatures;
	private int current;
	public static int max;
	private static Object lock = new Object();
	private List<Float> features;
	private ArrayList<Float> x;
	private ArrayList<Float> y;
	private ArrayList<Float> z;
	private String Activity;
	private static DB database;

	public TestExample(Context context, int windowsize, String Activity) {
		database = new DB(context);
		current = 0;
		max = windowsize;
		data = new float[windowsize * 3];
		x = new ArrayList<Float>();
		y = new ArrayList<Float>();
		z = new ArrayList<Float>();
		this.Activity = Activity;
	}

	/*
	 * public void setData(int i, float data) { this.data[i] = data; current =
	 * i; if (i % 3 == 0) x.add(data); else if (i % 3 == 1) y.add(data); else
	 * z.add(data); }
	 */
	public void setData(float[] data) {
		
		for (int i = 0; i < data.length; i++) {
			if (i % 3 == 0)
				x.add(data[i]);
			else if (i % 3 == 1)
				y.add(data[i]);
			else
				z.add(data[i]);
		}

		current = max;
	}

	public boolean reachedLimit() {
		if (this.max >= current)
			return false;
		else {
			return true;

		}
	}

	public ArrayList<Float> calcFeatures(List<Float> x, List<Float> y,
			List<Float> z) {
		calcFeatures = new CalcFeatures(x, y, z);
		Float[] meanArray = calcFeatures.CalcMean();
		Float[] variance = calcFeatures.CalcVariance(meanArray);
		Float[] correlation = calcFeatures.calcCorrelation(x, y, z, meanArray,
				variance);

		List<Float> mean = Arrays.asList(meanArray);
		List<Float> v = Arrays.asList(variance);
		List<Float> c = Arrays.asList(correlation);
		ArrayList<Float> list = new ArrayList<Float>(mean);
		list.addAll(v);
		list.addAll(c);
		return list;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		features = calcFeatures(x, y, z);
		database.open();
		database.save(features, Activity, DatabaseHelper.DATABASE_TEST_FEATURES);
		database.close();
	}
}
