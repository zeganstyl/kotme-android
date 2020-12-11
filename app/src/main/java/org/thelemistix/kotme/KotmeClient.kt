package org.thelemistix.kotme

import android.accounts.Account
import android.accounts.AccountManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class KotmeClient(val mainActivity: MainActivity) {
    var serverAddress: String = "192.168.0.2"

    val cookies = AcceptAllCookiesStorage()

    val client = HttpClient(Android) {
        followRedirects = false
        install(Auth) {
        }
        install(HttpCookies) {
            // Will keep an in-memory map with all the cookies from previous requests.
            storage = cookies

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
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }
        install(DefaultRequest) {
            headers.append("Accept","application/json")
            headers.append("Authorization","Bearer token")
        }
    }

    /** Sign in with saved credentials
     * @return true if account exists and can login, otherwise false */
    fun signIn(): Boolean {
        val am = AccountManager.get(mainActivity) // "this" references the current Context
        val accounts = am.getAccountsByType(Origin)

        if (accounts.isNotEmpty()) {
            val acc = accounts.last()
            return signIn(acc.name, am.getPassword(acc), false)
        }

        return false
    }

    fun setAccount(login: String, password: String) {
        val am = AccountManager.get(mainActivity) // "this" references the current Context
        val accounts = am.getAccountsByType(Origin)

        if (accounts.firstOrNull { it.name == login } == null) {
            val account = Account(login, Origin)
            am.addAccountExplicitly(account, password, null)
        }
    }

    /** Sign in on server
     * @return true if logged in, otherwise false */
    fun signIn(login: String, password: String, remember: Boolean): Boolean {
        var loggedIn = false

        runBlocking {
            val response = client.submitForm<HttpResponse>(
                "http://$serverAddress/kotme/www/index.php/site/login",
                parametersOf(
                    Pair("LoginForm[login]", listOf(login)),
                    Pair("LoginForm[password]", listOf(password))
                )
            )

            response.headers.forEach { s, list ->
                println("===")
                println(s)
                list.forEach {
                    println(it)
                }
            }

            println(cookies.container.firstOrNull { it.name == "PHPSESSID" }?.value)
            if (cookies.container.firstOrNull { it.name == "PHPSESSID" } != null) {
                if (remember) setAccount(login, password)
                loggedIn = true
                println("PHPSESSID yes")
            }
        }

        return loggedIn
    }

    /** Sign up on server */
    fun signUp(login: String, password: String) {
        runBlocking {
            client.submitForm<HttpResponse>(
                "http://$serverAddress/kotme/www/index.php/site/newregistration",
                parametersOf(
                    Pair("Users[login]", listOf(login)),
                    Pair("Users[name]", listOf(login)),
                    Pair("Users[password]", listOf(password))
                )
            )
        }

        mainActivity.login.loginText = login
        mainActivity.login.show()
    }

    fun checkCode(code: String, exercise: String) {
        runBlocking {
            val response = client.post<HttpResponse>("http://$serverAddress/kotme/www/index.php/begin/check") {
                contentType(ContentType.Application.FormUrlEncoded)
                body = Parameters.build {
                    append("exercise", exercise)
                    append("code", code)
                }.formUrlEncode()
            }

            var responseString = ""
            response.content.read {
                responseString = Charsets.UTF_8.decode(it).toString()
            }

            val responseJson = JSONObject(responseString)

            if (responseJson.getInt("status") == ResultStatus.TestsSuccess) {
                mainActivity.congratulations.show()
            } else {
                mainActivity.results.console = if (responseJson.has("console")) responseJson.getString("console") else ""
                mainActivity.results.message = if (responseJson.has("message")) responseJson.getString("message") else ""
                mainActivity.results.show()
            }

            println(responseString)
        }
    }

    companion object {
        val Origin = "org.thelemistix.kotme"
    }
}
