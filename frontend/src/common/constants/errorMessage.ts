import { ID } from './id';
import { NAME } from './name';
import { PASSWORD } from './password';

export const ERROR_MESSAGE = {
  INVALID_NAME_LENGTH: `이름은 ${NAME.MIN_LENGTH}자 이상 ${NAME.MAX_LENGTH}자 미만으로 입력해주세요.`,
  INVALID_NAME_CHARACTERS: '이름은 한글만 입력할 수 있습니다.',
  INVALID_PHONE_NUMBER_LENGTH: '전화번호 11자리를 입력해주세요.',
  INVALID_PASSWORD_LENGTH: `비밀번호는 ${PASSWORD.MIN_LENGTH}자 이상 ${PASSWORD.MAX_LENGTH}자 미만으로 입력해주세요.`,
  NOT_MATCH: '비밀번호가 일치하지 않습니다.',
  INVALID_ID_LENGTH: `아이디는 ${ID.MIN_LENGTH}자 이상 ${ID.MAX_LENGTH}자 미만으로 입력해주세요.`,
  INVALID_USER_ID_CHARACTERS: '아이디는 영문과 숫자만 입력할 수 있습니다.',
  INVALID_CHAT_URL: '올바른 카카오톡 오픈 채팅 주소를 입력해주세요.',
  CONTENT_LENGTH_TOO_LONG: `상세 설명은 5,000자 이하로 작성해주세요.`,
  DUPLICATE_USER_ID: '이미 사용 중인 아이디입니다.',
  INVALID_VERIFICATION_CODE: '인증번호 6자를 입력해주세요.',
};
