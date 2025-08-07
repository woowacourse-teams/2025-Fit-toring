import { PRICE_ERROR_MESSAGE, PRICE } from '../constants/price';

export const priceValidator = (price: number): string => {
  if (price < PRICE.MIN) {
    return PRICE_ERROR_MESSAGE.PRICE_INVALID;
  }
  if (!Number.isInteger(price)) {
    return PRICE_ERROR_MESSAGE.PRICE_NOT_INTEGER;
  }
  if (price > PRICE.MAX) {
    return PRICE_ERROR_MESSAGE.PRICE_TOO_HIGH;
  }
  return '';
};
