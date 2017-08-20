package tank.viraj.with.aac

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyPresenter(application: Application) : ViewModel() {

    private var activity: MyActivity? = null

    private var internetUtil = InternetUtil.Singleton.getInstance(application)
    private var myDataSource = MyDataSource(internetUtil)

    private val compositeDisposable = CompositeDisposable()

    private var myViewState = MyViewState()
    private val myLiveData = MutableLiveData<MyViewState>()

    init {
        getData()
    }

    fun bind(activity: MyActivity) {
        this.activity = activity

        myLiveData.observe(this.activity, Observer {
            this.activity?.updateView(it)
            if (it?.shouldWaitForInternet ?: false) waitForInternet()
        })
    }

    fun unBind() {

        this.activity = null
    }

    fun getData() {
        compositeDisposable.add(Maybe.concat(
                myDataSource.getDataFromFakeDatabase(),
                myDataSource.getDataFromFakeNetwork())
                .firstElement()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .doOnSubscribe {
                    myViewState = myViewState.copy(loadingState = true,
                            shouldWaitForInternet = false)
                    myLiveData.postValue(myViewState)
                }
                .subscribe({ data ->
                    myViewState = myViewState.copy(myData = data,
                            loadingState = false,
                            shouldWaitForInternet = false)
                    myLiveData.postValue(myViewState)
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
                    myLiveData.postValue(myViewState)
                })
    }

    private fun waitForInternet() {
        internetUtil.observe(this.activity, Observer {
            status ->
            if (status ?: false) {
                getData()
            }
        })
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}