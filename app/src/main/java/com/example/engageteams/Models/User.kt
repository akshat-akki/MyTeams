package com.example.engageteams.Models

//Model for User that takes displayName, UID,Image Url

data class User(val email:String="",
                val uid: String = "",
                val phone: String="",
                val displayName: String = "",
                val imageUrl: String = "")