import { specialtiesHandler } from './common/handlers';
import { createdMentoringHandler } from './createdMentoring/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { myProfileHandler } from './myProfile/handlers';

export const handlers = [
  ...mentoringHandler,
  ...myProfileHandler,
  ...specialtiesHandler,
  ...createdMentoringHandler,
];
