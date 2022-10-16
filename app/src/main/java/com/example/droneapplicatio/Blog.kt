package com.example.droneapplicatio

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.blog.*

import java.lang.Exception


class Blog: AppCompatActivity() {
    lateinit var madapter: FirestoreRecyclerAdapter<USER, RecyclerView.ViewHolder>
    val TAG="HELLO"
    lateinit var currentUser:USER
    val mauth=FirebaseAuth.getInstance()
    var query: Query = FirebaseFirestore.getInstance().collection("users")
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name",Query.Direction.DESCENDING).get( )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blog)

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,"online",currentUser.uid,currentUser.city,currentUser.number)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val options: FirestoreRecyclerOptions<USER> =
            FirestoreRecyclerOptions.Builder<USER>()
                .setQuery(query, USER::class.java)
                .build()

        madapter= object : FirestoreRecyclerAdapter<USER,RecyclerView.ViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                if (viewType==1){
                    return  EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.emptylayout,parent,false))
                }
                val view=LayoutInflater.from(parent.context).inflate(R.layout.listitem1,parent,false)
                return UserViewHolder(view)


            }



            override fun getItemViewType(position: Int): Int {
                val item = getItem(position).uid
                if (item==mauth.uid){
                    return  1
                }
                else {
                    return 2
                }
                return super.getItemViewType(position)
            }

            /**
             * @param model the model object containing the data that should be used to populate the view.
             * @see .onBindViewHolder
             */
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder,position: Int,model: USER){
                if (holder is UserViewHolder){
                    holder.setdetails(model)}
                else{
                    //Todo-Something
                }
            }


        }


        rcv1.apply {
            layoutManager = LinearLayoutManager(this@Blog)
            try {

                adapter=madapter
            }
            catch (e:Exception){
                Log.d(TAG, "onCreate:  e.localizedMessage.toString()")
                Toast.makeText(this@Blog, e.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
            }

        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
        return super.onSupportNavigateUp()
    }



    override fun onStart() {
        super.onStart()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,"online",currentUser.city,currentUser.uid,currentUser.number)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }
        madapter.startListening()
        madapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        if (madapter != null) {
            madapter.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.uid,currentUser.city,currentUser.number)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }

    }



    override fun onPause() {
        super.onPause()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(currentUser.name,currentUser.imageUrl,currentUser.thumbImage,currentUser.uid,currentUser.city,currentUser.number)

                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
                    Log.d(TAG, "now user is offline")
                }
            }
        Log.d(TAG, "onPause: activity pause")

    }









}
