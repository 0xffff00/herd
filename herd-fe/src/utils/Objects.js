function sortObject (o) {
  // return Object.keys(o).sort().reduce((r, k) => (r[k] = o[k], r), {})
  // TODO
  return o
}

function groupBy (obj, byFn) {
  if (!obj) {
    return {}
  }
  let res = {}
  Object.keys(obj).forEach(k => {
    let v = obj[k]
    let k1 = byFn(k, v)
    if (res[k1]) {
      res[k1].push(v)
    } else {
      res[k1] = [v]
    }
  })
  return res
}

export default {sortObject, groupBy}
