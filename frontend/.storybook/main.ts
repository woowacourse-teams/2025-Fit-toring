import type { StorybookConfig } from '@storybook/react-webpack5';

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-webpack5-compiler-swc', '@storybook/addon-docs'],
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  env: (config) => ({
    ...config,
    API_BASE_URL: process.env.API_BASE_URL || 'http://localhost:6006',
    SENTRY_DSN: process.env.SENTRY_DSN || '',
    NODE_ENV: process.env.NODE_ENV || 'development',
    GOOGLE_ANALYTICS_ID: process.env.GOOGLE_ANALYTICS_ID || '',
    KAKAO_REST_API_KEY: process.env.KAKAO_REST_API_KEY || '',
    KAKAO_REDIRECT_URL: process.env.KAKAO_REDIRECT_URL || '',
  }),
};
export default config;
