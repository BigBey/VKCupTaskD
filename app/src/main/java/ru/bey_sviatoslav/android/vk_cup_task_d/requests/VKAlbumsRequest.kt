package ru.bey_sviatoslav.android.vk_cup_task_d.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum

class VKAlbumsRequest : VKRequest<List<VKAlbum>> {
    constructor() : super("photos.getAlbums") {
        addParam("need_covers", 1)
        addParam("need_system", 1)
    }

    override fun parse(r: JSONObject): List<VKAlbum> {
        val response = r.getJSONObject("response")
        val albums = response.getJSONArray("items")
        val result = ArrayList<VKAlbum>()
        for (i in 0 until albums.length()) {
            result.add(VKAlbum.parse(albums.getJSONObject(i)))
        }
        return result
    }
}