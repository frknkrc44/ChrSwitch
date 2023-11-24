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

import android.app.Application

class App : Application() {
    companion object {
        var selectedPkgs = ArrayList<BrowserRVAdapter.Pkg>()
    }
}