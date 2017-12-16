import Urls from '../utils/Urls'
import RestApi from '../utils/RestApi'
import { responding, responding2, CB_NO_OP, DEFAULT_HEADERS } from '../utils/RestUtils'
import appConf from '../../config/sk2/app-conf'

const CTX = appConf.apis.herd.url

const ajaxList = url => (params, callback) => {
  let realUrl = Urls.buildQueryUrl(url, params)
  fetch(realUrl).then(r => r.json()).then(callback)
}
const ajaxActOnRepo = action => (params, okayCallback, failCallback) => {
  params = params || {}
  params['action'] = action
  const finalUrl = Urls.buildQueryUrl(`${CTX}/advanced/v1`, params)
  fetch(finalUrl, {
    method: 'POST',
    headers: DEFAULT_HEADERS,
    body: null
  })
    .then(responding(okayCallback, failCallback))
}

const getUrlByHash = function (hash, cacheCategory) {
  return CTX + '/file/' + hash + '.jpg?cache=' + cacheCategory
}

const listRepos = ajaxList(CTX + '/repos')
const listMedias = ajaxList(CTX + '/medias')
const listImageMedias = ajaxList(CTX + '/imageMedias')
const countImageMediasByYear = ajaxList(CTX + '/imageMedias/countByYear')
const countImageMediasByMonth = ajaxList(CTX + '/imageMedias/countByMonth')
const repoRestApi = new RestApi(CTX + '/repos/', '{name}')

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
const batchSync = {
  mediaPaths: {
    clear: clear,
    sync: sync,
    st2: st2
  }

}
// const repoSync = ajaxActOnRepo('sync')
// const repoSyncPath = ajaxActOnRepo('sync.path')
// const repoSyncInfoBrief = ajaxActOnRepo('sync.info.brief')
// const repoSyncInfoSenior = ajaxActOnRepo('sync.info.senior')
// const repoClear = ajaxActOnRepo('clear')
// const repoClearPath = ajaxActOnRepo('clear.path')
// const repoClearInfoBrief = ajaxActOnRepo('clear.info.brief')
// const repoClearInfoSenior = ajaxActOnRepo('clear.info.senior')
// const convert2jpg1Kq5 = ajaxActOnRepo('convert2jpg.1Kq5')
// const convert2jpg2Kq7 = ajaxActOnRepo('convert2jpg.2Kq7')

export default {
  listRepos,
  listMedias,
  listImageMedias,
  getUrlByHash,
  countImageMediasByYear,
  countImageMediasByMonth,
  repoRestApi,
  ajaxActOnRepo,
  batchSync
  // repoSync,
  // repoSyncPath,
  // repoSyncInfoBrief,
  // repoSyncInfoSenior,
  // repoClear,
  // repoClearPath,
  // repoClearInfoBrief,
  // repoClearInfoSenior,
  // convert2jpg1Kq5
}
