package com.example.panchagapp.ui.notifications_calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R
import com.example.panchagapp.ui.inscribirseEventos.TeamAdapterClass

class ClasificacionAdapterClass (private val dataList: ArrayList<ClasificacionDataClass>):
    RecyclerView.Adapter<ClasificacionAdapterClass.ViewHolderClass>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_teamrank_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.teamname.text = currentItem.teamname
        holder.jugados.text = currentItem.jugados
        holder.wins.text = currentItem.wins
        holder.draws.text = currentItem.draws
        holder.losses.text = currentItem.losses
        holder.goals.text = currentItem.goals
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val teamname:TextView = itemView.findViewById(R.id.textViewTeamName)
        val jugados:TextView = itemView.findViewById(R.id.textViewPlayedMatches)
        val wins:TextView = itemView.findViewById(R.id.textViewWins)
        val draws:TextView = itemView.findViewById(R.id.textViewDraws)
        val losses:TextView = itemView.findViewById(R.id.textViewLosses)
        val goals:TextView = itemView.findViewById(R.id.textViewGoalsScored)


    }



}