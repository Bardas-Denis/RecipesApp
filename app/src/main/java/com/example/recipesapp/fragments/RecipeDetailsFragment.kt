package com.example.recipesapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.recipesapp.R
import com.squareup.picasso.Picasso

class RecipeDetailsFragment: Fragment() {
    private lateinit var ingredientsListView: ListView
    private lateinit var instructionsListView: ListView
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_recipe_details, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
    }

    private fun setUpViews(view:View)
    {
        val args = RecipeDetailsFragmentArgs.fromBundle(requireArguments())
        val image = args.image
        val ingredients = args.ingredients
        val instructions = args.instructions

        ingredientsListView = view.findViewById(R.id.ingredientsListView)
        instructionsListView=view.findViewById(R.id.instructionsListView)
        imageView = view.findViewById(R.id.imageView2)

        val ingredientsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ingredients)
        val instructionsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, instructions)
        ingredientsListView.adapter = ingredientsAdapter
        instructionsListView.adapter = instructionsAdapter

        Picasso.get().load(image).into(imageView)
    }

}