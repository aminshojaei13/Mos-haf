package com.braveboy.mos_haf.presentation.feature.detail

import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.presentation.feature.player.data.Reciter
import com.braveboy.mos_haf.ui.theme.ThemeType

data class QuranDetailState(
    val isFromLast: Boolean = false,
    val isDarkMode: Boolean = false,
    val themeType: ThemeType = ThemeType.LIGHT,
    val selectedReciter: Reciter = Reciter.ALAFASY,
    val lastRead: LastReadModel? = null,
    val verses: List<Quran> = emptyList(),
    val suraNames: List<String> = emptyList(),
    val translations: List<String> = emptyList(),
    val suraName: String = "",
    val fontSize: Float? = null,
    val translationFontSize: Float? = null,
    val isLoading: Boolean = true
)