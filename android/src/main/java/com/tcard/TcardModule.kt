package com.tcard

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.Callback as ReactCallback
import ru.tbank.posterminal.p2psdk.Callback
import ru.tbank.posterminal.p2psdk.PaymentMethod
import ru.tbank.posterminal.p2psdk.PaymentTransactionData
import ru.tbank.posterminal.p2psdk.SoftposResult
import ru.tbank.posterminal.p2psdk.TSoftposManager

class TcardModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val softposManager = TSoftposManager(reactContext);

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun payToPhone(amount: Double, paymentMethod: String, promise: Promise) {
    try {

      val newAmount = amount.toLong()

      softposManager.payToPhone(
        transactionData = PaymentTransactionData(
          amount = newAmount,
          paymentMethod = when (paymentMethod.lowercase().trim()) {
            "nfc" -> PaymentMethod.NFC
            "qr" -> PaymentMethod.QR
            else -> PaymentMethod.UNDEFINED
          }
        ),
        callback = object : Callback {
          override fun onTransactionRegistered(softposResult: SoftposResult): Boolean {
            val result = softposResult.toString()

            promise.resolve(result)
            return true
          }

          override fun onError(e: Throwable) {
            promise.reject(e.toString())
          }
        }
      )
    } catch (e: Throwable) {
      promise.reject(e.toString())
    }
  }


  companion object {
    const val NAME = "Tcard"
  }
}
