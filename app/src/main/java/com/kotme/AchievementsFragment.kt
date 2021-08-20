package com.kotme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotme.data.Achievement
import com.kotme.databinding.AchievementItemBinding
import com.kotme.databinding.AchievementsBinding

class AchievementsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = AchievementsBinding.inflate(inflater).apply {
        val viewModel by kotmeViewModel()

        next.setOnClickListener {
            findNavController().navigateUp()
        }

        val adapter = AchievementListAdapter()
        items.adapter = adapter
//        viewModel.achievements.observe(viewLifecycleOwner) { list ->
//            adapter.submitList(list)
//            getString(R.string.achievements_count, list.count { it.received }.toString())
//        }

//        viewModel.userProgress.observe(viewLifecycleOwner) {
//            userProgress.progress = it
//        }
    }.root
}

class AchievementListAdapter :
    ListAdapter<Achievement, AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AchievementViewHolder(
            AchievementItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class AchievementViewHolder(private val binding: AchievementItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Achievement) {
        binding.title.text = item.name
        binding.description.text = item.conditionText
    }
}

private class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
    override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean =
        oldItem == newItem
}