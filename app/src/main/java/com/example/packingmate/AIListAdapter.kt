package com.example.packingmate

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.packingmate.db.ListItem

// 리스트 아이템 타입을 구분하기 위한 sealed class
sealed class RecyclerItem {
    data class ListItemType(val listItem: ListItem) : RecyclerItem()
    data class EditTextType(val id: Int, var text: String = "") : RecyclerItem()
}

class AIListAdapter(
    val items: MutableList<RecyclerItem>, // 기존 aiItemList 대신 사용
    private val onItemDeleted: (ListItem) -> Unit, // 기존 아이템 삭제 콜백
    private val onEditTextDeleted: (Int) -> Unit // EditText 삭제 콜백
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LIST_ITEM = 0
        const val TYPE_EDIT_TEXT = 1
    }

    // 기존 ListItem용 ViewHolder
    inner class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPlane: TextView = itemView.findViewById(R.id.item_plane)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
    }

    // EditText용 ViewHolder
    inner class EditTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.input_field)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        var textWatcher: TextWatcher? = null // TextWatcher 참조 저장
    }

    override fun getItemViewType(position: Int): Int {
        val viewType = when (items[position]) {
            is RecyclerItem.ListItemType -> TYPE_LIST_ITEM
            is RecyclerItem.EditTextType -> TYPE_EDIT_TEXT
        }
        android.util.Log.d("DEBUG_ViewType", "Position $position: ViewType $viewType")
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LIST_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ai_item_list, parent, false)
                ListItemViewHolder(view)
            }
            TYPE_EDIT_TEXT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_input, parent, false)
                EditTextViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemViewHolder -> {
                val item = (items[position] as RecyclerItem.ListItemType).listItem
                holder.itemName.text = item.itemName
                holder.itemPlane.text = item.itemPlane
                holder.deleteButton.setOnClickListener {
                    onItemDeleted(item)
                }
            }
            is EditTextViewHolder -> {
                val editTextItem = items[position] as RecyclerItem.EditTextType

                // 기존 TextWatcher 제거
                holder.textWatcher?.let { holder.editText.removeTextChangedListener(it) }

                holder.editText.setText(editTextItem.text)
                holder.editText.hint = "추가 물품 입력"

                // 새로운 TextWatcher 생성 및 등록
                val newTextWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        editTextItem.text = s.toString()
                    }
                }
                holder.textWatcher = newTextWatcher
                holder.editText.addTextChangedListener(newTextWatcher)

                holder.deleteButton.setOnClickListener {
                    onEditTextDeleted(editTextItem.id)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    // EditText 추가 함수
    fun addEditText() {
        val newId = System.currentTimeMillis().toInt() // 간단한 ID 생성
        val insertPosition = items.size
        items.add(RecyclerItem.EditTextType(newId))
        notifyItemInserted(insertPosition)

        android.util.Log.d("DEBUG_AddEdit", "EditText 추가됨. 위치: $insertPosition, 총 개수: ${items.size}")
    }

    // EditText 삭제 함수
    fun removeEditText(id: Int) {
        val position = items.indexOfFirst {
            it is RecyclerItem.EditTextType && it.id == id
        }
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // 기존 아이템 삭제 함수 (Activity에서 사용)
    fun removeListItem(item: ListItem) {
        val position = items.indexOfFirst {
            it is RecyclerItem.ListItemType && it.listItem.itemId == item.itemId
        }
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // EditText들의 텍스트 가져오기 (저장할 때 사용)
    fun getEditTextValues(): List<String> {
        return items.filterIsInstance<RecyclerItem.EditTextType>()
            .map { it.text }
            .filter { it.isNotBlank() }
    }
}