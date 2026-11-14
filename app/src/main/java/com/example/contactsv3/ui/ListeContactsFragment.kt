package com.example.contactsv3.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsv3.R
import com.example.contactsv3.viewmodel.ContactViewModel

class ListeContactsFragment : Fragment(R.layout.fragment_liste_contacts) {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val initialList = viewModel.getFilteredContacts("")
        adapter = ContactAdapter(initialList) { contact ->
            val detailFragment = DetailContactFragment().apply {
                arguments = Bundle().apply {
                    putLong("contact_id", contact.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        updateListAndEmptyState(initialList, tvEmptyState)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.setSearchQuery(query)
                val filtered = viewModel.getFilteredContacts(query)
                adapter.updateData(filtered)
                updateListAndEmptyState(filtered, tvEmptyState)
            }
        })

        viewModel.contacts.observe(viewLifecycleOwner) {
            val query = etSearch.text?.toString() ?: ""
            val filtered = viewModel.getFilteredContacts(query)
            adapter.updateData(filtered)
            updateListAndEmptyState(filtered, tvEmptyState)
        }
    }

    private fun updateListAndEmptyState(list: List<*>, tvEmptyState: TextView) {
        if (list.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
        } else {
            tvEmptyState.visibility = View.GONE
        }
    }
}
