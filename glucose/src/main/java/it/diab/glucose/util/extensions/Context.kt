/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.glucose.util.extensions

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes

@ColorInt
fun Context.getColorAttr(@StyleRes style: Int, @AttrRes attr: Int): Int {
    val attrs = intArrayOf(attr)
    val typedArray = obtainStyledAttributes(style, attrs)
    return typedArray.getColor(0, Color.BLACK).also { typedArray.recycle() }
}