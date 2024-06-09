package com.example.recipesapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.recipesapp.R

class CostsAdapter(
    private val context: Context,
    private val ingredients: MutableMap<String, String>,
    private val onDelete: (String) -> Unit
) : BaseAdapter() {

    private val keys = ingredients.keys.toList()

    override fun getCount(): Int = ingredients.size

    override fun getItem(position: Int): Any = keys[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.layout_costs, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        else
        {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val key = keys[position]
        val value = ingredients[key]

        viewHolder.ingredientTextView.text = key
        viewHolder.priceTextView.text = value

        viewHolder.deleteButton.setOnClickListener {
            onDelete(key)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val ingredientTextView: TextView = view.findViewById(R.id.ingredientTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }
}