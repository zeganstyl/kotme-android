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
        val client = HttpClient(Android) {
            engine {
                connectTimeout = 4_000
                socketTimeout = 4_000
            }
        }

        view.findViewById<View>(R.id.back).setOnClickListener {
            client.close()
            mainActivity.showFull(mainActivity.mainMenu)
        }

        val serverAddress = view.findViewById<TextView>(R.id.serverAddress)
        val status = view.findViewById<TextView>(R.id.status)

        view.findViewById<View>(R.id.check).setOnClickListener {
            status.setTextColor(Color.WHITE)
            status.text = "Проверка..."
            runBlocking {
                try {
                    val response = client.get<HttpResponse>("http://${serverAddress.text}/kotme/www/index.php")
                    status.setTextColor(if (response.status.isSuccess()) Color.GREEN else Color.RED)
                    status.text = response.status.description
                } catch (ex: SocketException) {
                    status.setTextColor(Color.RED)
                    status.text = "Нет связи"
                }
            }
        }
    }
}
