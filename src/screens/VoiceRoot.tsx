import { useEffect, useRef } from 'react';
import { Text, TouchableOpacity, PermissionsAndroid, Platform } from 'react-native';
import { NativeModules, NativeEventEmitter } from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { intentDetected, speechResult, speechStart } from '../redux/voice/actions';
import { parseIntent } from '../redux/voice/functions';

const { VoiceModule } = NativeModules;
const voiceEmitter = new NativeEventEmitter(VoiceModule);

function VoiceRoot() {
  const dispatch = useDispatch();
  const voiceState = useSelector((state) => state['voice']);
  const subscriptionRef = useRef(null);

  async function requestMicPermission() {
    if (Platform.OS === 'android') {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.RECORD_AUDIO
      );

      console.log('Mic permission:', granted);

      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        subscriptionRef.current = voiceEmitter.addListener(
          'onSpeechResult',
          event => {
            dispatch(speechResult(event.text));
            const intent = parseIntent(event.text);
            dispatch(intentDetected(intent));
          }
        );
      }
    }
  }

  useEffect(() => {
    requestMicPermission();

    return () => {
      subscriptionRef.current?.remove();
    };
  }, []);

  return (
    <>
      <TouchableOpacity
        style={{ position: 'absolute', top: 100, right: 100 }}
        onPress={() => {
          console.log('Button pressed');
          dispatch(speechStart());
          VoiceModule.startListening();
        }}
      >
        <Text style={{ color: '#fff' }}>Voice Emit</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={{ position: 'absolute', top: 150, right: 100 }}
        onPress={() => {
          VoiceModule.stopListening();
        }}
      >
        <Text style={{ color: '#fff' }}>Stop Listening</Text>
      </TouchableOpacity>
    </>
  );
}

export default VoiceRoot;
