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
    _failCall(resp, failCallback)
  }
}
const respondingAf = (okayCallback, failCallback) => resp => {
  const status = resp.status
  if (resp.ok) {
    const totalAffected = parseInt(resp.headers.get('X-Total-Affected')) || null
    console.log('resp - totalAffected: ', totalAffected)
    resp.text().then(text => {
      let data = text
      try {
        data = JSON.parse(text)
      } catch (err) {
      }
      okayCallback({totalAffected, data, status})
    })
  } else {
    _failCall(resp, failCallback)
  }
}

const _failCall = (resp, failCallback) => {
  let status = resp.status
  resp.text().then(text => {
    let data = text
    try {
      data = JSON.parse(text)
    } catch (err) {
    }
    // console.log(data)
    failCallback({data, status})
  })
}

const translateResp = (actionName, sk2Resp) => {
  console.log(actionName, sk2Resp)
  let st0 = sk2Resp.status || (sk2Resp.data ? sk2Resp.data.status : '')
  let st1 = st0 ? '[' + st0 + ']' : ''
  let err1 = sk2Resp.data && sk2Resp.data.error || ''
  let bd1 = sk2Resp.data ? (sk2Resp.data.message || '') + (sk2Resp.data.debugInfo || '') : null
  return {
    title: `${actionName}失败: ${st1} ${err1}`,
    body: bd1
  }
}

export { responding, respondingAf, CB_NO_OP, DEFAULT_HEADERS, translateResp }
