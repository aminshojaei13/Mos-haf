package com.braveboy.mos_haf.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.braveboy.mos_haf.presentation.feature.search.SearchViewModel
import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.data.local.datasource.LocalDataSource
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.data.repository.QuranRepository
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.feature.detail.QuranDetailViewModel
import com.braveboy.mos_haf.presentation.feature.home.HomeViewModel
import com.braveboy.mos_haf.presentation.feature.suralist.SuraListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "mos_haf_preferences")

val appModule = module {
    // Database
    single { AppDatabase.getInstance(androidContext()) }
    single<DataStore<Preferences>> {
        androidContext().userDataStore
    }
    
    // DAOs
    single { get<AppDatabase>().quranDao() }

    // DataSource
    singleOf(::LocalDataSource)
    
    // Repositories
    singleOf(::QuranRepository)
    singleOf(::PreferencesRepository)

    // UseCases
    singleOf(::GetQuranVersesUseCase)

    // ViewModels
    viewModelOf(::SearchViewModel)
    viewModelOf(::SuraListViewModel)
    viewModelOf(::QuranDetailViewModel)
    viewModelOf(::HomeViewModel)
}