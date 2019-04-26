/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.util.extensions

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeAllItemDecorators() {
    for (i in 0 until itemDecorationCount) {
        removeItemDecorationAt(i)
    }
}