package com.nikame.sfmanager.fragments

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nikame.sfmanager.R
import com.nikame.sfmanager.databinding.FragmentMainBinding
import com.nikame.sfmanager.helpers.FileIndexer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.log

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {


    val onClickListener = View.OnClickListener { view ->

        var id = 0
        var type=""
        val bundle = Bundle()

        if (view.id == R.id.frExplorer) {
            bundle.putSerializable("folder", Environment.getExternalStorageDirectory())
            id = R.id.action_mainFragment_to_explorer
        } else if (view.id == R.id.frDownload) {
            bundle.putSerializable(
                "folder",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            )
            id = R.id.action_mainFragment_to_explorer
        } else if (view.id == R.id.frImage) {
            id = R.id.action_mainFragment_to_photo
            type = "Images"
        } else if (view.id == R.id.frVideo) {
            id = R.id.action_mainFragment_to_photo
            type = "Video"
        } else if (view.id == R.id.frAudio) {
            id = R.id.action_mainFragment_to_photo
            type = "Audio"
        } else if (view.id == R.id.frDocuments) {
            id = R.id.action_mainFragment_to_photo
            type = "Documents"
        } else {
            return@OnClickListener
        }

        bundle.putString("typeFiles", type)
        findNavController().navigate(
            id,
            bundle
        )

    }

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!
    val df = DecimalFormat("#.#")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        //  viewLifecycleOwner.lifecycleScope.launch {//TODO попробовать добавить для корректного закрытия при выходе из фрагмента
//        CoroutineScope(Dispatchers.Default).launch {
        //viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            val rootDirectory =
                Environment.getExternalStorageDirectory()

            launch {
                val total = rootDirectory.totalSpace / 1.07374182E9f//1024f / 1024f / 1024f
//                val free = rootDirectory.freeSpace / 1.07374182E9f
                val used = rootDirectory.usableSpace / 1.07374182E9f//1024f / 1024f / 1024f

                launch(Dispatchers.Main) {
                    //binding.textView2.text = "total: ${total}ГБ, used: ${used}ГБ"
                    binding.frExplorer.tvSize.text =
                        "${df.format(used)} Гб / ${df.format(total)} Гб"/* / ${df.format(free)} Гб"*/
                }
            }

            launch {
                val directoryForFileSaving =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                //val sm:StorageManager
                val total =
                    directoryForFileSaving.length() // / 1.07374182E9f//1024f / 1024f / 1024f
                val list = directoryForFileSaving.listFiles()
                val count = list.size
                launch(Dispatchers.Main) {
                    //binding.textView2.text = "total: ${total}ГБ, used: ${used}ГБ"
                    binding.frDownload.tvSize.text =
                        "${df.format(total)} Гб (${count})"
                }
            }

//            launch {
////                val videoList = mutableListOf<Video>()
//
//                val collection =
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        MediaStore.Video.Media.getContentUri(
//                            MediaStore.VOLUME_EXTERNAL
//                        )
//                    } else {
//                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                    }
//
//                val projection = arrayOf(
//                    MediaStore.Video.Media._ID,
//                    MediaStore.Video.Media.DISPLAY_NAME,
//                    MediaStore.Video.Media.DURATION,
//                    MediaStore.Video.Media.SIZE
//                )
//
//// Show only videos that are at least 5 minutes in duration.
////                val selection = "${MediaStore.Video.Media.DURATION} >= ?"
////                val selectionArgs = arrayOf(
////                    TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
////                )
//
//// Display videos in alphabetical order based on their display name.
//                val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
//
//                val query = context?.applicationContext?.contentResolver?.query(
//                    collection,
//                    projection,
//                    null,
//                    null,
//                    sortOrder
//                )
//                query?.use { cursor ->
//                    // Cache column indices.
//                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//                    val nameColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
//                    val durationColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
//
//                    while (cursor.moveToNext()) {
//                        // Get values of columns for a given video.
//                        val id = cursor.getLong(idColumn)
//                        val name = cursor.getString(nameColumn)
//                        val duration = cursor.getInt(durationColumn)
//                        val size = cursor.getInt(sizeColumn)
//
//                        val contentUri: Uri = ContentUris.withAppendedId(
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                            id
//                        )
//
//                        // Stores column values and the contentUri in a local object
//                        // that represents the media file.
//                        Log.e("fileeeee","${contentUri}, ${name}, ${duration}, ${size}")
////                        videoList += Video(contentUri, name, duration, size)
//                    }
//                }
////                val directoryDocuments =
////                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
////                //val sm:StorageManager
////                val total = Files.size(directoryDocuments.toPath()) //directoryDocuments.length()// / 1.07374182E9f//1024f / 1024f / 1024f
////                val count = directoryDocuments.listFiles().size
////
////                viewLifecycleOwner.lifecycleScope.launch {
////                    //binding.textView2.text = "total: ${total}ГБ, used: ${used}ГБ"
////                    binding.frDocuments.tvSize.text =
////                        "${df.format(total)} Гб (${count})"
////                }
//            }

//            launch {
//
//                val collection =
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        MediaStore.Images.Media.getContentUri(
//                            MediaStore.VOLUME_EXTERNAL
//                        )
//                    } else {
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    }
//
//                val projection = arrayOf(
//                    MediaStore.Images.Media._ID,
//                    MediaStore.Images.Media.DISPLAY_NAME,
//                    MediaStore.Images.Media.SIZE,
//                    MediaStore.Images.Media.DATA
//                )
//
//// Show only Imagess that are at least 5 minutes in duration.
////                val selection = "${MediaStore.Images.Media.DURATION} >= ?"
////                val selectionArgs = arrayOf(
////                    TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
////                )
//
//// Display Imagess in alphabetical order based on their display name.
//                val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
//
//                val query = context?.applicationContext?.contentResolver?.query(
//                    collection,
//                    projection,
//                    null,
//                    null,
//                    sortOrder
//                )
//                query?.use { cursor ->
//                    // Cache column indices.
//                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//                    val nameColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
////                    val durationColumn =
////                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DURATION)
//                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
//                    val dataColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//
//
//                    while (cursor.moveToNext()) {
//                        // Get values of columns for a given Images.
//                        val id = cursor.getLong(idColumn)
//                        val name = cursor.getString(nameColumn)
////                        val duration = cursor.getInt(durationColumn)
//                        val size = cursor.getInt(sizeColumn)
//                        val data = cursor.getString(dataColumn)
//
//                        val contentUri: Uri = ContentUris.withAppendedId(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            id
//                        )
//
//                        // Stores column values and the contentUri in a local object
//                        // that represents the media file.
//                        Log.e("fileeeeeImages","${contentUri}, ${name},  ${size}, ${data}")//${duration},
////                        ImagesList += Images(contentUri, name, duration, size)
//                    }
//                }
//
//            }

//            launch {/*
//                var strt = activity?.intent?.getLongExtra("startTime", 0)
//                if (strt == null)
//                    strt = 0L
//*/
//                val filesCount =
//                    FileIndexer.getIndexCount()//countFilesTarget2(rootDirectory, arrayOf(audioTypesList,imageTypesList,videoTypesList)).await()//
//                // val fin = Date().time
//
//                viewLifecycleOwner.lifecycleScope.launch {
//                    //binding.frImage.tvSize.text = filesCount[1].toString()
//                    binding.frVideo.tvSize.text = filesCount[2].toString()
//                    binding.frAudio.tvSize.text = filesCount[0].toString()
//                    //binding.textView3.text = "Finded ${filesCount[1]} images by ${fin - strt} millis"
//                    // binding.textView4.text = "Finded ${filesCount[0]} audio by ${fin - strt} millis"
//                    //binding.textView5.text = "Finded ${filesCount[2]} video by ${fin - strt} millis"
//                }
//            }

//            launch {
//                var indexes = FileIndexer.getIndexes()
//                launch(Dispatchers.Main) {
//                    binding.frAudio.tvSize.text = indexes[0].count.toString()
//                    binding.frImage.tvSize.text = indexes[1].count.toString()
//                    binding.frVideo.tvSize.text = indexes[2].count.toString()
//                    binding.frDocuments.tvSize.text = indexes[3].count.toString()
//                }

            launch {
                val indexes = FileIndexer.getIndexes(requireContext().applicationContext, "Images")
                if (indexes != null) {
                    launch(Dispatchers.Main) {
                        binding.frImage.tvSize.text = indexes.count.toString()
                    }
                }
            }

            launch {
                val indexes = FileIndexer.getIndexes(requireContext().applicationContext, "Video")
                if (indexes != null) {
                    launch(Dispatchers.Main) {
                        binding.frVideo.tvSize.text = indexes.count.toString()
                    }
                }
            }

//            launch {
//                val indexes = FileIndexer.getIndexes(requireContext().applicationContext, "Images")
//                if (indexes != null) {
//                    launch(Dispatchers.Main) {
//                        binding.frImage.tvSize.text = indexes.count.toString()
//                    }
////                    binding.frAudio.tvSize.text = indexes[0].count.toString()
////                    binding.frVideo.tvSize.text = indexes[2].count.toString()
////                    binding.frDocuments.tvSize.text = indexes[3].count.toString()
//                }
//            }
//            }
            }
            // }
            return binding.root

        }

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        private fun getStringSize(size: Long): String {
            if (size <= 0)
                return "0"
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                size / Math.pow(
                    1024.0,
                    digitGroups.toDouble()
                )
            ) + " " + units[digitGroups]
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.frExplorer.tvName.text = "Память устройства"
            binding.frExplorer.root.setOnClickListener(onClickListener)
            binding.frExplorer.iv.setImageResource(R.drawable.folder_default)

            binding.frDownload.tvName.text = getString(R.string.download)
            binding.frDownload.root.setOnClickListener(onClickListener)
            binding.frDownload.iv.setImageResource(R.drawable.design_folder_download)

            binding.frImage.tvName.text = getString(R.string.images)
            binding.frImage.root.setOnClickListener(onClickListener)
            binding.frImage.iv.setImageResource(R.drawable.design_folder_image)

            binding.frVideo.tvName.text = getString(R.string.video)
            binding.frVideo.root.setOnClickListener(onClickListener)
            binding.frVideo.iv.setImageResource(R.drawable.design_folder_video)

            binding.frAudio.tvName.text = getString(R.string.audio)
            binding.frAudio.root.setOnClickListener(onClickListener)
            binding.frAudio.iv.setImageResource(R.drawable.design_folder_audio)

            binding.frDocuments.tvName.text = getString(R.string.documents)
            binding.frDocuments.root.setOnClickListener(onClickListener)
            binding.frDocuments.iv.setImageResource(R.drawable.design_folder_default)
            //binding.btnImages.setOnClickListener(onClickListener)
            //binding.btnDownload.setOnClickListener(onClickListener)

        }

        override fun onStop() {
            super.onStop()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }