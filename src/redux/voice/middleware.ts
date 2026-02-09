import { Linking, NativeModules, Platform } from "react-native";
import { INTENT_DETECTED } from "./actionTypes";

const { OpenApp, CallModule } = NativeModules;

async function openWhatsApp() {
    OpenApp.open('com.whatsapp');
}

function callNumber(phoneNumber) {
    console.log(phoneNumber, 'phoneNumber=======')
    CallModule.call(phoneNumber)
}

export const voiceMiddleware = store => next => action => {
    console.log(action.type === INTENT_DETECTED, 'action.type === INTENT_DETECTED')
    if (action.type === INTENT_DETECTED) {
        const intent = action.payload;

        console.log(intent.intent)
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
