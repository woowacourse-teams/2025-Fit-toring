import { authCodeHandler } from './authCode/authCode';
import { authCodeVerifyHandler } from './authCodeVerify/authCodeVerify';
import { commonHandler } from './common/handlers';
import { createdMentoringHandler } from './createdMentoring/handlers';
import { loginHandler } from './login/handler';
import { membersHandler } from './members/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { signupHandler } from './signup/signup';
import { validateIdHandler } from './validateId/validateId';

export const handlers = [
  ...mentoringHandler,
  ...validateIdHandler,
  ...authCodeHandler,
  ...authCodeVerifyHandler,
  ...signupHandler,
  ...mentoringHandler,
  ...commonHandler,
  ...membersHandler,
  ...loginHandler,
  ...createdMentoringHandler,
];
