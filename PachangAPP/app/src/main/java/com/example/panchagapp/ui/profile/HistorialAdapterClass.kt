package com.example.panchagapp.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.inscribirseEventos.TeamAdapterClass

class HistorialAdapterClass (private val dataList: ArrayList<HistorialDataClass>):
    RecyclerView.Adapter<HistorialAdapterClass.ViewHolderClass>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_historial_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.team1Image.setImageResource(currentItem.team1Image)
        holder.team1Name.text = currentItem.team1Name
        holder.result.text = currentItem.result
        holder.team2Name.text = currentItem.team2Name
        holder.team2Image.setImageResource(currentItem.team2Image)
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val team1Image:ImageView = itemView.findViewById(R.id.team1Image)
        val team1Name:TextView = itemView.findViewById(R.id.team1Name)
        val result:TextView = itemView.findViewById(R.id.result)
        val team2Name:TextView = itemView.findViewById(R.id.team2Name)
        val team2Image:ImageView = itemView.findViewById(R.id.team2Image)

    }



}