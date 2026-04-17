//package com.tcard
//
//import android.util.Log
//import com.facebook.react.bridge.ReactApplicationContext
//import com.facebook.react.bridge.ReactContextBaseJavaModule
//import com.facebook.react.bridge.ReactMethod
//import com.facebook.react.bridge.Promise
//import com.facebook.react.bridge.WritableMap
//import com.facebook.react.bridge.WritableNativeMap
//import com.facebook.react.modules.core.DeviceEventManagerModule
//import ru.tbank.posterminal.p2psdk.Callback
//import ru.tbank.posterminal.p2psdk.PaymentMethod
//import ru.tbank.posterminal.p2psdk.PaymentTransactionData
//import ru.tbank.posterminal.p2psdk.SoftposResult
//import com.tcard.TCardSingleton
//import ru.tbank.posterminal.p2psdk.RefundTransactionData
//import ru.tbank.posterminal.p2psdk.SoftposException
//import ru.tbank.posterminal.p2psdk.TLogger
//
//val ERRORS = mapOf("Failed to bind to Pay to phone" to "Pay To Phone не установлен на Вашем устройстве")
//
//class TcardModule(reactContext: ReactApplicationContext) :
//  ReactContextBaseJavaModule(reactContext) {
//
//  override fun getName(): String {
//    return NAME
//  }
//
//  @ReactMethod
//  fun payToPhone(amount: Double, paymentMethod: String, promise: Promise) {
//    try {
//      TCardSingleton.getInstance()?.initLogger(object : TLogger {
//        override fun logDebug(tag: String?, message: String) {
//          Log.d(tag, message)
//          sendLogToJS(tag, message)
//        }
//
//        override fun logInfo(tag: String?, message: String) {
//          Log.i(tag, message)
//          sendLogToJS(tag, message)
//        }
//
//        override fun logError(tag: String?, message: String?, throwable: Throwable?) {
//          Log.e(tag, message, throwable)
//          sendLogToJS(tag, message, throwable)
//        }
//      })
//
//      TCardSingleton.getInstance()?.payToPhone(
//        transactionData = PaymentTransactionData.create(
//          amount = amount.toLong(),
//          paymentMethod = when (paymentMethod.lowercase().trim()) {
//            "nfc" -> PaymentMethod.NFC
//            "qr" -> PaymentMethod.QR
//            else -> PaymentMethod.UNDEFINED
//          }
//        ),
//        callback = object : Callback {
//          override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
//            promise.resolve(prepareResult(softposResult))
//            return true
//          }
//
//          override fun onError(e: Throwable) {
//            promise.reject("Create Event Error", prepareError(e))
//          }
//        }
//      )
//    } catch (e: Throwable) {
//      promise.reject("Create Event Error", prepareError(e))
//    }
//  }
//
//    @ReactMethod
//  fun refundPayment(
//    amount: Double,
//    paymentMethod: String,
//    transactionId: Double,
//    mid: Double,
//    promise: Promise)
//  {
//    try {
//      TCardSingleton.getInstance()?.payToPhone(
//        transactionData = RefundTransactionData.create(
//          amount = amount.toLong(),
//          transactionId = transactionId.toLong(),
//          mid = mid.toLong(),
//          paymentMethod = when (paymentMethod.lowercase().trim()) {
//            "nfc" -> PaymentMethod.NFC
//            "qr" -> PaymentMethod.QR
//            else -> PaymentMethod.UNDEFINED
//          }
//        ),
//        callback = object : Callback {
//          override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
//            promise.resolve(prepareResult(softposResult))
//            return true
//          }
//
//          override fun onError(e: Throwable) {
//            promise.reject("Create Event Error", prepareError(e))
//          }
//        }
//      )
//    } catch (e: Throwable) {
//      promise.reject("Create Event Error", prepareError(e))
//    }
//  }
//
//  fun prepareResult(softposResult: SoftposResult): WritableMap {
//    val map  = WritableNativeMap()
//
//    map.putDouble("amount", softposResult.amount.toDouble())
//    map.putDouble("transactionId", softposResult.transactionId.toDouble())
//    map.putDouble("tid", softposResult.tid.toDouble())
//    map.putDouble("mid", softposResult.mid.toDouble())
//    map.putString("paymentMethod", softposResult.paymentMethod.toString())
//    map.putString("dateTime", softposResult.dateTime.toString())
//    map.putBoolean("isRefund", softposResult.isRefund)
//
//    return map
//  }
//
//  fun prepareError(error: Throwable): String {
//    val map  = WritableNativeMap()
//
//    map.putInt("code", -1)
//    map.putString("details", error.message.toString())
//
//    for ((err, mess) in ERRORS) {
//      if (err in error.message.toString()) {
//        map.putInt("code", -2)
//        map.putString("details", mess)
//      }
//    }
//
//    if (error is SoftposException) {
//      map.putInt("code", error.code)
//      map.putString("details", error.details)
//    }
//
//    return map.toString()
//  }
//
//  private fun sendLogToJS(tag: String?, message: String?, throwable: Throwable? = null) {
//    val map  = WritableNativeMap()
//
//    map.putString("tag", tag)
//    map.putString("message", message)
//    map.putString("throwable", throwable.toString())
//
//    reactApplicationContext
//      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
//      .emit("LogNativeEvent", map)
//  }
//
//
//}




package com.tcard

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableNativeMap

import ru.tbank.posterminal.p2psdk.Callback
import ru.tbank.posterminal.p2psdk.TSoftposManager
import ru.tbank.posterminal.p2psdk.PaymentMethod
import ru.tbank.posterminal.p2psdk.PaymentTransactionData
import ru.tbank.posterminal.p2psdk.SoftposResult
import ru.tbank.posterminal.p2psdk.RefundTransactionData

class TcardModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val NAME = "Tcard"
    }

    private var softposManager: TSoftposManager? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun getName(): String {
        return NAME
    }

    public fun getSoftposManager(): TSoftposManager {
        if (softposManager == null) {
            softposManager = TSoftposManager.create(reactApplicationContext)
        }
        return softposManager!!
    }

    @ReactMethod
    fun initialize(promise: Promise) {
        try {
            getSoftposManager()
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("INIT_ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun payToPhone(
        amount: String,
        paymentMethod: String,
        mid: String?,
        promise: Promise
    ) {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity == null) {
            promise.reject("NO_ACTIVITY", "No current activity")
            return
        }

        try {
            val paymentMethodEnum = when (paymentMethod.lowercase().trim()) {
                "nfc" -> PaymentMethod.NFC
                "qr" -> PaymentMethod.QR
                else -> PaymentMethod.UNDEFINED
            }

            val transactionData = PaymentTransactionData.create(
                amount = amount.toLong(),
                paymentMethod = paymentMethodEnum,
                mid = mid?.toLongOrNull()
            )

            mainHandler.post {
                getSoftposManager().payToPhone(
                    activity = currentActivity,
                    transactionData = transactionData,
                    callback = object : Callback {
                        override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
                            val result = WritableNativeMap().apply {
                                putString("result", softposResult.toString())
                                putString("type", "payment")
                            }
                            promise.resolve(result)
                            return true
                        }

                        override fun onError(e: Throwable) {
                            promise.reject("PAYMENT_ERROR", e.message, e)
                        }
                    }
                )
            }
        } catch (e: Exception) {
            promise.reject("PAYMENT_ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun refundPayment(
        amount: String,
        transactionId: String,
        mid: String,
        paymentMethod: String,
        promise: Promise
    ) {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity == null) {
            promise.reject("NO_ACTIVITY", "No current activity")
            return
        }

        try {
            val paymentMethodEnum = when (paymentMethod.lowercase().trim()) {
                "nfc" -> PaymentMethod.NFC
                "qr" -> PaymentMethod.QR
                else -> PaymentMethod.UNDEFINED
            }

            val transactionData = RefundTransactionData.create(
                amount = amount.toLong(),
                transactionId = transactionId.toLong(),
                mid = mid.toLong(),
                paymentMethod = paymentMethodEnum
            )

            mainHandler.post {
                getSoftposManager().payToPhone(
                    activity = currentActivity,
                    transactionData = transactionData,
                    callback = object : Callback {
                        override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
                            val result = WritableNativeMap().apply {
                                putString("result", softposResult.toString())
                                putString("type", "refund")
                            }
                            promise.resolve(result)
                            return true
                        }

                        override fun onError(e: Throwable) {
                            promise.reject("REFUND_ERROR", e.message, e)
                        }
                    }
                )
            }
        } catch (e: Exception) {
            promise.reject("REFUND_ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun unbind() {
        try {
            softposManager?.unbindSoftpos()
            softposManager = null
        } catch (e: Exception) {
            // Игнорируем ошибки при отвязке
        }
    }

    @ReactMethod
    fun cleanUp() {
        unbind()
    }
}

