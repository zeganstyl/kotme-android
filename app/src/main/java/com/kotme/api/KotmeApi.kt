package com.kotme.api

import com.kotme.data.*
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
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KotmeApi @Inject constructor() {
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
               tokenClient
               BearerTokens(tokenClient.get(), "")
            }
            refreshTokens { BearerTokens(tokenClient.get(), "") }
         }
      }
   }

   suspend fun getUpdates(from: Long = 0): UpdatesDTO = client.get("/user/updates/$from")

   suspend fun getExercises(): List<ExerciseDTO> = client.get("/exercises")
   suspend fun getAchievements(): List<Achievement> = client.get("/achievements")

   suspend fun checkCode(exercise: Int, code: String): CodeCheckResult = client.submitForm(
      url = "/user/codes/$exercise",
      formParameters = Parameters.build {
         append("exercise", exercise.toString())
         append("code", code)
      }
   )

   suspend fun getUserAchievements(): List<UserAchievementDTO> = client.get("/user/achievements")
}

@Serializable
data class ExerciseDTO(
   val id: Int,
   val number: Int,
   val name: String,
   val lessonText: String,
   val storyText: String,
   val exerciseText: String,
   val initialCode: String,
   val characterMessage: String
) {
   fun entity() = Exercise(
      id,
      number,
      name,
      lessonText,
      storyText,
      exerciseText,
      initialCode,
      characterMessage
   )
}

@Serializable
data class UpdatesDTO(
   val user: UserDTO,
   val exercises: List<ExerciseDTO>,
   val achievements: List<AchievementDTO>,
   val lastUpdateTime: Long = System.currentTimeMillis()
)

@Serializable
data class UserCodeDTO(
   val user: Int,
   val exercise: Int,
   val code: String,
   val uploadTime: Long,
   val completeTime: Long,
   val resultStatus: CodeCheckResultStatus
)

@Serializable
data class UserDTO(
   val id: Int,
   val name: String,
   val progress: Int,
   val codes: List<UserCodeDTO>,
   val achievements: List<UserAchievementDTO>
)

@Serializable
data class UserAchievementDTO(
   val user: Int,
   val achievement: Int,
   val receiveTime: Long
)

@Serializable
data class CodeCheckResult(
   val status: CodeCheckResultStatus,
   val message: String,
   val consoleLog: String,
   val newAchievements: List<UserAchievementDTO>
)

@Serializable
data class AchievementDTO(
   val id: Int,
   val name: String,
   val conditionText: String
) {
   fun entity() = Achievement(id, name, conditionText)
}