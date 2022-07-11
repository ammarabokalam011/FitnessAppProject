package de.hsfl.jkkab.fitnessappproject.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * HeartRateGraph zur Darstellung vom
 * Puls in festgelegten Korridoren.
 *
 * @author Kevin Blaue
 * @version 1.0 (21.05.2022)
 */
class HeartRateGraph(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val MAX_HEART_RATE = 200
    private val MIN_HEART_RATE = 80
    private val LEGEND_STEP = 25
    private val MAX_TIME_MILLIS = 45 * 60 * 1000
    private val heartRates: MutableList<HeartRatePoint>
    private val corridors: MutableList<HeartRateCorridor>
    private var corridorOffset: Long = 0
    private var timeBefore: Long
    private var isTracking = false
    private val paintHeartRate: Paint
    private val paintCorridor: Paint
    private val paintLegend: Paint
    fun setCorridorOffsetSeconds(corridorOffset: Long) {
        this.corridorOffset = Math.max(MAX_TIME_MILLIS.toLong(), corridorOffset * 1000)
    }

    fun startTracking() {
        isTracking = true
    }

    fun stopTracking() {
        isTracking = false
    }

    private fun initCorridors() {
        corridors.add(HeartRateCorridor(100, 0, 2 * 60 * 1000))
        corridors.add(HeartRateCorridor(150, 10 * 60 * 1000, 20 * 60 * 1000))
        corridors.add(HeartRateCorridor(180, 25 * 60 * 1000, 30 * 60 * 1000))
        corridors.add(HeartRateCorridor(140, 35 * 60 * 1000, 40 * 60 * 1000))
    }

    fun addHeartRate(heartRate: Int) {
        if (isTracking) {
            val currentTime = System.currentTimeMillis()
            val heartRatePoint = HeartRatePoint(
                heartRate,
                (currentTime - timeBefore).toFloat()
            )
            heartRates.add(heartRatePoint)
            timeBefore = currentTime
        }
        invalidate()
    }

    private fun drawLegend(canvas: Canvas): Int {
        var maxWidth = 0
        var i = MAX_HEART_RATE - LEGEND_STEP
        while (i > MIN_HEART_RATE) {
            val text = i.toString()
            val bounds = Rect()
            paintLegend.getTextBounds(text, 0, text.length, bounds)
            canvas.drawText(text, 2f, (getYCoord(i) + bounds.height() / 2).toFloat(), paintLegend)
            maxWidth = Math.max(maxWidth, bounds.width())
            i -= LEGEND_STEP
        }
        return maxWidth
    }

    override fun onDraw(canvas: Canvas) {
        val legendWidth = drawLegend(canvas) + 15
        val path = Path()
        if (heartRates.size > 0) {
            path.moveTo(legendWidth.toFloat(), getYCoord(heartRates[0].heartRate).toFloat())
        }
        val milliWidth = (width - legendWidth).toFloat() / MAX_TIME_MILLIS
        var currMillis: Long = 0
        var overflow = 0

        //Puls zeichnen
        for (i in heartRates.indices) {
            val point = heartRates[i]
            path.lineTo(
                currMillis.toInt() * milliWidth + legendWidth,
                getYCoord(point.heartRate).toFloat()
            )
            currMillis += point.timeBetween.toLong()
            if (currMillis * milliWidth > width) {
                overflow++
            }
        }
        //Überflüssige Elemente (älter als Maximalzeit) entfernen
        for (i in 0 until overflow) {
            heartRates.removeAt(i)
        }

        //Korridore zeichnen
        val offsetWidth = (milliWidth * (MAX_TIME_MILLIS - corridorOffset)).toInt()
        for (i in corridors.indices) {
            val corridor = corridors[i]
            canvas.drawLine(
                ((milliWidth * corridor.corridorStart).toInt() + offsetWidth + legendWidth).toFloat(),
                getYCoord(corridor.heartRate).toFloat(),
                (
                        (milliWidth * corridor.corridorEnd).toInt() + offsetWidth + legendWidth).toFloat(),
                getYCoord(corridor.heartRate).toFloat(),
                paintCorridor
            )
        }
        canvas.drawPath(path, paintHeartRate)
        super.onDraw(canvas)
    }

    private fun getYCoord(heartRate: Int): Int {
        var heartRate = heartRate
        heartRate = Math.min(Math.max(heartRate, MIN_HEART_RATE), MAX_HEART_RATE)
        heartRate -= MIN_HEART_RATE
        val yHeight = (height / (MAX_HEART_RATE - MIN_HEART_RATE) * heartRate).toFloat()
        return (height - yHeight).toInt()
    }

    internal inner class HeartRatePoint(val heartRate: Int, val timeBetween: Float)
    internal inner class HeartRateCorridor(
        val heartRate: Int,
        corridorStart: Long,
        corridorEnd: Long
    ) {
        val corridorStart: Float
        val corridorEnd: Float

        init {
            this.corridorStart = corridorStart.toFloat()
            this.corridorEnd = corridorEnd.toFloat()
        }
    }

    init {
        heartRates = ArrayList()
        corridors = ArrayList()
        paintHeartRate = Paint()
        paintCorridor = Paint()
        paintLegend = Paint()
        timeBefore = System.currentTimeMillis()
        initCorridors()
        setCorridorOffsetSeconds(0)
        paintHeartRate.color = Color.RED
        paintHeartRate.style = Paint.Style.STROKE
        paintHeartRate.strokeWidth = 2f
        paintCorridor.strokeWidth = 2f
        paintCorridor.color = Color.RED
        paintCorridor.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 5F)
        paintLegend.color = Color.WHITE
        paintLegend.textSize = 30f
    }
}