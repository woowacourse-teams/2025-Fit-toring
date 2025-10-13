import { postAuthCode } from '../../../common/mock/authCode/handlers';
import { postAuthCodeVerify } from '../../../common/mock/authCodeVerify/handlers';

export const identityVerificationHandlers = [postAuthCode, postAuthCodeVerify];
