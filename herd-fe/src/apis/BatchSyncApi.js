import Urls from '../utils/Urls'
import RestApi from '../utils/RestApi'
import { responding, responding2, CB_NO_OP, DEFAULT_HEADERS } from '../utils/RestUtils'
import appConf from '../../config/sk2/app-conf'
const CTX = appConf.apis.herd.url

let clear = (repoName, okayCallback, failCallback) => {
  fetch(CTX + '/batch-sync/media-paths?repoName=' + repoName, {method: 'DELETE', headers: DEFAULT_HEADERS})
    .then(responding(okayCallback, failCallback))
}
let sync = (repoName, okayCallback, failCallback) => {
  fetch(CTX + '/batch-sync/media-paths?repoName=' + repoName, {method: 'PUT', headers: DEFAULT_HEADERS})
    .then(responding(okayCallback, failCallback))
}
let st2 = (okayCallback, failCallback) => {
  fetch(CTX + '/batch-sync/media-paths/status/flatten', {method: 'GET', headers: DEFAULT_HEADERS})
    .then(responding(okayCallback, failCallback))
}


