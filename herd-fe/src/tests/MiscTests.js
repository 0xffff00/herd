import herdService from '../apis/HerdApi'
import RestApi from '../utils/RestApi'

console.log(herdService.repoCrudApi)
const restApi = new RestApi('http://xxx.com/api/users/', '?id={id}')
console.log(restApi.buildSoloUrl({id: 233}))

