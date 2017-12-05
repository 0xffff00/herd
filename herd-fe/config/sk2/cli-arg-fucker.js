// webpack.DefinePlugin屏蔽自定义命令行参数的坑：
// 想要拿到npm run --arg1=xxx的命令行参数arg1,
// 在src的js调用无论是require('../../config')还是process.env.npm_config_*都是没有用的
// 在webpack.*.conf.js里调用，src中process.env才有值，因为process.env被webpack.DefinePlugin重载了。

const wrapArg = function (x) {
  if (x === null || x === undefined) {
    return x
  }
  return '"' + x + '"'
}
const unwrapArg = function (x) {
  if (x !== null && (typeof x === 'string') && x.length > 1 && x[0] === '"' && x[x.length - 1] === '"') {
    return x.slice(1, -1)
  }
  return x
}
/**
 * env will be changed
 * @param env
 */
exports.addCustomCliArgsToEnv = function (env) {
  env.port = process.env.npm_config_port
  env.herdApiUrl = wrapArg(process.env.npm_config_herdApiUrl)
  env.dictApiUrl = wrapArg(process.env.npm_config_dictApiUrl)

  console.log('------ env (after custom CLI args added) -------')
  console.log(JSON.stringify(env))
}
exports.unwrapArg = unwrapArg
