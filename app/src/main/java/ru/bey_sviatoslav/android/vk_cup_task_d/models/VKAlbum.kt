package ru.bey_sviatoslav.android.vk_cup_task_d.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKAlbum (
    val id: Int = 0,
    val title : String = "",
    val size : Int,
    val thumbId : Int) : Parcelable{

        constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt()!!)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(title)
            parcel.writeInt(size)
            parcel.writeInt(thumbId)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<VKAlbum> {
            override fun createFromParcel(parcel: Parcel): VKAlbum {
                return VKAlbum(parcel)
            }

            override fun newArray(size: Int): Array<VKAlbum?> {
                return arrayOfNulls(size)
            }

            fun parse(json: JSONObject)
                    = VKAlbum(id = json.optInt("id", 0),
                title = json.optString("title", ""),
                size = json.optInt("size", 0),
                thumbId = json.optInt("thumb_id", 0))
        }
}