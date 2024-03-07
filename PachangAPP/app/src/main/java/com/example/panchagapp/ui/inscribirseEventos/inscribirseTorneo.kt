package com.example.panchagapp.ui.inscribirseEventos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

class inscribirseTorneo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<TeamDataClass>
    lateinit var teamimageList: Array<Int>
    lateinit var teamnameList: Array<String>
    lateinit var teamplayersList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscribirse_torneo)

        teamimageList = arrayOf(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
        )

        teamnameList = arrayOf(
            "Equipo 1",
            "Equipo 2",
            "Equipo 3",
            "Equipo 4",
            "Equipo 5",
            "Equipo 6",
        )

        teamplayersList = arrayOf(
            "7/11",
            "9/11",
            "10/11",
            "5/11",
            "0/11",
            "1/11",
        )

        recyclerView = findViewById(R.id.teamrv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<TeamDataClass>()
        getData()
    }

    private fun getData(){
        for (i in teamimageList.indices) {
            val dataClass = TeamDataClass(teamimageList[i], teamnameList[i], teamplayersList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = TeamAdapterClass(dataList)
    }
}