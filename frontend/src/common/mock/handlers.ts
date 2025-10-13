import { createdMentoringHandler } from '../../pages/createdMentoring/mock/handlers';
import { editProfileHandlers } from '../../pages/editProfile/mock/handler';
import { validateIdHandler } from '../../pages/signup/mock/validateId/handler';

import { authCodeHandler } from './authCode/authCode';
import { authCodeVerifyHandler } from './authCodeVerify/authCodeVerify';
import { commonHandler } from './common/handlers';
import { loginHandler } from './login/handler';
import { membersHandler } from './members/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { mentoringDetailHandler } from './mentoringDetail/handler';
import { participatedMentoringHandler } from './participatedMentoring/handler';
import { signupHandler } from './signup/signup';

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
  ...mentoringDetailHandler,
  ...participatedMentoringHandler,
  ...editProfileHandlers,
];
