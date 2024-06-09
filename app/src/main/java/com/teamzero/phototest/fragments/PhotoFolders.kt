package com.teamzero.phototest.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamzero.phototest.R
import com.teamzero.phototest.adapters.AudioAdapter
import com.teamzero.phototest.adapters.FilesImageAdapter
import com.teamzero.phototest.adapters.ImageAdapter
import com.teamzero.phototest.databinding.FragmentPhotoBinding
import com.teamzero.phototest.helpers.DirInfo
import com.teamzero.phototest.helpers.FileIndexer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
        val typeFiles: Int = arguments?.getInt("typeFiles", 0)!!

        if (arguments?.containsKey("folder") == false) {
            //todo find difference with viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default)

            //todo replace  
            // CoroutineScope(Dispatchers.Default).launch {launch{1} launch{2} await[1,2]  viewLifecycleOwner.lifecycleScope.launch{3}}
            // to viewLifecycleOwner.lifecycleScope.launch {launch(Dispatchers.Default){1} launch(Dispatchers.Default){2} await[1,2] 3 }}
            CoroutineScope(Dispatchers.Default).launch {
                launch {

                    val folders = FileIndexer.indexFiles[typeFiles]
                    val outMetrics = DisplayMetrics()
                    // val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        val display = activity?.display
                        display?.getRealMetrics(outMetrics)
                    } else {
                        val display = activity?.windowManager?.defaultDisplay
                        display?.getMetrics(outMetrics)
                    }

                    val size: Int = outMetrics.widthPixels / 3

                    viewLifecycleOwner.lifecycleScope.launch() {
                        //Toast.makeText(context, "allFinded", Toast.LENGTH_SHORT).show();

                        val rvImages: RecyclerView = binding.rvImages
                        rvImages.layoutManager = GridLayoutManager(binding.rvImages.context, 3)

                        rvImages.adapter = FilesImageAdapter(requireContext(), size, folders) {
                            val bundle = Bundle()
                            bundle.putSerializable("folder", it)
                            bundle.putInt("typeFiles", typeFiles)
                            findNavController().navigate(
                                R.id.action_photo_self2,
                                bundle
                            )
                        }
                    }
                }
            }
        } else {

            CoroutineScope(Dispatchers.Default).launch {
                launch {
                    val directoryInfo: DirInfo =
                        arguments?.getSerializable("folder") as DirInfo

                    //todo add deffered to search files

                    //todo init adapter with empty list before run searchFiles. when file be found in fun do adapter.addItem(file). in addItem fun make list.add(0,file) to revert list files
                    val folders: ArrayList<File> = searchFiles(
                        directoryInfo.rootFolder,
                        FileIndexer.types!![typeFiles]
                    ).reversed() as ArrayList<File>

                    val outMetrics = DisplayMetrics()

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        val display = activity?.display
                        display?.getRealMetrics(outMetrics)
                    } else {
                        val display = activity?.windowManager?.defaultDisplay
                        display?.getMetrics(outMetrics)
                    }

                    val size: Int = outMetrics.widthPixels / 3

                    viewLifecycleOwner.lifecycleScope.launch {
                        val rvImages: RecyclerView = binding.rvImages
                        rvImages.layoutManager = GridLayoutManager(binding.rvImages.context, 3)
                        if (typeFiles != 1) {
                            rvImages.adapter = AudioAdapter(requireContext(), size, folders)
                        } else {
                            rvImages.adapter = ImageAdapter(requireContext(), size, folders)
                        }
                    }
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

    private suspend fun searchFiles2(rootFile: File, typeList: ArrayList<String>): ArrayList<File> {
        val out: ArrayList<File> = arrayListOf()
        val list = rootFile.listFiles()
        val defList = arrayListOf<Deferred<File?>>()
        if (list != null && list.isNotEmpty()) {
            for (file in list) {
                defList.add(CoroutineScope(Dispatchers.Default).async {
                    if (file.isFile && typeList.contains(file.extension.lowercase()))
                        return@async file
                    return@async null
                })
            }
            for (def in defList) {
                def.await()?.let { out.add(it) }
            }
        }
        return out
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}