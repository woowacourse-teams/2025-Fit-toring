import { CAREER, ERROR_MESSAGE } from '../constants/career';

export const careerValidator = (career: number | null): string => {
  if (career === null) {
    return '';
  }
  if (career < CAREER.MIN) {
    return ERROR_MESSAGE.CAREER_INVALID;
  }
  if (!Number.isInteger(career)) {
    return ERROR_MESSAGE.CAREER_NOT_INTEGER;
  }
  return '';
};
