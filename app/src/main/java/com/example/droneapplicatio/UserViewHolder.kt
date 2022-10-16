package com.example.droneapplicatio

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class UserViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val view: View
    fun setdetails(user: USER) {
        val countv = view.findViewById<TextView>(R.id.countTv)
        val titletv = view.findViewById<TextView>(R.id.titleTv)
        val subtitletv = view.findViewById<TextView>(R.id.subTitleTv)

        val userimageview = view.findViewById<ImageView>(R.id.userImgView)
        countv.isEnabled=false
        countv.visibility=View.GONE
        titletv.text=user.name
        subtitletv.text=user.status
        Picasso.get().load(user.thumbImage).placeholder(R.drawable.ic_baseline_person_24)
            .into(userimageview)
        itemView.setOnClickListener {
            val intent= Intent(itemView.context, ChatActivity::class.java)
            intent.putExtra("UID",user.uid)

            intent.putExtra("IMAGE_URL",user.imageUrl)
            intent.putExtra("NAME",user.name)
        
            intent.putExtra("STATUS",user.status)
            intent.putExtra("THUMBIMAGE",user.thumbImage)
            intent.putExtra("number",user.number)
            intent.putExtra("number",user.number)


            itemView.context.startActivity(intent)

        }


    }



    init {
        view = itemView
    }
}