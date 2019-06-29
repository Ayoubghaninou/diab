/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
@file:JvmName("EditableStatus")

package it.diab.glucose.components.status

import it.diab.core.arch.ViewStatus
import java.util.Date

data class EditableInStatus(
    val glucoseUid: Long,
    val value: Int,
    val date: Date,
    val foodIntake: Int,
    val insulinUid: Long,
    val insulinDescription: String,
    val basalUid: Long,
    val basalDescription: String,
    val isEditing: Boolean,
    val supportsBasal: Boolean
) : ViewStatus

data class EditableOutStatus(
    val value: Int,
    val foodIntake: Int
) : ViewStatus
