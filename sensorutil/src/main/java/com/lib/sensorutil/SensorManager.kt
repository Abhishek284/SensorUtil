import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

object AccelerometerManager {
    private var context: Context? = null
    /**
     * Accuracy configuration
     */
    private var threshold = 15.0f
    private var interval = 200
    private var sensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    // you could use an OrientationListener array instead
// if you plans to use more than one listener
    private var listener: AccelerometerListener? = null
    /**
     * indicates whether or not Accelerometer Sensor is supported
     */
    private var supported: Boolean? = null
    /**
     * Returns true if the manager is listening to orientation changes
     */
    /**
     * indicates whether or not Accelerometer Sensor is running
     */
    var isListening = false


    /**
     * Unregisters listeners
     */
    fun stopListening() {
        isListening = false
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager!!.unregisterListener(sensorEventListener)
            }
        } catch (e: Exception) {
        }
    }

    /**
     * Returns true if at least one Accelerometer sensor is available
     */
    fun isSupported(cntxt: Context?): Boolean {
        context = cntxt
        if (supported == null) {
            if (context != null) {
                sensorManager =
                    context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                // Get all sensors in device
                val sensors =
                    sensorManager!!.getSensorList(
                        Sensor.TYPE_ACCELEROMETER
                    )
                supported = sensors.size > 0
            } else {
                supported = java.lang.Boolean.FALSE
            }
        }
        return supported!!
    }

    /**
     * Configure the listener for shaking
     *
     * @param threshold minimum acceleration variation for considering shaking
     * @param interval minimum interval between to shake events
     */
    fun configure(threshold: Int, interval: Int) {
        AccelerometerManager.threshold = threshold.toFloat()
        AccelerometerManager.interval = interval
    }

    /**
     * Registers a listener and start listening
     *
     * @param accelerometerListener callback for accelerometer events
     */
    fun startListening(accelerometerListener: AccelerometerListener?) {
        sensorManager =
            context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Take all sensors in device
        val sensors =
            sensorManager!!.getSensorList(
                Sensor.TYPE_ACCELEROMETER
            )
        if (sensors.size > 0) {
            sensor = sensors[0]
            // Register Accelerometer Listener
            isListening = sensorManager!!.registerListener(
                sensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
            listener = accelerometerListener
        }
    }

    /**
     * Configures threshold and interval
     * And registers a listener and start listening
     *
     * @param accelerometerListener callback for accelerometer events
     * @param threshold minimum acceleration variation for considering shaking
     * @param interval minimum interval between to shake events
     */
    fun startListening(
        accelerometerListener: AccelerometerListener?,
        threshold: Int,
        interval: Int
    ) {
        configure(threshold, interval)
        startListening(accelerometerListener)
    }

    private val sensorEventListener: SensorEventListener? = object : SensorEventListener {
        private var now: Long = 0
        private var timeDiff: Long = 0
        private var lastUpdate: Long = 0
        private var lastShake: Long = 0
        private var x = 0f
        private var y = 0f
        private var z = 0f
        private var lastX = 0f
        private var lastY = 0f
        private var lastZ = 0f
        private var force = 0f
        override fun onAccuracyChanged(
            sensor: Sensor,
            accuracy: Int
        ) {
        }

        override fun onSensorChanged(event: SensorEvent) { // use the event timestamp as reference
// so the manager precision won't depends
// on the AccelerometerListener implementation
// processing time
            now = event.timestamp
            x = event.values[0]
            y = event.values[1]
            z = event.values[2]
            // if not interesting in shake events
// just remove the whole if then else block
            if (lastUpdate == 0L) {
                lastUpdate = now
                lastShake = now
                lastX = x
                lastY = y
                lastZ = z
                Toast.makeText(
                    context,
                    "No Motion detected",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                timeDiff = now - lastUpdate
                if (timeDiff > 0) {
                    force = Math.abs(x + y + z - lastX - lastY - lastZ)
                    if (java.lang.Float.compare(force, threshold) > 0) {
                        if (now - lastShake >= interval) { // trigger shake event
                            listener?.onShake(force)
                        } else {
                            Toast.makeText(
                                context, "No Motion detected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        lastShake = now
                    }
                    lastX = x
                    lastY = y
                    lastZ = z
                    lastUpdate = now
                } else {
                    Toast.makeText(
                        context,
                        "No Motion detected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // trigger change event
            listener?.onAccelerationChanged(x, y, z)
        }
    }

    interface AccelerometerListener {
        fun onAccelerationChanged(
            x: Float,
            y: Float,
            z: Float
        )

        fun onShake(force: Float)
    }
}