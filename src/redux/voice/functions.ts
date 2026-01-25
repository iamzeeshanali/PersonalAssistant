export function parseIntent(text: any) {
    text = text.toLowerCase();

    if (text.includes('call')) {
        return { intent: 'CALL' };
    }

    if (text.includes('whatsapp')) {
        return { intent: 'OPEN_WHATSAPP' };
    }

    if (text.includes('brightness')) {
        return { intent: 'SET_BRIGHTNESS' };
    }

    return { intent: 'UNKNOWN' };
}
