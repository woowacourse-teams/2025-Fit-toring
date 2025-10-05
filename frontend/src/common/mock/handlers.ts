import { authCodeHandler } from './authCode/authCode';
import { authCodeVerifyHandler } from './authCodeVerify/authCodeVerify';
import { chatRoomHandler } from './chatrooms/handlers';
import { commonHandler } from './common/handlers';
import { createdMentoringHandler } from './createdMentoring/handlers';
import { editProfileHandlers } from './editProfile/handler';
import { loginHandler } from './login/handler';
import { membersHandler } from './members/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { mentoringDetailHandler } from './mentoringDetail/handler';
import { participatedMentoringHandler } from './participatedMentoring/handler';
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
  ...mentoringDetailHandler,
  ...participatedMentoringHandler,
  ...editProfileHandlers,
  ...chatRoomHandler,
];
