package org.hyperskill.phrases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhraseRecyclerAdapter(phrases: List<Phrase>) : RecyclerView.Adapter<PhraseViewHolder>() {
    interface PhraseDeleteCallback {
        fun onDeletePhrase(phrase: Phrase)
    }

    private var deleteCallback: PhraseDeleteCallback? = null

    fun setPhraseDeleteCallback(callback: PhraseDeleteCallback) {
        deleteCallback = callback
    }

    var data: MutableList<Phrase> = phrases.toMutableList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        return PhraseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_phrase, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.phraseTextView).text =
            data[position].phrase
        holder.itemView.findViewById<TextView>(R.id.deleteTextView).setOnClickListener {
            deleteCallback?.onDeletePhrase(data[position])
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class PhraseViewHolder(phraseView: View) : RecyclerView.ViewHolder(phraseView)