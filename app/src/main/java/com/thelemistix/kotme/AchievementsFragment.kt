package com.thelemistix.kotme

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView

class AchievementsFragment(): FragmentBase(R.layout.achievements) {
    val achievementViews = ArrayList<View>()

    var achievementsCountView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.next).setOnClickListener {
            mainActivity.back()
        }

        achievementsCountView = view.findViewById(R.id.achievementsCount)

        val titleView = view.findViewById<TextView>(R.id.title)
        val descriptionView = view.findViewById<TextView>(R.id.description)

        achievementViews.add(view.findViewById(R.id.achiv1))
        achievementViews.add(view.findViewById(R.id.achiv2))
        achievementViews.add(view.findViewById(R.id.achiv3))
        achievementViews.add(view.findViewById(R.id.achiv4))
        achievementViews.add(view.findViewById(R.id.achiv5))

        mainActivity.db.iterateAchievementDescriptions { id, name, description ->
            val achView = achievementViews[id - 1]
            achView.background = ColorDrawable(Color.GRAY)
            achView.setOnClickListener {
                titleView.text = name
                descriptionView.text = description
            }
        }

        setAchievements()
    }

    fun setAchievements() {
        if (achievementViews.size > 0) {
            achievementViews.forEach {
                (it.background as ColorDrawable).color = Color.GRAY
            }

            mainActivity.db.achievements.forEach {
                (achievementViews[it - 1].background as ColorDrawable).color = Color.parseColor("#5FE50B")
            }
        }

        achievementsCountView?.text = "Ваши достижения: ${mainActivity.db.achievements.size}/10"
    }
}
