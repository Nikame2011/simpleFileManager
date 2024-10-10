package com.nikame.sfmanager.adapters

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikame.sfmanager.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date


class AudioAdapter(
    private val context: Context,
    private val size: Int,
    private val files: ArrayList<File>
) : RecyclerView.Adapter<AudioAdapter.MyViewHolder>(), AdapterInterface {

    private val checked: ArrayList<Int> = ArrayList()

    private var isSelectionMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return MyViewHolder(itemView)
    }

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
        //TODO to video files add other adapter - because video-adapter need view as photo-adapter, but audio-adapter be like folder-view

        holder.tvDescr.text = (files[position].length() / 1024f / 1024f).toString()
        holder.tvDate.text = Date(files[position].lastModified()).toString()

        CoroutineScope(Dispatchers.Main).launch {
            if (files[position].length() > 0) {
                try {
                    val metaRetriver = MediaMetadataRetriever()
                    metaRetriver.setDataSource(files[position].absolutePath)
                    val art = metaRetriver.getEmbeddedPicture()
                    if (art != null) {
                        Glide.with(context).load(art).into(holder.ivPresent)
                    } else {
                        Glide.with(context).load(R.drawable.file_audio).into(holder.ivPresent)
                    }
                } catch (e: Exception) {
                    Log.e("audio icon error", files[position].absolutePath, e)
                    //Toast.makeText(context, "audio icon error", Toast.LENGTH_LONG).show()
                    Glide.with(context).load(R.drawable.file_audio).into(holder.ivPresent)
                }
            } else {
                Glide.with(context).load(R.drawable.file_audio).into(holder.ivPresent)
            }
        }
    }

    override fun getItemCount() = files.size

    private val longListener: View.OnLongClickListener = View.OnLongClickListener {
        val number: Int = it.tag as Int
        if (isSelectionMode) {
            /*      TODO add code to open file in other apps or add audio/video players
                   val intent = Intent(Intent.ACTION_VIEW, Uri.fromFile(files[number]))
                   intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   intent.setDataAndType(Uri.fromFile(files[number]), getMimeType(files[number]))
                   it.context.startActivity(intent)*/
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
            /*      TODO add code to open file in other apps or add audio/video players
                   val intent = Intent(Intent.ACTION_VIEW, Uri.fromFile(files[number]))
                   intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   intent.setDataAndType(Uri.fromFile(files[number]), getMimeType(files[number]))
                   it.context.startActivity(intent)*/

//            val intent = Intent(context, FullscreenActivity::class.java)
//            intent.putExtra("CODE", files)
//            intent.putExtra("SELECTED", number)
//            it.context.startActivity(intent)
        }
    }

    @Synchronized
    override fun addItem(file: File) {
        files.add(file)
        notifyItemInserted(files.size - 1)
    }



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: ViewGroup = itemView.findViewById(R.id.clRoot)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescr: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivPresent: ImageView = itemView.findViewById(R.id.ivPresent)
        val cbSelected: CheckBox = itemView.findViewById(R.id.cbSelected)
    }
}