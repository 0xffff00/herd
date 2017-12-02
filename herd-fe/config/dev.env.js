let merge = require('webpack-merge')
let prodEnv = require('./prod.env')
/*
can not use 'process.env.npm_config_herdApiUrl' here, which throws 'Uncaught SyntaxError: Unexpected token :'
 */
module.exports = merge(prodEnv, {
  NODE_ENV: '"development"'
})
