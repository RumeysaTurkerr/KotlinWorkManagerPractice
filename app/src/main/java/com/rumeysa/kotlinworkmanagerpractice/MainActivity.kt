package com.rumeysa.kotlinworkmanagerpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.rumeysa.kotlinworkmanagerpractice.ui.theme.KotlinWorkManagerPracticeTheme
import java.sql.Time
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data=Data.Builder().putInt("intKey",1).build()

        val constraints =Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false).build()

       /* val myWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            //.setInitialDelay(5, TimeUnit.HOURS)
            //.addTag("myTag")
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)*/

        //15 dakikadan düşük bir sürede yapamazsın. App kapalı olsa bile arka planda yapılacak.
        val myWorkRequest : PeriodicWorkRequest = PeriodicWorkRequestBuilder<RefreshDatabase>(15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)


        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id).observe(this,
            Observer {
                if(it.state == WorkInfo.State.RUNNING)
                    println("running")
                else if(it.state == WorkInfo.State.FAILED)
                    println("failed")
                else if(it.state == WorkInfo.State.SUCCEEDED)
                    println("succeeded")
            })
        //iptal
       // WorkManager.getInstance(this).cancelAllWork()


        //Chaining-->bağlama(zincirleme)
        val oneTimeRequest : OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).beginWith(oneTimeRequest)//bitince
            .then(oneTimeRequest)//bunu yap
            .then(oneTimeRequest)
            .enqueue()
    }


}