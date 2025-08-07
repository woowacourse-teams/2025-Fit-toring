import { INTRODUCE, INTRODUCE_ERROR_MESSAGE } from '../constants/introduce';

export const introduceValidator = (introduction: string): string => {
  if (introduction.length > INTRODUCE.MAX) {
    return INTRODUCE_ERROR_MESSAGE.INTRODUCE_MAX_LENGTH;
  }
  return '';
};
