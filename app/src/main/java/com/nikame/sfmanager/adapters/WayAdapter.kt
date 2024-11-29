package com.nikame.sfmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nikame.sfmanager.R

class WayAdapter(
    private val files: ArrayList<String>
) : RecyclerView.Adapter<WayAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_way, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = files[position]
        holder.root.tag = position
        holder.root.setOnClickListener(shortListener)
    }

    override fun getItemCount() = files.size

    private val shortListener: View.OnClickListener = View.OnClickListener {
        val number: Int = it.tag as Int
//            val intent = Intent(context, FullscreenActivity::class.java)
//            intent.putExtra("CODE", files)
//            intent.putExtra("SELECTED", number)
//            it.context.startActivity(intent)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: ViewGroup = itemView.findViewById(R.id.clRoot)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}