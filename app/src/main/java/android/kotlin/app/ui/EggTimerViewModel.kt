package android.kotlin.app.ui

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.kotlin.app.receiver.AlarmReceiver
import android.os.CountDownTimer
import androidx.lifecycle.*
import android.kotlin.app.R
import android.kotlin.app.util.cancelNotifications
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EggTimerViewModel(private val app: Application) : AndroidViewModel(app) {
    private val REQUEST_CODE = 0
    private val TRIGGER_TIME = "TRIGGER_AT"

    private val minute: Long = 60_000L
    private val second: Long = 1_000L

    private val timerLengthOptions: IntArray
    private val notifyPendingIntent: PendingIntent

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var prefs = app.getSharedPreferences("com.example.android.eggtimernotifications", Context.MODE_PRIVATE)
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    private val _timeSelection = MutableLiveData<Int>()

    val timeSelection: LiveData<Int>
        get() = _timeSelection

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long>
        get() = _elapsedTime

    private var _alarmOn = MutableLiveData<Boolean>()
    val isAlarmOn: LiveData<Boolean>
        get() = _alarmOn


    private lateinit var timer: CountDownTimer

    init {
        _alarmOn.value = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        timerLengthOptions = app.resources.getIntArray(R.array.minutes_array)

        //If alarm is not null, resume the timer back for this alarm
        if (_alarmOn.value!!) {
            createTimer()
        }

    }

    fun setAlarm(isChecked: Boolean) {
        when (isChecked) {
            true -> timeSelection.value?.let { startTimer(it) }
            false -> cancelNotification()
        }
    }

    fun setTimeSelected(timerLengthSelection: Int) {
        _timeSelection.value = timerLengthSelection
    }



    private fun startTimer(position : Int){
        _alarmOn.value?.let{
            if(!it){
                Log.e("_____" , "start Timer")
                _alarmOn.value = true
                val selectedInterval = when(position){
                    0 -> second * 10
                    else -> timerLengthOptions[position] * minute
                }
                val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

                val notificationManager = ContextCompat.getSystemService(app ,NotificationManager::class.java ) as NotificationManager

                notificationManager.cancelNotifications()

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notifyPendingIntent
                )
                Toast.makeText(app ,"notificationCreated" , Toast.LENGTH_SHORT ).show()

                viewModelScope.launch {
                    saveTime(triggerTime)
                }


            }
        }
        createTimer()
    }

    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, second) {
                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value = triggerTime - SystemClock.elapsedRealtime()
                    if (_elapsedTime.value!! <= 0) {
                        resetTimer()
                    }
                }

                override fun onFinish() {
                    resetTimer()
                }
            }
            timer.start()
        }
    }



    private fun cancelNotification() {
        resetTimer()
        alarmManager.cancel(notifyPendingIntent)
    }

    private fun resetTimer() {
        timer.cancel()
        _elapsedTime.value = 0
        _alarmOn.value = false
    }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }





}