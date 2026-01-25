import { Linking, NativeModules, Platform } from "react-native";
import { INTENT_DETECTED } from "./actionTypes";

const { OpenApp, CallModule } = NativeModules;

async function openWhatsApp() {
    OpenApp.open('com.whatsapp');
}

function callNumber(phoneNumber) {
    CallModule.call(phoneNumber)
}

export const voiceMiddleware = store => next => action => {
    if (action.type === INTENT_DETECTED) {
        const intent = action.payload;

        switch (intent.intent) {
            case 'CALL':
                callNumber('+917894765842');
                break;

            case 'OPEN_WHATSAPP':
                openWhatsApp()
                break;
        }
    }

    return next(action);
};
