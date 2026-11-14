package com.example.contactsv3.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.contactsv3.R
import com.example.contactsv3.viewmodel.ContactViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class CarteContactFragment : Fragment(R.layout.fragment_carte_contact) {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuration obligatoire pour osmdroid
        Configuration.getInstance().userAgentValue = requireContext().packageName

        val contactId = arguments?.getLong("contact_id", -1L) ?: -1L
        val contact = viewModel.getContactById(contactId)
        val tvMapMessage = view.findViewById<TextView>(R.id.tvMapMessage)
        mapView = view.findViewById(R.id.map)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        if (contact != null && contact.latitude != null && contact.longitude != null) {
            val lat = contact.latitude!!
            val lng = contact.longitude!!
            if (lat in -90.0..90.0 && lng in -180.0..180.0) {
                tvMapMessage.visibility = View.GONE
                mapView.visibility = View.VISIBLE

                val startPoint = GeoPoint(lat, lng)
                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(startPoint)

                val marker = Marker(mapView)
                marker.position = startPoint
                marker.title = "${contact.prenom} ${contact.nom}"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)
                mapView.invalidate()
            } else {
                showInvalidCoordinatesMessage(tvMapMessage)
            }
        } else {
            showInvalidCoordinatesMessage(tvMapMessage)
        }
    }

    private fun showInvalidCoordinatesMessage(tvMessage: TextView) {
        tvMessage.text = getString(R.string.map_error_message)
        tvMessage.visibility = View.VISIBLE
        mapView.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
