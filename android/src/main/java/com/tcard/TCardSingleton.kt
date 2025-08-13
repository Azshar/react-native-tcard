package com.tcard

import androidx.activity.ComponentActivity
import ru.tbank.posterminal.p2psdk.TSoftposManager

class TCardSingleton () {
    companion object {
        @Volatile var INSTANCE: TSoftposManager? = null

        fun getInstance(activity: ComponentActivity) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TSoftposManager(activity).also { INSTANCE = it }
            }

        fun getInstance(): TSoftposManager? = INSTANCE
    }
}
