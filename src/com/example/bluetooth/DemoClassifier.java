package com.example.bluetooth;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import android.R.attr;
import android.content.Context;
import android.database.Cursor;

import weka.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.SplitCriterion;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class DemoClassifier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4825667049136314581L;

	private Instances m_train_instance;

	private Classifier m_classifier;

	private static DB database;

	// private int numAttributes;
	public DemoClassifier(DB database) {

		String nameOfDataset = "Example";
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
		m_train_instance = new Instances(nameOfDataset,
				(ArrayList<Attribute>) attributes, 50);
		m_train_instance.setClass(classAttributes);
		m_classifier = new J48();
	}

	public void readData(int skipNRows) {
		database.open();
		Cursor cursor = database.retrieveRows(DatabaseHelper.DATABASE_TABLE_USERS);
		Scanner scanner = new Scanner(cursor.getString(0));
		String line = null;
		
		while (scanner.hasNextLine()) {
			if(skipNRows == 0){
				line = scanner.nextLine();
				String[] array = line.split(",");
				makeInstance(array);
			}else{
				--skipNRows;
			}
		}
		/*
		try {
			m_classifier.buildClassifier(m_train_instance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
		database.close();
	}

	public String crossValidate(){
		Evaluation eval;
		
		try {
			eval = new Evaluation(m_train_instance);
			eval.crossValidateModel(m_classifier, m_train_instance, m_train_instance.numInstances()/3 + 3, new Random(1));
			//ArrayList list = eval.predictions();
			
			return eval.toMatrixString() +  eval.toSummaryString();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		 return e.toString();
		}
		
		
		
		
		
	}
	private void makeInstance(String[] array) {
		// TODO Auto-generated method stub
		Instance instance = new DenseInstance(array.length);
		instance.setDataset(m_train_instance);
		for (int i = 0; i < array.length-1; i++) {

			instance.setValue(i, Double.parseDouble(array[i]));
			
		}
		instance.setValue(array.length-1, array[array.length-1]);
		instance.setClassValue(array[array.length - 1]);
		m_train_instance.add(instance);
	}
}
