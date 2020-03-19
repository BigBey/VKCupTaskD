package ru.bey_sviatoslav.android.vk_cup_task_d.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.bey_sviatoslav.android.vk_cup_task_d.MainActivity
import ru.bey_sviatoslav.android.vk_cup_task_d.R
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKPhoto

class PhotosAdapter(private val activity: MainActivity, private var photos : ArrayList<VKPhoto>) : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.photo_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotosAdapter.ViewHolder, position: Int) {
        val vkPhoto = photos.get(position)
        holder.bind(vkPhoto)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

        var photoImageView: ImageView? = null
        init {
            photoImageView = itemView!!.findViewById(R.id.photoImageView)
        }
        fun bind(vkPhoto: VKPhoto){
            Glide.with(activity).load(vkPhoto.photoUrl).centerCrop().into(photoImageView!!)
        }
    }

    internal fun addPhoto(vkPhoto: VKPhoto){
        photos.add(vkPhoto)
    }
}