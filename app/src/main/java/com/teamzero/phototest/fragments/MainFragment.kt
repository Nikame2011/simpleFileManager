package com.teamzero.phototest.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.teamzero.phototest.R
import com.teamzero.phototest.databinding.FragmentMainBinding
import com.teamzero.phototest.indexer.getIndexCount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {


    val onClickListener = View.OnClickListener { view ->

        var id = 0
        var type = -1
        val bundle = Bundle()

        if (view.id == R.id.frExplorer) {
            id = R.id.action_mainFragment_to_explorer
        } else if (view.id == R.id.frDownload) {
            //id=R.id.action_mainFragment_to_explorer
        } else if (view.id == R.id.frImage) {
            id = R.id.action_mainFragment_to_photo
            type = 1
        } else if (view.id == R.id.frVideo) {
            id = R.id.action_mainFragment_to_photo
            type = 2
        } else if (view.id == R.id.frAudio) {
            id = R.id.action_mainFragment_to_photo
            type = 0
        } else {
            return@OnClickListener
        }

        bundle.putInt("typeFiles", type)
        findNavController().navigate(
            id,
            bundle
        )


    }

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!
    val df = DecimalFormat("#.##")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        //  viewLifecycleOwner.lifecycleScope.launch {//TODO попробовать добавить для корректного закрытия при выходе из фрагмента
        CoroutineScope(Dispatchers.Default).launch {
            val rootDirectory =
                Environment.getExternalStorageDirectory()

            launch {
                val total = rootDirectory.totalSpace / 1.07374182E9f//1024f / 1024f / 1024f
                val used = rootDirectory.usableSpace / 1.07374182E9f//1024f / 1024f / 1024f

                viewLifecycleOwner.lifecycleScope.launch {
                    //binding.textView2.text = "total: ${total}ГБ, used: ${used}ГБ"
                    binding.frExplorer.tvSize.text =
                        "${df.format(used)} Гб / ${df.format(total)} Гб"
                }
            }

            launch {/*
                var strt = activity?.intent?.getLongExtra("startTime", 0)
                if (strt == null)
                    strt = 0L
*/
                val filesCount =
                    getIndexCount()//countFilesTarget2(rootDirectory, arrayOf(audioTypesList,imageTypesList,videoTypesList)).await()//
                // val fin = Date().time

                viewLifecycleOwner.lifecycleScope.launch {
                    binding.frImage.tvSize.text = filesCount[1].toString()
                    binding.frVideo.tvSize.text = filesCount[2].toString()
                    binding.frAudio.tvSize.text = filesCount[0].toString()
                    //binding.textView3.text = "Finded ${filesCount[1]} images by ${fin - strt} millis"
                    // binding.textView4.text = "Finded ${filesCount[0]} audio by ${fin - strt} millis"
                    //binding.textView5.text = "Finded ${filesCount[2]} video by ${fin - strt} millis"
                }
            }
        }
        // }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.frExplorer.tvName.text = "Память устройства"
        binding.frExplorer.root.setOnClickListener(onClickListener)

        binding.frImage.tvName.text = "Изображения"
        binding.frImage.root.setOnClickListener(onClickListener)

        binding.frVideo.tvName.text = "Видео"
        binding.frVideo.root.setOnClickListener(onClickListener)

        binding.frAudio.tvName.text = "Аудио"
        binding.frAudio.root.setOnClickListener(onClickListener)

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