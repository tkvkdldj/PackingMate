package com.example.packingmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AIListAdapter (
    val aiItemList : MutableList<ListItem>, //리스트가 수정 가능하게 하기 위함
    private val onItemDeleted : (ListItem) -> Unit //삭제 콜백
    ) :

    RecyclerView.Adapter<AIListAdapter.ViewHolder>(){
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val itemName : TextView = itemView.findViewById(R.id.item_name)
        val itemPlane : TextView = itemView.findViewById(R.id.item_plane)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.ai_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = aiItemList[position]

        holder.itemName.text = item.itemName
        holder.itemPlane.text = item.itemPlane

        holder.deleteButton.setOnClickListener{
            onItemDeleted(item) //콜백
        }
    }

    override fun getItemCount(): Int {
        return aiItemList.size
    }

}

