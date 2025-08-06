import { PRICE } from './price';

export const ERROR_MESSAGE = {
  PRICE_INVALID: `가격은 ${PRICE.MIN} 이상의 숫자여야 합니다.`,
  PRICE_NOT_INTEGER: '숫자를 입력해주세요.',
  PRICE_TOO_HIGH: `가격은 ${PRICE.MAX} 이하로 설정해주세요.`,
};
