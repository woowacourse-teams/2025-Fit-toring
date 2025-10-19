import { createdMentoringHandler } from '../../pages/createdMentoring/mock/handlers';
import { editProfileHandlers } from '../../pages/editProfile/mock/handler';
import { homeHandler } from '../../pages/home/mock/handlers';
import { identityVerificationHandlers } from '../../pages/identityVerification/mock/handlers';
import { loginHandler } from '../../pages/login/mock/handler';
import { mentoringCreateHandler } from '../../pages/mentoringCreate/mock/handlers';
import { mentoringUpdateHandler } from '../../pages/mentoringUpdate/mock/handlers';
import { participatedMentoringHandler } from '../../pages/participatedMentoring/mock/handler';
import { signupHandler } from '../../pages/signup/mock/handlers';

import { imageUploadHandler } from './imageUpload/handler';

export const handlers = [
  ...signupHandler,
  ...loginHandler,
  ...createdMentoringHandler,
  ...mentoringCreateHandler,
  ...mentoringUpdateHandler,
  ...participatedMentoringHandler,
  ...editProfileHandlers,
  ...identityVerificationHandlers,
  ...homeHandler,
  ...imageUploadHandler,
];
