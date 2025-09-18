package com.example.supabasemanager.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.supabasemanager.R
import com.example.supabasemanager.models.Activity
import com.example.supabasemanager.models.ActivityStatus
import com.example.supabasemanager.models.Priority

class ActivityAdapter(
    private val onDeleteClick: (Activity) -> Unit
) : ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        private val statusChip: TextView = itemView.findViewById(R.id.statusChip)
        private val priorityText: TextView = itemView.findViewById(R.id.priorityText)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        fun bind(activity: Activity) {
            titleText.text = activity.title
            descriptionText.text = activity.description
            categoryText.text = activity.category
            statusChip.text = activity.status.name
            priorityText.text = activity.priority.name

            // Set status chip color
            val statusColor = when (activity.status) {
                ActivityStatus.PENDING -> Color.parseColor("#FFA500")
                ActivityStatus.IN_PROGRESS -> Color.parseColor("#2196F3")
                ActivityStatus.COMPLETED -> Color.parseColor("#4CAF50")
                ActivityStatus.CANCELLED -> Color.parseColor("#F44336")
            }
            statusChip.setBackgroundColor(statusColor)

            // Set priority text color
            val priorityColor = when (activity.priority) {
                Priority.LOW -> Color.parseColor("#9E9E9E")
                Priority.MEDIUM -> Color.parseColor("#FF9800")
                Priority.HIGH -> Color.parseColor("#FF5722")
                Priority.URGENT -> Color.parseColor("#E91E63")
            }
            priorityText.setTextColor(priorityColor)

            deleteButton.setOnClickListener {
                onDeleteClick(activity)
            }
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }
    }
}