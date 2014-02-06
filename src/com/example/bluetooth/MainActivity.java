package com.example.bluetooth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import weka.classifiers.trees.J48;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final int phoneAccel = R.id.radioPhone;
	private static final int bothAccel = R.id.radioBoth;
	private static final int externalAccel = R.id.radioAccelerometer;
	private SensorManager sensorManager;
	ArrayAdapter<String> listadaptor;
	ArrayList<String> pairedDevices;
	private DB db;
	Button connect;
	ListView listview;
	BluetoothAdapter b_adapter;
	private Sensor a;
	Set<BluetoothDevice> devicesArray;
	ArrayList<BluetoothDevice> devices;
	IntentFilter filter;
	BroadcastReceiver receiver;
	EditText textbox;
	private SensorEventListener acc_listener;
	private String email;
	private String name;
	private int whichAccel; // 0 = phone, 1 = both, 2 = accelerometer
	private String Activity;
	private Example example;
	private Example example2;
	private boolean record = false;
	public static final int SUCCESS_CONNECT = 0;
	public static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805f9b34fb");
	public static final int MESSAGE_READ = 1;
	private float[] acceleration;
	private MClassifer classifier;
	private boolean start = true;
	private boolean train = true;
	private Example testEx;

	public static Object o = new Object();
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS_CONNECT:
				ConnectedThread connectedThread = new ConnectedThread(
						(BluetoothSocket) msg.obj);
				connectedThread.start();
				Toast.makeText(getApplicationContext(), "Connect",
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_READ:

				// Toast.makeText(getApplicationContext(), string,
				// Toast.LENGTH_LONG).show();
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplicationContext().deleteDatabase(DatabaseHelper.DATABASE_NAME);
		Intent intent = getIntent();
		Activity = intent.getStringExtra("Activity");
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		init();
		File dir = getFilesDir();

		if (b_adapter == null) {
			Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		} else {
			if (!b_adapter.isEnabled()) {
				Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(i, 1);
			}
		}

		getPairedDevices();
		a = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		setupListener();
	}

	public void startDiscovery(View v) {
		// TODO Auto-generated method stub
		b_adapter.cancelDiscovery();
		b_adapter.startDiscovery();
	}

	public void Train(View v) {
		if (train) {
			((Button) findViewById(R.id.train)).setText("Test");
		} else {
			((Button) findViewById(R.id.train)).setText("Train");
		}
		train ^= true;
	}

	private void init() {
		// TODO Auto-generated method stub
		example2 = new Example(getApplicationContext(), 10, Activity,
				DatabaseHelper.DATABASE_TABLE_USERS);
		example = new Example(getApplicationContext(), 10, Activity,
				DatabaseHelper.DATABASE_TABLE_USERS);
		testEx = new Example(getApplicationContext(), 10, Activity,
				DatabaseHelper.DATABASE_TEST_FEATURES);
		Bundle bundle = getIntent().getExtras();
		db = new DB(getApplicationContext());
		classifier = new MClassifer(db, new J48());
		name = bundle.getString("Name");
		email = bundle.getString("Email");
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		whichAccel = bundle.getInt("WhichAccel");
		Activity = bundle.getString("Activity");
		connect = (Button) findViewById(R.id.button);
		listview = (ListView) findViewById(R.id.listView1);
		listadaptor = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		listview.setAdapter(listadaptor);
		b_adapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = new ArrayList<String>();
		devices = new ArrayList<BluetoothDevice>();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					devices.add(device);
					for (int i = 0; i < pairedDevices.size(); i++) {
						if (device.getName().equals(pairedDevices.get(i))) {
							listadaptor.add(device.getName() + " (Paired)\n"
									+ device.getAddress());
							break;
						}
					}
					listadaptor.add(device.getName() + "\n"
							+ device.getAddress());
					// Add the name and address to an array adapter to show in a
					// ListView

				}
			}

		};

		acceleration = new float[3];
		registerReceiver(receiver, filter);
		// Register the BroadcastReceiver
		; // Don't forget to unregister during onDestroy
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (whichAccel == phoneAccel)
					return;
				if (b_adapter.isDiscovering()) {
					b_adapter.cancelDiscovery();
				}
				// TODO Auto-generated method stub
				if (listadaptor.getItem(arg2).contains("Paired")) {
					Toast.makeText(getApplicationContext(), "Device is Paired",
							Toast.LENGTH_LONG).show();
					BluetoothDevice selectedDevice = devices.get(arg2);
					if (phoneAccel != whichAccel) {
						ConnectThread connect = new ConnectThread(
								selectedDevice);
						
						connect.start();

					}

					Log.d("finished start1", "connect.start");

				} else {
					Toast.makeText(getApplicationContext(),
							"Device is not Paired", Toast.LENGTH_LONG).show();
					BluetoothDevice selectedDevice = devices.get(arg2);
					if (phoneAccel != whichAccel) {
						ConnectThread connect = new ConnectThread(
								selectedDevice);
						connect.start();
					}

					Log.d("finished start2", "connect.start");
				}
			}

		});
	}

	private void setupListener() {
		// TODO Auto-generated method stub

		acc_listener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				List<Float> list;
				if (Activity == null) {
					return;
				}
				if (record && train) {
					if (example.reachedLimit()) {
						list = example.calcFeatures();
						db.open();
						db.save(list, Activity,
								DatabaseHelper.DATABASE_TABLE_USERS);
						db.close();
						example = new Example(getApplicationContext(), 10,
								Activity, DatabaseHelper.DATABASE_TABLE_USERS);
					} else {
						acceleration[0] = event.values[0];
						acceleration[1] = event.values[1];
						acceleration[2] = event.values[2];
						example.setData(acceleration);
					}
				} else if (record && !train) {

					if (testEx.reachedLimit()) {
						list = testEx.calcFeatures();
						db.open();
						db.save(list, Activity,
								DatabaseHelper.DATABASE_TEST_FEATURES);
						db.close();
						testEx = new Example(getApplicationContext(), 10,
								Activity, DatabaseHelper.DATABASE_TEST_FEATURES);
					} else {
						acceleration[0] = event.values[0];
						acceleration[1] = event.values[1];
						acceleration[2] = event.values[2];
						testEx.setData(acceleration);
					}

				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}

		};
		if (acc_listener != null) {
			if (whichAccel == bothAccel || whichAccel == phoneAccel) {
				sensorManager.registerListener(acc_listener, a,
						SensorManager.SENSOR_DELAY_UI);
			}
		}
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "Must enable bluetooth", Toast.LENGTH_LONG)
					.show();
			finish();
		}

	}

	private void getPairedDevices() {
		// TODO Auto-generated method stub
		devicesArray = b_adapter.getBondedDevices();
		if (devicesArray.size() > 0) {
			for (BluetoothDevice device : devicesArray) {
				// listadaptor.add(device.getName() + "\n" +
				// device.getAddress());
				pairedDevices.add(device.getName());
				devices.add(device);
				for (String paired : pairedDevices) {
					if (device.getName().equals(paired)) {
						listadaptor.add(device.getName() + "(Paired)" + "\n"
								+ device.getAddress());
					}
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class ConnectThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
		@SuppressLint("NewApi")
		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code

				// ParcelUuid[] uuids = mmDevice.getUuids();

				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.d("entered into run", "run");
			// Cancel discovery because it will slow down the connection
			b_adapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				Log.d("calling connect", "calling mmSocket.connect");
				mmSocket.connect();
				Log.d("following the connect function", "mmSocket is done");
			} catch (IOException e) {
				// Close the socket
				try {
					Log.d("IOException 1", e.toString());
					mmSocket.close();
				} catch (IOException e2) {
					Log.d("IOException Connect Thread", e2.toString());
				}
				// connectionFailed();
				Log.e("IOException", e.toString());
				return;
			}

			// Do work to manage the connection (in a separate thread)
			Log.d("calling mangeConnected Socket", "entering into function");
			mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();

		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	@SuppressWarnings("resource")
	public void SendEmail(View v) throws UnsupportedEncodingException,
			IOException {
		// first save settings. No error checking
		Pause(v);
		// Toast.makeText(getApplicationContext(), "email?",
		// Toast.LENGTH_SHORT).show();
		/*
		classifier = new MClassifer(db, new J48());
		if (whichAccel != phoneAccel) {
			classifier.train(true);
		} else if (whichAccel != externalAccel) {
			classifier.train(true);
		}
		classifier.train(false);
		 */
		db.open();
		StringBuilder strB = new StringBuilder();
		String str = db.getData(DatabaseHelper.DATABASE_TABLE_USERS);
		
		
		//String str = classifier.evaluate();

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		// i.setType("UTF-8");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "jonathan.suit@gmail.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");

		i.putExtra(Intent.EXTRA_TEXT, str);
		if (str == null) {
			finish();
		}else{
			i.putExtra(Intent.EXTRA_TEXT, str);
		}
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(MainActivity.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}

	}

	public void Pause(View v) {
		if (!record) {

			((Button) findViewById(R.id.pause)).setText("Pause");

		} else {
			((Button) findViewById(R.id.pause)).setText("Start");

		}
		record ^= true;
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			super("ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			// buffer store for the stream
			int bytes; // bytes returned from read()
			List<Byte> buffer3 = new ArrayList<Byte>();
			List<Byte> byteBuffer = new ArrayList<Byte>();
			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					if (record) {
						Log.d("INSIDE RUN", "INSIDE RUN");
						byte[] buffer = new byte[1];
						// Read from the InputStream
						bytes = mmInStream.read(buffer);
						if (byteBuffer.size() == 29) {
							synchronized (o) {
								byteBuffer.add(buffer[0]);
								if (record && train) {
									Log.d("record= and train =", "" + record
											+ " " + train);
									example2 = new Example(
											getApplicationContext(), 10,
											Activity,
											DatabaseHelper.DATABASE_TABLE_USERS);
									float[] floatArray = new float[30];
									int i = 0;
									for (byte b : byteBuffer) {
										floatArray[i++] = (float) b;
									}
									i = 0;

									example2.setData(floatArray);
									byteBuffer.clear();
									List<Float> features = example2
											.calcFeatures();
									db.open();
									db.save(features, Activity);
									db.close();
									buffer3.clear();
								} else if (record && !train) {
									// Log.e("TRAIN = ", "false");
									Log.d("record= and train =", "" + record
											+ " " + train);
									example2 = new Example(
											getApplicationContext(),
											10,
											Activity,
											DatabaseHelper.DATABASE_TEST_FEATURES);
									float[] floatArray = new float[30];
									int i = 0;
									for (byte b : byteBuffer) {
										floatArray[i++] = (float) b;
									}
									i = 0;
									example2.setData(floatArray);
									List<Float> features = example2
											.calcFeatures();
									byteBuffer.clear();
									db.open();
									long returncode = db
											.save(features,
													Activity,
													DatabaseHelper.DATABASE_TEST_FEATURES);
									Log.d("Activity = ", Activity);
									db.close();
									buffer3.clear();
								}
								// mHandler.obtainMessage(MESSAGE_READ, bytes,
								// -1,
								// buffer).sendToTarget();
							}
						} else {
							byteBuffer.add(buffer[0]);
						}
					}

					// String string = new String(buffer);
					// Log.d("STRING IS", " " + string);
					// Send the obtained bytes to the UI activity

				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		public void open(){

		}
		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

}
