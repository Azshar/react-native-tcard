import { NativeModules, Platform } from 'react-native';
import { type ResultType } from './types';

const LINKING_ERROR =
  `The package 'react-native-tcard' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Tcard = NativeModules.Tcard
  ? NativeModules.Tcard
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * @typedef ResultType
 * @property {number} amount - Сумма, соответствует значению в копейках. Повторяет значение amount из входных параметров.
 * @property {NFC | QR} paymentMethod - Способ оплаты — NFC/QR.
 * @property {number} transactionId -  Идентификатор операции, соответствует rrn/sbpRrn.
 * @property {bool} isRefund -  Тип операции — оплата или возврат.
 * @property {string} dateTime -  Дата и время проведения транзакции. Значение передаётся в часовом поясе устройства в формате yyyy-MM-dd'T'HH:mm:ssXXX, согласно стандарту ISO 8601.
 * @property {number} tid -  Идентификатор терминала.
 * @property {number} mid -  Идентификатор торговой точки.
 */

/**
 * @typedef ErrorType
 * @property {number} code - Внутренний код ошибки.
 * @property {string} details - Текстовая интерпретация ошибки.
 */

/**
 * @param {number} amount - целочисленная сумма в копейках (50000 = 500 руб. 00 коп.).
 * @param {NFC | QR} payment_type - тип оплаты.
 * @returns {Promise<ResultType | ErrorType>}
 */
export function payToPhone(
  amount: number,
  payment_type: 'NFC' | 'QR' = 'NFC'
): Promise<ResultType> {
  if (Platform.OS === 'ios') {
    return Promise.reject('OS not supported');
  }
  return Tcard.payToPhone(amount, payment_type).catch((e: Error) =>
    prepareError(e)
  );
}

/**
 * @param {number} amount - целочисленная сумма в копейках (50000 = 500 руб. 00 коп.).
 * @param {NFC | QR} payment_type - тип оплаты.
 * @param {number} transactionId - Идентификатор платежа, соответствует rrn/sbpRrn.
 * @param {number} mid - Идентификатор торговой точки.
 * @returns {Promise<ResultType | ErrorType>}
 */
export function refundPayment(
  amount: number,
  payment_type: 'NFC' | 'QR' = 'NFC',
  transactionId: number,
  mid: number
): Promise<ResultType> {
  if (Platform.OS === 'ios') {
    return Promise.reject('OS not supported');
  }
  return Tcard.refundPayment(amount, payment_type, transactionId, mid).catch(
    (e: Error) => prepareError(e)
  );
}

function prepareError(e: any) {
  return Promise.reject(JSON.parse(e));

  // if (e instanceof Error) {
  //   try {
  //     const message = JSON.parse(e.message);

  //     return Promise.reject(message);
  //   } catch (err) {
  //     return Promise.reject(err);
  //   }
  // }
  // return Promise.reject(e);
}
