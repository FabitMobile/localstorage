package ru.fabit.localstorage


import io.reactivex.Completable
import io.reactivex.Observable


interface ObservableLocalStorageService {

    fun getData(key: String, defaultValue: String): Observable<String>

    fun saveData(key: String, value: String): Completable

    fun getDataSet(key: String): Observable<Set<String>>

    fun addToSet(key: String, value: String): Completable
}