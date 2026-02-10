# react-native-tcard

Модуль React-Native для работы с приложение "Pay to Phone" от Т-Банка.

## Installation

```sh
npm install react-native-tcard
```

## Android integration
**Необходимо импортировать пакеты в MainActivity.kt**

```sh
...
import com.tcard.TCardSingleton
import ru.tbank.posterminal.p2psdk.TSoftposManager

class MainActivity : ReactActivity() {
    private val tSoftposManager by lazy { TSoftposManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        TCardSingleton.INSTANCE = tSoftposManager
    }

    override fun onDestroy() {
        super.onDestroy()
        TCardSingleton.INSTANCE = null

        if (!tSoftposManager.isTransactionInProgress) {
            tSoftposManager.unbindSoftpos()
        }
    }
}
```

## Usage
```ts
import React, {FC, useEffect, useState} from 'react';
import {Text, TouchableOpacity} from 'react-native';
import {logger, payToPhone, refundPayment} from 'react-native-tcard';
import type {TLoggerEvent} from 'react-native-tcard';

const Component: FC = () => {
  // Сумма передаваемая в Pay To Phone, должны быть в формате Integer 100 руб 00 коп = 10000
  const [sum, setSum] = useState(10000);
  const [payment, setPayment] = useState({});

  useEffect(() => {
    const subscription = logger.addListener(
      'LogNativeEvent',
      (event: TLoggerEvent) => {},
    );

    return () => {
      subscription.remove();
    };
  });

  const refund = async () => {
    await refundPayment(
        parseSum,
        'NFC',
        parseInt(payment.transaction_id, 10),
        parseInt(payment.mid, 10),
    );
  }

  const payToPhone = async () => {
    try {
      const payment = await payToPhone(sum);
    } catch (e: any) {
      let error = '';

      if (e?.details) {
        error += `${e.details} `;
      }

      if (e?.code) {
        error += `Код ошибки: ${e.code}`;
      }
    }
  };

  return (
    <>
        <TouchableOpacity onPress={payToPhone}>
            <Text>Принять оплату</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={refund}>
            <Text>Возврат оплаты</Text>
        </TouchableOpacity>
    </>
  );
};
```

## License

MIT
