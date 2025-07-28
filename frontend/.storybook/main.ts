import path from 'path';

import DotenvWebpackPlugin from 'dotenv-webpack';

import type { StorybookConfig } from '@storybook/react-webpack5';

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-webpack5-compiler-swc', '@storybook/addon-docs'],
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  webpackFinal: async (config) => {
    config.plugins = config.plugins || [];

    config.plugins.push(
      new DotenvWebpackPlugin({
        path: path.resolve(__dirname, '../.env'),
        systemvars: true,
        safe: false,
      }),
    );

    return config;
  },
};
export default config;
