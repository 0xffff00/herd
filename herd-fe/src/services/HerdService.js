import fetch from 'node-fetch'
import UrlUtils from '../utils/UrlUtils'

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
const countImageMediasByDate = ajaxList(CTX + '/herd/imageMedias/count')

export default {listRepos, listMedias, listImageMedias, getUrlByHash, countImageMediasByDate}
