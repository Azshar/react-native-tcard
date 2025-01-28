import { useState } from 'react';
import { Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import { payToPhone, refundPayment } from 'react-native-tcard';
import type { ErrorType, ResultType } from '../../src/types';

export default function App() {
  const [transaction, setTransaction] = useState<ResultType | null>(null);
  const [error, setError] = useState<ErrorType | null>(null);

  const pay = async () => {
    try {
      const data = await payToPhone(43012, 'NFC');

      setTransaction(data);
    } catch (e: unknown) {
      const err = e as ErrorType;
      if (err?.code && err?.details) {
        setError(err);
      }
    }
  };

  const refund = async () => {
    try {
      if (transaction) {
        const data = await refundPayment(
          transaction.amount,
          transaction.paymentMethod,
          transaction.transactionId,
          transaction.mid
        );

        setTransaction(data);
      }
    } catch (e: unknown) {
      const err = e as ErrorType;
      if (err?.code && err?.details) {
        setError(err);
      }
    }
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={pay} style={styles.btnWrap}>
        <Text style={styles.btnText}>Принять оплату</Text>
      </TouchableOpacity>

      {transaction ? (
        <>
          <TouchableOpacity onPress={refund} style={styles.btnWrap}>
            <Text style={styles.btnText}>Сделать возврат</Text>
          </TouchableOpacity>
          <Text>Сумма: {transaction?.amount}</Text>
          <Text>Возврат ли? {transaction?.isRefund ? 'Да' : 'Нет'}</Text>
          <Text>Способ оплаты: {transaction?.paymentMethod}</Text>
          <Text>ID транзакции: {transaction?.transactionId}</Text>
          <Text>Время транзакции: {transaction?.dateTime}</Text>
          <Text>ID терминала: {transaction?.tid}</Text>
          <Text>ID торговой точки: {transaction?.mid}</Text>
        </>
      ) : null}
      {error ? (
        <>
          <Text>Код ошибки: {error?.code}</Text>
          <Text>Сообщение об ошибке: {error?.details}</Text>
        </>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  btnWrap: {
    backgroundColor: 'orange',
    padding: 20,
    width: '100%',
    marginBottom: 20,
  },
  btnText: {
    textAlign: 'center',
  },
});
