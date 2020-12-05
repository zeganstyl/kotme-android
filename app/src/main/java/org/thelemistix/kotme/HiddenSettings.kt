package org.thelemistix.kotme

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.net.SocketException

class HiddenSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_settings)

        supportActionBar?.hide()

        val client = HttpClient(Android) {
            engine {
                connectTimeout = 4_000
                socketTimeout = 4_000
            }
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            client.close()
            finish()
        }

        val serverAddress = findViewById<TextView>(R.id.serverAddress)
        val status = findViewById<TextView>(R.id.status)

        findViewById<Button>(R.id.check).setOnClickListener {
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
