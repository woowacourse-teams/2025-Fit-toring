import { NAME } from './name';

export const ERROR_MESSAGE = {
  INVALID_NAME_LENGTH: `이름은 ${NAME.MIN_LENGTH}자 이상 ${NAME.MAX_LENGTH}자 미만으로 입력해주세요.`,
  INVALID_NAME_CHARACTERS: '이름은 한글만 입력할 수 있습니다.',
  INVALID_PHONE_NUMBER_LENGTH: '전화번호 11자리를 입력해주세요.',
};
