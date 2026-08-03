package com.braveboy.mos_haf.presentation.feature.search

import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import com.braveboy.mos_haf.presentation.feature.player.data.Reciter

data class SearchState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val selectedReciter: Reciter = Reciter.ALAFASY,
    val lastRead: LastReadModel? = null,
    val verses: List<Quran> = emptyList(),
    val translations: List<String> = emptyList(),
    val suraNames: List<String> = emptyList(),
    val ayaCounts: List<Int> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val fontSize: Float? = null,
    val totalVerses: Int = 0,
)
