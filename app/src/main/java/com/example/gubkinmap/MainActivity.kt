package com.example.gubkinmap

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View

import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gubkinmap.ui.search.QRCodeScanner
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    lateinit var root: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root = window.decorView.rootView
        val iS: InputStream = root.resources.openRawResource(R.raw.base)
        val fileIn: List<String> = iS.bufferedReader().readLines()
        val mAuds: ArrayList<String> = ArrayList()
        val mapBG: Drawable = root.resources.getDrawable(R.drawable.ic_first_floor_1)
        val manIm: Drawable = root.resources.getDrawable(R.drawable.ic_people)
        val lblErr = root.findViewById<TextView>(R.id.textView)
        val plcA = root.findViewById<EditText>(R.id.placeholder_a)
        val plcB = root.findViewById<EditText>(R.id.placeholder_b)
        var res: Bitmap
        var i = 0

        while (i < fileIn.size) {
            mAuds.add(fileIn[i])
            i += 3
        }
        
        root.findViewById<FloatingActionButton>(R.id.button_qr).setOnClickListener {
            val intent = Intent(this, QRCodeScanner::class.java)
            startActivityForResult(intent, 1)
        }

        root.findViewById<FloatingActionButton>(R.id.button_search).setOnClickListener {
            lblErr.text = ""
            val therePoint = plcA.text.toString()
            val wherePoint = plcB.text.toString()
            if(therePoint.isNotEmpty() && wherePoint.isNotEmpty()){
                if(mAuds.contains(therePoint)) {
                    if(mAuds.contains(wherePoint))
                    {
                        res = getRoute(therePoint, wherePoint, mapBG, manIm, root)
                        root.findViewById<PhotoView>(R.id.photoView).setImageBitmap(res)
                    }
                    else{
                        val msg = getString(R.string.error_msg, wherePoint)
                        lblErr.text = msg
                    }
                }
                else{
                    val msg = getString(R.string.error_msg, therePoint)
                    lblErr.text = msg
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val res: String? = data?.getStringExtra("res_code")
                root.findViewById<EditText>(R.id.placeholder_a).setText(res)
                val img: Bitmap = drawWhereAmI(res, root)
                root.findViewById<PhotoView>(R.id.photoView).setImageBitmap(img)
            }
        }
    }
}