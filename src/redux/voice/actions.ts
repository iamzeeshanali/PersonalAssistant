import { INTENT_DETECTED, SPEECH_RESULT, SPEECH_START } from "./actionTypes";

export const speechStart = () => ({ type: SPEECH_START });
export const speechResult = (text: any) => ({
  type: SPEECH_RESULT,
  payload: text,
});
export const intentDetected = (intent: any) => ({
  type: INTENT_DETECTED,
  payload: intent,
});
