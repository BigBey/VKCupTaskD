package ru.bey_sviatoslav.android.vk_cup_task_d

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiExecutionException
import ru.bey_sviatoslav.android.vk_cup_task_d.fragments.AlbumFragment
import ru.bey_sviatoslav.android.vk_cup_task_d.fragments.ListOfAlbumsFragment
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKAlbum
import ru.bey_sviatoslav.android.vk_cup_task_d.models.VKPhoto
import ru.bey_sviatoslav.android.vk_cup_task_d.requests.VKSaveToAlbumCommand
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 1889

    private var currentAlbumId : Int = 0

    companion object{
        val AlbumFragmentTAG = "AlbumFragment"
        val ListOfAlbumsFragmentTAG = "ListOfAlbumsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        loginVK()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                showListOfAlbumsFragment()
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                val selectedImage = copyImageToAppMemory(data!!.data!!)
                (supportFragmentManager.findFragmentByTag(AlbumFragmentTAG) as AlbumFragment)
                    .savePhoto(selectedImage)
            }
        }
    }
    private fun loginVK(){
        if(!VK.isLoggedIn())
            VK.login(this, arrayListOf(VKScope.PHOTOS))
        else
            showListOfAlbumsFragment()
    }

    internal fun showListOfAlbumsFragment(){
        supportFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.forFragment, ListOfAlbumsFragment(), ListOfAlbumsFragmentTAG)
                    .commitNowAllowingStateLoss()
        }

    }

    internal fun showAlbumFragment(vkAlbum: VKAlbum){
        supportFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.forFragment, AlbumFragment(vkAlbum), AlbumFragmentTAG)
                    .commitNow()
        }
    }


    internal fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        intent.type = "image/*"

        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun copyImageToAppMemory(uri : Uri) : Uri {
        var inputStream = contentResolver.openInputStream(uri)
        var outputFile = File(filesDir, "image.jpg")
        var outputStream = FileOutputStream(outputFile)
        inputStream!!.copyTo(outputStream)
        return Uri.parse("file://" + outputFile.path)
    }

    internal fun deleteImageFromAppMemory(uri : Uri){
        val fdelete = File(uri.path!!)
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :$uri")
            } else {
                System.out.println("file not Deleted :$uri")
            }
        }
    }
}
