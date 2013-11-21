package com.example.bluetooth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import android.database.Cursor;
import android.util.Log;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class MClassifer {

	private Classifier classifier;
	private DB database;
	private Instances m_train_instances;
	private Instances m_test_instances;
	private boolean classifierBuilt = false;
	private int lastRow;
	private Cursor cursor;

	public MClassifer(DB database, Classifier classifier) {
		String nameOfDataset = "train";
		String nameOfTestDataset = "test";
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute meanX = new Attribute("meanX");
		Attribute meanY = new Attribute("meanY");
		Attribute meanZ = new Attribute("meanZ");
		Attribute varX = new Attribute("varX");
		Attribute varY = new Attribute("varY");
		Attribute varZ = new Attribute("varZ");
		Attribute corrX = new Attribute("corrX");
		Attribute corrY = new Attribute("corrY");
		Attribute corrZ = new Attribute("corrZ");
		// all the attributes + 1 for the class attribute
		// numAttributes = attributes.size() + 1;
		List<String> classValues = new Vector<String>();
		classValues.add("Walking");
		classValues.add("Sitting");
		classValues.add("Running");
		classValues.add("Kicking");
		Attribute classAttributes = new Attribute("Class Value", classValues);
		attributes.add(meanX);
		attributes.add(meanY);
		attributes.add(meanZ);
		attributes.add(varX);
		attributes.add(varY);
		attributes.add(varZ);
		attributes.add(corrX);
		attributes.add(corrY);
		attributes.add(corrZ);
		attributes.add(classAttributes);
		this.database = database;
		this.classifier = classifier;
		m_train_instances = new Instances(nameOfDataset,
				(ArrayList<Attribute>) attributes, 50);
		m_train_instances.setClass(classAttributes);
		m_test_instances = new Instances(nameOfTestDataset,
				(ArrayList<Attribute>) attributes, 50);
		m_test_instances.setClass(classAttributes);
	}

	public void readDataFromLine(int skipNRows, int line) {
		database.open();
		cursor = database.getCursor();
		if (cursor == null) {
			Log.e("cursor error", "cursor is null");
		}
		loop(lastRow, m_train_instances, cursor);
		lastRow = cursor.getCount();
		cursor.close();
		database.close();
	}

	public void readData(int skipNRows, String table, boolean train) {
		database.open();
		if (train) {
			if (m_train_instances.numInstances() == 0) {
				cursor = database.retrieveRows(table);
				lastRow = cursor.getCount();
				loop(0, m_train_instances, cursor);
				cursor.close();
			} else {
				readDataFromLine(skipNRows, lastRow);

			}
		} else {
			cursor = database.retrieveRows(table);
			// loop makes all the instances
			loop(0, m_test_instances, cursor);
		}

		database.close();
		/*
		 * scanner = new Scanner(data);
		 * 
		 * String line = null;
		 * 
		 * while (scanner.hasNextLine()) { if (skipNRows == 0) { line =
		 * scanner.nextLine(); String[] array = line.split(",");
		 * makeInstance(array); } else { --skipNRows; } }
		 */
		/*
		 * try { m_classifier.buildClassifier(m_train_instance); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	private void loop(int startIndex, Instances dataset, Cursor cursor) {
		// TODO Auto-generated method stub

		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setGroupingUsed(false);
		formatter.setMinimumFractionDigits(7);
		if (cursor.moveToPosition(startIndex)) {
			int i = cursor.getColumnCount();
			String[] buffer = new String[i - 1];
			while (cursor.isAfterLast() == false) {
				// start at 1 because the very first element is actually the row
				// number
				for (int j = 1; j < i - 1; j++) {
					buffer[j - 1] = formatter.format(cursor.getFloat(j));
				}
				// this appends the classification
				buffer[i - 2] = cursor.getString(i - 1);

				makeInstance(buffer, dataset);
				cursor.moveToNext();
				lastRow++;
			}
		}

	}

	public void train(boolean start) throws Exception {
		if (start) {
			readData(5, DatabaseHelper.DATABASE_TABLE_USERS, true);
			classifier.buildClassifier(m_train_instances);
		}else{
			readData(lastRow, DatabaseHelper.DATABASE_TEST_FEATURES, true);
		}
	}

	public void readTestData() {
		readData(0, DatabaseHelper.DATABASE_TEST_FEATURES, false);

	}

	public String evaluate() {

		try {
			Evaluation eval = new Evaluation(m_train_instances);
			eval.evaluateModel(classifier, m_test_instances);
			return eval.toMatrixString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private void makeInstance(String[] array, Instances dataset) {
		// TODO Auto-generated method stub
		Instance instance = new DenseInstance(array.length);
		instance.setDataset(dataset);
		for (int i = 0; i < array.length - 1; i++) {

			instance.setValue(i, Double.parseDouble(array[i]));

		}
		instance.setValue(array.length - 1, array[array.length - 1]);
		instance.setClassValue(array[array.length - 1]);
		dataset.add(instance);
	}
}
