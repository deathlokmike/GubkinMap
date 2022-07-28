package com.example.gubkinmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import java.io.File
import java.io.InputStream
import kotlin.math.pow


class Audience {
    var name: String = ""
    var mCord: ArrayList<Int> = ArrayList()
    var mPoint: ArrayList<Int> = ArrayList()
}

fun audsFromFile(root: View): ArrayList<Audience> {
    val auds: ArrayList<Audience> = ArrayList()
    val iS:InputStream = root.resources.openRawResource(R.raw.base)
    val reader =  iS.bufferedReader()
    while (true) {
        val aud = Audience()
        aud.name = reader.readLine() ?: break
        val mCord = reader.readLine().split(", ","[","]")
        for (i in mCord) {
            val j = i.toIntOrNull()
            if (j != null) {
                aud.mCord.add(j)
            }
        }
        val mPoint = reader.readLine().split(", ","[","]")
        for (i in mPoint) {
            val j = i.toIntOrNull()
            if (j != null) {
                aud.mPoint.add(j)
            }
        }
        auds.add(aud)
    }
    return auds
}

fun norm(A: ArrayList<Int>, B: ArrayList<Int>): Double{
    val dems = A.size-1
    var res = 0.0
    for (i in 0..dems) {
        res += (A[i]-B[i]).toDouble().pow(2.0)
    }
    res = res.pow(0.5)
    return res
}

class Graph {
    var name: String = ""
    private var weightMatrix: ArrayList<ArrayList<Double>> = ArrayList(ArrayList())
    private var pointsMatrix: ArrayList<ArrayList<Int>> = ArrayList(ArrayList())
    private var mCords: ArrayList<ArrayList<Int>> = ArrayList(ArrayList())

    fun floyd(){
        val matrixSize = weightMatrix.size-1
        for (k in 0..matrixSize){
            for (i in 0..matrixSize){
                if (i != k) {
                    for (j in 0..matrixSize){
                        if (j != k) {
                            val newElement = weightMatrix[i][k]+weightMatrix[k][j]
                            if (newElement < weightMatrix[i][j]) {
                                weightMatrix[i][j] = newElement
                                pointsMatrix[i][j] = pointsMatrix[k][j]
                            }
                        }
                    }
                }
            }
        }
    }

    private fun reverseFloyd(StartInd: Int, EndInd: Int): ArrayList<Int> {
        val res: ArrayList<Int> = ArrayList()
        var i = EndInd
        res.add(i)
        while (i != StartInd) {
            i = pointsMatrix[StartInd][i]
            res.add(i)
        }
        return res
    }

    fun way(StartPt: Audience, EndPt: Audience): ArrayList<ArrayList<Int>> {
        val res: ArrayList<ArrayList<Int>> = ArrayList(ArrayList())
        res.add(StartPt.mCord)
        val mIndex: ArrayList<Int>
        var startInd = 0
        var endInd = 0
        if (StartPt.mPoint != EndPt.mPoint) {
            var distance = 999999.9
            for (i in StartPt.mPoint) {
                for (j in EndPt.mPoint) {
                    val distStart = norm(StartPt.mCord, mCords[i])*
                            weightMatrix[StartPt.mPoint[0]][StartPt.mPoint[1]]/
                            norm(mCords[StartPt.mPoint[0]], mCords[StartPt.mPoint[1]])
                    val distEnd = norm(EndPt.mCord, mCords[j])*
                            weightMatrix[EndPt.mPoint[0]][EndPt.mPoint[1]]/
                            norm(mCords[EndPt.mPoint[0]], mCords[EndPt.mPoint[1]])
                    val startEnd = distStart + weightMatrix[i][j] + distEnd
                    if (distance > startEnd) {
                        distance = startEnd
                        startInd = i
                        endInd = j
                    }
                }
            }
            mIndex = reverseFloyd(startInd, endInd)
            mIndex.reverse()
            for (i in mIndex) {
                res.add(mCords[i])
            }
        }

        res.add(EndPt.mCord)
        return res
    }

    fun base() {
        val matrixSize = 9
        val inf = 999999.9
        for (i in 0..matrixSize) {
            val vector: ArrayList<Int> = ArrayList()
            for (j in 0..matrixSize) {
                vector.add(i)
            }
            pointsMatrix.add(vector)
        }
        weightMatrix.add(arrayListOf(0.0, 10.0, inf, inf, inf, inf, inf, inf, inf, inf))
        weightMatrix.add(arrayListOf(10.0, 0.0, 7.0, 15.0, inf, inf, inf, inf, inf, inf))
        weightMatrix.add(arrayListOf(inf, 7.0, 0.0, inf, inf, inf, inf, inf, inf, 10.0))
        weightMatrix.add(arrayListOf(inf, 15.0, inf, 0.0, 15.0, inf, inf, inf, 12.0, inf))
        weightMatrix.add(arrayListOf(inf, inf, inf, 15.0, 0.0, 10.0, 7.0, inf, inf, inf))
        weightMatrix.add(arrayListOf(inf, inf, inf, inf, 10.0, 0.0, inf, inf, inf, inf))
        weightMatrix.add(arrayListOf(inf, inf, inf, inf, 7.0, inf, 0.0, 10.0, inf, inf))
        weightMatrix.add(arrayListOf(inf, inf, inf, inf, inf, inf, 10.0, 0.0, inf, inf))
        weightMatrix.add(arrayListOf(inf, inf, inf, 12.0, inf, inf, inf, inf, 0.0, inf))
        weightMatrix.add(arrayListOf(inf, inf, 10.0, inf, inf, inf, inf, inf, inf, 0.0))
    }

    fun cordsToFile () {
        val outputName = "mCords.txt"
        val writer = File(outputName).bufferedWriter()
        for (i in mCords) {
            writer.write("$i\n")
        }
        writer.close()
    }

    fun cordsFromFile (root:View) {
        val iS:InputStream = root.resources.openRawResource(R.raw.cords)
        val reader =  iS.bufferedReader()
        while (true) {
            val ints: ArrayList<Int> = ArrayList()
            val str = reader.readLine() ?: break
            val mCord = str.split(", ","[","]")
            for (i in mCord) {
                val j = i.toIntOrNull()
                if (j != null) {
                    ints.add(j)
                }
            }
            mCords.add(ints)
        }
        reader.close()
    }

    fun pointsToFile () {
        val outputName = "points.txt"
        val writer = File(outputName).bufferedWriter()
        for (i in pointsMatrix) {
            writer.write("$i\n")
        }
        writer.close()
    }

    fun pointsFromFile (root: View) {
        val iS:InputStream = root.resources.openRawResource(R.raw.points)
        val reader =  iS.bufferedReader()
        while (true) {
            val ints: ArrayList<Int> = ArrayList()
            val str = reader.readLine() ?: break
            val mPoint = str.split(", ","[","]")
            for (i in mPoint) {
                val j = i.toIntOrNull()
                if (j != null) {
                    ints.add(j)
                }
            }
            pointsMatrix.add(ints)
        }
        reader.close()
    }

    fun weightsToFile () {
        val outputName = "weights.txt"
        val writer = File(outputName).bufferedWriter()
        for (i in weightMatrix) {
            writer.write("$i\n")
        }
        writer.close()
    }

    fun weightsFromFile (root:View) {
        val iS:InputStream = root.resources.openRawResource(R.raw.weights)
        val reader =  iS.bufferedReader()
        while (true) {
            val ints: ArrayList<Double> = ArrayList()
            val str = reader.readLine() ?: break
            val weights = str.split(", ","[","]")
            for (i in weights) {
                val j = i.toDoubleOrNull()
                if (j != null) {
                    ints.add(j)
                }
            }
            weightMatrix.add(ints)
        }
        reader.close()
    }
}

fun find(Aud_Name: String?, audiences: ArrayList<Audience>): Audience {
    var aud = Audience()
    for (i in audiences) {
        if (Aud_Name == i.name) {
            aud = i
            break
        }
    }
    return aud
}

private fun getBitmap(vectorDrawable: Drawable): Bitmap {
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return bitmap
}

fun drawRoute(WayVector: ArrayList<ArrayList<Int>>, bg: Drawable, man: Drawable): Bitmap {
    val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val image = getBitmap(bg)
    val mapWithRoute: Bitmap = Bitmap.createScaledBitmap(image, 1795, 870, false)
    val canvas = Canvas(mapWithRoute)
    val vSize = WayVector.size
    mPaint.color = Color.BLUE
    mPaint.strokeWidth = 7F

    for (i in 0 until vSize - 1) {
        canvas.drawLine(WayVector[i][0].toFloat(), WayVector[i][1].toFloat(),
            WayVector[i+1][0].toFloat(), WayVector[i+1][1].toFloat(), mPaint)
    }

    mPaint.color = Color.RED
    canvas.drawCircle(WayVector[vSize - 1][0].toFloat(),
        WayVector[vSize - 1][1].toFloat(), 13F, mPaint)

    man.setBounds(WayVector[0][0]-24, WayVector[0][1]-24, WayVector[0][0]+24, WayVector[0][1]+24)
    man.draw(canvas)

    return mapWithRoute

}

fun drawWhereAmI(StartName: String?, rt: View): Bitmap{
    val background: Drawable = rt.resources.getDrawable(R.drawable.ic_first_floor_1)
    val man: Drawable = rt.resources.getDrawable(R.drawable.ic_people)
    val audiences: ArrayList<Audience> = audsFromFile(rt)
    val startAud: Audience = find(StartName, audiences)
    val image = getBitmap(background)
    val map: Bitmap = Bitmap.createScaledBitmap(image, 1795, 870, false)
    val canvas = Canvas(map)
    Log.i("tag", "${startAud.mCord}")
    man.setBounds(startAud.mCord[0]-24, startAud.mCord[1]-24, startAud.mCord[0]+24, startAud.mCord[1]+24)
    man.draw(canvas)
    return map
}

fun getRoute(StartName: String, EndName: String, background: Drawable, man: Drawable, rt: View): Bitmap {
    val level1 = Graph()
    val audiences: ArrayList<Audience> = audsFromFile(rt)

    level1.name = "First Level"
    level1.cordsFromFile(rt)
    level1.pointsFromFile(rt)
    level1.weightsFromFile(rt)

    val startAud: Audience = find(StartName, audiences)

    val endAud: Audience = find(EndName, audiences)

    val wayVector = level1.way(startAud, endAud)

    return drawRoute(wayVector, background, man)
}
