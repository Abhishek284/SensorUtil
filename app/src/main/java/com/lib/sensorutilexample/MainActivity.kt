package com.lib.sensorutilexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() , AccelerometerManager.AccelerometerListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AccelerometerManager.isSupported(this)
    }

    override fun onResume() {
        super.onResume()
        AccelerometerManager.startListening(this)
    }

    override fun onAccelerationChanged(x: Float, y: Float, z: Float) {
        Toast.makeText(this, " Accelerometer $x $y", Toast.LENGTH_SHORT).show();

    }

    override fun onShake(force: Float) {
    }

    override fun onDestroy() {
        super.onDestroy()
        if (AccelerometerManager.isListening) {
            AccelerometerManager.stopListening();

            Toast.makeText(this, "onDestroy Accelerometer Stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
