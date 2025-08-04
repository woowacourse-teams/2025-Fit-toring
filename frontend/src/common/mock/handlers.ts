import { authCodeHandler } from './authCode/authCode';
import { authCodeVerifyHandler } from './authCodeVerify/authCodeVerify';
import { mentoringHandler } from './mentoring/handlers';
import { signupHandler } from './signup/signup';
import { validateIdHandler } from './validateId/validateId';

export const handlers = [
  ...mentoringHandler,
  ...validateIdHandler,
  ...authCodeHandler,
  ...authCodeVerifyHandler,
  ...signupHandler,
];
