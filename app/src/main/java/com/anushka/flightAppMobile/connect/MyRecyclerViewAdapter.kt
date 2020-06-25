package com.anushka.flightAppMobile.connect


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.anushka.flightAppMobile.R
import com.anushka.flightAppMobile.databinding.ListItemBinding
import com.anushka.flightAppMobile.db.ServerDetails

/**
 * This is adapter class for the Recycler table of DB server
 */
class MyRecyclerViewAdapter(private val clickListener:(ServerDetails)->Unit)
    : RecyclerView.Adapter<MyViewHolder>()
{
    private val serverList = ArrayList<ServerDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding : ListItemBinding =
          DataBindingUtil.inflate(layoutInflater,
              R.layout.list_item,parent,false)
      return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return serverList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      holder.bind(serverList[position],clickListener)
    }
    //reset the table
    fun setList(servers: List<ServerDetails>) {
        serverList.clear()
        serverList.addAll(servers)

    }

}

class MyViewHolder(val binding: ListItemBinding):RecyclerView.ViewHolder(binding.root){

    fun bind(serverDetails: ServerDetails,clickListener:(ServerDetails)->Unit){
          binding.nameTextView.text = serverDetails.url
          binding.listItemLayout.setOnClickListener{
             clickListener(serverDetails)
          }
    }
}