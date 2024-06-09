package com.example.recipesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recipesapp.R
import com.example.recipesapp.activities.dataStore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MenuFragment:Fragment() {
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)
        setUpViews(rootView)
        return rootView
    }

    private fun setUpViews(rootView: View)
    {
        val daysOfWeekIds = mapOf(
            "M" to Pair(R.id.textViewM, R.id.editTextM),
            "Tu" to Pair(R.id.textViewTu, R.id.editTextTu),
            "W" to Pair(R.id.textViewW, R.id.editTextW),
            "T" to Pair(R.id.textViewT, R.id.editTextT),
            "F" to Pair(R.id.textViewF, R.id.editTextF),
            "Sa" to Pair(R.id.textViewSa, R.id.editTextSa),
            "S" to Pair(R.id.textViewS, R.id.editTextS)
        )

        for ((day, ids) in daysOfWeekIds)
        {
            val (textViewId, editTextId) = ids
            val textView = rootView.findViewById<TextView>(textViewId)
            val editText = rootView.findViewById<EditText>(editTextId)

            val textViewFlow = loadTextView(Firebase.auth.currentUser?.email.toString()+"textView$day")
            lifecycleScope.launch {
                textViewFlow.collect { recipe ->
                    if(recipe!="") {
                        textView.text = recipe
                    }
                }
            }

            textView.setOnClickListener {
                textView.visibility = View.GONE
                editText.visibility = View.VISIBLE
            }
        }
        saveButton = rootView.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            for ((day, ids) in daysOfWeekIds) {
                val (textViewId, editTextId) = ids
                val textView = rootView.findViewById<TextView>(textViewId)
                val editText = rootView.findViewById<EditText>(editTextId)

                if (editText.text.isNotEmpty())
                {
                    textView.text = editText.text
                    editText.visibility = View.GONE
                    textView.visibility = View.VISIBLE

                    lifecycleScope.launch {
                        saveTextView(
                            Firebase.auth.currentUser?.email.toString() + "textView$day",
                            textView.text.toString()
                        )
                    }
                }
            }
        }
    }

    private suspend fun saveTextView(textViewKey: String, content: String)
    {
        val day = stringPreferencesKey(textViewKey)
        requireContext().dataStore.edit { menu->
            menu[day] = content
        }
    }

    private fun loadTextView(textViewKey: String):Flow<String>
    {
        val day = stringPreferencesKey(textViewKey)
        val dayFlow: Flow<String> = requireContext().dataStore.data
            .map { preferences ->
                preferences[day] ?: ""
            }
        return dayFlow
    }

}