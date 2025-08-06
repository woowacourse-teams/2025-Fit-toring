import { authCodeHandler } from './authCode/authCode';
import { authCodeVerifyHandler } from './authCodeVerify/authCodeVerify';
import { specialtiesHandler } from './common/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { myProfileHandler } from './myProfile/handlers';
import { signupHandler } from './signup/signup';
import { validateIdHandler } from './validateId/validateId';

export const handlers = [
  ...mentoringHandler,
  ...validateIdHandler,
  ...authCodeHandler,
  ...authCodeVerifyHandler,
  ...signupHandler,
  ...mentoringHandler,
  ...myProfileHandler,
  ...specialtiesHandler,
];
