import fetch from 'node-fetch'
import Urls from '../utils/Urls'
import RestApi from '../utils/RestApi'

const CTX = 'http://localhost:8080/herd'
const ajaxList = url => (params, callback) => {
  let realUrl = Urls.buildQueryUrl(url, params)
  fetch(realUrl).then(r => r.json()).then(callback)
}

const getUrlByHash = function (hash, cacheCategory) {
  return CTX + '/herd/pic2/' + hash + '.jpg?cache=' + cacheCategory
}

const listRepos = ajaxList(CTX + '/herd/repos')
const listMedias = ajaxList(CTX + '/herd/medias')
const listImageMedias = ajaxList(CTX + '/herd/imageMedias')
const countImageMediasByYear = ajaxList(CTX + '/herd/imageMedias/countByYear')
const countImageMediasByMonth = ajaxList(CTX + '/herd/imageMedias/countByMonth')
const repoRestApi = new RestApi(CTX + '/herd/repos/', '?name={name}')
export default {
  listRepos,
  listMedias,
  listImageMedias,
  getUrlByHash,
  countImageMediasByYear,
  countImageMediasByMonth,
  repoRestApi
}
