package com.anushka.flightAppMobile.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anushka.flightAppMobile.R
import com.anushka.flightAppMobile.databinding.ActivityMainBinding
import com.anushka.flightAppMobile.db.ServerDetails
import com.anushka.flightAppMobile.db.ServerDetailsDataBase
import com.anushka.flightAppMobile.db.ServerDetailsRepository


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var serverViewModel: ServerViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        val dao = ServerDetailsDataBase.getInstance(application).serverDetailsDAO
        val repository = ServerDetailsRepository(dao)
        val factory =
            ServerViewModelFactory(
                repository,
                this
            )
        serverViewModel = ViewModelProvider(this,factory).get(ServerViewModel::class.java)
        binding.myViewModel = serverViewModel
        binding.lifecycleOwner = this
        initRecyclerView()

        serverViewModel.message.observe(this, Observer {
         it.getContentIfNotHandled()?.let {
             Toast.makeText(this, it, Toast.LENGTH_LONG).show()
         }
        })

    }

   private fun initRecyclerView(){
       binding.serverRecyclerView.layoutManager = LinearLayoutManager(this)
       adapter = MyRecyclerViewAdapter(
           { selectedItem: ServerDetails -> listItemClicked(selectedItem) })
       binding.serverRecyclerView.adapter = adapter
       displayServerList()
   }

    private fun displayServerList(){
        serverViewModel.servers.observe(this, Observer {
            Log.i("MYTAG",it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(serverDetails: ServerDetails){
        Toast.makeText(this,"selected name is" +
                " ${serverDetails.url}",Toast.LENGTH_LONG).show()
        serverViewModel.initServer(serverDetails)
    }
}
