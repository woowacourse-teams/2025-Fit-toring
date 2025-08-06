import { ID } from './id';
import { PASSWORD } from './password';

export const ERROR_MESSAGE = {
  INVALID_PASSWORD_LENGTH: `비밀번호는 ${PASSWORD.MIN_LENGTH}자 이상 ${PASSWORD.MAX_LENGTH}자 미만으로 입력해주세요.`,
  NOT_MATCH: '비밀번호가 일치하지 않습니다.',
  INVALID_ID_LENGTH: `아이디는 ${ID.MIN_LENGTH}자 이상 ${ID.MAX_LENGTH}자 미만으로 입력해주세요.`,
  DUPLICATE_USER_ID: '이미 사용 중인 아이디입니다.',
  INVALID_USER_ID_CHARACTERS: '아이디는 영문과 숫자만 입력할 수 있습니다.',
  INVALID_VERIFICATION_CODE: '인증번호 6자를 입력해주세요.',
};
