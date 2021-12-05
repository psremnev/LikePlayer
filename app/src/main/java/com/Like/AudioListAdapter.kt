package com.Like

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider

class AudioListAdapter(private val ctx: MainActivity): BaseAdapter() {
    var ltInflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    private val model = ViewModelProvider(ctx)[Model::class.java]

    override fun getCount(): Int {
        return model.audioLiveData.value!!.size
    }

    override fun getItem(position: Int): Constants.Audio {
        return model.audioLiveData.value!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = ltInflater.inflate(R.layout.audio_list_item, parent, false);
        val itemData = getItem(position)
        val audioPlayContent: LinearLayout = view.findViewById(R.id.audioPlayContent)
        val image: ImageView? = view.findViewById(R.id.audioImage)
        val name: TextView? = view.findViewById(R.id.audioName)
        val artist: TextView? = view.findViewById(R.id.audioArtist)
        val menu: Button? = view.findViewById(R.id.audioMenu)
        val emptyAlbumPhoto: LinearLayout = view.findViewById(R.id.emptyAlbumPhoto)

        val newName = itemData.name.replace(Regex("""[.com.mp3]*"""), "")
        name?.text = newName
        if (itemData.artist != Constants.unknownArtist) {
            artist?.text = itemData.artist
        } else {
            artist?.text = ""
        }
        val uri = getImageUriByAlbumId(itemData.albumId)
        image?.setImageURI(uri)
        // если нет превью у аудио
        if (image?.drawable == null) {
            emptyAlbumPhoto.visibility = View.VISIBLE
        }

        audioPlayContent.setOnClickListener {
            model.audioPlayItemLiveData.value = itemData
        }
        return view
    }

    private fun getImageUriByAlbumId(albumId: Long): Uri {
        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }

}