package com.example.bluetooth;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class DataSettings extends Activity {

	private EditText name;
	private EditText email;
	private RadioButton phone;
	private RadioButton accel;
	private RadioButton both;
	private RadioGroup radioGroup;
	private Button pause;
	private Button getData;
	private String Name;
	private String emailAddr;
	private int accelerometers_used; //0 phone,1 both, 2 accel only 
	private Button sendEmail;
	private Activity MainActivity;
	private Spinner spinner;
	private String label;

	ArrayAdapter<CharSequence> spinner_adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 DatabaseHelper h = new DatabaseHelper(getApplicationContext());
		 h.getWritableDatabase().delete(DatabaseHelper.DATABASE_TABLE_USERS, null, null);
		 h.getWritableDatabase().delete(DatabaseHelper.DATABASE_TEST_FEATURES, null, null);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_settings);
		name = (EditText) findViewById(R.id.name);
		email = (EditText) findViewById(R.id.email);
		phone = (RadioButton) findViewById(R.id.radioPhone);
		accel = (RadioButton) findViewById(R.id.radioAccelerometer);
		both = (RadioButton) findViewById(R.id.radioBoth);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
		pause = (Button) findViewById(R.id.pause);
		getData = (Button) findViewById(R.id.getData);
		sendEmail = (Button) findViewById(R.id.sendEmail);
		spinner = (Spinner) findViewById(R.id.Activity);
		spinner_adapter = ArrayAdapter.createFromResource(this,
		        R.array.Activities, android.R.layout.simple_spinner_item);
		spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(spinner_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
			 label = (String) arg0.getItemAtPosition(arg2);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_settings, menu);
		return true;
	}

	public void getData(View v){
		//first save settings. No error checking
		Name = name.getText().toString();
		emailAddr = email.getText().toString();
		accelerometers_used = radioGroup.getCheckedRadioButtonId();
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("Activity", label);
		intent.putExtra("Name", Name);
		intent.putExtra("Email", emailAddr);
		intent.putExtra("WhichAccel", accelerometers_used);
		startActivity(intent);
	}
	
	public void Pause(View v){
		
	}
	
	public void SendEmail(View v){
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{Name});
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_TEXT   , "body of email");
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(DataSettings.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}
