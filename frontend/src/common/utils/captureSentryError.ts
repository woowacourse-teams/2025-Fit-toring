import * as Sentry from '@sentry/react';

interface CaptureSentryErrorParams {
  error: unknown;
  level: 'warning' | 'error';
  feature: string;
  step: string;
}

export const captureSentryError = ({
  error,
  level,
  feature,
  step,
}: CaptureSentryErrorParams) => {
  Sentry.captureException(error, {
    level,
    tags: {
      feature,
      step,
    },
  });
};
