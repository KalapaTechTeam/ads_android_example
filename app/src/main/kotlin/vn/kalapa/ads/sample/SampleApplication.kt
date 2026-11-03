package vn.kalapa.ads.sample

import android.app.Application
import vn.kalapa.ads.adsui.AdsUi
import vn.kalapa.ads.miniapp.MiniAppPresenter

/**
 * SDK step 1: create the mini-app presenter once, before the first Activity resumes, so any code can open a
 * mini-app. Register this class as `android:name` in the manifest.
 */
internal class SampleApplication : Application() {
    lateinit var miniAppPresenter: MiniAppPresenter
        private set

    override fun onCreate() {
        super.onCreate()
        miniAppPresenter = AdsUi.miniAppPresenter(this)
    }
}
