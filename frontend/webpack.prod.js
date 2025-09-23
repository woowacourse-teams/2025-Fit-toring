const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const { sentryWebpackPlugin } = require('@sentry/webpack-plugin');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  optimization: {
    splitChunks: {
      chunks: 'all',
    },
  },
  plugins: [
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
