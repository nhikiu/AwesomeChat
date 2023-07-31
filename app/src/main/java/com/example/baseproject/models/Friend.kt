package com.example.baseproject.models

data class Friend(val id: String, val name: String, val avatar: String?, var status: String){
    fun copy():Friend{
        return Friend(this.id,this.name,this.avatar,this.status)
    }
}

