package org.thelemistix.kotme

import io.ktor.client.features.cookies.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

class AcceptAllCookiesStorage : CookiesStorage {
    val container: MutableList<Cookie> = ArrayList()
    private val oldestCookie: AtomicLong = AtomicLong(0L)
    private val mutex = Mutex()

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val date = GMTDate()
        if (date.timestamp >= oldestCookie.get()) cleanup(date.timestamp)

        return@withLock container.filter { it.matches(requestUrl) }
    }

    fun String.toLowerCasePreservingASCIIRules(): String {
        val firstIndex = indexOfFirst {
            toLowerCasePreservingASCII(it) != it
        }

        if (firstIndex == -1) {
            return this
        }

        val original = this
        return buildString(length) {
            append(original, 0, firstIndex)

            for (index in firstIndex .. original.lastIndex) {
                append(toLowerCasePreservingASCII(original[index]))
            }
        }
    }

    fun toLowerCasePreservingASCII(ch: Char): Char = when (ch) {
        in 'A' .. 'Z' -> ch + 32
        in '\u0000' .. '\u007f' -> ch
        else -> ch.toLowerCase()
    }

    fun Cookie.matches(requestUrl: Url): Boolean {
        val domain = domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
            ?: error("Domain field should have the default value")

        val path = with(path) {
            val current = path ?: error("Path field should have the default value")
            if (current.endsWith('/')) current else "$path/"
        }

        val host = requestUrl.host.toLowerCasePreservingASCIIRules()
        val requestPath = let {
            val pathInRequest = requestUrl.encodedPath
            if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
        }

        if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
            return false
        }

        if (path != "/" &&
            requestPath != path &&
            !requestPath.startsWith(path)
        ) return false

        return !(secure && !requestUrl.protocol.isSecure())
    }

    fun Cookie.fillDefaults(requestUrl: Url): Cookie {
        var result = this

        if (result.path?.startsWith("/") != true) {
            result = result.copy(path = requestUrl.encodedPath)
        }

        if (result.domain.isNullOrBlank()) {
            result = result.copy(domain = requestUrl.host)
        }

        return result
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie): Unit = mutex.withLock {
        with(cookie) {
            if (name.isBlank()) return@withLock
        }

        container.removeAll { it.name == cookie.name && it.matches(requestUrl) }
        container.add(cookie.fillDefaults(requestUrl))
        cookie.expires?.timestamp?.let { expires ->
            if (oldestCookie.get() > expires) {
                oldestCookie.set(expires)
            }
        }
    }

    override fun close() {
    }

    private fun cleanup(timestamp: Long) {
        container.removeAll { cookie ->
            val expires = cookie.expires?.timestamp ?: return@removeAll false
            expires < timestamp
        }

        val newOldest = container.fold(Long.MAX_VALUE) { acc, cookie ->
            cookie.expires?.timestamp?.let { min(acc, it) } ?: acc
        }

        oldestCookie.set(newOldest)
    }
}