package com.example.recipesapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.recipesapp.R

class FridgeAdapter(
    private val context: Context,
    private val ingredients: MutableList<String>,
    private val onDelete: (String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = ingredients.size

    override fun getItem(position: Int): Any = ingredients[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.layout_fridge, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        else
        {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val ingredient = ingredients[position]

        viewHolder.ingredientTextView.text = ingredient
        viewHolder.deleteButton.setOnClickListener {
            onDelete(ingredient)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val ingredientTextView: TextView = view.findViewById(R.id.ingredientTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }
}
