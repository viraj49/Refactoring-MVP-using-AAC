package tank.viraj.with.aac

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyPresenterFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPresenter::class.java)) {
            return MyPresenter(application) as T
        }
        throw IllegalArgumentException("Unknown MyPresenter class")
    }
}