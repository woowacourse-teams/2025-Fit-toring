import { ERROR_MESSAGE } from '../constants/errorMessage';

export const validateTextarea = (content: string): string => {
  if (content.length > 5000) {
    return ERROR_MESSAGE.CONTENT_LENGTH_TOO_LONG;
  }
  return '';
};
