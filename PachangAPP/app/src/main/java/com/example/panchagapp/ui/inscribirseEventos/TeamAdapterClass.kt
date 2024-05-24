import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.inscribirseEventos.TeamDataClass

class TeamAdapterClass(val dataList: ArrayList<TeamDataClass>,
    private val itemLayoutResId: Int
) : RecyclerView.Adapter<TeamAdapterClass.ViewHolderClass>() {

    private lateinit var mlistener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistener = listener
    }

    fun addItem(item: String) {
        dataList.add(TeamDataClass(R.drawable.baseline_account_circle_24, item, "0"))
        notifyItemInserted(dataList.size - 1) // Notify RecyclerView about the new item
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < dataList.size) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(itemLayoutResId, parent, false)
        return ViewHolderClass(itemView, mlistener)
    }

    class ViewHolderClass(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val rvimage: ImageView = itemView.findViewById(R.id.teamlogo)
        val teamname: TextView = itemView.findViewById(R.id.teamname)
        val players: TextView = itemView.findViewById(R.id.teamplayers)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvimage.setImageResource(currentItem.teamImage)
        holder.teamname.text = currentItem.teamTitle
        holder.players.text = currentItem.players
    }
}