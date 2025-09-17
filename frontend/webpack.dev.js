const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'eval-source-map',
  devServer: {
    static: {
      directory: path.join(__dirname, 'dist'),
    },
    allowedHosts: 'all', // ✅ 모든 호스트 허용

    port: 3000,
    open: true,
    hot: true,
    historyApiFallback: true,
    client: {
      overlay: true,
    },
  },
  plugins: [
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
