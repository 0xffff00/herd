import 'whatwg-fetch'

const CB_NO_OP = (d) => {
}
const DEFAULT_HEADERS = {
  'Accept': 'application/json, text/plain, */*',
  'Content-Type': 'application/json'
}
// respond for REST API in Skean 2.x
const responding = (okayCallback, failCallback) => resp => {
  if (resp.ok) {
    const totalAffected = parseInt(resp.headers.get('X-Total-Affected')) || null
    const totalCount = parseInt(resp.headers.get('X-Total-Count')) || null
    const status = resp.status
    resp.json().then(data => {
      let arg = {data, status}
      if (totalAffected) arg.totalAffected = totalAffected
      if (totalCount) arg.totalCount = totalCount
      okayCallback(arg)
    })
  } else {
    resp.json().then(failCallback)
  }
}
const respondingAf = (okayCallback, failCallback) => resp => {
  const status = resp.status
  if (resp.ok) {
    const totalAffected = parseInt(resp.headers.get('X-Total-Affected')) || null
    console.log('aaaaasaaa', totalAffected)
    resp.text().then(text => {
      try {
        const data = JSON.parse(text)
        okayCallback({totalAffected, data, status})
      } catch (err) {
      }
    })
  } else {
    console.log('aaaaaaaa', resp)
    resp.json().then(data => failCallback({data, status}))
  }
}

export { responding, respondingAf, CB_NO_OP, DEFAULT_HEADERS }
