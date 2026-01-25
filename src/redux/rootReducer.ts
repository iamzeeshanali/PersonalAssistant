import { combineReducers } from 'redux';
import voiceReducer from './voice/reducer';

export default combineReducers({
  voice: voiceReducer
});
