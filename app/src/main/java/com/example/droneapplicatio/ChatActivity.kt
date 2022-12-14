package com.example.droneapplicatio

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


const val USER_ID = "userId"
const val USER_THUMB_IMAGE = "thumbImage"
const val USER_NAME = "userName"

class ChatActivity : AppCompatActivity() {

    lateinit var friendId: String
    lateinit var name: String
    lateinit var myself:USER
    lateinit var image: String
    var mCurrentUid: String =FirebaseAuth.getInstance().uid!!
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: USER
    lateinit var chatAdapter: ChatAdapter
    lateinit var number:String
    val TAG="WELCOME"

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val mutableItems: MutableList<ChatEvent> = mutableListOf()
    private val mLinearLayout: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(name=currentUser.name, imageUrl = currentUser.imageUrl, thumbImage = currentUser.thumbImage, status = "online",uid=currentUser.uid,number=currentUser.number,city=currentUser.city)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
//                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }
        friendId= intent.getStringExtra("UID").toString()
        gobackasap.setOnClickListener {
            finish()
        }
        var actionBar = getSupportActionBar()

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        number=intent.getStringExtra("number").toString()
        name=intent.getStringExtra("NAME").toString()
        image=intent.getStringExtra("THUMBIMAGE").toString()
        updateReadCount()
        Log.d(TAG, "onCreate: "+number)
        FirebaseFirestore.getInstance().collection("users").document(friendId).addSnapshotListener { value, error ->

            val p= value!!.toObject(USER::class.java)
            if (p!!.status=="Typing..."){
                Log.d(TAG, "user is offline now")
                userstatus.visibility=View.VISIBLE
                userstatus.text=p!!.status

            }
            else if ((p!!.status)=="offline"){
                Log.d(TAG, "user is offline now")
                userstatus.visibility=View.GONE
            }
            else{
                Log.d(TAG, "user is now"+p!!.status)
                userstatus.visibility=View.VISIBLE
                userstatus.setTextColor(resources.getColor(R.color.white))
                userstatus.text=p.status

            }


        }



        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
            if (mutableItems.size - 1>0){
                msgRv.smoothScrollToPosition(mutableItems.size - 1)}
        }

        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
            }

        chatAdapter = ChatAdapter(mutableItems, mCurrentUid)
        callk.setOnClickListener {

            if ((checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)) {
                val permission = arrayOf(android.Manifest.permission.CALL_PHONE)

                requestPermissions(permission, 101)

            }

            else {
                FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
                    .addOnSuccessListener {
                        currentUser = it.toObject(USER::class.java)!!
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:" + number)
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                        val formatted = current.format(formatter)
                        val im= UUID.randomUUID().toString()
                 }

            }
        }

        msgRv.apply {
            layoutManager = mLinearLayout
            adapter = chatAdapter
        }

        nameTv.text = name
        Picasso.get().load(image).error(R.drawable.circle).placeholder(R.drawable.ic_baseline_person_24).
        into(userImgView)

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }



        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

        listenMessages() { msg, update ->
            if (update) {
                updateMessage(msg)
            } else {
                addMessage(msg)
            }
        }

        chatAdapter.highFiveClick = { id, status ->
            updateHighFive(id, status)
        }
        updateReadCount()
    }




    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId).child("count").setValue(0)
    }

    private fun updateHighFive(id: String, status: Boolean) {
        getMessages(friendId).child(id).updateChildren(mapOf("liked" to status))
    }

    private fun addMessage(event: Message) {
        val eventBefore = mutableItems.lastOrNull()

        // Add date header if it's a different day
        if ((eventBefore != null
                    && !eventBefore.sentAt.isSameDayAs(event.sentAt))
            || eventBefore == null
        ) {
            mutableItems.add(
                DateHeader(
                    event.sentAt, this
                )
            )
        }
        mutableItems.add(event)

        chatAdapter.notifyItemInserted(mutableItems.size)
        msgRv.smoothScrollToPosition(mutableItems.size + 1)
    }

    private fun updateMessage(msg: Message) {
        val position = mutableItems.indexOfFirst {
            when (it) {
                is Message -> it.msgId == msg.msgId
                else -> false
            }
        }
        mutableItems[position] = msg

        chatAdapter.notifyItemChanged(position)
    }

    private fun listenMessages(newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(data: DataSnapshot, p1: String?) {
                    val msg = data.getValue(Message::class.java)!!
                    newMsg(msg, true)
                }

                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    val msg = data.getValue(Message::class.java)!!
                    newMsg(msg, false)
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })

    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendId).push().key
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, mCurrentUid, id,number)
        getMessages(friendId).child(id).setValue(msgMap)
        updateLastMessage(msgMap, mCurrentUid)
    }

    private fun updateLastMessage(message: Message, mCurrentUid: String) {
        val inboxMap = Inbox(
            message.msg,
            friendId,
            name,
            image,
            message.sentAt,
            0,
            number
        )

        getInbox(mCurrentUid, friendId).setValue(inboxMap)

        getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(Inbox::class.java)
                inboxMap.apply {
                    from = message.senderId
                    name = currentUser.name
                    image = currentUser.thumbImage
                    count = 1
                }
                if (value?.from == message.senderId) {
                    inboxMap.count = value.count + 1
                }
                getInbox(friendId, mCurrentUid).setValue(inboxMap)
            }

        })
    }


    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("chats/$toUser/$fromUser")


    private fun getId(friendId: String): String {
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }


    private fun getcall(toUser: String, fromUser: String) =
        db.reference.child("calls/$toUser/$fromUser")

    override fun onResume() {

        super.onResume()
        updateReadCount()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(name=currentUser.name, imageUrl = currentUser.imageUrl, thumbImage = currentUser.thumbImage, status = "online",uid=currentUser.uid,number=currentUser.number,city=currentUser.city)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
//                    Log.d(TAG, "onCreate: now user is online")
                }.addOnFailureListener {
                    Log.d(TAG, "onCreate: "+it.localizedMessage)
                }
            }
        rootView.viewTreeObserver
            .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }

    override fun onStart() {
        super.onStart()
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(USER::class.java)!!
                val a=USER(name=currentUser.name, imageUrl = currentUser.imageUrl, thumbImage = currentUser.thumbImage, status = "online",uid=currentUser.uid,number=currentUser.number,city=currentUser.city)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
//                    Log.d(TAG, "onCreate: now user is online")
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
                val a=USER(name=currentUser.name, imageUrl = currentUser.imageUrl, thumbImage = currentUser.thumbImage, status = "offline",uid=currentUser.uid,number=currentUser.number,city=currentUser.city)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().uid!!).set(a).addOnSuccessListener {
//                    Log.d(TAG, "now user is offline")
                }
            }
        Log.d(TAG, "onPause: activity pause")

    }



    companion object {

        fun createChatActivity(context: Context, id: String, name: String, image: String,number:String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("UID", id)
            intent.putExtra("NAME", name)
            intent.putExtra("THUMBIMAGE", image)
            intent.putExtra("number",number)

            return intent
        }
    }
}