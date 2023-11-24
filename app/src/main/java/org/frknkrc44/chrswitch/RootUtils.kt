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

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.Executors


object RootUtils {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    val handler: Handler = Handler(Looper.getMainLooper())

    fun checkRootAccess(): String? {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-v"))
            val inputStream = process.inputStream
            val buf = ByteArray(1024)
            var count: Int
            val baos = ByteArrayOutputStream()
            while (inputStream.read(buf, 0, buf.size).also { count = it } > 0) {
                baos.write(buf, 0, count)
            }
            baos.close()
            inputStream.close()
            process.waitFor()
            return baos.toString()
        } catch (ignored: Throwable) {
        }
        return null
    }

    @Throws(IOException::class, InterruptedException::class)
    fun executeSuCommand(vararg commands: String) {
        val process = Runtime.getRuntime().exec("su")
        val outputStream = process.outputStream
        for (command in commands) {
            outputStream.write(command.toByteArray(StandardCharsets.UTF_8))
            outputStream.write('\n'.code)
        }
        outputStream.write("exit\n".toByteArray(StandardCharsets.UTF_8))
        outputStream.close()
        process.waitFor()
        process.destroy()
    }

    fun runThrowable(runnable: CustomRunnable) {
        executor.execute {
            try {
                runnable.runThrowable()
            } catch (x: Throwable) {
                runnable.runOnError(x)
            }
        }
    }

    interface CustomRunnable {
        @Throws(Throwable::class)
        fun runThrowable()
        fun runOnError(error: Throwable?)
    }
}