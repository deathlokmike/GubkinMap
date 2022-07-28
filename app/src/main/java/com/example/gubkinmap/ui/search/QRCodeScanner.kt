package com.example.gubkinmap.ui.search

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.example.gubkinmap.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class QRCodeScanner : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector
    private var firstDetection=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)
        if(ContextCompat.checkSelfPermission(this@QRCodeScanner,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            askForCameraPermission()
        } else {
            setupControls()
        }
    }

    private fun setupControls(){
        detector = BarcodeDetector.Builder(this@QRCodeScanner).build()
        cameraSource = CameraSource.Builder(this@QRCodeScanner, detector)
            .setRequestedPreviewSize(2200, 1080)
            .setAutoFocusEnabled(true)
            .build()
        val sv = findViewById<View>(R.id.cameraSurfaveView) as SurfaceView
        sv.holder.addCallback(surfaceCallBack)
        detector.setProcessor(processor)
    }

    private fun askForCameraPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupControls()
            }
        }
    }

    private val surfaceCallBack = object: SurfaceHolder.Callback{
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            cameraSource.start(surfaceHolder)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }
    }

    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {

        }
        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if (detections.detectedItems.isNotEmpty() && firstDetection){
                val qrCode: SparseArray<Barcode> = detections.detectedItems
                val code = qrCode.valueAt(0)
                val backIntent = Intent()
                backIntent.putExtra("res_code", code.displayValue)
                setResult(RESULT_OK, backIntent)
                finish()
                firstDetection = false
            }
        }
    }
}
