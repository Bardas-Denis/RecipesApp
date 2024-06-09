package com.example.recipesapp.adapters

import android.content.ContentValues.TAG
import com.example.recipesapp.models.Recipe
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.models.Favorite
import com.example.recipesapp.R
import com.example.recipesapp.interfaces.RecyclerViewInterface
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class DataAdapter(
    private val context: Context,
    private val rowId: Int,
    private var recipes: Array<Recipe>,
    private val recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(rowId, parent, false)
        return ViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        val context = holder.itemView.context

        holder.name.text = recipe.name
        holder.price.text = context.getString(R.string.estimated_price,recipe.price.toString())
        holder.time.text = context.getString(R.string.estimated_time,recipe.time.toString())
        holder.description.text = recipe.description
        Picasso.get().load(recipe.image).into(holder.icon)

        holder.favButton.setOnClickListener {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val currentUser = auth.currentUser
            var email = ""

            holder.favButton.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                holder.favButton.isEnabled = true
            }, 3000)

            if(currentUser!=null)
            {
                email= currentUser.email.toString()
            }
            val fav = Favorite(id = "", recipeId = recipe.id, userEmail = email)

            db.collection("favorites")
                .whereEqualTo("recipeId",recipe.id)
                .whereEqualTo("userEmail",email)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty)
                    {
                        db.collection("favorites")
                            .add(fav)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(context,"Recipe added to favorites",Toast.LENGTH_SHORT).show()
                                fav.id = documentReference.id
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                    else
                    {
                        result.documents.forEach { document ->
                            db.collection("favorites")
                                .document(document.id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context,"Recipe deleted from favorites",Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun updateData(newData: Array<Recipe>) {
        recipes = newData

    }

    fun getData():Array<Recipe>
    {
        return recipes
    }

    inner class ViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameTextView)
        val icon: ImageView = itemView.findViewById(R.id.imageView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val time: TextView = itemView.findViewById(R.id.timeTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val favButton: ImageButton = itemView.findViewById(R.id.favoriteImageButton)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onClickItem(position)
                }
            }
        }
    }
}
