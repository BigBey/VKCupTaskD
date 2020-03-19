package ru.bey_sviatoslav.android.vk_cup_task_d.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.exceptions.VKApiExecutionException
import ru.bey_sviatoslav.android.vk_cup_task_d.MainActivity
import ru.bey_sviatoslav.android.vk_cup_task_d.R
import ru.bey_sviatoslav.android.vk_cup_task_d.adapter.AlbumAdapter
import ru.bey_sviatoslav.android.vk_cup_task_d.adapter.PhotosAdapter
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKPhoto
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKAlbumsRequest
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKPhotosInAlbumRequest
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKSaveToAlbumCommand
import kotlin.concurrent.fixedRateTimer

class AlbumFragment(private val vkAlbum: VKAlbum) : Fragment() {

    private lateinit var photos : ArrayList<VKPhoto>

    private lateinit var photosRecyclerView: RecyclerView
    private lateinit var photosAdapter : PhotosAdapter

    private lateinit var backImageButton : ImageView
    private lateinit var plusImageButton : ImageView

    private lateinit var titleTextView: TextView

    private lateinit var refreshLayout : SwipeRefreshLayout
    private lateinit var progressBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_album, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        refreshLayout = view.findViewById(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener {
            loadPhotos(view)
        }

        initButtons(view)
        initTextViews(view)
        loadPhotos(view)
        return view
    }

    private fun loadPhotos(view: View){
        if(VK.isLoggedIn()) {
            VK.execute(VKPhotosInAlbumRequest(vkAlbum.id), object : VKApiCallback<List<VKPhoto>> {
                override fun success(result: List<VKPhoto>) {
                    photos = result as ArrayList<VKPhoto>
                    initRecyclerView(view)
                }

                override fun fail(error: VKApiExecutionException) {
                    val a = 1
                }
            })
        }
    }

    private fun initRecyclerView(view : View){
        photosRecyclerView = view.findViewById<RecyclerView>(R.id.photosRecyclerView)
        photosRecyclerView.setLayoutManager(GridLayoutManager(context, 3))
        photosAdapter = PhotosAdapter(activity as MainActivity, photos)
        photosRecyclerView.adapter = photosAdapter

        photosRecyclerView.setHasFixedSize(true)
        photosRecyclerView.setItemViewCacheSize(20)
        photosRecyclerView.isDrawingCacheEnabled = true

        progressBar.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }

    private fun initButtons(view : View){
        backImageButton = view.findViewById(R.id.backImageButton)
        backImageButton.setOnClickListener {
            (activity as MainActivity).showListOfAlbumsFragment()
        }
        plusImageButton = view.findViewById(R.id.plusImageButton)
        plusImageButton.setOnClickListener {
            (activity as MainActivity).pickFromGallery()
        }
    }

    private fun initTextViews(view : View){
        titleTextView = view.findViewById(R.id.albumTitleTextView)
        titleTextView.setText(vkAlbum.title)
    }

    internal fun savePhoto(uri : Uri){
        VK.execute(VKSaveToAlbumCommand(vkAlbum.id, uri),object: VKApiCallback<VKPhoto> {
            override fun success(result: VKPhoto){
                photosAdapter.addPhoto(result)
                photosAdapter.notifyDataSetChanged()
                (activity as MainActivity).deleteImageFromAppMemory(uri)
            }
            override fun fail(error: VKApiExecutionException) {
                Log.d("MainActivity_", error.toString())
            }
        })
    }
}
