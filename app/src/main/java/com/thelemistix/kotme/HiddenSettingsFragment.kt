package com.thelemistix.kotme

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.ktor.http.*

class HiddenSettingsFragment() : FragmentBase(R.layout.hidden_settings, true) {
    private var addressView: TextView? = null
    private var protocolView: TextView? = null
    private var urlView: TextView? = null

    var address: String = "192.168.0.2"
        set(value) {
            field = value
            addressView?.text = value
        }

    var protocol: String = "http"
        set(value) {
            field = value
            protocolView?.text = value
        }

    var url: String = "/kotme/www/index.php"
        set(value) {
            field = value
            urlView?.text = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val protocol = view.findViewById<TextView>(R.id.protocol).also { protocolView = it }
        val address = view.findViewById<TextView>(R.id.address).also { addressView = it }
        val url = view.findViewById<TextView>(R.id.url).also { urlView = it }
        val status = view.findViewById<TextView>(R.id.status)

        protocol.text = this.protocol
        address.text = this.address
        url.text = this.url

        view.findViewById<View>(R.id.back).setOnClickListener {
            mainActivity.client.protocol = protocol.text.toString()
            mainActivity.client.address = address.text.toString()
            mainActivity.client.url = url.text.toString()
            mainActivity.client.saveServerConfig()

            mainActivity.mainMenu.show()
        }

        view.findViewById<View>(R.id.check).setOnClickListener {
            status.setTextColor(Color.WHITE)
            status.text = "Проверка..."

            val response = mainActivity.client.checkServerLink(address.text.toString(), protocol.text.toString(), url.text.toString())
            if (response != null) {
                status.setTextColor(if (response.status.isSuccess()) Color.GREEN else Color.RED)
                status.text = "${response.status.value} - ${response.status.description}"
            } else {
                status.setTextColor(Color.RED)
                status.text = "Нет связи"
            }
        }
    }
}
