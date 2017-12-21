import Urls from '../utils/Urls'
import RestApi from '../utils/RestApi'
import { responding, responding2, CB_NO_OP, DEFAULT_HEADERS } from '../utils/RestUtils'
import appConf from '../../config/sk2/app-conf'

const CTX = appConf.apis.herd.url

const ajaxList = url => (params, callback) => {
  let realUrl = Urls.appendQParams(url, params)
  fetch(realUrl).then(r => r.json()).then(callback)
}

const getUrlByHash = function (hash, cacheCategory) {
  return CTX + '/file/' + hash + '.jpg?cache=' + cacheCategory
}

const mediaRepos = new RestApi(CTX + '/media-repos/', '{name}')
const mediaFiles = new RestApi(CTX + '/media-files/', '{path}')
const imageInfos = new RestApi(CTX + '/image-infos/', '{hash}')
// const listRepos = ajaxList(CTX + '/repos')
// const listMedias = ajaxList(CTX + '/medias')
// const listImageMedias = ajaxList(CTX + '/image-infos')
imageInfos.countByYear = ajaxList(CTX + '/image-infos/countByYear')
imageInfos.countByMonth = ajaxList(CTX + '/image-infos/countByMonth')
imageInfos.countByDate = ajaxList(CTX + '/image-infos/countByDate')

const jobs = {
  batchSync: {},
  thumbnail: {},
  mediaRepos: {}
}
jobs.batchSync.start = (params, okayCallback, failCallback) => {
  new RestApi(CTX + '/jobs/batch-sync', '?repoName={repoName}')
    .httpPut(params, null, okayCallback, failCallback)
}
jobs.batchSync.status = (okayCallback, failCallback) => {
  new RestApi(CTX + '/jobs/batch-sync/status')
    .httpGet(null, okayCallback, failCallback)
}
jobs.thumbnail.status = (okayCallback, failCallback) => {
  new RestApi(CTX + '/jobs/image/thumbnails/status')
    .httpGet(null, okayCallback, failCallback)
}
jobs.mediaRepos.truncate = (params, okayCallback, failCallback) => {
  new RestApi(CTX + '/jobs/media-repos', '?repoName={repoName}')
    .httpDelete(params, okayCallback, failCallback)
}

export default {
  getUrlByHash,
  mediaFiles,
  imageInfos,
  mediaRepos,
  jobs
}
