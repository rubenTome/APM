package com.example.panchagapp.ui.inscribirseEventos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.panchagapp.MainActivity
import com.example.panchagapp.R
import com.example.panchagapp.WeatherService
import com.example.panchagapp.ui.listaeventos.EventosAdapterClass
import com.example.panchagapp.ui.listaeventos.EventosDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [inscribirseTorneoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class inscribirseTorneoFragment : Fragment()  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<TeamDataClass>
    lateinit var teamimageList: Array<Int>
    lateinit var teamnameList: Array<String>
    lateinit var teamplayersList: Array<String>
    private val apiKey = "3e3d57cbd2e191d212257ab99f6ab698"
    private lateinit var weatherService: WeatherService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        weatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        // Replace "CityName" with the desired city

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_inscribirse_torneo, container, false)
        GlobalScope.launch(Dispatchers.IO) {
            val weatherData = weatherService.getWeather("Santiago de Compostela", apiKey)
            withContext(Dispatchers.Main) {
                updateUI(rootview,weatherData)
            }
        }
        return rootview
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            inscribirseTorneoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        var trackbutton = view.findViewById<Button>(R.id.trackbutton)

        trackbutton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_torneo_to_navigation_direction)
            Toast.makeText(activity, "Como llegar a localizacion!", Toast.LENGTH_SHORT).show()
        }

        var tvname = view.findViewById<TextView>(R.id.tveventname)
        val bundle = arguments
        if (bundle == null) {
            Log.d("Confirmation", "FragmentTorneo sin arguments")
            return
        }
        val args = inscribirseEventoFragmentArgs.fromBundle(bundle)
        if(args.eventName.isNullOrBlank()){
            tvname.text = "Torneo sin Nombre"
        }
        else {
            tvname.text = args.eventName
        }

        recyclerView = view.findViewById(R.id.teamrv)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        var adapter  = TeamAdapterClass(dataList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: TeamAdapterClass.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedItem = dataList[position]
                findNavController().navigate(R.id.action_navigation_torneo_to_navigation_home)
                Toast.makeText(activity, "Inscrito en " + selectedItem.teamTitle.toString(), Toast.LENGTH_SHORT).show()
            }
        })



    }
    private fun updateUI(view: View,weatherData: MainActivity.WeatherData) {
        view.findViewById<TextView>(R.id.weathername).text = weatherData.name
        view.findViewById<TextView>(R.id.weathertemp).text = "${weatherData.main.temp.toInt()}°C"
        val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"
        Glide.with(this).load(iconUrl).into(view.findViewById(R.id.imageView2))
    }



    private fun dataInitialize() {
        dataList = arrayListOf<TeamDataClass>()
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

        for (i in teamimageList.indices) {
            val dataClass = TeamDataClass(teamimageList[i], teamnameList[i], teamplayersList[i])
            dataList.add(dataClass)
        }
    }


}