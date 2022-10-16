package com.example.droneapplicatio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso;
import kotlinx.android.synthetic.main.fragment_accountfragment.*
import kotlinx.android.synthetic.main.fragment_accountfragment.view.*
import kotlinx.android.synthetic.main.signup_acitivity.*
import kotlinx.android.synthetic.main.signup_acitivity.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [accountfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class accountfragment : Fragment() {
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mAuth=FirebaseAuth.getInstance();
        val docRef = FirebaseFirestore.getInstance().collection("users").document(mAuth.uid!!)
        var imageUrl:String=""


        val parent=inflater.inflate(R.layout.fragment_accountfragment, container, false)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    imageUrl= document.data!!["imageUrl"] as String
                    Log.d("data is",imageUrl);
                    Picasso.get().load(imageUrl).into(parent.useraccountimage)
                } else {
                    Log.d("i am in accountfragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("i Am in accountfragment", "get failed with ", exception)
            }
//        val userimage= view!!.findViewById<ImageView>(R.id.useraccountimage)
Log.d("image url is",imageUrl)
       parent.blogpage.setOnClickListener {
           val i= Intent(context,Blog::class.java)
           startActivity(i)
       }
//        Picasso.get().load(imageUrl).into(parent.useraccountimage)
        parent.details.setOnClickListener {
            val i=Intent(context, Details::class.java)
            startActivity(i)
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
         * @return A new instance of fragment accountfragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            accountfragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}