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

  /*
   * const repoRestApi = new RestApi(CTX + '/users/', '?name={name}&team={team}')   // normal style
   * const repoRestApi = new RestApi(CTX + '/users/', 'name={name};team={team}')    // matrix var style
   * const repoRestApi = new RestApi(CTX + '/users/', '{id}')                       // simple style
   * const repoRestApi = new RestApi(CTX + '/users/', params => CTX+'/users/'+params.id)  // set a func
   * @param baseUrl
   * @param soloUrlTemplate function or string
   */
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

  buildSoloUrl (params) {
    return this.soloUrlBuilder(params)
  }

  buildQueryUrl (params) {
    return Urls.buildQueryUrl(this.baseUrl, params)
  }

  /**
   * get a list of part and count of all by http GET request
   * @param params
   * @param okayCallback
   * @param failCallback
   */
  getSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildQueryUrl(params)
    fetch(finalUrl, {method: 'GET'}).then(resp => {
      if (resp.ok) {
        const count = resp.headers.get('X-Total-Count')
        resp.json().then(data => okayCallback({items: data, totalCount: count}))
      } else {
        resp.json().then(failCallback)
      }
    })
  }

  get (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(params)
    fetch(finalUrl, {method: 'GET'}).then(responding(okayCallback, failCallback))
  }

  post (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
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

  delete (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(responding2(okayCallback, failCallback))
  }

  deleteSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildQueryUrl(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(responding2(okayCallback, failCallback))
  }

  put (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(oldParams)
    fetch(finalUrl, {
      method: 'PUT',
      headers: defaultHeaders,
      body: JSON.stringify(newParams)
    })
      .then(responding2(okayCallback, failCallback))
  }

  patch (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(oldParams)
    fetch(finalUrl, {
      method: 'PATCH',
      headers: defaultHeaders,
      body: JSON.stringify(newParams)
    })
      .then(responding2(okayCallback, failCallback))
  }
}

