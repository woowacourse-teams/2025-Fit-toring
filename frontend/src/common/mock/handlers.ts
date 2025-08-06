import { specialtiesHandler } from './common/handlers';
import { loginHandler } from './login/handler';
import { mentoringHandler } from './mentoring/handlers';
import { myProfileHandler } from './myProfile/handlers';

export const handlers = [
  ...mentoringHandler,
  ...myProfileHandler,
  ...specialtiesHandler,
  ...loginHandler,
];
