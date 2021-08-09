package com.wahkor.spotifyclone.utils

data class Resource<out T>(val status: Status,val data:T?,val message:String?){
companion object{
    fun <T> success(data:T?)= Resource(Status.Success,data,null)
    fun <T> error(message: String,data:T?)=Resource(Status.Error,data,message)
    fun <T> loading(data:T?) = Resource(Status.Loading,data,null)
}
}

enum class Status{
    Success,Error,Loading
}