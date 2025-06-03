package com.pw.contactappassignment.data.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pw.contactappassignment.R
import com.pw.contactappassignment.data.model.Contact
import java.io.FileNotFoundException

class ContactAdapter(private val mContext: Context, private var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private val colorList = listOf(
        R.color.orange,
        R.color.green,
        R.color.blue,
        R.color.pink
    )

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textViewName)
        val tvContentSuffixName: TextView = itemView.findViewById(R.id.tvContentSuffixName)
        val image: ImageView = itemView.findViewById(R.id.imageViewContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.name.text = contact.name

        // Set background color tint
        val colorResId = colorList[position % colorList.size]
        val color = ContextCompat.getColor(holder.itemView.context, colorResId)
        holder.tvContentSuffixName.backgroundTintList = ColorStateList.valueOf(color)

        if (!contact.imagePath.isNullOrEmpty()) {
            holder.tvContentSuffixName.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            loadImageFromFilePath(contact.imagePath, holder.image)
        } else {
            holder.image.visibility = View.GONE
            holder.tvContentSuffixName.visibility = View.VISIBLE
            holder.tvContentSuffixName.text = contact.name?.takeIf { it.isNotBlank() }
                ?.substring(0, 1)?.uppercase() ?: "?"
        }
    }

    override fun getItemCount(): Int = contacts.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    private fun loadImageFromFilePath(uriString: String, imageView: ImageView) {
        try {
            val uri = Uri.parse(uriString)
            mContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.initial_circle)
                    Log.e("ContactAdapter", "Bitmap is null")
                }
            }
        } catch (e: FileNotFoundException) {
            imageView.setImageResource(R.drawable.initial_circle)
            Log.e("ContactAdapter", "Image file not found: ${e.message}")
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.initial_circle)
            Log.e("ContactAdapter", "Error loading image: ${e.message}")
        }
    }
}
