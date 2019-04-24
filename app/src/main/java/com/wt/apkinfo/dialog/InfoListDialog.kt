package com.wt.apkinfo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.wt.apkinfo.R
import com.wt.apkinfo.entity.ComponentInfo

/**
 * Created by kenumir on 24.09.2017.
 *
 */

class InfoListDialog : DialogFragment() {

    private var mOnGetData: OnGetData? = null

    interface OnGetData {
        fun onGetData(): List<ComponentInfo>
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mOnGetData = context as OnGetData
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = if (arguments != null) arguments!!.getString(KEY_TITLE, "List") else "List"
        val dialog = MaterialDialog.Builder(this.activity!!)
                .title(title)
                .positiveText(R.string.label_close)
                .adapter(ListItemsAdapter(mOnGetData!!.onGetData()), LinearLayoutManager(this.activity!!))
                .build()

        //val dialog = MaterialDialog(this.activity!!)
        //        .title(0, title)
        //        .positiveButton(R.string.label_close)
        //        .customListAdapter(ListItemsAdapter(mOnGetData!!.onGetData()))
        //dialog.setTitle(title)
        dialog.getRecyclerView().addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL))
        return dialog
    }

    class ListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var text1: TextView
        var text2: TextView

        init {
            text1 = itemView.findViewById(R.id.text1)
            text2 = itemView.findViewById(R.id.text2)
        }
    }

    private class ListItemsAdapter(internal var data: List<ComponentInfo>?) : RecyclerView.Adapter<ListItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
            return ListItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_component_info, parent, false))
        }

        override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
            val d = data!![position]
            holder.text1.text = d.name
            holder.text2.text = d.className
        }

        override fun getItemCount(): Int {
            return if (data != null) data!!.size else 0
        }
    }

    companion object {

        private val KEY_TITLE = "title"

        fun newInstance(title: String): InfoListDialog {
            val d = InfoListDialog()
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            d.arguments = args
            return d
        }
    }

}
