/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.core.data.converters

import it.diab.core.data.entities.TimeFrame
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFrameConverterTest {
    private val converter = TimeFrameConverter()

    @Test
    fun convertToInt() {
        assertEquals(1, converter.toInt(TimeFrame.LATE_MORNING))
        assertEquals(TimeFrame.EXTRA.toInt(), converter.toInt(null))
    }

    @Test
    fun convertToTimeFrame() {
        assertEquals(TimeFrame.LATE_MORNING, converter.toTimeFrame(1))
        assertEquals(TimeFrame.EXTRA, converter.toTimeFrame(null))
    }
}