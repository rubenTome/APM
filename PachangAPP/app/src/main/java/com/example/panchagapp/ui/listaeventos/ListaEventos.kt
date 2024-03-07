package com.example.panchagapp.ui.listaeventos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panchagapp.R

class ListaEventos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<EventosDataClass>
    lateinit var eventimageList: Array<Int>
    lateinit var eventnameList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_eventos)

        eventimageList = arrayOf(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_account_circle_24,
        )

        eventnameList = arrayOf(
            "Evento 1",
            "Evento 2",
            "Torneo 1",
            "Evento 3",
            "Evento 4",
            "Torneo 2",
        )

        recyclerView = findViewById(R.id.eventrv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<EventosDataClass>()
        getData()
    }

    private fun getData(){
        for (i in eventimageList.indices) {
            val dataClass = EventosDataClass(eventimageList[i], eventnameList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = EventosAdapterClass(dataList)
    }
}