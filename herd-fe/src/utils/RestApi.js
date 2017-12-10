import Urls from './Urls'
import { responding, responding2, CB_NO_OP, DEFAULT_HEADERS } from './RestUtils'

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
  httpGetSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
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

  httpGet (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(params)
    fetch(finalUrl, {method: 'GET'}).then(responding(okayCallback, failCallback))
  }

  httpPost (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.baseUrl
    fetch(finalUrl, {
      method: 'POST',
      headers: DEFAULT_HEADERS,
      body: JSON.stringify(params)
    })
      .then(responding2(okayCallback, failCallback))
  }

  httpDelete (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(responding2(okayCallback, failCallback))
  }

  httpDeleteSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildQueryUrl(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(responding2(okayCallback, failCallback))
  }

  httpPut (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(oldParams)
    fetch(finalUrl, {
      method: 'PUT',
      headers: DEFAULT_HEADERS,
      body: JSON.stringify(newParams)
    })
      .then(responding2(okayCallback, failCallback))
  }

  httpPatch (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.buildSoloUrl(oldParams)
    fetch(finalUrl, {
      method: 'PATCH',
      headers: DEFAULT_HEADERS,
      body: JSON.stringify(newParams)
    })
      .then(responding2(okayCallback, failCallback))
  }
}
