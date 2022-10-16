package com.example.droneapplicatio;

data class USER
    (
    val name: String,
    val imageUrl: String,
    val thumbImage: String,
    val uid: String,
    val city: String,
    val status:String,
    var number:String,
    )
{
    constructor() : this("", "", "", "", "offline","","")
    constructor(
        name: String,
        imageUrl: String,
        thumbImage: String,
        uid: String,
        number: String,
        city: String
    ):
            this(
                name,
                imageUrl,
                thumbImage, uid, city=city,status = "offline",  number)

}

