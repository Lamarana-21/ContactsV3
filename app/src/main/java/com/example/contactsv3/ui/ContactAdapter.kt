package com.example.contactsv3.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsv3.R
import com.example.contactsv3.model.Contact
import java.io.File

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPhoto: ImageView = view.findViewById(R.id.imgContactPhoto)
        val tvName: TextView = view.findViewById(R.id.tvContactName)
        val tvPhone: TextView = view.findViewById(R.id.tvContactPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewTest: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvName.text = "${contact.nom} ${contact.prenom}"
        holder.tvPhone.text = contact.telephone

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(contact.photoUri)
                if (uri.scheme == null) {
                    holder.imgPhoto.setImageURI(Uri.fromFile(File(contact.photoUri!!)))
                } else {
                    holder.imgPhoto.setImageURI(uri)
                }
            } catch (e: Exception) {
                holder.imgPhoto.setImageResource(R.mipmap.ic_launcher)
            }
        } else {
            holder.imgPhoto.setImageResource(R.mipmap.ic_launcher)
        }

        holder.itemView.setOnClickListener {
            onClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateData(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
