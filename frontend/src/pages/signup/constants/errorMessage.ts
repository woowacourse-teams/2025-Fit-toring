import { PASSWORD } from './password';

export const ERROR_MESSAGE = {
  INVALID_PASSWORD_LENGTH: `비밀번호는 ${PASSWORD.MIN_LENGTH}자 이상 ${PASSWORD.MAX_LENGTH}자 미만으로 입력해주세요.`,
  NOT_MATCH: '비밀번호가 일치하지 않습니다.',
};
