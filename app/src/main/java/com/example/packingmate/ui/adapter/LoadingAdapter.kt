package com.example.packingmate.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.R

import com.example.packingmate.data.db.ListItem
import com.example.packingmate.data.db.DBHelper


class LoadingAdapter(
    private val dbHelper: DBHelper,
    private val onDelete: () -> Unit  // 삭제 후 리스트 재로딩 콜백
) : ListAdapter<ListItem, LoadingAdapter.ViewHolder>(diffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameText: TextView = view.findViewById(R.id.item_name)
        val itemPlaneText: TextView = view.findViewById(R.id.item_plane)
        val deleteButton: ImageView = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ai_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemNameText.text = item.itemName
        holder.itemPlaneText.text = item.itemPlane

        holder.deleteButton.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("listItem", "itemId = ?", arrayOf(item.itemId.toString()))
            onDelete()  // 삭제 후 리스트 다시 불러오기 콜백 호출
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) = oldItem.itemId == newItem.itemId
            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) = oldItem == newItem
        }
    }
}

