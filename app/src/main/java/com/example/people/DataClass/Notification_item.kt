package com.example.people.DataClass

class Notification_item{
    private var userid: String = ""
    private var text: String = ""
    private var postid: String = ""
    private var sign:String=""
    private var ispost=false


    constructor()

    constructor(userid:String,text:String,postid:String,sign:String,ispost:Boolean){
        this.userid=userid
        this.text=text
        this.postid=postid
        this.sign=sign
        this.ispost=ispost
    }


    fun getuserid():String{
        return userid
    }

    fun gettext():String{
        return text
    }

    fun getpostid():String{
        return postid
    }

    fun getispost():Boolean{
        return ispost
    }
    fun getSign():Boolean{
        return ispost
    }
}
