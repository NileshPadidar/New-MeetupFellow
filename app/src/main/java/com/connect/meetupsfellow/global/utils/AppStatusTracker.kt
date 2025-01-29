import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.connect.meetupsfellow.global.utils.GlobalUtils

object AppStatusTracker : Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var isInBackground = true


    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // No-op
    }

    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground
            isInBackground = false
            onAppForeground(activity.applicationContext, activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        // No-op
    }

    override fun onActivityPaused(activity: Activity) {
        // No-op
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
            isInBackground = true
            onAppBackground(activity.applicationContext)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // No-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // No-op
    }

    private fun onAppForeground(context: Context, activity: Activity) {
        Log.e("AppStatusTracker", "App is in the foreground")
        // Perform your operations when the app is in the foreground
       /// GlobalUtils.create(context, false, "", activity.intent)
        GlobalUtils.clearNotifications(context)
    }

    private fun onAppBackground(context: Context) {
        // Perform your operations when the app is in the background
        Log.e("AppStatusTracker", "App is in the background")
        /*  val intent = Intent("com.connect.meetupsfellow.ACTION_BACKGROUND")
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent)*/
    }

    fun isAppInBackground(): Boolean {
        return isInBackground
    }

}
