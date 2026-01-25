import { createStore, applyMiddleware } from 'redux';
import rootReducer from './rootReducer';
import { thunk } from 'redux-thunk';
import { voiceMiddleware } from './voice/middleware';

const middlewares = [thunk, voiceMiddleware];

export const store = createStore(
  rootReducer,
  applyMiddleware(...middlewares)
);

export type RootState = ReturnType<typeof rootReducer>;
