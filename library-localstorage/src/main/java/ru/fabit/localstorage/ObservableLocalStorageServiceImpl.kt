package ru.fabit.localstorage

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class ObservableLocalStorageServiceImpl @Inject
constructor(private val localStorageService: LocalStorageService) : ObservableLocalStorageService {

    private val subscribersTypeString = HashMap<String, LocalStorageListener<String>>()
    private val subscribersTypeSetString = HashMap<String, LocalStorageListener<Set<String>>>()
    private val index = AtomicInteger(0)

    //region ===================== Implementation ======================

    override fun getData(key: String, defaultValue: String): Observable<String> {
        return Observable.create { emitter ->
            val localStorageListener = object : LocalStorageListener<String> {
                override fun onDataChanged(data: String) {
                    emitter.onNext(data)
                }
            }
            emitter.onNext(localStorageService.getData(key, defaultValue))
            val subscriberKey = key + "_" + index.addAndGet(1)
            subscribersTypeString[subscriberKey] = localStorageListener

            emitter.setDisposable(object : Disposable {
                override fun dispose() {
                    subscribersTypeString.remove(subscriberKey)
                }

                override fun isDisposed(): Boolean {
                    return subscribersTypeString.containsKey(subscriberKey)
                }
            })
        }
    }

    override fun saveData(key: String, value: String): Completable {
        return Completable.create { emitter ->
            localStorageService.saveData(key, value)
            notifySubscribers(subscribersTypeString, key, value)
            emitter.onComplete()
        }
    }

    override fun getDataSet(key: String): Observable<Set<String>> {
        return Observable.create { emitter ->
            val localStorageListener = object : LocalStorageListener<Set<String>> {
                override fun onDataChanged(data: Set<String>) {
                    emitter.onNext(data)
                }
            }
            emitter.onNext(localStorageService.getDataSet(key) ?: hashSetOf())
            val subscriberKey = key + "_" + index.addAndGet(1)
            subscribersTypeSetString[subscriberKey] = localStorageListener

            emitter.setDisposable(object : Disposable {
                override fun dispose() {
                    subscribersTypeSetString.remove(subscriberKey)
                }

                override fun isDisposed(): Boolean {
                    return subscribersTypeSetString.containsKey(subscriberKey)
                }
            })
        }
    }

    override fun addToSet(key: String, value: String): Completable {
        return Completable.create { emitter ->
            localStorageService.addToSet(key, value)
            val myValue = localStorageService.getDataSet(key)
            notifySubscribers(subscribersTypeSetString, key, myValue)
            emitter.onComplete()
        }
    }
    //endregion

    //region ===================== Internal logic ======================

    private fun <T> notifySubscribers(
        subscribers: HashMap<String, LocalStorageListener<T>>,
        key: String,
        value: T
    ) {
        val copied = HashMap(subscribers)
        for (pair in copied) {
            if (pair.key.contains(key)) {
                copied[pair.key]?.onDataChanged(value)
            }
        }
    }
    //endregion

    //region ===================== Callbacks ======================

    interface LocalStorageListener<T> {
        fun onDataChanged(data: T)
    }
    //endregion
}