package org.thelemistix.kotme

import android.accounts.Account
import android.accounts.AccountManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.SocketException

class KotmeClient(val mainActivity: MainActivity) {
    var serverAddress: String = "192.168.0.2"

    val client = HttpClient(Android) {
        followRedirects = false
        HttpResponseValidator {
            validateResponse {
            }
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
        }
    }

    var login: CharArray = charArrayOf()
    var password: CharArray = charArrayOf()

    /** Sign in with saved credentials
     * @return true if account exists and can login, otherwise false */
    fun signIn(): String {
        val am = AccountManager.get(mainActivity) // "this" references the current Context
        val accounts = am.getAccountsByType(Origin)

        if (accounts.isNotEmpty()) {
            val acc = accounts.last()
            return signIn(acc.name, am.getPassword(acc), false)
        }

        return "Создайте аккаунт"
    }

    fun setAccount(login: String, password: String) {
        this.login.fill(' ')
        this.password.fill(' ')

        val am = AccountManager.get(mainActivity) // "this" references the current Context
        val accounts = am.getAccountsByType(Origin)

        if (accounts.firstOrNull { it.name == login } == null) {
            val account = Account(login, Origin)
            am.addAccountExplicitly(account, password, null)
        }
    }

    /** Sign in on server
     * @return true if authenticated, otherwise false */
    fun signIn(login: String, password: String, remember: Boolean): String {
        var loggedIn = ""

        runBlocking {
            val response = client.post<HttpResponse>("http://$serverAddress/kotme/www/index.php/api/check_auth") {
                contentType(ContentType.Application.FormUrlEncoded)
                body = Parameters.build {
                    append("username", login)
                    append("password", password)
                }.formUrlEncode()
            }

            loggedIn = when (response.status.value) {
                200 -> {
                    if (remember) {
                        setAccount(login, password)
                    } else {
                        this@KotmeClient.login = login.toCharArray()
                        this@KotmeClient.password = password.toCharArray()
                    }
                    ""
                }
                401 -> "Не верный логин или пароль"
                else -> "Ошибка сервера"
            }
        }

        return loggedIn
    }

    private fun appendLoginPassword(builder: ParametersBuilder) {
        val am = AccountManager.get(mainActivity) // "this" references the current Context
        val accounts = am.getAccountsByType(Origin)

        if (accounts.isNotEmpty()) {
            val acc = accounts.last()
            builder.append("username", acc.name)
            builder.append("password", am.getPassword(acc))
        } else {
            builder.append("username", login.concatToString())
            builder.append("password", password.concatToString())
        }
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
            val response = client.post<HttpResponse>("http://$serverAddress/kotme/www/index.php/api/check_code") {
                contentType(ContentType.Application.FormUrlEncoded)
                body = Parameters.build {
                    appendLoginPassword(this)
                    append("exercise", exercise)
                    append("code", code)
                }.formUrlEncode()
            }

            when (response.status.value) {
                200 -> {
                    var responseString = ""
                    response.content.read {
                        responseString = Charsets.UTF_8.decode(it).toString()
                    }

                    val responseJson = JSONObject(responseString)

                    mainActivity.results.console = if (responseJson.has("console")) responseJson.getString("console") else ""
                    mainActivity.results.message = if (responseJson.has("message")) responseJson.getString("message") else ""

                    if (responseJson.getInt("status") == ResultStatus.TestsSuccess) {
                        mainActivity.exercise.resultsButtonText = "Задание выполнено"
                        mainActivity.congratulations.show()
                    } else {
                        mainActivity.exercise.resultsButtonText = mainActivity.results.message.substringBefore('\n')
                        mainActivity.results.show()
                    }
                }
                401 -> {
                    mainActivity.login.show()
                }
            }
        }
    }

    fun checkServerLink(serverAddress: String): HttpResponse? {
        var response: HttpResponse? = null
        runBlocking {
            try {
                response = mainActivity.client.client.get<HttpResponse>("http://${serverAddress}/kotme/www/index.php")
            } catch (ex: SocketException) {
            }
        }
        return response
    }

    fun checkServerLink(): Boolean = checkServerLink(serverAddress)?.status?.isSuccess() == true

    companion object {
        val Origin = "org.thelemistix.kotme"
    }
}
