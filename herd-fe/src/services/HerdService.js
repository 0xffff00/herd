import Vue from 'vue'
import fetch from 'node-fetch'

const CTX = 'http://localhost:8080/herd'
const urls = {
  rest: {
    medias: params => urls.utils.buildQueryUrl(CTX + '/herd/medias', params),
    repos: CTX + '/herd/repos',
    files: params => urls.utils.buildQueryUrl(CTX + '/files', params),
    catalogs: CTX + '/catalogs'
  },
  utils: {
    buildQueryUrl (urlStr, params) {
      var url = new URL(urlStr)
      if (params) {
        Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))
      }
      return url.href
    }
  }
}

const listRepos = function (params, callback) {
  // Vue.http.get(urls.rest.repos).then(callback)
  fetch(urls.rest.repos).then(r => r.json()).then(callback)
}
const listMedias = function (params, callback) {
  fetch(urls.rest.medias(params)).then(r => r.json()).then(callback)
}
const getUrlByHash = function (hash, cacheCategory) {
  return CTX + '/herd/pic2/' + hash + '.jpg?cache=' + cacheCategory
}
export default {listRepos, listMedias, getUrlByHash}
