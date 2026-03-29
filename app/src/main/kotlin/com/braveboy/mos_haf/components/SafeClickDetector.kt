package com.braveboy.mos_haf.components

import android.os.SystemClock
import android.view.MotionEvent
import kotlin.math.abs

private var lastEventTimeMs: Long = 0
private const val ALLOWED_SIMILAR_TOUCHES = 3
private const val BETWEEN_CLICK_THRESHOLD = 300L
private const val CLEAR_LIST_THRESHOLD = 10000L

object SafeClickDetector {
    private val recentGestures = ArrayList<Touch>()
    private const val HISTORY_SIZE = 10
    private var isDetectingSimilarGestures = false

    private data class Touch(
        val x: Float,
        val y: Float
    ) {
        fun isSimilar(other: Touch): Boolean {
            return abs(this.x - other.x) < 1f &&
                abs(this.y - other.y) < 1f
        }
    }

    private fun clearGestureHistory() {
        recentGestures.clear()
    }

    private fun trimGestureHistory() {
        if (recentGestures.size > HISTORY_SIZE) {
            recentGestures.remove(recentGestures.first())
        }
    }

    private fun checkSimilarGestures(newTouch: Touch): Boolean {
        isDetectingSimilarGestures =
            recentGestures.count { it.isSimilar(newTouch) } >= ALLOWED_SIMILAR_TOUCHES
        return isDetectingSimilarGestures
    }

    fun recordEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newTouch = Touch(event.rawX, event.rawY)
                if (!checkSimilarGestures(newTouch)) {
                    recentGestures.add(newTouch)
                    trimGestureHistory()
                }
            }
        }
        return isDetectingSimilarGestures
    }

    fun isSafeClick(currentTime: Long): Boolean {
        var isSafe = false
        if (currentTime - lastEventTimeMs >= BETWEEN_CLICK_THRESHOLD && !isDetectingSimilarGestures) {
            if (lastEventTimeMs != 0L && currentTime - lastEventTimeMs >= CLEAR_LIST_THRESHOLD) {
                clearGestureHistory()
            }
            isSafe = true
        }
        lastEventTimeMs = SystemClock.uptimeMillis()
        return isSafe
    }
}
