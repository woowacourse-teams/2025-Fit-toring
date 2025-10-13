// import webpack from 'webpack';

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
  // webpackFinal: async (config) => {
  //   config.plugins = config.plugins || [];
  //   config.plugins.push(
  //     new webpack.DefinePlugin({
  //       'process.env': {
  //         NODE_ENV: JSON.stringify(process.env.NODE_ENV || 'development'),
  //         SENTRY_DSN: JSON.stringify(process.env.SENTRY_DSN || ''),
  //         API_BASE_URL: JSON.stringify(
  //           process.env.API_BASE_URL || 'http://localhost:6006',
  //         ),
  //         GOOGLE_ANALYTICS_ID: JSON.stringify(
  //           process.env.GOOGLE_ANALYTICS_ID || '',
  //         ),
  //       },
  //     }),
  //   );

  //   return config;
  // },
};
export default config;
