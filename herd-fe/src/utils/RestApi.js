import { appendQParams, CB_NO_OP, DEFAULT_HEADERS, responding, respondingAf } from './RestUtils'
import Urls from './Urls'

const stringify = obj => obj ? JSON.stringify(obj) : null
export default class RestApi {

  /**
   * <pre>
   *  a RestAPI of user's addresses
   *  templated url: "http:localhost:8080/my-app/users/{uid}/addresses/{addressCode}"
   *  concete url:   "http:localhost:8080/my-app/users/20032/addresses/mars-olp03-32-1021"
   *    parts:        |<------ CTX ------------>|
   *                  |<----------------  pluralUrl ----------------->|<--singularPart-->|
   *                  |<------------------------ singularUrl --------------------------->|
   *
   * const repoRestApi = new RestApi(CTX + '/users/', '?name={name}&team={team}')   // uri q var style
   * const repoRestApi = new RestApi(CTX + '/users/', 'name={name};team={team}')    // matrix var style
   * const repoRestApi = new RestApi(CTX + '/users/', '{id}')                       // uri path var style
   * const repoRestApi = new RestApi(CTX + '/users/', params => CTX+'/users/'+params.id)  // set a func
   * </pre>
   * @param pluralUrl {string}
   * @param singularPart {function|string}, build a url made by primary key columns.
   */
  constructor (pluralUrl, singularPart = '') {
    this.pluralUrl = pluralUrl
    if (typeof singularPart === 'string') {
      this.singularUrlBuilder = (params) => {
        let u = pluralUrl + (singularPart || '')
        if (params) {
          Object.keys(params).forEach(key => {
            u = u.replace('{' + key + '}', params[key])
          })
        }
        return u
      }
    } else if (typeof singularPart === 'function') {
      this.singularUrlBuilder = singularPart
    } else {
      throw new Error('RestApi has illegal singularPartTemplate')
    }
  }

  getPluralUrlWithWithQParams (params) {
    return Urls.appendQParams(this.pluralUrl, params)
  }

  /**
   * get a list of part and count of all by http GET request
   * @param params
   * @param okayCallback
   * @param failCallback
   */
  httpGetSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.getPluralUrlWithWithQParams(params)
    fetch(finalUrl, {method: 'GET'}).then(responding(okayCallback, failCallback))
  }

  httpGet (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.singularUrlBuilder(params)
    fetch(finalUrl, {method: 'GET'}).then(responding(okayCallback, failCallback))
  }

  httpPost (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.pluralUrl
    fetch(finalUrl, {
      method: 'POST',
      headers: DEFAULT_HEADERS,
      body: stringify(params)
    })
      .then(respondingAf(okayCallback, failCallback))
  }

  httpDelete (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.singularUrlBuilder(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(respondingAf(okayCallback, failCallback))
  }

  httpDeleteSome (params, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.getPluralUrlWithWithQParams(params)
    fetch(finalUrl, {
      method: 'DELETE'
    })
      .then(respondingAf(okayCallback, failCallback))
  }

  /**
   *
   * @param oldParams body in PUT request
   * @param newParams uri-q-params in PUT request
   * @param okayCallback
   * @param failCallback
   */
  httpPut (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.singularUrlBuilder(oldParams)
    fetch(finalUrl, {
      method: 'PUT',
      headers: DEFAULT_HEADERS,
      body: stringify(newParams)
    })
      .then(respondingAf(okayCallback, failCallback))
  }

  httpPatch (oldParams, newParams, okayCallback = CB_NO_OP, failCallback = CB_NO_OP) {
    const finalUrl = this.singularUrlBuilder(oldParams)
    fetch(finalUrl, {
      method: 'PATCH',
      headers: DEFAULT_HEADERS,
      body: stringify(newParams)
    })
      .then(respondingAf(okayCallback, failCallback))
  }
}
