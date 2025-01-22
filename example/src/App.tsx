import { useState, useEffect } from 'react';
import { Text, View, StyleSheet, Alert } from 'react-native';
import { multiply, payToPhone } from 'react-native-tcard';

export default function App() {
  const [result, setResult] = useState<number | undefined>();

  useEffect(() => {
    multiply(2, 13).then(setResult);

    payToPhone(100, 'NFC')
      .then((res) => {
        Alert.alert('SUCCESS', res);
      })
      .catch((e) => {
        Alert.alert('FAIL', e.toString());
      });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
