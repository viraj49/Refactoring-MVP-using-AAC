package tank.viraj.without.aac

import android.app.Application
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyPresenter(application: Application) {

    private var view: MyFragment? = null

    private var internetUtil = InternetUtil(application)
    private var myDataSource = MyDataSource(internetUtil)

    private var myViewState = MyViewState()
    private val viewSubscription = BehaviorSubject.create<MyViewState>()!!

    private val viewSubscriptions = CompositeDisposable()
    private val dataSubscriptions = CompositeDisposable()

    init {
        getData()
    }

    fun bind(view: MyFragment) {
        this.view = view

        viewSubscriptions.add(viewSubscription
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    this.view?.updateView(it)
                    if (it?.shouldWaitForInternet ?: false) waitForInternet()
                }))
    }

    fun unBind() {
        internetUtil.stopWaitForInternet()
        viewSubscriptions.clear()
        this.view = null
    }

    fun getData() {
        dataSubscriptions.add(Maybe.concat(
                myDataSource.getDataFromFakeDatabase(),
                myDataSource.getDataFromFakeNetwork())
                .firstElement()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .doOnSubscribe {
                    myViewState = myViewState.copy(loadingState = true,
                            shouldWaitForInternet = false)
                    viewSubscription.onNext(myViewState)
                }
                .subscribe({ data ->
                    myViewState = myViewState.copy(myData = data,
                            loadingState = false,
                            shouldWaitForInternet = false)
                    viewSubscription.onNext(myViewState)
                }) { error ->
                    if (error is NoInternetException) {
                        myViewState = myViewState.copy(myData = "No Internet",
                                loadingState = false,
                                shouldWaitForInternet = true)
                    } else {
                        myViewState = myViewState.copy(myData = "Generic Error",
                                loadingState = false,
                                shouldWaitForInternet = false)
                    }
                    viewSubscription.onNext(myViewState)
                })
    }

    private fun waitForInternet() {
        viewSubscriptions.add(internetUtil.waitForInternet()
                .observeOn(Schedulers.computation())
                .subscribe({
                    status ->
                    if (status ?: false) {
                        internetUtil.stopWaitForInternet()
                        getData()
                    }
                }))
    }

    fun cleanUp() {
        dataSubscriptions.dispose()
    }
}