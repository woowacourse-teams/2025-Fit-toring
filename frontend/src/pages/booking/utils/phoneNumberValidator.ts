import { ERROR_MESSAGE } from '../constants/errorMessage';

const validatePhoneNumberLength = (phoneNumber: string) => {
  if (phoneNumber === '') {
    return false;
  }

  const digits = phoneNumber.replace(/\D/g, '');
  return digits.length !== 11;
};

export const getPhoneNumberErrorMessage = (phoneNumber: string) => {
  if (validatePhoneNumberLength(phoneNumber)) {
    return ERROR_MESSAGE.INVALID_PHONE_NUMBER_LENGTH;
  }

  return '';
};
