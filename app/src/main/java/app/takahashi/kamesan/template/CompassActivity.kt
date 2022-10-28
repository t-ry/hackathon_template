package app.takahashi.kamesan.template

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import app.takahashi.kamesan.template.databinding.ActivityCompassBinding

class CompassActivity : AppCompatActivity() ,SensorEventListener {
    private lateinit var binding: ActivityCompassBinding

    // SensorManager
    private lateinit var sensorManager: SensorManager

    // Sensor
    private var mAccelerometerSensor: Sensor? = null
    private var mMagneticFieldSensor: Sensor? = null

    // Sensorの値
    private var mAccelerometerValue: FloatArray = FloatArray(3)
    private var mMagneticFieldValue: FloatArray = FloatArray(5)

    // 一度でも地磁気センサーの値を取得したか
    private var mMagneticFiledFlg: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SensorManagerのインスタンスを生成
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 加速度センサーを取得する
        mAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // 地磁気センサーを取得する
        mMagneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()

        // リスナーをセットする
        setSensorEventListener()
    }

    override fun onPause() {
        super.onPause()

        // リスナーを解除する
        sensorManager.unregisterListener(this@CompassActivity)
    }

    // センサーの精度が変化した時の処理
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /* 何もしない */
    }

    // センサーの値が変化した時の処理
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            holdSensorEventValues(event)

            // 地磁気センサーの値を取得できている場合のみ処理をする
            if (mMagneticFiledFlg) Log.d("DEGREE", calculateDegree().toString())
        }
    }

    private fun setSensorEventListener() {
        // SensorManagerにリスナーをセットする
        // リスナー：センサーの値が変化したときに何の処理をするかを定義したインスタンス
        mAccelerometerSensor?.also { sensor: Sensor ->
            sensorManager.registerListener(
                this@CompassActivity,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        mMagneticFieldSensor?.also { sensor: Sensor ->
            sensorManager.registerListener(
                this@CompassActivity,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun holdSensorEventValues(event: SensorEvent) {
        // 値が変わったセンサーの値を保存する
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (event.values != null) {
                    mAccelerometerValue = event.values
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                if (event.values != null) {
                    mMagneticFieldValue = event.values
                    mMagneticFiledFlg = true
                }
            }
        }
    }

    private fun calculateDegree(): Float {
        // 方位を出すための変換行列
        val rotate = FloatArray(16)
        val inclination = FloatArray(16)

        // 回転角
        val orientation = FloatArray(3)

        // 行列化
        SensorManager.getRotationMatrix(
            rotate,
            inclination,
            mAccelerometerValue,
            mMagneticFieldValue
        )

        // 回転角を取得
        SensorManager.getOrientation(
            rotate,
            orientation
        )

        // 角度を求める
        val doubleOrientation = orientation[0].toDouble()
        return Math.toDegrees(doubleOrientation).toFloat()
    }
}