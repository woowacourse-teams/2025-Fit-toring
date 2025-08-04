import { mentoringHandler } from './mentoring/handlers';
import { validateIdHandler } from './validateId/validateId';

export const handlers = [...mentoringHandler, ...validateIdHandler];
