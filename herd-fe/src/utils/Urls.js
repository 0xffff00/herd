const buildQueryUrl = function (urlStr, params) {
  var url = new URL(urlStr)
  if (params) {
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))
  }
  return url.href
}
export default {buildQueryUrl}
