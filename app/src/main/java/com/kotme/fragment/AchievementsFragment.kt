package com.kotme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotme.KotmeRepository
import com.kotme.R
import com.kotme.data.Achievement
import com.kotme.databinding.AchievementItemBinding
import com.kotme.databinding.AchievementsBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AchievementsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = AchievementsBinding.inflate(inflater).apply {
        val viewModel by viewModels<AchievementsViewModel>()

        next.setOnClickListener {
            findNavController().navigateUp()
        }

        val adapter = AchievementListAdapter()
        items.adapter = adapter
        viewModel.achievements.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            getString(R.string.achievements_count, list.count { it.received }.toString())
        }

        viewModel.userProgress.observe(viewLifecycleOwner) {
            userProgress.progress = it ?: 0
        }
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

@HiltViewModel
class AchievementsViewModel @Inject constructor(val repo: KotmeRepository) : ViewModel() {
    val achievements = repo.achievementDao.all().asLiveData()

    val userProgress = repo.userProgress().asLiveData()
}