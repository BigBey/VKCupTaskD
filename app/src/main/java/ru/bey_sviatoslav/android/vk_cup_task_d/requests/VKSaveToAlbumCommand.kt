package ru.bey_sviatoslav.android.vk_cup_task_d.requests

import android.net.Uri
import android.util.Log
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKHttpPostCall
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKFileUploadInfo
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKPhoto
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKSaveInfo
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKServerUploadInfo
import java.util.concurrent.TimeUnit

class VKSaveToAlbumCommand (private val albumId: Int,
                            private val photo: Uri): ApiCommand<VKPhoto>() {
    private val TAG = "VKSaveToAlbumCommand"
    override fun onExecute(manager: VKApiManager): VKPhoto {
        val callBuilder = VKMethodCall.Builder()
            .method("photos.save")
            .args("album_id", albumId)
            .version(manager.config.version)

        try {
            val uploadInfo = getServerUploadInfo(manager)
            Log.d(TAG, uploadInfo.albumId.toString())
            val fileUploadInfo = uploadPhoto(photo, uploadInfo, manager)
            callBuilder.args("server", fileUploadInfo.server)
            callBuilder.args("photos_list", fileUploadInfo.photo)
            callBuilder.args("hash", fileUploadInfo.hash)
        }catch (e : Exception){
            Log.d(TAG, e.toString())
        }
        Log.d(TAG, "success")
        return manager.execute(callBuilder.build(), ResponseApiParser())
    }

    private fun getServerUploadInfo(manager: VKApiManager): VKServerUploadInfo {
        val uploadInfoCall = VKMethodCall.Builder()
            .method("photos.getUploadServer")
            .args("album_id", albumId)
            .version(manager.config.version)
            .build()
        return manager.execute(uploadInfoCall, ServerUploadInfoParser())
    }

    private fun uploadPhoto(uri: Uri, serverUploadInfo: VKServerUploadInfo, manager: VKApiManager): VKFileUploadInfo {
        val fileUploadCall = VKHttpPostCall.Builder()
            .url(serverUploadInfo.uploadUrl)
            .args("photo", uri, "image.jpg")
            .timeout(TimeUnit.MINUTES.toMillis(5))
            .retryCount(RETRY_COUNT)
            .build()
        Log.d(TAG, fileUploadCall.url)

        val fileUploadInfo = manager.execute(fileUploadCall, null, FileUploadInfoParser())

        return fileUploadInfo
    }

    companion object {
        const val RETRY_COUNT = 3
    }

    private class ResponseApiParser : VKApiResponseParser<VKPhoto> {
        override fun parse(response: String): VKPhoto {
            try {
                return VKPhoto.parse(JSONObject(response).getJSONArray("response").getJSONObject(0))
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class ServerUploadInfoParser : VKApiResponseParser<VKServerUploadInfo> {
        override fun parse(response: String): VKServerUploadInfo{
            try {
                val joResponse = JSONObject(response).getJSONObject("response")
                return VKServerUploadInfo(
                    uploadUrl = joResponse.getString("upload_url"),
                    albumId = joResponse.getInt("album_id"),
                    userId = joResponse.getInt("user_id"))
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class FileUploadInfoParser: VKApiResponseParser<VKFileUploadInfo> {
        override fun parse(response: String): VKFileUploadInfo {
            try {
                val joResponse = JSONObject(response)
                return VKFileUploadInfo(
                    server = joResponse.getString("server"),
                    photo = joResponse.getString("photos_list"),
                    hash = joResponse.getString("hash"),
                    aid = joResponse.getInt("aid")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }

    private class SaveInfoParser: VKApiResponseParser<VKSaveInfo> {
        override fun parse(response: String): VKSaveInfo {
            try {
                val joResponse = JSONObject(response).getJSONArray("response").getJSONObject(0)
                return VKSaveInfo(
                    id = joResponse.getInt("id"),
                    albumId = joResponse.getInt("album_id"),
                    ownerId = joResponse.getInt("owner_id")
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}