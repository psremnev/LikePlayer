package com.like.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R
import com.like.dataClass.Album
import com.like.databinding.AlbumListItemBinding


class AlbumListAdapter(ctx: MainActivity, val data: ArrayList<Album>): RecyclerView.Adapter<AlbumListAdapter.ViewHolder>()  {
    private val model = ViewModelProvider(ctx)[MainActivityModel::class.java]

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