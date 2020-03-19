package ru.bey_sviatoslav.android.vk_cup_task_d.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbumCover

class VKPhotoByIdResuest : VKRequest<VKAlbumCover> {
    constructor(photoId : Int, albumId : Int) : super("photos.get") {
        addParam("photo_ids", photoId)
        addParam("album_id", albumId)
        addParam("photo_sizes", 1)
    }

    override fun parse(r: JSONObject): VKAlbumCover {
        val response = r.getJSONObject("response")
        val photos = response.getJSONArray("items")
        val result = VKAlbumCover.parse(photos.getJSONObject(0))
        return result
    }
}