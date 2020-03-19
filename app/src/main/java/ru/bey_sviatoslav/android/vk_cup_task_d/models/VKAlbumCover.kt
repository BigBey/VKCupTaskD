package ru.bey_sviatoslav.android.vk_cup_task_d.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

class VKAlbumCover(
    val id: Int = 0,
    val albumId : Int = 0,
    val photoUrl : String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(albumId)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKAlbumCover> {
        override fun createFromParcel(parcel: Parcel): VKAlbumCover {
            return VKAlbumCover(parcel)
        }

        override fun newArray(size: Int): Array<VKAlbumCover?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = VKAlbumCover(
            id = json.optInt("id", 0),
            albumId = json.optInt("album_id", 0),
            photoUrl = json.optJSONArray("sizes").optJSONObject(4).optString("url", ""))
    }
}