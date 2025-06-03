package com.pw.contactappassignment.data.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pw.contactappassignment.ui.AddContactActivity
import com.pw.contactappassignment.R
import com.pw.contactappassignment.data.model.User
import com.pw.contactappassignment.ui.EditContactActivity

class SyncContactAdapter(
    private val context: Context, private val list: ArrayList<User>
) : RecyclerView.Adapter<SyncContactAdapter.MenuListViewHolder>() {

    private val onItemClicked: OnItemClicked = context as OnItemClicked
    private val colorList = listOf(
        R.color.orange_add, R.color.green_add, R.color.blue_add, R.color.yellow
    )

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MenuListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return MenuListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MenuListViewHolder, position: Int
    ) {
        val model = list[position]

        val colorResId = colorList[position % colorList.size]
        val color = ContextCompat.getColor(context, colorResId)
        holder.contactCard.backgroundTintList = ColorStateList.valueOf(color)

        holder.name.text = model.fullName
        holder.role.text = model.course
        holder.phone.text = model.phone
        holder.email.text = model.email

        holder.editBtn.setOnClickListener {
            onItemClicked.onItemClick(position, model)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // Corrected method: return list of User objects
    fun getContacts(): List<User> {
        return list
    }

    inner class MenuListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val role: TextView = itemView.findViewById(R.id.role)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val email: TextView = itemView.findViewById(R.id.email)
        val contactCard: CardView = itemView.findViewById(R.id.contact_card)
        val editBtn: ImageView = itemView.findViewById(R.id.edit_icon)
    }
}

interface OnItemClicked {
    fun onItemClick(position: Int, model: User)
}