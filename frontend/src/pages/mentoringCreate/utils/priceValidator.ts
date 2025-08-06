import { ERROR_MESSAGE } from '../constants/errorMessage';
import { PRICE } from '../constants/price';

export const priceValidator = (price: number | null): string => {
  if (price === null || price < PRICE.MIN) {
    return ERROR_MESSAGE.PRICE_INVALID;
  }
  if (!Number.isInteger(price)) {
    return ERROR_MESSAGE.PRICE_NOT_INTEGER;
  }
  if (price > PRICE.MAX) {
    return ERROR_MESSAGE.PRICE_TOO_HIGH;
  }
  return '';
};
