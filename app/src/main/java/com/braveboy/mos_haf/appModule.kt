package com.braveboy.mos_haf

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