import { createdMentoringHandler } from '../../pages/createdMentoring/mock/handlers';
import { editProfileHandlers } from '../../pages/editProfile/mock/handler';
import { loginHandler } from '../../pages/login/mock/handler';
import { mentoringCreateHandler } from '../../pages/mentoringCreate/mock/handlers';
import { mentoringUpdateHandler } from '../../pages/mentoringUpdate/mock/handlers';
import { signupHandler } from '../../pages/signup/mock/signup/handlers';
import { validateIdHandler } from '../../pages/signup/mock/validateId/handler';

import { authCodeHandler } from './authCode/authCode';
import { authCodeVerifyHandler } from './authCodeVerify/authCodeVerify';
import { commonHandler } from './common/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { participatedMentoringHandler } from './participatedMentoring/handler';

export const handlers = [
  ...mentoringHandler,
  ...validateIdHandler,
  ...authCodeHandler,
  ...authCodeVerifyHandler,
  ...signupHandler,
  ...mentoringHandler,
  ...commonHandler,
  ...loginHandler,
  ...createdMentoringHandler,
  ...mentoringCreateHandler,
  ...mentoringUpdateHandler,
  ...participatedMentoringHandler,
  ...editProfileHandlers,
];
