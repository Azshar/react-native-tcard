# react-native-tcard

Модуль React-Native для работы с приложение "[Pay to Phone](https://www.rustore.ru/catalog/app/ru.tinkoff.posterminal)" от Т-Банка. Информация о [SDK](https://developer.tbank.ru/docs/sdk/paytophone)

## Установка

```sh
npm install react-native-tcard
```

## Использование
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
  }, []);

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
        await initialize();

        const payment = await payToPhone(parseInt((card * 100).toFixed(0), 10));

        setTimeout(async () => {
            await API.sendPayment();
            await unbind();
        }, 500);
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

## Комментарии

- initialize(); - метод инициализации пакета, обязателен перед попыткой оплаты/возврата. Можно перенести в useEffect, в котором обязательно необходимо вернуть unbind();
- unbind(); - метод отвязки пакета от активити. Обязательно использовать после оплаты/возврата, либо вернуть в useEffect.
- setTimeout - необходимо использовать, если хотите отправить информацию на сервер. На некоторых устройствах во время возврата в RN, отрубается интернет. Этому может помочь отключение "Режима энергосбережения", но не в 100% случаях. 

## Лицензия

MIT
