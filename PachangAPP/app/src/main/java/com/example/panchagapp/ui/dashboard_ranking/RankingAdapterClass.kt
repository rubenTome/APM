package com.example.panchagapp.ui.dashboard_ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

class RankingAdapterClass (private val dataList: ArrayList<RankingDataClass>):
    RecyclerView.Adapter<RankingAdapterClass.ViewHolderClass>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
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
        holder.rvimage.setImageResource(currentItem.playerImage)
        holder.teamname.text = (currentItem.playerTitle)
    }

    class ViewHolderClass(itemView: View,listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val rvimage:ImageView = itemView.findViewById(R.id.playerlogo)
        val teamname:TextView = itemView.findViewById(R.id.playername)
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }



}