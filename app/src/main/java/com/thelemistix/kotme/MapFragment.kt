package com.thelemistix.kotme

import android.os.Bundle
import android.view.View
import android.widget.TextView

class MapFragment() : FragmentBase(R.layout.map) {
    var exercise: Int = 1
        set(value) {
            field = value
            markers.forEach { it.background = null }
            for (i in 0 until value) {
                markers[i].setBackgroundResource(R.drawable.map_marker_completed)
            }
            markers[value - 1].setBackgroundResource(R.drawable.map_marker_current)
            messageView?.text = mainActivity.db.getReplic(value)
        }

    private var messageView: TextView? = null

    val markers = ArrayList<View>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        markers.clear()
        markers.add(view.findViewById(R.id.exercise1))
        markers.add(view.findViewById(R.id.exercise2))
        markers.add(view.findViewById(R.id.exercise3))
        markers.add(view.findViewById(R.id.exercise4))
        markers.add(view.findViewById(R.id.exercise5))
        markers.add(view.findViewById(R.id.exercise6))
        markers.add(view.findViewById(R.id.exercise7))

        messageView = view.findViewById(R.id.message)

        for (i in 0 until markers.size) {
            val it = markers[i]
            it.setOnClickListener {
                mainActivity.lesson.lesson = i + 1
                mainActivity.lesson.show()
            }
        }

        view.findViewById<View>(R.id.next).setOnClickListener {
            mainActivity.lesson.lesson = exercise
            mainActivity.lesson.show()
        }

        exercise = exercise
    }

    fun setCurrentExercise() {
        exercise = mainActivity.db.progress + 1
    }
}
