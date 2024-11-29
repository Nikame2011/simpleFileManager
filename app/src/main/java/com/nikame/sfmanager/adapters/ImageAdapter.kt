package com.nikame.sfmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nikame.sfmanager.R
import com.nikame.sfmanager.helpers.FileInfo
import com.nikame.sfmanager.helpers.FileUtils

class ImageAdapter(
    private val context: Context,
    private val size: Int,
    private val files: ArrayList<FileInfo>
) : RecyclerView.Adapter<ImageAdapter.MyViewHolder>()/*, AdapterInterface*/ {

    private val checked: ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)
        return MyViewHolder(itemView)
    }

    private var isSelectionMode = false

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.ivPresent.layoutParams.height = size
        holder.ivPresent.layoutParams.width = size
        holder.tvName.text = files[position].name
        holder.root.tag = position
        holder.root.setOnLongClickListener(longListener)
        holder.root.setOnClickListener(shortListener)
        if (checked.contains(position)) {
            holder.cbSelected.visibility = View.VISIBLE
            holder.cbSelected.isChecked = checked.contains(position)
        } else {
            holder.cbSelected.visibility = View.GONE
        }

        FileUtils.runGlide(context, files[position], holder.ivPresent)
    }

    override fun getItemCount() = files.size

    private val longListener: View.OnLongClickListener = View.OnLongClickListener {
        val number: Int = it.tag as Int
        if (isSelectionMode) {
            FileUtils.tryOpenFile(context, files[number])
//            val intent = Intent(context, FullscreenActivity::class.java)
//            intent.putExtra("CODE", files)
//            intent.putExtra("SELECTED", number)
//            it.context.startActivity(intent)
        } else {
            if (checked.contains(number)) {
                checked.remove(number)
                if (checked.size == 0) {
                    isSelectionMode = false
                }
                notifyItemChanged(number)
            } else {
                checked.add(number)
                if (!isSelectionMode) {
                    isSelectionMode = true
                }
                notifyItemChanged(number)
            }
        }
        true
    }

    private val shortListener: View.OnClickListener = View.OnClickListener {
        val number: Int = it.tag as Int
        if (isSelectionMode) {
            if (checked.contains(number)) {
                checked.remove(number)
                if (checked.size == 0) {
                    isSelectionMode = false
                }
                notifyItemChanged(number)
            } else {
                checked.add(number)
                notifyItemChanged(number)
            }
        } else {
//            val intent = Intent(context, FullscreenActivity::class.java)
//            intent.putExtra("CODE", files)
//            intent.putExtra("SELECTED", number)
//            it.context.startActivity(intent)
            FileUtils.tryOpenFile(context, files[number])
        }
    }

    /*
        @Synchronized
        override*/ fun addItem(file: FileInfo) {
        files.add(file)
        notifyItemInserted(files.size - 1)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: ViewGroup = itemView.findViewById(R.id.clRoot)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val ivPresent: ImageView = itemView.findViewById(R.id.ivPresent)
        val cbSelected: CheckBox = itemView.findViewById(R.id.cbSelected)
    }
}