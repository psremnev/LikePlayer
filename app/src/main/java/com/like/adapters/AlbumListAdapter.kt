package com.like.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.like.App
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R
import com.like.dataClass.Album
import com.like.databinding.AlbumListItemBinding
import javax.inject.Inject

class AlbumListAdapter(ctx: MainActivity, val data: ArrayList<Album>): RecyclerView.Adapter<AlbumListAdapter.ViewHolder>()  {
    @Inject lateinit var model: MainActivityModel

    init {
        val mainActivityComponent = (ctx.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: AlbumListItemBinding? = DataBindingUtil.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: AlbumListItemBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.album_list_item,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        model.onBindAlbumListViewHolder(this, holder, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItemData(position: Int): Album {
        return data[position]
    }
}