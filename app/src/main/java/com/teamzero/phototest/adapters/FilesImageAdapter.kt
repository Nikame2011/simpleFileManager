package com.teamzero.phototest.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamzero.phototest.FullscreenActivity
import com.teamzero.phototest.R
import com.teamzero.phototest.helpers.DirInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//todo add documentation to all classes and methods
class FilesImageAdapter(
    private val context: Context,
    private val size: Int,
    private val files: ArrayList<DirInfo>,
    val onFolderSelectedListener: (DirInfo) -> Unit
) : RecyclerView.Adapter<FilesImageAdapter.MyViewHolder>() {

    private val checked: ArrayList<Int> = ArrayList()
    private var isSelectionMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_photo, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.ivPresent.layoutParams.height = size
        holder.ivPresent.layoutParams.width = size
        holder.tvName.text = files[position].name
        holder.root.tag = position
        if (isSelectionMode) {
            holder.cbSelected.visibility = View.VISIBLE
            holder.cbSelected.isChecked = checked.contains(position)
            holder.root.setOnLongClickListener(openLongListener)
            holder.root.setOnClickListener(checkShortListener)
        } else {
            holder.cbSelected.visibility = View.GONE
            holder.root.setOnLongClickListener(checkLongListener)
            holder.root.setOnClickListener(openShortListener)
        }
        CoroutineScope(Dispatchers.Main).launch {
            Glide.with(context).load(files[position].file).into(holder.ivPresent)
        }
    }

    override fun getItemCount() = files.size

    private val checkLongListener: View.OnLongClickListener = View.OnLongClickListener {
        val number: Int = it.tag as Int
        if (checked.contains(number)) {
            checked.remove(number)
            if (checked.size == 0) {
                isSelectionMode = false
                notifyDataSetChanged()
            } else {
                notifyItemChanged(number)
            }
        } else {
            checked.add(number)
            if (!isSelectionMode) {
                isSelectionMode = true
                notifyDataSetChanged()
            } else {
                notifyItemChanged(number)
            }
        }

        true
    }

    private val openLongListener: View.OnLongClickListener = View.OnLongClickListener {
        val number: Int = it.tag as Int
        val intent = Intent(context, FullscreenActivity::class.java)
        intent.putExtra("CODE", files)
        intent.putExtra("SELECTED", number)
        it.context.startActivity(intent)
        true
    }

    private val checkShortListener: View.OnClickListener = View.OnClickListener {
        val number: Int = it.tag as Int
        if (checked.contains(number)) {
            checked.remove(number)
            if (checked.size == 0) {
                isSelectionMode = false
                notifyDataSetChanged()
            } else {
                notifyItemChanged(number)
            }
        } else {
            checked.add(number)
            if (!isSelectionMode) {
                isSelectionMode = true
                notifyDataSetChanged()
            } else {
                notifyItemChanged(number)
            }
        }
    }

    private val openShortListener: View.OnClickListener = View.OnClickListener {
        val number: Int = it.tag as Int
        val file = files[number]
        onFolderSelectedListener(file)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: ViewGroup = itemView.findViewById(R.id.clRoot)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val ivPresent: ImageView = itemView.findViewById(R.id.ivPresent)
        val cbSelected: CheckBox = itemView.findViewById(R.id.cbSelected)
    }
}