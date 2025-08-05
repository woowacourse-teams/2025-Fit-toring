import { commonHandler } from './common/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { myProfileHandler } from './myProfile/handlers';

export const handlers = [
  ...mentoringHandler,
  ...myProfileHandler,
  ...commonHandler,
];
