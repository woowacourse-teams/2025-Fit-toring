import * as Sentry from '@sentry/react';

interface CaptureSentryErrorParams {
  error: unknown;
  level: Sentry.SeverityLevel;
  feature: string;
  step: string;
  extras?: Record<string, unknown>;
}

export const captureSentryError = ({
  error,
  level,
  feature,
  step,
  extras,
}: CaptureSentryErrorParams) => {
  Sentry.withScope((scope) => {
    scope.setTag('navigator_online', navigator.onLine);

    if (extras) {
      Object.keys(extras).forEach((key) => {
        scope.setExtra(key, extras[key]);
      });
    }

    Sentry.captureException(error, {
      level,
      tags: {
        feature,
        step,
      },
    });
  });
};
