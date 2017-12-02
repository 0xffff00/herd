/**
 * designed by HZK
 * 实在不会该webpack-merge,一用就报错Unexpected token :
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
