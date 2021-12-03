package com.Like

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

class AudioListAdapter(private val ctx: MainActivity): BaseAdapter() {
    var ltInflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    private val model = ViewModelProvider(ctx).get(Model::class.java)
    private val audioLiveData = model?.getAudioData()
    private val audioPlayLiveData = model?.getAudioPlayItemData()

    override fun getCount(): Int {
        return audioLiveData?.value!!.size
    }

    override fun getItem(position: Int): Constants.Audio {
        return audioLiveData?.value!![position]
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
        val menu: Button? = view.findViewById(R.id.audioMenu)

        name?.text = itemData.name

        audioPlayContent.setOnClickListener {
            model?.audioPlayItemLiveData?.value = itemData
        }
        return view
    }
}