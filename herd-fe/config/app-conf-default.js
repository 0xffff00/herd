/**
 * designed by HZK
 * 实在不会改vue-cli自动生成的webpack-merge相关js,一用就报错Unexpected token :
 */
module.exports = {
  port: 8981,
  apis: {
    herd: {
      url: 'http://localhost:8980'
    },
    dict: {
      url: 'http://localhost:8920'
    }
  }
}
