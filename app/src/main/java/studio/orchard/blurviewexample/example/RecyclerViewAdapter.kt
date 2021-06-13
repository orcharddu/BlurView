package studio.orchard.blurviewexample.example

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import studio.orchard.blurviewexample.R

class RecyclerViewAdapter(var items: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClickListener: ((View, Int) -> Unit)? = null
    var onItemLongClickListener: ((View, Int) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DefaultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.activity_recycler_view_example_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is DefaultViewHolder -> {
                holder.bookTitle.text = items[position]
                holder.itemView.setOnClickListener {
                    onItemClickListener?.let { it1 -> it1(holder.itemView, holder.layoutPosition) }
                }
                holder.itemView.setOnLongClickListener {
                    onItemLongClickListener?.let { it1 -> it1(holder.itemView, holder.layoutPosition) }?: true
                }
            }

        }
    }

    class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookTitle: TextView = view.findViewById(R.id.example_item_title)
        private val bookCover: ImageView = view.findViewById(R.id.example_item_cover)
        init {
            bookCover.elevation = 10f
        }
    }

    class ItemDecoration(context: Context,
                         private val spanCount: Int,
                         leftRightMargin: Int,
                         private val topMargin: Int) : RecyclerView.ItemDecoration() {
        private val leftRightMarginPx = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            leftRightMargin.toFloat(),
                            context.resources.displayMetrics
                        ).toInt()

        override fun getItemOffsets(
                                outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State) {
            when(parent.getChildLayoutPosition(view) % spanCount){
                0               -> outRect.left = leftRightMarginPx
                spanCount - 1   -> outRect.right = leftRightMarginPx
            }
            when {
                parent.getChildLayoutPosition(view) < spanCount -> outRect.top = topMargin
            }
        }
    }

}

