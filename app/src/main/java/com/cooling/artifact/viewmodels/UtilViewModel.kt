package com.cooling.artifact.viewmodels

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cooling.artifact.bean.AppInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow
import kotlin.random.Random

class UtilViewModel(application: Application) : AndroidViewModel(application) {

    val app = MutableLiveData<AppInfo>()
    val apps = MutableLiveData<MutableList<AppInfo>>()
    val scanningState = MutableLiveData<Boolean>()
//    val services = MutableLiveData<ServiceInfo>()
//    val record = MutableLiveData<Int>()

    /**
     * 延迟获取应用信息
     */
    fun getAppDelay() {
        val pm = getApplication<Application>().packageManager
        Observable.create<MutableList<ResolveInfo>> {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                it.onNext(pm.queryIntentActivities(intent, PackageManager.MATCH_ALL))
            } else {
                it.onNext(pm.queryIntentActivities(intent, 0))
            }
            it.onComplete()
        }.filter {
            it.isNullOrEmpty().not()
        }.flatMap {
            if (it.size > 80) {
                val randomNumber = Random.nextInt(65, 81) // [65,80]
                Observable.fromIterable(it.subList(0, randomNumber).shuffled())
            } else {
                Observable.fromIterable(it.shuffled())
            }
        }.concatMap {
            val packageName = it.activityInfo.applicationInfo.packageName
            val firstInstallTime = pm.getPackageInfo(packageName, 0).firstInstallTime
            val appInfo = AppInfo(
                label = it.loadLabel(pm).toString(),
                packageName = packageName,
                firstInstallTime = firstInstallTime,
                icon = it.loadIcon(pm)
            )
            Observable.just(appInfo).delay(50, TimeUnit.MILLISECONDS)
        }.subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribe(
            { app.postValue(it) },
            { scanningState.postValue(false) },
            { scanningState.postValue(true) }
        )
    }

    /**
     * 获取应用信息
     */
    fun getApps() {
        val pm = getApplication<Application>().packageManager
        Observable.create<MutableList<ResolveInfo>> {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                it.onNext(pm.queryIntentActivities(intent, PackageManager.MATCH_ALL))
            } else {
                it.onNext(pm.queryIntentActivities(intent, 0))
            }
            it.onComplete()
        }.filter {
            // 过滤null或空的列表
            it.isNullOrEmpty().not()
        }.flatMap {
            val list = mutableListOf<AppInfo>()
            for (item in it) {
                // 过滤系统应用
                if ((ApplicationInfo.FLAG_SYSTEM.and(item.activityInfo.applicationInfo.flags) != 0).not()) {
                    val packageName = item.activityInfo.applicationInfo.packageName
                    val length = File(item.activityInfo.applicationInfo.publicSourceDir).length()
                    val firstInstallTime = pm.getPackageInfo(packageName, 0).firstInstallTime
                    val appInfo = AppInfo(
                        label = item.loadLabel(pm).toString(),
                        packageName = packageName,
                        firstInstallTime = firstInstallTime,
                        size = readableFileSize(length),
                        length = length,
                        icon = item.loadIcon(pm)
                    )
                    list.add(appInfo)
                }
            }
            Observable.just(list)
        }.flatMap {
            if (it.size > 120) {
                val randomNumber = Random.nextInt(85, 121) // [85,120]
                val newList = it.subList(0, randomNumber)
                newList.sortByDescending { you -> you.firstInstallTime }
                Observable.just(newList)
            } else {
                it.sortByDescending { you -> you.firstInstallTime }
                Observable.just(it)
            }
        }.subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribe(
            { apps.postValue(it) },
            { scanningState.postValue(false) },
            { scanningState.postValue(true) }
        )
    }

    /**
     * 转换成带单位的文件大小
     */
    private fun readableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf(
            "B",
            "kB",
            "MB",
            "GB",
            "TB"
        )
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }

//    fun getServices() {
//        val am: ActivityManager =
//            getApplication<Application>().getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
//
//        val runningAppProcesses = am.runningAppProcesses
//        if (runningAppProcesses.isNullOrEmpty().not()) {
//            for (runningAppProcessInfo in runningAppProcesses) {
//                Log.i("xxxyyyzzz", "processName: ${runningAppProcessInfo.processName}")
//            }
//        } else {
//            Log.i("xxxyyyzzz", "process 无")
//        }
//
//        Observable.create<MutableList<ActivityManager.RunningServiceInfo>> {
//            it.onNext(am.getRunningServices(80))
//            it.onComplete()
//        }.filter {
//            it.isNullOrEmpty().not()
//        }.flatMap {
//            if (it.size > 80) {
//                it.subList(0, 80)
//            }
//            it.remove(it.random())
//            Observable.fromIterable(it.shuffled())
//        }.concatMap {
//            Observable.just(
//                ServiceInfo(
//                    it.service.className,
//                    it.service.packageName,
//                    it.service.shortClassName
//                )
//            ).delay(50, TimeUnit.MILLISECONDS)
//        }.subscribeOn(
//            Schedulers.io()
//        ).observeOn(
//            AndroidSchedulers.mainThread()
//        ).subscribe(
//            { services.postValue(it) },
//            { scanningState.postValue(false) },
//            { scanningState.postValue(true) }
//        )
//    }

}