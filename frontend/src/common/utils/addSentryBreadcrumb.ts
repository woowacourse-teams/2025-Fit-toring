import * as Sentry from '@sentry/react';

interface AddSentryBreadcrumbParams {
  category: string;
  message: string;
  level?: Sentry.SeverityLevel;
  data?: Record<string, unknown>;
}

export const addSentryBreadcrumb = ({
  category,
  message,
  level = 'info',
  data,
}: AddSentryBreadcrumbParams) => {
  Sentry.addBreadcrumb({
    category,
    message,
    level,
    data,
  });
};
