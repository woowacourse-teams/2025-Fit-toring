import { CAREER } from './career';
import { INTRODUCE } from './introduce';
import { PRICE } from './price';

export const ERROR_MESSAGE = {
  PRICE_INVALID: `가격은 ${PRICE.MIN} 이상의 숫자여야 합니다.`,
  PRICE_NOT_INTEGER: '숫자를 입력해주세요.',
  PRICE_TOO_HIGH: `가격은 ${PRICE.MAX} 이하로 설정해주세요.`,
  INTRODUCE_MAX_LENGTH: `소개는 최대 ${INTRODUCE.MAX}자까지 입력할 수 있습니다.`,
  CAREER_INVALID: `경력은 ${CAREER.MIN} 이상의 숫자여야 합니다.`,
  CAREER_NOT_INTEGER: '경력은 숫자로 입력해주세요.',
};
