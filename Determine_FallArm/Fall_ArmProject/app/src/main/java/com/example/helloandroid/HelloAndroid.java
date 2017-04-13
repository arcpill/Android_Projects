package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class HelloAndroid extends Activity implements SensorEventListener {
	private SensorManagerSimulator sensorManager;

	TextView xCoor; // declare X axis object
	TextView yCoor; // declare Y axis object
	TextView zCoor; // declare Z axis object

	TextView xOrCoor; // declare X axis object
	TextView yOrCoor; // declare Y axis object
	TextView zOrCoor; // declare Z axis object

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy =
					new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		xCoor = (TextView) findViewById(R.id.xcoor); // create X axis object
		yCoor = (TextView) findViewById(R.id.ycoor); // create Y axis object
		zCoor = (TextView) findViewById(R.id.zcoor); // create Z axis object

		//Orientation views
		xOrCoor = (TextView) findViewById(R.id.xorcoor); // create X axis object
		yOrCoor = (TextView) findViewById(R.id.yorcoor); // create Y axis object
		zOrCoor = (TextView) findViewById(R.id.zorcoor); // create Z axis object

		sensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
		sensorManager.connectSimulator();
	}
		@Override
		protected void onResume(){
			super.onResume();
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					sensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					sensorManager.SENSOR_DELAY_NORMAL);

		}

		@Override
				protected void onStop(){
			sensorManager.unregisterListener(this);
			super.onStop();
		}

	public void onAccuracyChanged(Sensor sensor,int accuracy){

	}

	public void onSensorChanged(SensorEvent event){

		int sensor = event.type;
		float[] values = event.values;

		// check sensor type
		if( sensor==Sensor.TYPE_ACCELEROMETER){

			// assign directions
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			xCoor.setText("X: "+x);
			yCoor.setText("Y: "+y);
			zCoor.setText("Z: "+z);
		}
		if( sensor ==  Sensor.TYPE_ORIENTATION ){

			// assign directions
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			xOrCoor.setText("X: "+x);
			yOrCoor.setText("Y: "+y);
			zOrCoor.setText("Z: "+z);
		}

		// Connect to Server ------- START

		String[] value1 = xCoor.getText().toString().split(":");
		String[] value2 = yCoor.getText().toString().split(":");
		String[] value3 = zCoor.getText().toString().split(":");
		String[] value4 = xOrCoor.getText().toString().split(":");
		String[] value5 = yOrCoor.getText().toString().split(":");
		String[] value6 = zOrCoor.getText().toString().split(":");

		String sendData = value1[1] + "," + value2[1] +  "," + value3[1]
						+ "," + value4[1] + "," + value5[1] + "," + value6[1];

		Socket socket = null;
		DataOutputStream dataOutputStream = null;
		DataInputStream dataInputStream = null;

		try {

			socket = new Socket("10.0.2.2", 8888);
			Log.d("ServerSocket", "Connected: -" + sendData);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream.writeUTF(sendData);
			//textIn.setText(dataInputStream.readUTF());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dataOutputStream != null){
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dataInputStream != null){
				try {
					dataInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// Connect to Server ------- END
	}
}

/*
package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;

public class HelloAndroid extends Activity {
*/
/*
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
*/