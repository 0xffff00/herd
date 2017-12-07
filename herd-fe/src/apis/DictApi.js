import RestApi from '../utils/RestApi'
import appConf from '../../config/sk2/app-conf'

const CTX = appConf.apis.dict.url

const words = new RestApi(CTX + '/words/', '{text}')
const basicRelations = new RestApi(CTX + '/relations/basic/', 'src={src};attr={attr};no={no}')

const responding2 = (okayCallback, failCallback) => resp => {
  if (resp.ok) {
    const totalAffected = parseInt(resp.headers.get('X-Total-Affected'))
    okayCallback({status: resp.status, totalAffected})
  } else {
    resp.json().then(failCallback)
  }
}
// TODO need  refactor & simplify
const x1Relations = new RestApi(CTX + '/relations/x1/', 'src={src};attr={attr};no={no}')
x1Relations.batchCreate = function (params, okayCallback, failCallback) {
  fetch(CTX + '/relations/x1/?batch=true', {
    method: 'POST',
    headers: new Headers({
      'Accept': 'application/json, text/plain, */*',
      'Content-Type': 'application/json'
    }),
    body: JSON.stringify(params)
  })
    .then(responding2(okayCallback, failCallback))
}
export default {words, basicRelations, x1Relations}

