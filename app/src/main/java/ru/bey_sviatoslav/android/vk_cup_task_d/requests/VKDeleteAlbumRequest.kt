package ru.bey_sviatoslav.android.vk_cup_task_d.requests

import com.vk.api.sdk.VK
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum

class VKDeleteAlbumRequest: VKRequest<Int> {
    constructor(albumId : Int) : super("photos.deleteAlbum") {
        addParam("album_id", albumId)
    }

    override fun parse(r: JSONObject): Int {
        val result = r.getInt("response")
        return result
    }
}