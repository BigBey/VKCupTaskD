package ru.bey_sviatoslav.android.vk_cup_task_d.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.exceptions.VKApiExecutionException
import ru.bey_sviatoslav.android.vk_cup_task_d.MainActivity

import ru.bey_sviatoslav.android.vk_cup_task_d.R
import ru.bey_sviatoslav.android.vk_cup_task_d.adapter.AlbumAdapter
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKAlbumsRequest

class ListOfAlbumsFragment : Fragment() {

    private lateinit var albums : ArrayList<VKAlbum>

    private lateinit var albumsRecyclerView: RecyclerView
    private lateinit var albumsAdapter : AlbumAdapter


    private lateinit var documentsTextView : TextView
    private lateinit var defaultModeLayout : LinearLayout
    private lateinit var editModeLayout : LinearLayout
    internal lateinit var closeImageView : ImageView
    internal lateinit var editImageView : ImageView
    internal lateinit var plusImageView: ImageView

    private lateinit var progressBar: ProgressBar
    private lateinit var refreshLayout : SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_of_albums, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        refreshLayout = view.findViewById(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener {
            loadAlbums(view)
        }

        initToolbar(view)
        loadAlbums(view)
        return view
    }

    private fun loadAlbums(view: View){
        VK.execute(VKAlbumsRequest(), object: VKApiCallback<List<VKAlbum>> {
            override fun success(result: List<VKAlbum>) {
                albums = result as ArrayList<VKAlbum>
                initRecyclerView(view)
            }
            override fun fail(error: VKApiExecutionException) {
                val a = 1
            }
        })
    }

    private fun initRecyclerView(view : View){
        albumsRecyclerView = view.findViewById<RecyclerView>(R.id.albumsRecyclerView)
        albumsRecyclerView.setLayoutManager(GridLayoutManager(context, 2))
        albumsAdapter = AlbumAdapter(activity as MainActivity, albums)
        albumsRecyclerView.adapter = albumsAdapter

        albumsRecyclerView.setHasFixedSize(true)
        albumsRecyclerView.setItemViewCacheSize(20)
        albumsRecyclerView.isDrawingCacheEnabled = true
        progressBar.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }

    private fun initToolbar(view: View){
        documentsTextView = view.findViewById(R.id.documentsTextView)
        defaultModeLayout = view.findViewById(R.id.defaultLinearLayout)

        editModeLayout = view.findViewById(R.id.editLinearLayout)
        editModeLayout.visibility = View.GONE

        closeImageView = view.findViewById(R.id.closeImageView)

        editImageView = view.findViewById(R.id.editImageView)

        plusImageView = view.findViewById(R.id.plusImageView)
    }

    internal fun setRedactorMode(isRedactorMode : Boolean){
        if(isRedactorMode){
            documentsTextView.visibility = View.GONE
            defaultModeLayout.visibility = View.GONE
            editModeLayout.visibility = View.VISIBLE
        }else{
            documentsTextView.visibility = View.VISIBLE
            defaultModeLayout.visibility = View.VISIBLE
            editModeLayout.visibility = View.GONE
        }
    }
}
