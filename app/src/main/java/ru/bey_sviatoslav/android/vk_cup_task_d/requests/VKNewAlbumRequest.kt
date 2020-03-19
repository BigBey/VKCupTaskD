package ru.bey_sviatoslav.android.vk_cup_task_d.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum

class VKNewAlbumRequest: VKRequest<VKAlbum> {
    constructor(title : String) : super("photos.createAlbum") {
        addParam("title", title)
        addParam("privacy_view", "all")
        addParam("privacy_comment", "all")
    }

    override fun parse(r: JSONObject): VKAlbum {
        val result = VKAlbum.parse(r.getJSONObject("response"))
        return result
    }

}