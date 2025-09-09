import { ERROR_MESSAGE } from '../constants/errorMessage';

export const validateChatUrl = (url: string): string => {
  const pattern = /^https:\/\/open\.kakao\.com\/.+$/;
  if (url === undefined) {
    return '';
  }
  if (!pattern.test(url)) {
    return ERROR_MESSAGE.INVALID_CHAT_URL;
  }
  return '';
};
