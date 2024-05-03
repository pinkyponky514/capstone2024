// CommunityImageAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.CommunityItem
import com.example.reservationapp.R

class CommunityImageAdapter(private val itemList: List<CommunityItem>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_FOOTER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
                ItemViewHolder(view)
            }
            VIEW_TYPE_FOOTER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_footer, parent, false)
                FooterViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(itemList[position])
            holder.itemView.setOnClickListener {
                onItemClick(position)
            }
        } else if (holder is FooterViewHolder) {
            holder.itemView.findViewById<Button>(R.id.buttonAddItem).setOnClickListener {
                onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        // itemList의 크기 + 1 반환
        return itemList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        // 마지막 항목은 Footer로 설정
        return if (position == itemList.size) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)

        fun bind(item: CommunityItem) {
            imageView.setImageResource(item.imageResource)
            textViewTitle.text = item.title

            // 이미지 클릭 이벤트 처리
            itemView.setOnClickListener {
                onItemClick(adapterPosition) // 클릭한 아이템의 위치를 전달
            }
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
