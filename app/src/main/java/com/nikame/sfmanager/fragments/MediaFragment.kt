package com.nikame.sfmanager.fragments

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nikame.sfmanager.R
import com.nikame.sfmanager.adapters.AudioAdapter
import com.nikame.sfmanager.adapters.FilesAudioAdapter
import com.nikame.sfmanager.adapters.FilesImageAdapter
import com.nikame.sfmanager.adapters.ImageAdapter
import com.nikame.sfmanager.adapters.WayAdapter
import com.nikame.sfmanager.databinding.FragmentPhotoBinding
import com.nikame.sfmanager.helpers.DirInfo
import com.nikame.sfmanager.helpers.FileIndexer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MediaFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPhotoBinding.inflate(inflater, container, false)

        /*
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    Toast.makeText(context, "backIntercept", Toast.LENGTH_SHORT).show();
                    if (binding.rvImages.isVisible) {
                    }
                }
        */
        val typeFiles: String = arguments?.getString("typeFiles")!!

        val directoryInfo: DirInfo? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable("folder", DirInfo::class.java)
            } else {
                arguments?.getParcelable("folder") as DirInfo?
            }

        if (directoryInfo == null) {
            showFolders(typeFiles)
        } else {
            showFiles(typeFiles, directoryInfo)
        }
        return binding.root
    }

    private fun showFolders(typeFiles: String) {
        //todo find difference with viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default)

        //todo replace
        // CoroutineScope(Dispatchers.Default).launch {launch{1} launch{2} await[1,2]  viewLifecycleOwner.lifecycleScope.launch{3}}
        // to viewLifecycleOwner.lifecycleScope.launch {launch(Dispatchers.Default){1} launch(Dispatchers.Default){2} await[1,2] 3 }}
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {

            val folders =
                FileIndexer.getIndexes(requireContext().applicationContext, typeFiles)
                    ?.getDirectoriies()

            val size: Int = getDisplayWidth() / 3

            launch(Dispatchers.Main) {
                //Toast.makeText(context, "allFinded", Toast.LENGTH_SHORT).show();

                binding.rvWay.layoutManager =
                    LinearLayoutManager(binding.rvWay.context, RecyclerView.VERTICAL, false)
                binding.rvWay.adapter = WayAdapter(
                    arrayListOf(
                        if (typeFiles.equals("Audio"))
                            requireContext().getString(R.string.audio)
                        else if (typeFiles.equals("Images"))
                            requireContext().getString(R.string.images)
                        else if (typeFiles.equals("Video"))
                            requireContext().getString(R.string.video)
                        else
                            requireContext().getString(R.string.documents)
                    )
                )

                if (typeFiles.equals("Audio") || typeFiles.equals("Documents")) {
                    binding.rvImages.layoutManager =
                        LinearLayoutManager(binding.rvImages.context, RecyclerView.VERTICAL, false)

                    binding.rvImages.adapter =
                        FilesAudioAdapter(requireContext(), size / 2, folders!!) {
                            openFolder(it, typeFiles)
                        }
                } else {
                    binding.rvImages.layoutManager = GridLayoutManager(binding.rvImages.context, 3)
                    binding.rvImages.adapter =
                        FilesImageAdapter(requireContext(), size, folders!!) {
                            openFolder(it, typeFiles)
                        }
                }
            }
        }
    }

    private fun showFiles(typeFiles: String, directoryInfo: DirInfo) {
//        viewLifecycleOwner.lifecycleScope.launch {
        var way = ArrayList<String>()
        var folder: File? = directoryInfo.rootFolder
        while (folder != null) {
            if (folder.equals(Environment.getExternalStorageDirectory())) {
                way.add(0, getString(R.string.rootFolder))
                break
            }
            way.add(0, folder.name)
            folder = folder.parentFile
        }

        binding.rvWay.layoutManager =
            LinearLayoutManager(binding.rvWay.context, RecyclerView.HORIZONTAL, false)
        binding.rvWay.adapter = WayAdapter(way)
        binding.rvWay.scrollToPosition(way.size - 1)

        if (typeFiles.equals("Audio") || typeFiles.equals("Documents")) {
            val size: Int = getDisplayWidth() / 6
            binding.rvImages.layoutManager =
                LinearLayoutManager(binding.rvImages.context, RecyclerView.VERTICAL, false)
            binding.rvImages.adapter = AudioAdapter(requireContext(), size, directoryInfo.listFiles)
        } else {
            val countItem = 4
            val size: Int = getDisplayWidth() / countItem
            binding.rvImages.layoutManager =
                GridLayoutManager(binding.rvImages.context, countItem)
            binding.rvImages.adapter = ImageAdapter(requireContext(), size, directoryInfo.listFiles)
        }

//        }
    }

    private fun openFolder(folder: DirInfo, typeFiles: String) {
        val bundle = Bundle()
        bundle.putParcelable("folder", folder)
        bundle.putString("typeFiles", typeFiles)
        findNavController().navigate(
            R.id.action_media_self,
            bundle
        )
    }

//    private fun searchFiles(rootFile: File, typeList: ArrayList<String>): ArrayList<File> {
//        val out: ArrayList<File> = arrayListOf()
//        val list = rootFile.listFiles()
//        if (list != null && list.isNotEmpty()) {
//            for (file in list) {
//                if (file.isFile) {
//                    if (typeList.contains(file.extension.lowercase())) {
//                        out.add(file)
//                    }
//                }
//            }
//        }
//        return out
//    }

    fun getDisplayWidth(): Int {
        val outMetrics = DisplayMetrics()
        // val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = activity?.windowManager?.defaultDisplay
            display?.getMetrics(outMetrics)
        }

        //todo use adapterType as input and call this.resources.configuration.orientation
        // to calculate item size
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels)
    }

//    private suspend fun searchFiles3(
//        rootFile: File,
//        typeList: ArrayList<String>,
//        adapter: AdapterInterface
//    ) {
//        val list = rootFile.listFiles()
//        if (list != null && list.isNotEmpty()) {
//            for (file in list.reversed()) {
//                if (file.isFile) {
//                    if (typeList.contains(file.extension.lowercase())) {
//                        viewLifecycleOwner.lifecycleScope.launch {
//                            adapter.addItem(file)
//                        }
//                    }
//                }
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}