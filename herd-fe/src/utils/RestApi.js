import Urls from './Urls'

const CB_NO_OP = (d) => {}
const defaultHeaders = new Headers({
  'Accept': 'application/json, text/plain, */*',
  'Content-Type': 'application/json'
})
const responding = (okayCallback, failCallback) => resp => {
  if (resp.ok) {
    resp.json().then(okayCallback)
  } else {
    resp.json().then(failCallback)
  }
}
const responding2 = (okayCallback, failCallback) => resp => {
  if (resp.ok) {
    const totalAffected = parseInt(resp.headers.get('X-Total-Affected'))
    okayCallback({status: resp.status, totalAffected})
  } else {
    resp.json().then(failCallback)
  }
}
export default class RestApi {

  constructor (baseUrl, soloUrlTemplate) {
    this.baseUrl = baseUrl
    if (typeof soloUrlTemplate === 'string') {
      this.soloUrlBuilder = (params) => {
        let u = baseUrl + soloUrlTemplate
        Object.keys(params).forEach(key => {
          u = u.replace('{' + key + '}', params[key])
        })
        return u
      }
    } else if (typeof soloUrlTemplate === 'function') {
      this.soloUrlBuilder = soloUrlTemplate
    } else {
      throw new Error('RestApi has illegal soloUrlTemplate')
    }
  }

  getSoloUrl (params) {
    return this.soloUrlBuilder(params)
  }

  readListAndCount (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = Urls.buildQueryUrl(this.baseUrl, params)
    fetch(finalUrl, {method: 'GET'}).then(resp => {
      if (resp.ok) {
        const count = resp.headers.get('X-Total-Count')
        resp.json().then(data => okayCallback({items: data, totalCount: count}))
      } else {
        resp.json().then(failCallback)
      }
    })
  }

  readOne (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.getSoloUrl(params)
    fetch(finalUrl, {method: 'GET'}).then(responding(okayCallback, failCallback))
  }

  createOne (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.baseUrl
    var data = new FormData()
    data.append('json', JSON.stringify(params))
    fetch(finalUrl, {
      method: 'POST',
      headers: defaultHeaders,
      body: JSON.stringify(params)
    })
      .then(responding2(okayCallback, failCallback))
  }

  deleteOne (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.getSoloUrl(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(responding2(okayCallback, failCallback))
  }

  deleteSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = Urls.buildQueryUrl(this.baseUrl, params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(responding2(okayCallback, failCallback))
  }

  updateOne (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.getSoloUrl(oldParams)
    fetch(finalUrl, {
      method: 'PUT',
      headers: defaultHeaders,
      body: JSON.stringify(newParams)
    })
      .then(responding2(okayCallback, failCallback))
  }
}

