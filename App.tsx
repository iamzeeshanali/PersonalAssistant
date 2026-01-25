/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import { NewAppScreen } from '@react-native/new-app-screen';
import { StatusBar, StyleSheet, Text, TouchableOpacity, useColorScheme, View } from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';
import { NativeModules, NativeEventEmitter } from 'react-native';
import { useEffect } from 'react';
import { PermissionsAndroid, Platform } from 'react-native';
import { speechResult } from './src/redux/voice/actions';
import { Provider, useDispatch } from 'react-redux';
import { store } from './src/redux/store';
import VoiceRoot from './src/screens/VoiceRoot';

function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (

    <SafeAreaProvider>
      <Provider store={store}>
        <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
        <VoiceRoot />
      </Provider>
    </SafeAreaProvider>
  );
}

export default App;
