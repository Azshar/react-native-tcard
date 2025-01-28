export type ResultType = {
  amount: number;
  paymentMethod: 'NFC' | 'QR';
  transactionId: number;
  isRefund: boolean;
  dateTime: string;
  tid: number;
  mid: number;
};

export type ErrorType = {
  code: number;
  details: string;
};
