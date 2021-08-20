package com.kotme

import android.accounts.Account
import android.accounts.AccountManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.net.SocketException

class KotmeClient(val mainActivity: MainActivity) {
    var protocol: String = "http"
    var address: String = "192.168.0.2:8000"
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
            url {
                protocol = URLProtocol.HTTPS
                host = "kotme-service.herokuapp.com"
                encodedPath = "/api/$encodedPath"
            }
        }
    }

    var login: CharArray = charArrayOf()
    var password: CharArray = charArrayOf()

    var isOnline = true

//    val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//    val prefs = EncryptedSharedPreferences.create(
//        "kotme-prefs",
//        masterKey,
//        mainActivity,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )

    fun syncAll() {
    }

    fun url(address: String = this.address): String = "$protocol://$address$url"

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
            }
        } catch (ex: SocketException) {
            isOnline = false
        }
    }

    companion object {
        const val Origin = "com.kotme"
    }
}
