const conf0 = require('./app-conf-default')
const _ = require('lodash')
const fucker = require('./cli-arg-fucker')
// conf0= _.cloneDeep(conf0)
let conf1 = {
  port: process.env.port,
  apis: {
    herd: {
      url: fucker.unwrapArg(process.env.herdApiUrl)
    },
    dict: {
      url: fucker.unwrapArg(process.env.dictApiUrl)
    }
  }
}
module.exports = _.merge(conf0, conf1)

