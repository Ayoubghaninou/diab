/*
 * Copyright (c) 2019 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.glucose.suggestion.status

import it.diab.data.entities.TimeFrame

internal data class CheckAgainStatus(
    val value: Int,
    val timeFrame: TimeFrame,
    val lowThreshold: Int
) : SuggestionStatus