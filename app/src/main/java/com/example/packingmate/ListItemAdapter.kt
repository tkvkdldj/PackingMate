package com.example.packingmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListItemAdapter (
    private val itemList : List<ListItem>,
    private val onItemCheckChanged : (ListItem, Boolean)->Unit
) : RecyclerView.Adapter<ListItemAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checkBox : CheckBox = itemView.findViewById(R.id.checkbox)
        val itemName : TextView = itemView.findViewById(R.id.item_name)
        val itemPlane : TextView = itemView.findViewById(R.id.item_plane)
    }

    //ViewHolder를 생성해 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //새로운 뷰를 생성해 뷰홀더의 인자로 넣어줌
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_detail, parent, false)
        return ViewHolder(view)
    }

    //반환된 ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.itemName.text = item.itemName
        holder.itemPlane.text = item.itemPlane
        holder.checkBox.isChecked = item.isChecked

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            onItemCheckChanged(item, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

