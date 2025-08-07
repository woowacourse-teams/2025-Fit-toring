import { ERROR_MESSAGE, INTRODUCE } from '../constants/introduce';

export const introduceValidator = (introduction: string | null): string => {
  if (introduction === null) {
    return '';
  }
  if (introduction.length > INTRODUCE.MAX) {
    return ERROR_MESSAGE.INTRODUCE_MAX_LENGTH;
  }
  return '';
};
