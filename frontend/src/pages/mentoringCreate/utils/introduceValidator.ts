import { ERROR_MESSAGE, INTRODUCE } from '../constants/introduce';

export const introduceValidator = (introduction: string): string => {
  if (introduction.length > INTRODUCE.MAX) {
    return ERROR_MESSAGE.INTRODUCE_MAX_LENGTH;
  }
  return '';
};
