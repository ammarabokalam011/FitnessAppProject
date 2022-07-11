package de.hsfl.jkkab.fitnessappproject.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hsfl.jkkab.fitnessappproject.R
import de.hsfl.jkkab.fitnessappproject.models.MenuEntry

class MenuRecyclerViewAdapter(private val context: Context, menuEntries: List<MenuEntry>) :
    RecyclerView.Adapter<MenuRecyclerViewAdapter.ViewHolder?>() {
    private val menuEntries: List<MenuEntry>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.menu_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuEntry: MenuEntry = menuEntries[position]
        holder.menuTitle.setText(menuEntry.menuTitle)
        holder.menuIcon.setImageDrawable(context.getDrawable(menuEntry.icon))
    }

    override fun getItemCount(): Int {
        return menuEntries.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var menuIcon: ImageView
        var menuTitle: TextView
        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, getAdapterPosition())
        }

        init {
            menuIcon = itemView.findViewById(R.id.ivMenuLogo)
            menuTitle = itemView.findViewById<TextView>(R.id.tvMenuTitle)
            itemView.setOnClickListener(this)
        }
    }

    fun getItem(id: Int): MenuEntry {
        return menuEntries[id]
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    init {
        mInflater = LayoutInflater.from(context)
        this.menuEntries = menuEntries
    }
}