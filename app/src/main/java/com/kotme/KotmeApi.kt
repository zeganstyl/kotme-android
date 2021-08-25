package com.kotme

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kotme.common.*
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KotmeApi @Inject constructor(@ApplicationContext val context: Context) {
   private val masterKey = MasterKey.Builder(context)
      .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
      .build()

   val credentialsPrefs = EncryptedSharedPreferences.create(
      context,
      "kotme",
      masterKey,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
   )

   private var tempLogin: String = ""
   private var tempPass: String = ""

   suspend fun getUpdates(from: Long = 0): UpdatesDTO = client.get("${PATH.api_user_updates}/$from")

   suspend fun checkCode(exercise: Int, code: String): CodeCheckResult =
      client.post("${PATH.api_user_codes}/$exercise") { body = code }

   suspend fun checkCodeAnonym(exercise: Int, code: String): CodeCheckResult =
      clientAnonym.post("${PATH.api_code}/$exercise") { body = code }

   suspend fun signUp(name: String, login: String, password: String): HttpResponse = clientAnonym.submitForm(
      url = PATH.api_signup,
      formParameters = Parameters.build {
         append("name", name)
         append("login", login)
         append("password", password)
      }
   )

   suspend fun tryLogin(name: String, pass: String): Boolean = try {
      tempLogin = name
      tempPass = pass
      tokenClient.get<String>()
      tempLogin = ""
      tempPass = ""
      true
   } catch (ex: Exception) {
      ex.printStackTrace()
      false
   }

   private val tokenClient = HttpClient(Android) {
      install(JsonFeature) {
         serializer = KotlinxSerializer()
      }
      install(Auth) {
         basic {
            credentials {
               val name = tempLogin.ifEmpty { credentialsPrefs.getString("name", "") ?: "" }
               val pass = tempPass.ifEmpty { credentialsPrefs.getString("pass", "") ?: "" }
               BasicAuthCredentials(name, pass)
            }
         }
      }
      install(DefaultRequest) {
         url {
            protocol = URLProtocol.HTTPS
            host = "kotme-service.herokuapp.com"
            encodedPath = PATH.api_token
         }
      }
   }

   private fun HttpClientConfig<AndroidEngineConfig>.setupClient() {
      followRedirects = false
      install(JsonFeature) {
         serializer = KotlinxSerializer()
      }
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
         url {
            protocol = URLProtocol.HTTPS
            host = "kotme-service.herokuapp.com"
         }
      }
   }

   private val clientAnonym: HttpClient = HttpClient(Android) {
      setupClient()
   }

   private val client: HttpClient = HttpClient(Android) {
      setupClient()
      install(Auth) {
         bearer {
            loadTokens {
               BearerTokens(tokenClient.get(), "")
            }
            refreshTokens {
               BearerTokens(tokenClient.get(), "")
            }
         }
      }
   }
}