const CB_NO_OP = (d) => {
}
const DEFAULT_HEADERS = new Headers({
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
export { responding, responding2, CB_NO_OP, DEFAULT_HEADERS }
