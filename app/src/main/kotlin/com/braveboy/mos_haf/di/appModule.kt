package com.braveboy.mos_haf.di

import com.braveboy.mos_haf.presentation.feature.search.QuranViewModel
import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.data.local.datasource.LocalDataSource
import com.braveboy.mos_haf.data.repository.QuranRepository
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {
    // Database
    single { AppDatabase.getInstance(androidContext()) }
    
    // DAOs
    single { get<AppDatabase>().quranDao() }

    // DataSource
    singleOf(::LocalDataSource)
    
    // Repositories
    singleOf(::QuranRepository)

    // UseCases
    singleOf(::GetQuranVersesUseCase)

    // ViewModels
    viewModelOf(::QuranViewModel)
}