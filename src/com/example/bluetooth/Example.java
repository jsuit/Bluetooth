package com.example.bluetooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;


public class Example implements Runnable {
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
	private String table;

	Example(Context context, int windowsize, String Activity, String table) {
		database = new DB(context);
		this.max = windowsize;
		data = new float[windowsize * 3];
		x = new ArrayList<Float>();
		y = new ArrayList<Float>();
		z = new ArrayList<Float>();
		this.Activity = Activity;
		this.table = table;
	}

	public void setData(int i, float data) {
		current = i;
		if (i % 3 == 0)
			x.add(data);
		else if (i % 3 == 1)
			y.add(data);
		else
			z.add(data);
	}

	public void setData(float[] data) {
		for (int i = 0; i < data.length; i++) {
			if (i % 3 == 0)
				x.add(data[i]);
			else if (i % 3 == 1)
				y.add(data[i]);
			else
				z.add(data[i]);
		}

		
	}
	
	public void setData(Float[] data){
		for (int i = 0; i < data.length; i++) {
			if (i % 3 == 0)
				x.add(data[i]);
			else if (i % 3 == 1)
				y.add(data[i]);
			else
				z.add(data[i]);
		}
	}

	public boolean reachedLimit() {
		//return true if we have reached limit
		//else false
		//Log.d("max =", " "+ this.max);
		return this.max <= x.size();
	}

	public List<Float> calcFeatures() {
		// call only when you have reached limit
		if(x.size() == 0) return null;
		calcFeatures = new CalcFeatures(x,y,z);
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

		// mean is really the feature vectors by the end of this method
		return list;
	}

	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		
			this.features = calcFeatures();	
		
		database.open();
		long id = database.save(features, Activity, table);
		database.close();

	}

}
