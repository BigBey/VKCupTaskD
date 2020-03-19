package ru.bey_sviatoslav.android.vk_cup_task_d.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.exceptions.VKApiExecutionException
import ru.bey_sviatoslav.android.vk_cup_task_d.MainActivity
import ru.bey_sviatoslav.android.vk_cup_task_d.R
import ru.bey_sviatoslav.android.vk_cup_task_d.fragments.ListOfAlbumsFragment
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbumCover
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKDeleteAlbumRequest
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKNewAlbumRequest
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKPhotoByIdResuest
import ru.bey_sviatoslav.android.vk_cup_task_d.utils.getPhotoAddition


//Унаследуем наш класс AlbumAdapter от класса RecyclerView.Adapter
//Тут же указываем наш собственный ViewHolder, который предоставит нам доступ к view компонентам
class AlbumAdapter(private val activity: MainActivity, private var albums : ArrayList<VKAlbum>, private var isEditMode: Boolean = false) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.album_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: AlbumAdapter.ViewHolder, position: Int) {
        val vkAlbum = albums.get(position)
        holder.itemView.isLongClickable = true
        holder.itemView.setOnLongClickListener {
            isEditMode = true
            notifyDataSetChanged()
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            activity.showAlbumFragment(vkAlbum)
        }
        holder.bind(vkAlbum, position)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

        var albumCoverImageView: ImageView? = null
        var deleteImageView: ImageView? = null
        var albumTitleTextView : TextView? = null
        var photosNumberTextView : TextView? = null
        init {
            albumCoverImageView = itemView!!.findViewById(R.id.albumCoverImageView)
            deleteImageView = itemView!!.findViewById(R.id.deleteImageView)
            albumTitleTextView = itemView!!.findViewById(R.id.albumTitleTextView)
            photosNumberTextView = itemView!!.findViewById(R.id.numberOfPhotosTextView)
            (activity.supportFragmentManager.findFragmentByTag(MainActivity.ListOfAlbumsFragmentTAG)
                    as ListOfAlbumsFragment).apply {
                editImageView.setOnClickListener {
                    setRedactorMode(true)
                    isEditMode = true
                    notifyDataSetChanged()
                }
                closeImageView.setOnClickListener{
                    setRedactorMode(false)
                    isEditMode = false
                    notifyDataSetChanged()
                }
                plusImageView.setOnClickListener {
                    val dialogBuilder: AlertDialog = AlertDialog.Builder(context!!).create()
                    val inflater = this.layoutInflater
                    val dialogView: View = inflater.inflate(R.layout.new_album_dialog, null)

                    val editText =
                        dialogView.findViewById<View>(R.id.edt_comment) as EditText
                    val button1: Button =
                        dialogView.findViewById<View>(R.id.buttonSubmit) as Button
                    val button2: Button =
                        dialogView.findViewById<View>(R.id.buttonCancel) as Button

                    button2.setOnClickListener(View.OnClickListener { dialogBuilder.dismiss() })
                    button1.setOnClickListener(View.OnClickListener {
                        VK.execute(VKNewAlbumRequest(editText.text.toString()), object: VKApiCallback<VKAlbum> {
                            override fun success(result: VKAlbum) {
                                albums.add(result)
                                notifyDataSetChanged()
                                dialogBuilder.dismiss()
                            }
                            override fun fail(error: VKApiExecutionException) {
                                Log.d("MainActivity_", error.toString())
                            }
                        })
                    })

                    dialogBuilder.setView(dialogView)
                    dialogBuilder.show()
                }
            }
        }
        fun bind(vkAlbum: VKAlbum, position: Int){
            if(isEditMode){
                activity.supportFragmentManager.apply {
                    (findFragmentByTag(MainActivity.ListOfAlbumsFragmentTAG) as ListOfAlbumsFragment)
                        .setRedactorMode(true)
                }
                if(vkAlbum.id == -6){
                    albumCoverImageView!!.alpha = 0.5F
                    albumTitleTextView!!.alpha = 0.5F
                    photosNumberTextView!!.alpha = 0.5F
                }else {
                    val animShake = AnimationUtils.loadAnimation(
                        activity,
                        R.anim.album_shaking_animation
                    ) as Animation
                    albumCoverImageView!!.startAnimation(animShake)
                    deleteImageView!!.visibility = View.VISIBLE
                }
            }else{
                albumCoverImageView!!.alpha = 1F
                albumTitleTextView!!.alpha = 1F
                photosNumberTextView!!.alpha = 1F
                albumCoverImageView!!.animation = null
                deleteImageView!!.visibility = View.GONE
            }
            VK.execute(VKPhotoByIdResuest(vkAlbum.thumbId, vkAlbum.id),object: VKApiCallback<VKAlbumCover> {
                override fun success(result: VKAlbumCover) {
                    Glide.with(activity).load(result.photoUrl).centerCrop().placeholder(R.drawable.camera_200).into(albumCoverImageView!!)
                    albumTitleTextView!!.setText(vkAlbum.title)
                    photosNumberTextView!!.setText("${vkAlbum.size} ${vkAlbum.size.getPhotoAddition()}")
                }
                override fun fail(error: VKApiExecutionException) {
                    albumCoverImageView!!.setImageDrawable(activity.resources.getDrawable(R.drawable.camera_200))
                    albumTitleTextView!!.setText(vkAlbum.title)
                    photosNumberTextView!!.setText("${vkAlbum.size} ${vkAlbum.size.getPhotoAddition()}")
                }
            })
            deleteImageView!!.setOnClickListener {
                VK.execute(VKDeleteAlbumRequest(vkAlbum.id),object: VKApiCallback<Int> {
                    override fun success(result: Int) {
                        if(result == 1){
                            albums.removeAt(position)
                            this@AlbumAdapter.notifyItemRemoved(position)
                            this@AlbumAdapter.notifyItemRangeChanged(position, albums.size)
                            this@AlbumAdapter.notifyDataSetChanged()
                            deleteImageView!!.visibility = View.GONE
                        }
                    }
                    override fun fail(error: VKApiExecutionException) {
                        val a = 1
                    }
                })
            }
        }

    }
}