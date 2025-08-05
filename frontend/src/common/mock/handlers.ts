import { specialtiesHandler } from './common/handlers';
import { membersHandler } from './members/handlers';
import { mentoringHandler } from './mentoring/handlers';
import { myProfileHandler } from './myProfile/handlers';

export const handlers = [
  ...mentoringHandler,
  ...myProfileHandler,
  ...specialtiesHandler,
  ...membersHandler,
];
