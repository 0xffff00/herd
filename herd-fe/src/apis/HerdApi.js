import fetch from 'node-fetch'
import Urls from '../utils/Urls'
import RestApi from '../utils/RestApi'
import appConf from '../../config/app-conf'

const CTX = appConf.apis.herd.url

const ajaxList = url => (params, callback) => {
  let realUrl = Urls.buildQueryUrl(url, params)
  fetch(realUrl).then(r => r.json()).then(callback)
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
export default {
  listRepos,
  listMedias,
  listImageMedias,
  getUrlByHash,
  countImageMediasByYear,
  countImageMediasByMonth,
  repoRestApi
}
