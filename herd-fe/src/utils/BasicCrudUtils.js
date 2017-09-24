import UrlUtils from './UrlUtils'
import fetch from 'node-fetch'

/**
 *
 * @param baseUrl
 * @param soloUrlBuilder (baseUrl,params) => a final solo url
 */
function buildCrudApi (baseUrl, soloUrlBuilder) {
  return {
    readListAndCount: readingPageAndCounting(baseUrl),
    readOne: (params, okCB, faCB) =>
      readingOne(soloUrlBuilder(baseUrl, params))(params, okCB, faCB),
    create: (params, okCB, faCB) =>
      creatingOne(soloUrlBuilder(baseUrl, params))(params, okCB, faCB),
    delete: (params, okCB, faCB) =>
      readingOne(soloUrlBuilder(baseUrl, params))(params, okCB, faCB),
    update: (params, okCB, faCB) =>
      updatingOne(soloUrlBuilder(baseUrl, params))(params, okCB, faCB)

  }
}

const CB_NO_OP = (d) => {}

const readingPageAndCounting = baseUrl => (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) => {
  const realUrl = UrlUtils.buildQueryUrl(baseUrl, params)
  fetch(realUrl, {method: 'GET'}).then(resp => {
    if (resp.ok) {
      const count = resp.headers.get('X-Total-Count')
      resp.json().then(data => okayCallback({items: data, totalCount: count}))
    } else {
      resp.json().then(failCallback)
    }
  })
}

const readingOne = finalUrl => (okayCallback = CB_NO_OP, failCallback = CB_NO_OP) => {
  fetch(finalUrl, {method: 'GET'}).then(_responding(okayCallback, failCallback))
}

const creatingOne = finalUrl => (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) => {
  fetch(finalUrl, {
    method: 'POST',
    headers: {
      'Accept': 'application/json, text/plain, */*',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(params)
  })
    .then(_responding(okayCallback, failCallback))
}
const deletingOne = finalUrl => (okayCallback = CB_NO_OP, failCallback = CB_NO_OP) => {
  fetch(finalUrl, {
    method: 'DELETE'
  })
    .then(_responding(okayCallback, failCallback))
}
const updatingOne = finalUrl => (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) => {
  fetch(finalUrl, {
    method: 'PUT',
    headers: {
      'Accept': 'application/json, text/plain, */*',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(params)
  })
    .then(_responding(okayCallback, failCallback))
}

const _responding = (okayCallback, failCallback) => resp => {
  if (resp.ok) {
    resp.json().then(okayCallback)
  } else {
    resp.json().then(failCallback)
  }
}

export default {
  buildCrudApi
}
