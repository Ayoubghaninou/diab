/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.viewmodels.insulin

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import it.diab.data.repositories.InsulinRepository

class InsulinViewModel internal constructor(
    insulinRepository: InsulinRepository
) : ViewModel() {

    val list = LivePagedListBuilder(insulinRepository.all, 5).build()
}