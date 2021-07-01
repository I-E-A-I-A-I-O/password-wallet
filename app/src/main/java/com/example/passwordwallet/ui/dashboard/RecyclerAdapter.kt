package com.example.passwordwallet.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordwallet.R
import com.example.passwordwallet.room.entities.Passwords

class RecyclerAdapter(
    private val items: List<Passwords>,
    private val showBottomSheet: (item: Passwords) -> Unit
): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val passwordDescription: TextView = view.findViewById(R.id.passwordDescription)
        val passwordText: TextView = view.findViewById(R.id.password)
        val menuButton: ImageButton = view.findViewById(R.id.passwordMoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_user_password, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.passwordDescription.text = items[position].description
        holder.passwordText.text = items[position].password
        holder.menuButton.setOnClickListener {
            showBottomSheet(items[position])
        }
    }

    override fun getItemCount(): Int = items.count()
}