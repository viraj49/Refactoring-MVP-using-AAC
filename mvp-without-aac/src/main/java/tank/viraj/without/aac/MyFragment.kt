package tank.viraj.without.aac

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment.*

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyFragment : Fragment() {

    private val myPresenter = MyPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh_view.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        myPresenter.bind(this)
    }

    override fun onStop() {
        myPresenter.unBind()
        super.onStop()
    }

    override fun onDestroy() {
        myPresenter.cleanUp()
        super.onDestroy()
    }

    fun updateView(myViewState: MyViewState?) {
        text_view.text = myViewState?.myData
        refresh_view.isRefreshing = myViewState?.loadingState ?: false
    }
}