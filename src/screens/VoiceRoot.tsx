import { useEffect, useRef } from 'react';
import { Text, TouchableOpacity, PermissionsAndroid, Platform, Alert, AppStateStatus, AppState, View, Animated, Easing, StyleSheet } from 'react-native';
import { NativeModules, NativeEventEmitter } from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { intentDetected, speechResult, speechStart } from '../redux/voice/actions';
import { parseIntent } from '../redux/voice/functions';
import { interpret } from '../apiServices/openAI';

const { JarvisModule, VoiceModule, WakeWord } = NativeModules;
const voiceEmitter = new NativeEventEmitter(VoiceModule);
const wakeWordEventEmitter = new NativeEventEmitter(WakeWord);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#050B14',
    alignItems: 'center',
    justifyContent: 'center'
  },

  title: {
    position: 'absolute',
    top: 80,
    color: '#00E5FF',
    fontSize: 24,
    letterSpacing: 4,
    fontWeight: '600'
  },

  outerRing: {
    position: 'absolute',
    width: 260,
    height: 260,
    borderRadius: 130,
    borderWidth: 2,
    borderColor: '#00E5FF',
    opacity: 0.4
  },

  middleRing: {
    position: 'absolute',
    width: 200,
    height: 200,
    borderRadius: 100,
    borderWidth: 2,
    borderColor: '#00B8D4',
    opacity: 0.6
  },

  core: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: '#00E5FF',
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#00E5FF',
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.9,
    shadowRadius: 20,
    elevation: 20
  },

  coreText: {
    color: '#050B14',
    fontWeight: 'bold',
    letterSpacing: 2
  },

  stopButton: {
    position: 'absolute',
    bottom: 100
  },

  stopText: {
    color: '#FF5252',
    fontSize: 14,
    letterSpacing: 2
  },

  status: {
    position: 'absolute',
    bottom: 60,
    color: '#00E5FF',
    fontSize: 12,
    letterSpacing: 1
  }
});


function VoiceRoot() {
  const dispatch = useDispatch();
  const pulseAnim = useRef(new Animated.Value(1)).current;
  const rotateAnim = useRef(new Animated.Value(0)).current;
  const appState = useRef<AppStateStatus>(AppState.currentState);
  const voiceState = useSelector((state) => state.voice);
  const subscriptionRef = useRef(null);
  const wakeWordSubscription = null;

  async function requestContactsPermission() {
    if (Platform.OS !== 'android') return true;

    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_CONTACTS,
      {
        title: 'Contacts Access',
        message: 'Jarvis needs access to your contacts to make calls',
        buttonPositive: 'Allow',
        buttonNegative: 'Deny',
      }
    );

    return granted === PermissionsAndroid.RESULTS.GRANTED;
  }

  async function requestCallPermission() {
    if (Platform.OS !== 'android') return true;

    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.CALL_PHONE,
      {
        title: 'Contacts Access',
        message: 'Jarvis needs access to your contacts to make calls',
        buttonPositive: 'Allow',
        buttonNegative: 'Deny',
      }
    );

    return granted === PermissionsAndroid.RESULTS.GRANTED;
  }

  async function requestMicPermission() {
    if (Platform.OS === 'android') {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.RECORD_AUDIO
      );

      console.log('Mic permission:', granted);

      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        console.log('allowed===')
        subscriptionRef.current = voiceEmitter.addListener(
          'onSpeechResult',
          event => {
            console.log(event.text, 'text=====')
            dispatch(speechResult(event.text));
            const intent = parseIntent(event.text);
            console.log(intent, 'rintent')
            dispatch(intentDetected(intent));
          }
        );
      }
    }
  }
  const startWakeWord = async () => {
    if (Platform.OS === 'android') {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
        {
          title: "Microphone Permission",
          message: "This app needs microphone access to listen for 'Hey Jarvis'",
          buttonNeutral: "Ask Me Later",
          buttonNegative: "Cancel",
          buttonPositive: "OK"
        }
      );
      if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
        Alert.alert("Permission denied");
        return;
      }
    }

    try {
      await WakeWord.startListening();
      Alert.alert("Listening", "Say 'Hey Jarvis'...");
    } catch (err) {
      console.error("Failed to start listening", err);
    }
  };

  const startPulse = () => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulseAnim, {
          toValue: 1.2,
          duration: 1200,
          useNativeDriver: true
        }),
        Animated.timing(pulseAnim, {
          toValue: 1,
          duration: 1200,
          useNativeDriver: true
        })
      ])
    ).start();
  };

  const startRotation = () => {
    Animated.loop(
      Animated.timing(rotateAnim, {
        toValue: 1,
        duration: 6000,
        easing: Easing.linear,
        useNativeDriver: true
      })
    ).start();
  };

  const rotation = rotateAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg']
  });

  useEffect(() => {
    requestMicPermission();
    requestContactsPermission();
    requestCallPermission();
    startPulse();
    startRotation();

    // ✅ START SERVICE WHILE APP IS FOREGROUND
    if (AppState.currentState === 'active') {
      console.log('Starting Jarvis service while app is active');
      JarvisModule.startJarvisOverlay();
    }

    const subscription = AppState.addEventListener(
      'change',
      nextAppState => {
        console.log('AppState changed to:', nextAppState);
        appState.current = nextAppState;
      }
    );

    return () => {
      subscription.remove();
    };
  }, []);


  return (
    <View style={styles.container}>
      <Text style={styles.title}>J.A.R.V.I.S</Text>

      <Animated.View
        style={[
          styles.outerRing,
          { transform: [{ rotate: rotation }] }
        ]}
      />

      <Animated.View
        style={[
          styles.middleRing,
          { transform: [{ scale: pulseAnim }] }
        ]}
      />

      <TouchableOpacity
        activeOpacity={0.7}
        onPress={() => {
          dispatch(speechStart());
          // interpret('Can you please call my Jaan?')
          VoiceModule.startListening();
        }}
      >
        <View style={styles.core}>
          <Text style={styles.coreText}>SPEAK</Text>
        </View>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.stopButton}
        onPress={() => VoiceModule.stopListening()}
      >
        <Text style={styles.stopText}>STOP</Text>
      </TouchableOpacity>

      <Text style={styles.status}>Awaiting Command...</Text>
    </View>
  );
}

export default VoiceRoot;
