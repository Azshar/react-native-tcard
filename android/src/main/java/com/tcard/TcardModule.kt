package com.tcard

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import ru.tbank.posterminal.p2psdk.Callback
import ru.tbank.posterminal.p2psdk.PaymentMethod
import ru.tbank.posterminal.p2psdk.PaymentTransactionData
import ru.tbank.posterminal.p2psdk.SoftposResult
import com.tcard.TCardSingleton
import ru.tbank.posterminal.p2psdk.RefundTransactionData
import ru.tbank.posterminal.p2psdk.SoftposException
import ru.tbank.posterminal.p2psdk.TLogger

class TcardModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun payToPhone(amount: Double, paymentMethod: String, promise: Promise) {
    try {
      TCardSingleton.getInstance()?.initLogger(object : TLogger {
        override fun logDebug(tag: String?, message: String) {
          Log.d(tag, message)
        }

        override fun logInfo(tag: String?, message: String) {
          Log.i(tag, message)
        }

        override fun logError(tag: String?, message: String?, throwable: Throwable?) {
          Log.e(tag, message, throwable)
        }
      })

      TCardSingleton.getInstance()?.payToPhone(
        transactionData = PaymentTransactionData(
          amount = amount.toLong(),
          paymentMethod = when (paymentMethod.lowercase().trim()) {
            "nfc" -> PaymentMethod.NFC
            "qr" -> PaymentMethod.QR
            else -> PaymentMethod.UNDEFINED
          }
        ),
        callback = object : Callback {
          override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
            promise.resolve(prepareResult(softposResult))
            return true
          }

          override fun onError(e: Throwable) {
            promise.reject(prepareError(e))
          }
        }
      )
    } catch (e: Throwable) {
      promise.reject(prepareError(e))
    }
  }

  @ReactMethod
  fun refundPayment(
    amount: Double,
    paymentMethod: String,
    transactionId: Double,
    mid: Double,
    promise: Promise)
  {
    try {
      TCardSingleton.getInstance()?.payToPhone(
        transactionData = RefundTransactionData(
          amount = amount.toLong(),
          transactionId = transactionId.toLong(),
          mid = mid.toLong(),
          paymentMethod = when (paymentMethod.lowercase().trim()) {
            "nfc" -> PaymentMethod.NFC
            "qr" -> PaymentMethod.QR
            else -> PaymentMethod.UNDEFINED
          }
        ),
        callback = object : Callback {
          override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
            promise.resolve(prepareResult(softposResult))
            return true
          }

          override fun onError(e: Throwable) {
            promise.reject(prepareError(e))
          }
        }
      )
    } catch (e: Throwable) {
      promise.reject(prepareError(e))
    }
  }

  fun prepareResult(softposResult: SoftposResult): WritableMap {
    val map  = WritableNativeMap()

    map.putDouble("amount", softposResult.amount.toDouble())
    map.putString("paymentMethod", softposResult.paymentMethod.toString())
    map.putDouble("transactionId", softposResult.transactionId.toDouble())
    map.putBoolean("isRefund", softposResult.isRefund)
    map.putString("dateTime", softposResult.dateTime)
    map.putDouble("tid", softposResult.tid.toDouble())
    map.putDouble("mid", softposResult.mid.toDouble())

    return map
  }

  fun prepareError(error: Throwable): String {
    val map  = WritableNativeMap()

    map.putInt("code", 0)
    map.putString("details", error.message.toString())

    if (error is SoftposException) {
      map.putInt("code", error.code)
      map.putString("details", error.details)
    }

    return map.toString()
  }

  companion object {
    const val NAME = "Tcard"
  }
}
