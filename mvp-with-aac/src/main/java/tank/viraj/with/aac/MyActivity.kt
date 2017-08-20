package tank.viraj.with.aac

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.activity.*

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyActivity : LifecycleActivity() {

    private lateinit var myPresenterFactory: MyPresenterFactory
    private lateinit var myPresenter: MyPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        myPresenterFactory = MyPresenterFactory(application)
        myPresenter = ViewModelProviders.of(this, myPresenterFactory).get(MyPresenter::class.java)

        refresh_view.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        myPresenter.bind(this)
    }

    override fun onStop() {
        myPresenter.unBind()
        super.onStop()
    }

    fun updateView(myViewState: MyViewState?) {
        text_view.text = myViewState?.myData
        refresh_view.isRefreshing = myViewState?.loadingState ?: false
    }
}