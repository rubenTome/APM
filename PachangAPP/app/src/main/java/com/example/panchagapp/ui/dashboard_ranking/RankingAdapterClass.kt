package com.example.panchagapp.ui.dashboard_ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.panchagapp.R

class RankingAdapterClass (private val dataList: ArrayList<RankingDataClass>):
    RecyclerView.Adapter<RankingAdapterClass.ViewHolderClass>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int, username: String)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_player_list_item, parent, false)
        return ViewHolderClass(itemView, mlistener )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.teamname.text = (currentItem.playerTitle!!)
        holder.rank.text = "#" + ((position + 1 ).toString())
        Glide.with(holder.itemView.context)
            .load(currentItem.playerImage)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .transform(CircleCrop()) // Apply circle transformation for rounded corners
            .into(holder.rvimage)

    }

    fun addItem(item: RankingDataClass) {
        dataList.add(item)
        notifyItemInserted(dataList.size - 1)
    }

    class ViewHolderClass(itemView: View,listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val rvimage:ImageView = itemView.findViewById(R.id.playerlogo)
        val teamname:TextView = itemView.findViewById(R.id.playername)
        val rank:TextView = itemView.findViewById(R.id.rank)
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, teamname.text.toString())
                }
            }
        }

    }



}