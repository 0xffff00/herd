// test pass command line args
// > npm run learn3 --x1=233
// > npm run learn3 --port=1234 -env.port=1235 --env.xx1=222
// > node learn-pass-cli-arg.js --x1=233
// console.log('3js argv ======== ', require('yargs').argv)
console.log('3js process.argv=========', JSON.stringify(process.argv))
console.log('3js app-conf==========', require('../../config/sk2/app-conf'))
console.log('3js config=========', JSON.stringify(require('../../config')))

console.log('3js prod.env=========', JSON.stringify(require('../../config/prod.env')))
console.log('3js npm_config_x1========', process.env.npm_config_x1)
console.log('3js npm_package_config_x2========', process.env.npm_package_config_x2)
let x1 = process.env.npm_config_x1 + ',' + process.env.x222
module.exports = {x1}
