import RestApi from '../utils/RestApi'
import appConf from '../../config/sk2/app-conf'
import { responding, respondingAf, CB_NO_OP, DEFAULT_HEADERS } from '../utils/RestUtils'

const CTX = appConf.apis.dict.url

const words = new RestApi(CTX + '/words/', '{text}')
const basicRelations = new RestApi(CTX + '/relations/basic/', 'src={src};attr={attr};no={no}')

// TODO need  refactor & simplify
const x1Relations = new RestApi(CTX + '/relations/x1/', 'src={src};attr={attr};no={no}')
x1Relations.batchCreate = function (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
  fetch(CTX + '/relations/x1/?batch=true', {
    method: 'POST',
    headers: DEFAULT_HEADERS,
    body: JSON.stringify(params)
  })
    .then(respondingAf(okayCallback, failCallback))
}
export default {words, basicRelations, x1Relations}

