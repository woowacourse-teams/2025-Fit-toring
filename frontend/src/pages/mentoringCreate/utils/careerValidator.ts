import { CAREER, ERROR_MESSAGE } from '../constants/career';

export const careerValidator = (career: number): string => {
  if (career < CAREER.MIN) {
    return ERROR_MESSAGE.CAREER_INVALID;
  }
  if (career > CAREER.MAX) {
    return ERROR_MESSAGE.CAREER_MAX;
  }
  if (!Number.isInteger(career)) {
    return ERROR_MESSAGE.CAREER_NOT_INTEGER;
  }
  return '';
};
