package com.example.panchagapp.ui.listaeventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

class EventosAdapterClass (private val dataList: ArrayList<EventosDataClass>): RecyclerView.Adapter<EventosAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layour_event_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvimage.setImageResource(currentItem.eventImage)
        holder.teamname.text = currentItem.eventTitle
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvimage:ImageView = itemView.findViewById(R.id.eventlogo)
        val teamname:TextView = itemView.findViewById(R.id.eventmname)

    }



}