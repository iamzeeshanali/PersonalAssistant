import { INTENT_DETECTED, SPEECH_RESULT, SPEECH_START } from "./actionTypes";

const INITIAL_STATE = {
  listening: false,
  transcript: '',
  intent: null,
  status: 'idle', // idle | listening | executing
};

export default function voiceReducer(state = INITIAL_STATE, action: any) {
  switch (action.type) {
    case SPEECH_START:
      return { ...state, listening: true, status: 'listening' };

    case SPEECH_RESULT:
      return {
        ...state,
        transcript: action.payload,
        listening: false,
        status: 'idle',
      };

    case INTENT_DETECTED:
      return {
        ...state,
        intent: action.payload,
        status: 'executing',
      };

    default:
      return state;
  }
}

