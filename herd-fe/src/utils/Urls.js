const buildQueryUrl = function (urlStr, params) {
  var url = new URL(urlStr)
  if (params) {
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))
  }
  return url.href
}

const getCurrentUrlPathname = function () {
  // TODO maybe not right
  return window.location.pathname
}
export default {buildQueryUrl, getCurrentUrlPathname}
