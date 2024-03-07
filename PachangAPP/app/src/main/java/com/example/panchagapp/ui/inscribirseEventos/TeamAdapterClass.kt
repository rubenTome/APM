package com.example.panchagapp.ui.inscribirseEventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

class TeamAdapterClass (private val dataList: ArrayList<TeamDataClass>): RecyclerView.Adapter<TeamAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_team_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
    return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
       val currentItem = dataList[position]
        holder.rvimage.setImageResource(currentItem.teamImage)
        holder.teamname.text = currentItem.teamTitle
        holder.teamplayers.text = currentItem.teamplayers
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvimage:ImageView = itemView.findViewById(R.id.teamlogo)
        val teamname:TextView = itemView.findViewById(R.id.teamname)
        val teamplayers:TextView = itemView.findViewById(R.id.teamplayers)

    }



}