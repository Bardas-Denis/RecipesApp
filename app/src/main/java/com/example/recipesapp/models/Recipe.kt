package com.example.recipesapp.models

import java.io.Serializable

data class Recipe(
    var id: String,var name: String, var time: Int, var price: Double,
    var description: String, var ingredients: MutableList<String>,
    var instructions: MutableList<String>,var origin: String,var diet: String,var allergens:MutableList<String>,var difficulty:String,var type: String, var image: String) : Serializable {


    constructor() : this("","",0,0.0,"", mutableListOf(), mutableListOf(), "", "", mutableListOf(),"","","")


    override fun toString(): String {
        return "(name='$name', time=$time, price=$price,description=$description, ingredients=$ingredients, instructions=$instructions, image='$image')"
    }
}
