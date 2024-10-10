package com.nikame.sfmanager.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nikame.sfmanager.R
import com.nikame.sfmanager.adapters.ExplorerAdapter
import com.nikame.sfmanager.databinding.FragmentExplorerBinding
import kotlinx.coroutines.launch
import java.io.File

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Explorer : Fragment() {

    private var _binding: FragmentExplorerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExplorerBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            val directoryInfo: File =
                arguments?.getSerializable("folder") as File

            val size: Int = getDisplayWidth() / 3
            binding.rv.layoutManager =
                LinearLayoutManager(binding.rv.context, RecyclerView.VERTICAL, false)
            var list =  arrayListOf<File>()
            directoryInfo.listFiles()?.let { list.addAll(it) }

            binding.rv.adapter = ExplorerAdapter(requireContext(), size, list){
                val bundle = Bundle()
                bundle.putSerializable("folder", it)
                findNavController().navigate(
                    R.id.action_explorer_self,
                    bundle
                )
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO add code to show explorer and navigate in folders
        //binding.buttonFirst.setOnClickListener {
        //    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        // }


        // поиск файлов


        //var counter: Long = 0
        // viewLifecycleOwner.lifecycleScope.launch {
        /*CoroutineScope(Dispatchers.Default).launch {
            for (i in 0..10000000000) {
                counter++
            }
        }*/



//        binding.imageView.visibility = VISIBLE
//        /*runBlocking {
//            var l =  CoroutineScope(Dispatchers.Default).async {
//                var counter: Long = 0
//                for (i in 0..10000000000) {
//                    counter++
//                }
//                return@async counter
//            }.await()
//            binding.textView.text = l.toString()
//        }*/
//
//        CoroutineScope(Dispatchers.Default).launch {
//            var counter: Long = 0
//            for (i in 0..2000000000) {
//                counter++
//            }
//            viewLifecycleOwner.lifecycleScope.launch {
//                binding.textView.text = counter.toString()
//            }
//        }

        /*  viewLifecycleOwner.lifecycleScope.launch {
              var l =  CoroutineScope(Dispatchers.Default).async {
                  var counter: Long = 0
                  for (i in 0..2000000000) {
                      counter++
                  }
                  return@async counter
              }.await()
              binding.textView.text = l.toString()
          }*/
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}