const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const DotenvWebpackPlugin = require('dotenv-webpack');
const { sentryWebpackPlugin } = require('@sentry/webpack-plugin');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        sentryVendor: {
          test: /[\\/]node_modules[\\/](@sentry|@sentry-internal)[\\/]/,
          name: 'vendor-sentry',
          chunks: 'all',
          priority: 10,
        },
        reactVendor: {
          test: /[\\/]node_modules[\\/](react|react-dom|react-router|react-router-dom)[\\/]/,
          name: 'vendor-react',
          chunks: 'all',
          priority: 10,
        },
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendor-else',
          chunks: 'initial',
          priority: -10,
        },
      },
    },
  },
  plugins: [
    new DotenvWebpackPlugin({
      path: path.resolve(__dirname, '.env.prod'),
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'public/robots.prod.txt',
          to: 'robots.txt',
        },
      ],
    }),
    sentryWebpackPlugin({
      org: 'fittoring',
      project: 'production',
      authToken: process.env.SENTRY_AUTH_TOKEN,
    }),
  ],
});
