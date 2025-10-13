import { getSpecialties } from '../../../common/mock/getSpecialties/handlers';
import { getUserInfoSummary } from '../../../common/mock/getUserInfoSummary/handler';

export const mentoringUpdateHandler = [getUserInfoSummary, getSpecialties];
