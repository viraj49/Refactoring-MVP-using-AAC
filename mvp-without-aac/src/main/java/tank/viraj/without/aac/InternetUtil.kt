package tank.viraj.without.aac


import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


/**
 * Created by Viraj Tank, 15-08-2017.
 */
object InternetUtil {

    private var broadcastReceiver: BroadcastReceiver? = null
    private val internetSubscription = PublishSubject.create<Boolean>()
    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }

    fun isInternetOn(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun waitForInternet(): Observable<Boolean> {
        registerBroadCastReceiver()
        return internetSubscription.serialize()
    }

    fun stopWaitForInternet() {
        unRegisterBroadCastReceiver()
    }

    private fun registerBroadCastReceiver() {
        if (broadcastReceiver == null) {
            val filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(_context: Context, intent: Intent) {
                    val extras = intent.extras
                    val info = extras.getParcelable<NetworkInfo>("networkInfo")
                    internetSubscription.onNext(info.state == NetworkInfo.State.CONNECTED)
                }
            }

            application.registerReceiver(broadcastReceiver, filter)
        }
    }

    private fun unRegisterBroadCastReceiver() {
        if (broadcastReceiver != null) {
            application.unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
        }
    }
}