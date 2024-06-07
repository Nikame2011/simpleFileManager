package com.teamzero.phototest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.teamzero.phototest.databinding.FragmentExplorerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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



        binding.imageView.visibility = VISIBLE
        /*runBlocking {
            var l =  CoroutineScope(Dispatchers.Default).async {
                var counter: Long = 0
                for (i in 0..10000000000) {
                    counter++
                }
                return@async counter
            }.await()
            binding.textView.text = l.toString()
        }*/

        CoroutineScope(Dispatchers.Default).launch {
            var counter: Long = 0
            for (i in 0..2000000000) {
                counter++
            }
            viewLifecycleOwner.lifecycleScope.launch {
                binding.textView.text = counter.toString()
            }
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}