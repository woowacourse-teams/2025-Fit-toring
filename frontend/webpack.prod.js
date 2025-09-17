const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'hidden-source-map',
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
  ],
});
