package com.nikame.sfmanager.fragments

import android.os.Bundle
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
import com.nikame.sfmanager.adapters.AdapterInterface
import com.nikame.sfmanager.adapters.AudioAdapter
import com.nikame.sfmanager.adapters.FilesAudioAdapter
import com.nikame.sfmanager.adapters.FilesImageAdapter
import com.nikame.sfmanager.adapters.ImageAdapter
import com.nikame.sfmanager.databinding.FragmentPhotoBinding
import com.nikame.sfmanager.helpers.DirInfo
import com.nikame.sfmanager.helpers.FileIndexer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PhotoFolders : Fragment() {

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

        if (arguments?.containsKey("folder") == false) {
            //todo find difference with viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default)

            //todo replace  
            // CoroutineScope(Dispatchers.Default).launch {launch{1} launch{2} await[1,2]  viewLifecycleOwner.lifecycleScope.launch{3}}
            // to viewLifecycleOwner.lifecycleScope.launch {launch(Dispatchers.Default){1} launch(Dispatchers.Default){2} await[1,2] 3 }}
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {

                val folders =
                    FileIndexer.getIndexes(requireContext().applicationContext,typeFiles)/*[typeFiles]*/?.getDirectoriies()//FileIndexer.indexFiles[typeFiles]

                val size: Int = getDisplayWidth() / 3

                launch(Dispatchers.Main) {
                    //Toast.makeText(context, "allFinded", Toast.LENGTH_SHORT).show();

                    if (typeFiles.equals("Audio")) {
                        binding.rvImages.layoutManager =
                            LinearLayoutManager(binding.rvImages.context, RecyclerView.VERTICAL, false)

                        binding.rvImages.adapter = FilesAudioAdapter(requireContext(), size/2,
                            folders!!
                        ) {
                            val bundle = Bundle()
                            bundle.putSerializable("folder", it)
                            bundle.putString("typeFiles", typeFiles)
                            findNavController().navigate(
                                R.id.action_photo_self2,
                                bundle
                            )
                        }
                    } else {
                        binding.rvImages.layoutManager = GridLayoutManager(binding.rvImages.context, 3)

                        binding.rvImages.adapter = FilesImageAdapter(requireContext(), size,
                            folders!!
                        ) {
                            val bundle = Bundle()
                            bundle.putSerializable("folder", it)
                            bundle.putString("typeFiles", typeFiles)
                            findNavController().navigate(
                                R.id.action_photo_self2,
                                bundle
                            )
                        }
                    }
                }
            }
        } else {

//            CoroutineScope(Dispatchers.Default).launch {
//                launch {
//                    val directoryInfo: DirInfo =
//                        arguments?.getSerializable("folder") as DirInfo
//
//                    //todo add deffered to search files
//
//                    val folders: ArrayList<File> = searchFiles(
//                        directoryInfo.rootFolder,
//                        FileIndexer.types!![typeFiles]
//                    ).reversed() as ArrayList<File>
//
//                    /* Code to test other methods for searching
//                    val sz=30
//                    val dates = Array<Long>(sz){0}
//                    val dates2 = Array<Long>(sz){0}
//
//                    for (i in 0..sz-1){
//                        val strt = Date().time
//                        val folders: ArrayList<File> = searchFiles(
//                            directoryInfo.rootFolder,
//                            FileIndexer.types!![typeFiles]
//                        ).reversed() as ArrayList<File>
//                        val fin = Date().time
//
//                        val strt2 = Date().time
//                        val folders2: ArrayList<File> = searchFiles2(
//                            directoryInfo.rootFolder,
//                            FileIndexer.types!![typeFiles]
//                        ).reversed() as ArrayList<File>
//                        val fin2 = Date().time
//
//                        dates[i]=fin-strt
//                        dates2[i]=fin2-strt2
//
//                        viewLifecycleOwner.lifecycleScope.launch {
//                            binding.textView1.text= "old max: ${dates.max()} inter: ${dates.sum()/(i+1)} min: ${dates.min()}"
//                            binding.textView2.text= "new max: ${dates2.max()} inter: ${dates2.sum()/(i+1)} min: ${dates2.min()}"
//                        }
//                    }
//                    */
//
//                    val outMetrics = DisplayMetrics()
//
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                        val display = activity?.display
//                        display?.getRealMetrics(outMetrics)
//                    } else {
//                        val display = activity?.windowManager?.defaultDisplay
//                        display?.getMetrics(outMetrics)
//                    }
//
//                    val size: Int = outMetrics.widthPixels / 3
//
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        val rvImages: RecyclerView = binding.rvImages
//                        rvImages.layoutManager = GridLayoutManager(binding.rvImages.context, 3)
//                        if (typeFiles != 1) {
//                            rvImages.adapter = AudioAdapter(requireContext(), size, folders)
//                        } else {
//                            rvImages.adapter = ImageAdapter(requireContext(), size, folders)
//                        }
//                    }
//                }

            viewLifecycleOwner.lifecycleScope.launch {

                if (typeFiles.equals("Audio")) {
                    val size: Int = getDisplayWidth() / 6
                    binding.rvImages.layoutManager =
                        LinearLayoutManager(binding.rvImages.context, RecyclerView.VERTICAL, false)
                    binding.rvImages.adapter = AudioAdapter(requireContext(), size, ArrayList())
                } else {
                    val countItem=4
                    val size: Int = getDisplayWidth() / countItem
                    binding.rvImages.layoutManager = GridLayoutManager(binding.rvImages.context, countItem)
                    binding.rvImages.adapter = ImageAdapter(requireContext(), size, ArrayList())
                }

                launch(Dispatchers.Default) {
                    val directoryInfo: DirInfo =
                        arguments?.getSerializable("folder") as DirInfo

                    //if(rvImages.adapter!=null && rvImages.adapter is AdapterInterface )
//                    searchFiles3(
//                        directoryInfo.rootFolder, FileIndexer.types!![typeFiles],
//                        binding.rvImages.adapter as AdapterInterface
//                    )
                    // )
                }
            }
        }
        return binding.root
    }

    private fun searchFiles(rootFile: File, typeList: ArrayList<String>): ArrayList<File> {
        val out: ArrayList<File> = arrayListOf()
        val list = rootFile.listFiles()
        if (list != null && list.isNotEmpty()) {
            for (file in list) {
                if (file.isFile) {
                    if (typeList.contains(file.extension.lowercase())) {
                        out.add(file)
                    }
                }
            }
        }
        return out
    }

    fun getDisplayWidth():Int{
        val outMetrics = DisplayMetrics()
        // val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = activity?.windowManager?.defaultDisplay
            display?.getMetrics(outMetrics)
        }

        return Math.min(outMetrics.widthPixels,outMetrics.heightPixels)
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