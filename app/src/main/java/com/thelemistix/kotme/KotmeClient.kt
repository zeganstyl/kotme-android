package com.thelemistix.kotme

import android.accounts.Account
import android.accounts.AccountManager
import android.os.SystemClock
import android.widget.Toast
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
    var protocol: String = "http"
    var address: String = "192.168.0.2"
    var url: String = "/index.php"

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

    var isOnline = true
        set(value) {
            if (field != value) {
                field = value
                mainActivity.runOnUiThread {
                    toast(if (value) "В режиме онлайн" else "В режиме оффлайн")
                    syncAll()
                }
            }
        }

    init {
        Thread {
            while (true) {
                if (!isOnline) updateServerLink()
                SystemClock.sleep(30000L)
            }
        }.start()
    }

    fun loadServerConfig() {
        if (mainActivity.prefs.contains("server")) {
            try {
                val serverJson = JSONObject(mainActivity.prefs.getString("server", "{}")!!)
                if (serverJson.has("protocol")) protocol = serverJson.getString("protocol")
                if (serverJson.has("address")) address = serverJson.getString("address")
                if (serverJson.has("url")) url = serverJson.getString("url")
            } catch (ex: Exception) {}
        }

        mainActivity.settings.address = address
        mainActivity.settings.protocol = protocol
        mainActivity.settings.url = url
    }

    fun syncProgress(showNewAchievements: Boolean = true) {
        runBlocking {
            try {
                val response = client.post<HttpResponse>("${url()}/api/progress") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    body = Parameters.build {
                        appendLoginPassword(this)
                    }.formUrlEncode()
                }
                var responseString = ""
                response.content.read {
                    responseString = Charsets.UTF_8.decode(it).toString()
                }

                mainActivity.db.setProgress(responseString, showNewAchievements)
            } catch (ex: SocketException) {
                isOnline = false
            }
        }
    }

    fun syncAll() {
        mainActivity.db.checkAllUncheckedCode()

        runBlocking {
            try {
                val response = client.post<HttpResponse>("${url()}/api/sync_all") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    body = Parameters.build {
                        appendLoginPassword(this)
                    }.formUrlEncode()
                }
                var responseString = ""
                response.content.read {
                    responseString = Charsets.UTF_8.decode(it).toString()
                }

                mainActivity.db.setProgress(responseString)
            } catch (ex: SocketException) {
                isOnline = false
            }
        }
    }

    fun syncExercise(exercise: Int) {
        runBlocking {
            try {
                val response = client.post<HttpResponse>("${url()}/api/cached_code") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    body = Parameters.build {
                        appendLoginPassword(this)
                        append("exercise", exercise.toString())
                    }.formUrlEncode()
                }

                if (response.status.value == 200) {
                    var responseString = ""
                    response.content.read {
                        responseString = Charsets.UTF_8.decode(it).toString()
                    }

                    if (responseString.isNotEmpty()) mainActivity.db.setExerciseCache(exercise, responseString, true)
                }
            } catch (ex: SocketException) {
                isOnline = false
            }
        }
    }

    fun url(address: String = this.address): String = "$protocol://$address$url"

    fun saveServerConfig() {
        val serverJson = JSONObject()
        serverJson.put("protocol", protocol)
        serverJson.put("address", address)
        serverJson.put("url", url)

        mainActivity.prefs.edit().putString("server", serverJson.toString(4)).apply()
    }

    /** Sign in with saved credentials
     * @return true if account exists and can login, otherwise false */
    fun signIn(): Int {
        val am = AccountManager.get(mainActivity) // "this" references the current Context
        val accounts = am.getAccountsByType(Origin)

        if (accounts.isNotEmpty()) {
            val acc = accounts.last()
            return signIn(acc.name, am.getPassword(acc), false)
        }

        return SignInStatus.NoRememberedAccount
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
    fun signIn(login: String, password: String, remember: Boolean): Int {
        var status = SignInStatus.Fail

        runBlocking {
            try {
                val response = client.post<HttpResponse>("${url()}/api/check_auth") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    body = Parameters.build {
                        append("username", login)
                        append("password", password)
                    }.formUrlEncode()
                }

                status = when (response.status.value) {
                    200 -> {
                        if (remember) {
                            setAccount(login, password)
                        } else {
                            this@KotmeClient.login = login.toCharArray()
                            this@KotmeClient.password = password.toCharArray()
                        }
                        SignInStatus.OK
                    }
                    401 -> SignInStatus.Fail
                    else -> SignInStatus.ServerError
                }
            } catch (ex: SocketException) {
                isOnline = false
                status = SignInStatus.LinkIsDown
            }
        }

        return status
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
        try {
            runBlocking {
                client.submitForm<HttpResponse>(
                    "${url()}/site/newregistration",
                    parametersOf(
                        Pair("Users[login]", listOf(login)),
                        Pair("Users[name]", listOf(login)),
                        Pair("Users[password]", listOf(password))
                    )
                )

                mainActivity.login.loginText = login
                mainActivity.login.show()
            }
        } catch (ex: SocketException) {
            isOnline = false
        }
    }

    fun checkCode(code: String, exercise: String, workInBackground: Boolean = false): Boolean {
        var result = false
        runBlocking {
            try {
                val response = client.post<HttpResponse>("${url()}/api/check_code") {
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

                        if (!workInBackground) {
                            if (responseJson.getInt("status") == ResultStatus.TestsSuccess) {
                                mainActivity.exercise.resultsButtonText = "Задание выполнено"
                                mainActivity.congratulations.show()
                            } else {
                                mainActivity.exercise.resultsButtonText = mainActivity.results.message.substringBefore('\n')
                                mainActivity.results.show()
                            }
                        } else {
                            if (responseJson.getInt("status") == ResultStatus.TestsSuccess) {
                                toast("Задание $exercise проверено\nВыполнено успешно")
                            } else {
                                if (mainActivity.exercise.exercise == exercise.toInt()) {
                                    mainActivity.exercise.resultsButtonText = mainActivity.results.message.substringBefore('\n')
                                }
                                toast("Задание $exercise проверено\nЕсть ошибки")
                            }
                        }

                        syncProgress(true)
                        mainActivity.map.setCurrentExercise()

                        result = true
                    }
                    401 -> {
                        if (!workInBackground) {
                            mainActivity.login.show()
                        }
                    }
                    500 -> {
                        if (!workInBackground) {
                            mainActivity.systemMessage.message = "Ошибка сервера"
                            mainActivity.systemMessage.show()
                        }
                    }
                }
            } catch (ex: SocketException) {
                isOnline = false

                if (!workInBackground) {
                    mainActivity.systemMessage.message = "На данный момент сервер не доступен.\nКод будет сохранен. Как только появится соединение с сервером, код будет проверен"
                    mainActivity.systemMessage.show()
                }
            }
        }
        return result
    }

    fun toast(text: String) = Toast.makeText(mainActivity, text, Toast.LENGTH_LONG).show()

    fun checkServerLink(address: String, protocol: String, url: String): HttpResponse? {
        var response: HttpResponse? = null
        runBlocking {
            try {
                response = mainActivity.client.client.get<HttpResponse>("$protocol://$address$url")
            } catch (ex: SocketException) {
            }
        }
        return response
    }

    fun updateServerLink(): Boolean {
        isOnline = checkServerLink(address, protocol, url)?.status?.isSuccess() == true
        return isOnline
    }

    companion object {
        const val Origin = "com.thelemistix.kotme"
    }
}
