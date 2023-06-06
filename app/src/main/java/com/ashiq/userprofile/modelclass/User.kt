package com.ashiq.userprofile.modelclass

data class User(
    val userId : String ?= null,
    val firstName : String ?= null,
    val secondName : String ?= null,
    val bio : String ?= null,
)