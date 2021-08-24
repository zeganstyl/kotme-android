package com.kotme.api

import android.content.Context
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
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KotmeApi @Inject constructor(@ApplicationContext val context: Context) {
   suspend fun getUpdates(from: Long = 0): UpdatesDTO = client.get("/user/updates/$from")

   suspend fun getExercises(): List<ExerciseDTO> = client.get("/exercises")
   suspend fun getAchievements(): List<AchievementDTO> = client.get("/achievements")

   suspend fun checkCode(exercise: Int, code: String): CodeCheckResult = client.submitForm(
      url = "/user/codes/$exercise",
      formParameters = Parameters.build {
         append("exercise", exercise.toString())
         append("code", code)
      }
   )

   suspend fun getUserAchievements(): List<UserAchievementDTO> = client.get("/user/achievements")

   suspend fun signUp(name: String, login: String, password: String): HttpRequest = client.submitForm(
      url = PATH.api_signup,
      formParameters = Parameters.build {
         append("name", name)
         append("login", login)
         append("password", password)
      }
   )

   suspend fun login(login: String, password: String) {
      tokenClient.get<String>("/")
   }

   private val tokenClient = HttpClient(Android) {
      install(JsonFeature) {
         serializer = KotlinxSerializer()
      }
      install(Auth) {
         basic {
            credentials { BasicAuthCredentials("root", "root") }
            realm = "kotme.com"
         }
      }
      install(DefaultRequest) {
         url {
            protocol = URLProtocol.HTTPS
            host = "kotme-service.herokuapp.com"
            encodedPath = "/api/token"
         }
      }
   }

   private val client: HttpClient = HttpClient(Android) {
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
         headers.append("Accept","application/json")
         url {
            protocol = URLProtocol.HTTPS
            host = "kotme-service.herokuapp.com"
            encodedPath = "/api/$encodedPath"
         }
      }
      install(Auth) {
         bearer {
            loadTokens {
               BearerTokens(tokenClient.get(), "")
            }
            refreshTokens {
               context
               BearerTokens(tokenClient.get(), "")
            }
         }
      }
   }
}