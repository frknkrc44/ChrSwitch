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

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), OnMenuItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, BrowserFragment())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(android.R.string.ok)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.apply {
            icon = getDrawable(android.R.drawable.ic_media_ff)
            setOnMenuItemClickListener(this@MainActivity)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (RootUtils.checkRootAccess() == null) {
            Toast.makeText(this, "No root permission found!", Toast.LENGTH_SHORT).show()
            return false
        }

        item.isEnabled = false
        val dialog = AlertDialog.Builder(this).apply {
            title = "Processing"
            setContentView(TextView(context).apply {
                text = "Please wait..."
            })
            setCancelable(false)
        }.create()
        dialog.show()
        RootUtils.runThrowable(object : RootUtils.CustomRunnable {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun runThrowable() {
                val sourcePkg = App.selectedPkgs.first().packageName
                val targetPkg = App.selectedPkgs.last().packageName
                val dataMainDir = "/data_mirror/data_ce/null/0"
                RootUtils.executeSuCommand(
                    "am force-stop $sourcePkg",
                    "am force-stop $targetPkg",
                    "rm -rf $dataMainDir/$targetPkg/*",
                    "cp -Rf $dataMainDir/$sourcePkg/* $dataMainDir/$targetPkg/",
                    "export GROUP=\$(ls -al $dataMainDir | grep $targetPkg | awk '{ print \$3 }')",
                    "chown -R \$GROUP:\$GROUP $dataMainDir/$targetPkg"
                )
                RootUtils.handler.post {
                    item.isEnabled = true
                    App.selectedPkgs.clear()
                    dialog.dismiss()
                    Toast.makeText(this@MainActivity, "Completed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun runOnError(error: Throwable?) {
                RootUtils.handler.post {
                    item.isEnabled = true
                    App.selectedPkgs.clear()
                    dialog.dismiss()
                    Toast.makeText(this@MainActivity, "$error", Toast.LENGTH_SHORT).show()
                }
            }

        })

        return true
    }
}