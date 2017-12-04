// test pass command line args
console.log('3js argv ======== ', require('yargs').argv)
console.log('3js process.argv=========', JSON.stringify(process.argv))
console.log('3js app-conf==========', require('../../config/app-conf'))
console.log('3js config=========', JSON.stringify(require('../../config')))

console.log('3js prod.env=========', JSON.stringify(require('../../config/prod.env')))
console.log('3js npm_package_config_x2========', process.env.npm_package_config_x2)
