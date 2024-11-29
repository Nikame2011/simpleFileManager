package com.nikame.sfmanager.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nikame.sfmanager.FullscreenActivity
import com.nikame.sfmanager.R
import com.nikame.sfmanager.helpers.FileUtils
import java.io.File
import java.util.Date

class ExplorerAdapter(
    private val context: Context,
    private val size: Int,
    private val files: ArrayList<File>,
    val onFolderSelectedListener: (File) -> Unit
) : RecyclerView.Adapter<ExplorerAdapter.MyViewHolder>()/*, AdapterInterface*/ {

    private val checked: ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
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
//        val totalSize =
//            files[position].walkTopDown().filter { it.isFile }.map { it.length() }.sum()
//
//        holder.tvDescr.text = FileUtils.getStringSize (totalSize)

        if (files[position].isDirectory) {
            val folders = files[position].listFiles({ file -> file.isDirectory })?.size
            val counts = files[position].listFiles({ file -> !file.isDirectory })?.size
            holder.tvDescr.text =
                "Папок: ${if (folders == null) 0 else folders}. Файлов: ${if (counts == null) 0 else counts}"
        } else {
            holder.tvDescr.text = FileUtils.getStringSize(files[position].length())
        }
        val dateFormat = android.text.format.DateFormat.getMediumDateFormat(context)
        holder.tvDate.text = dateFormat.format(Date(files[position].lastModified()))

        FileUtils.runGlide(context, files[position], holder.ivPresent)
    }

    override fun getItemCount() = files.size

    private val longListener: View.OnLongClickListener = View.OnLongClickListener {
        val number: Int = it.tag as Int
        if (isSelectionMode) {
            val intent = Intent(context, FullscreenActivity::class.java)
            intent.putExtra("CODE", files)
            intent.putExtra("SELECTED", number)
            it.context.startActivity(intent)
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
            val file = files[number]
            if (file.isDirectory) {
                onFolderSelectedListener(file)
            } else {
                FileUtils.tryOpenFile(context, file)
            }
        }
    }

//    /*@Synchronized
//    override*/ fun addItem(file: File) {
//        files.add(file)
//        notifyItemInserted(files.size - 1)
//    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: ViewGroup = itemView.findViewById(R.id.clRoot)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescr: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivPresent: ImageView = itemView.findViewById(R.id.ivPresent)
        val cbSelected: CheckBox = itemView.findViewById(R.id.cbSelected)
    }
}