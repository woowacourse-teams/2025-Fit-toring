import { ERROR_MESSAGE } from '../../pages/booking/constants/errorMessage';

const VALID_PHONE_NUMBER_LENGTH = 11;

const validatePhoneNumberLength = (phoneNumber: string) => {
  if (phoneNumber === '') {
    return false;
  }

  const digits = phoneNumber.replace(/\D/g, '');
  return digits.length !== VALID_PHONE_NUMBER_LENGTH;
};

export const getPhoneNumberErrorMessage = (phoneNumber: string) => {
  if (validatePhoneNumberLength(phoneNumber)) {
    return ERROR_MESSAGE.INVALID_PHONE_NUMBER_LENGTH;
  }

  return '';
};
