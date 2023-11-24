/*
 *  This file is part of ChrSwitch.
 *
 *  ChrSwitch is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ChrSwitch is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with ChrSwitch.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.frknkrc44.chrswitch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.frknkrc44.chrswitch.App.Companion.selectedPkgs

import org.frknkrc44.chrswitch.databinding.FragmentBrowserItemBinding

class BrowserRVAdapter(context: Context) : RecyclerView.Adapter<BrowserRVAdapter.ViewHolder>() {
    private var context: Context
    private var pkgInfos = ArrayList<Pkg>()

    init {
        this.context = context
        (context as Activity).setTitle("Select two packages")

        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            var resolves = context.packageManager.queryIntentActivities(intent, 0)

            resolves = resolves.filter {
                it.activityInfo.applicationInfo.className == "org.chromium.chrome.browser.base.SplitChromeApplication"
            }

            if (resolves.isNotEmpty()) {
                resolves.sortWith { r1, r2 ->
                    r1.loadLabel(context.packageManager).toString().lowercase()
                        .compareTo(r2.loadLabel(context.packageManager).toString().lowercase())
                }

                pkgInfos.addAll(resolves.map {
                    Pkg(
                        it.loadLabel(context.packageManager).toString(),
                        it.activityInfo.packageName,
                        it.loadIcon(context.packageManager)
                    )
                })
            }
        } catch (ignored: Throwable) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentBrowserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = pkgInfos[position]
        holder.titleView.text = item.title
        holder.packageNameView.text = item.packageName
        holder.iconView.setImageDrawable(item.icon)
        holder.itemView.setOnClickListener {
            if(item in selectedPkgs) {
                selectedPkgs.remove(item)
                holder.itemView.foreground = ColorDrawable(Color.TRANSPARENT)
            } else {
                if (selectedPkgs.size >= 2) {
                    return@setOnClickListener
                }

                selectedPkgs.add(item)
                holder.itemView.foreground = ColorDrawable(Color.parseColor("#22000000"))
            }

            if (selectedPkgs.isEmpty()) {
                (context as Activity).setTitle("Select two packages")
            } else if (selectedPkgs.size == 1) {
                (context as Activity).setTitle(selectedPkgs.first().title + " -> ???")
            } else {
                (context as Activity).setTitle(selectedPkgs.first().title + " -> " + selectedPkgs.last().title)
            }
        }
    }

    override fun getItemCount(): Int = pkgInfos.size

    inner class ViewHolder(binding: FragmentBrowserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.title
        val packageNameView: TextView = binding.packageName
        val iconView: ImageView = binding.icon

        override fun toString(): String {
            return super.toString() + " '" + packageNameView.text + "'"
        }
    }

    inner class Pkg(title: String, packageName: String, icon: Drawable) {
        var title: String
        var packageName: String
        var icon: Drawable

        init {
            this.title = title
            this.packageName = packageName
            this.icon = icon
        }
    }
}