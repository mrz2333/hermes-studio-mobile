package xyz.rflg.hstudiodirect

import org.json.JSONArray
import org.json.JSONObject

/**
 * One Hermes Studio deployment the user has signed in to.
 *
 * HStudio keeps the same list under `hermes-studio:local-devices`; here it lives
 * in the encrypted preferences, so the bearer token never lands on disk in the
 * clear and every studio the user owns is one tap away.
 */
data class StudioInstance(
    val url: String,
    val label: String,
    val username: String,
    val token: String,
    val lastUsedAt: Long,
) {
    /** Host shown under the label, e.g. `hermes.example.com:8446`. */
    val host: String get() = hostLabel(url)
}

internal fun hostLabel(url: String): String =
    url.removePrefix("https://").removePrefix("http://").trimEnd('/').substringBefore('/')

internal fun instancesToJson(list: List<StudioInstance>): String {
    val array = JSONArray()
    list.forEach { item ->
        array.put(
            JSONObject()
                .put("url", item.url)
                .put("label", item.label)
                .put("username", item.username)
                .put("token", item.token)
                .put("lastUsedAt", item.lastUsedAt),
        )
    }
    return array.toString()
}

internal fun instancesFromJson(raw: String): List<StudioInstance> {
    if (raw.isBlank()) return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val url = item.optString("url").trim().trimEnd('/')
            if (url.isBlank()) return@mapNotNull null
            StudioInstance(
                url = url,
                label = item.optString("label").ifBlank { hostLabel(url) },
                username = item.optString("username"),
                token = item.optString("token"),
                lastUsedAt = item.optLong("lastUsedAt"),
            )
        }
    }.getOrElse { emptyList() }
}

/** Newest first; the same URL is replaced rather than duplicated. */
internal fun upsertInstance(list: List<StudioInstance>, item: StudioInstance): List<StudioInstance> =
    (listOf(item) + list.filterNot { it.url == item.url }).sortedByDescending { it.lastUsedAt }
