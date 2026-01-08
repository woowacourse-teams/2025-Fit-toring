const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const DotenvWebpackPlugin = require('dotenv-webpack');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'eval-source-map',
  devServer: {
    static: {
      directory: path.resolve(__dirname, 'public'),
    },
  },
  plugins: [
    new DotenvWebpackPlugin({
      path: path.resolve(__dirname, '.env.dev'),
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'public/robots.dev.txt',
          to: 'robots.txt',
        },
      ],
    }),
  ],
});
