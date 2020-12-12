package org.thelemistix.kotme

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.net.SocketException

class HiddenSettingsFragment(val mainActivity: MainActivity) : Fragment(R.layout.hidden_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val serverAddress = view.findViewById<TextView>(R.id.serverAddress)
        val status = view.findViewById<TextView>(R.id.status)

        view.findViewById<View>(R.id.back).setOnClickListener {
            mainActivity.client.serverAddress = serverAddress.text.toString()
            mainActivity.showFull(mainActivity.mainMenu)
        }

        view.findViewById<View>(R.id.check).setOnClickListener {
            status.setTextColor(Color.WHITE)
            status.text = "Проверка..."

            val response = mainActivity.client.checkServerLink(serverAddress.text.toString())
            if (response != null) {
                status.setTextColor(if (response.status.isSuccess()) Color.GREEN else Color.RED)
                status.text = response.status.description
            } else {
                status.setTextColor(Color.RED)
                status.text = "Нет связи"
            }
        }
    }
}
