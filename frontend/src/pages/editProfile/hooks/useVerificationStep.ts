import { useState } from 'react';

type VerificationStep = 'idle' | 'requested' | 'verified';
type FeatureType = 'signup' | 'editProfile';

const initialVerificationStep = {
  signup: 'idle',
  editProfile: 'verified',
} as const;

const useVerificationStep = (featureType: FeatureType) => {
  const [verificationStep, setVerificationStep] = useState<VerificationStep>(
    initialVerificationStep[featureType],
  );

  const reset = () => {
    setVerificationStep('idle');
  };

  const request = () => {
    setVerificationStep('requested');
  };

  const complete = () => {
    setVerificationStep('verified');
  };

  return { verificationStep, reset, request, complete };
};

export default useVerificationStep;
