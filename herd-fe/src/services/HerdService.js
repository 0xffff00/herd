import fetch from 'node-fetch'
import UrlUtils from '../utils/UrlUtils'
import BasicCrudUtils from '../utils/BasicCrudUtils'

const CTX = 'http://localhost:8080/herd'
const ajaxList = url => (params, callback) => {
  let realUrl = UrlUtils.buildQueryUrl(url, params)
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
const repoCrudApi = BasicCrudUtils.buildCrudApi(CTX + '/herd/repos',
  (baseUrl, params) => baseUrl + '/?name=' + params.name
)
export default {
  listRepos,
  listMedias,
  listImageMedias,
  getUrlByHash,
  countImageMediasByYear,
  countImageMediasByMonth,
  repoCrudApi
}
