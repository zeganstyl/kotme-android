package org.thelemistix.kotme

import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.markusressel.kodehighlighter.core.util.SpannableHighlighter
import de.markusressel.kodehighlighter.language.kotlin.KotlinRuleBook
import de.markusressel.kodehighlighter.language.kotlin.colorscheme.DarkBackgroundColorScheme
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.json.JSONObject

class ExerciseFragment(val mainActivity: MainActivity) : Fragment(R.layout.exercise) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //        val am = AccountManager.get(this) // "this" references the current Context
//        val accounts = am.getAccountsByType("org.thelemistix.kotme")
//        am.getPassword(accounts[0])
//
//
//        val account = Account("user", "org.thelemistix.kotme")
//        am.addAccountExplicitly(account, "password", null)


        val ruleBook = KotlinRuleBook()

        val highlighter = SpannableHighlighter(ruleBook, DarkBackgroundColorScheme())

        val code = view.findViewById<TextView>(R.id.code)
        code.text = "fun main() {\nprintln(\"Привет Котлин!\")\n}"

        val spannable = SpannableString.valueOf(code.text.toString())

        runBlocking {
            highlighter.highlight(spannable)
            code.text = spannable
        }

        view.findViewById<View>(R.id.description).setOnClickListener {
            mainActivity.exerciseDescription.show()
        }

        view.findViewById<View>(R.id.check).setOnClickListener {
            val client = HttpClient(Android) {
                followRedirects = false
                install(Auth) {
                    basic {
                        username = "root"
                        password = "root"
                    }
                }
                install(HttpCookies) {
                    // Will keep an in-memory map with all the cookies from previous requests.
                    storage = AcceptAllCookiesStorage()

                    // Will ignore Set-Cookie and will send the specified cookies.
                    //storage = ConstantCookiesStorage(Cookie("mycookie1", "value"), Cookie("mycookie2", "value"))
                }
                engine {
                    connectTimeout = 100_000
                    socketTimeout = 100_000
                }
                install(HttpRedirect) {
                    checkHttpMethod = false
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
                install(DefaultRequest) {
                    headers.append("Accept","application/json")
                    headers.append("Authorization","Bearer token")
                }
            }

            runBlocking {
                client.submitForm<String>(
                    "http://192.168.0.2/kotme/www/index.php/site/login",
                    parametersOf(
                        Pair("LoginForm[login]", listOf("root")),
                        Pair("LoginForm[password]", listOf("root"))
                    )
                )

                val response = client.post<String>("http://192.168.0.2/kotme/www/index.php/begin/check") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    body = Parameters.build {
                        append("exercise", "1")
                        append("code", code.text.toString())
                    }.formUrlEncode()
                }

                if (response == "Отличное начало. Продолжай в том же духе!") {
                    mainActivity.congratulations.show()
                }

                println(response)
            }
        }
    }
}
