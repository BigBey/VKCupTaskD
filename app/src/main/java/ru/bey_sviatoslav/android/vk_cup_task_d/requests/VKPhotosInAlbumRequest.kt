package ru.bey_sviatoslav.android.vk_cup_task_d.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbumCover
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKPhoto

class VKPhotosInAlbumRequest: VKRequest<List<VKPhoto>> {
    constructor(albumId : Int) : super("photos.get") {
        addParam("album_id", albumId)
        addParam("count", 1000)
    }

    override fun parse(r: JSONObject): List<VKPhoto> {
        val response = r.getJSONObject("response")
        val photos = response.getJSONArray("items")
        val result = ArrayList<VKPhoto>()
        for (i in 0 until photos.length()) {
            result.add(VKPhoto.parse(photos.getJSONObject(i)))
        }
        return result
    }
}