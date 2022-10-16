package com.example.droneapplicatio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.R
import android.util.Log
import android.view.View.OnFocusChangeListener

import android.widget.ArrayAdapter

import android.widget.Spinner
import kotlinx.android.synthetic.main.details.*
import kotlinx.android.synthetic.main.details.view.*
import kotlinx.android.synthetic.main.fragment_sowing.view.*
import android.widget.AdapterView
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [sowing.newInstance] factory method to
 * create an instance of this fragment.
 */
class sowing : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val parent =
            inflater.inflate(com.example.droneapplicatio.R.layout.fragment_sowing, container, false)
        var depthview=parent.optdepth
        var distview=parent.optdist

        val items = arrayOf("Beans", "Maize", "Wheat", "Barley", "Rice")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        parent.spinner2.adapter = adapter
        parent.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        Log.d(TAG, "onItemSelected: "+view)
//
                            depthview.setText("45.72")
                            distview.setText("3")
//                        }

                    }

                    1 -> {

                        depthview.setText("75")
                        distview.setText("10")
                    }

                    2 -> {

                        depthview.setText("22.5")
                        distview.setText("5")
                    }

                    3 -> {

                        depthview.setText("20")
                        distview.setText("5")
                    }

                    4 -> {

                        depthview.setText("22.5")
                        distview.setText("3")
                    }




                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                // sometimes you need nothing here
            }
        }
        return parent
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment sowing.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            sowing().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}