package com.example.people.Notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    companion object{
        private var retrofit:Retrofit?=null

        fun getClint(uri:String?) :Retrofit? {
            if (retrofit==null){
                retrofit=Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }
}