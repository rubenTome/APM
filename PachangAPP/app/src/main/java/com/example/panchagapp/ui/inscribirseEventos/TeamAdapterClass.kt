package com.example.panchagapp.ui.inscribirseEventos

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

class TeamAdapterClass (private val dataList: ArrayList<TeamDataClass>):
    RecyclerView.Adapter<TeamAdapterClass.ViewHolderClass>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_team_list_item, parent, false)
        return ViewHolderClass(itemView,mlistener)
    }

    class ViewHolderClass(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val rvimage:ImageView = itemView.findViewById(R.id.teamlogo)
        val teamname:TextView = itemView.findViewById(R.id.teamname)
        val teamplayers:TextView = itemView.findViewById(R.id.teamplayers)

        init {
            itemView.setOnClickListener{
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
        holder.teamplayers.text = currentItem.teamplayers
    }




}