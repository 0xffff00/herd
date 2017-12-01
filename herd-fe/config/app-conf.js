const conf0 = require('./app-conf-default')
const _ = require('lodash')

// conf0= _.cloneDeep(conf0)
let conf1 = {
  port: process.env.npm_config_port,
  apis: {
    herd: {
      url: process.env.npm_config_herdApiUrl
    },
    dict: {
      url: process.env.npm_config_dictApiUrl
    }
  }
}
module.exports = _.merge(conf0, conf1)

