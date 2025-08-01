package com.example.packingmate.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.R
import com.example.packingmate.data.db.ListItem

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
        //holder.checkBox.isChecked = item.isChecked

        val itemRoot = holder.itemView.findViewById<LinearLayout>(R.id.item_root)

        if(item.isChecked){
            itemRoot.setBackgroundResource(R.drawable.strike_line)
        }
        else{
            itemRoot.setBackgroundColor(Color.TRANSPARENT)
        }

        //이벤트 제거
        holder.checkBox.setOnCheckedChangeListener(null)
        //체크박스 상태 반영
        holder.checkBox.isChecked = item.isChecked
        //이벤트 재등록
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            item.isChecked = isChecked
            onItemCheckChanged(item, isChecked)

            if(isChecked){
                itemRoot.setBackgroundResource(R.drawable.strike_line)
            }
            else{
                itemRoot.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

