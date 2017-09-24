import _ from 'lodash'

const toYYYYMM = function (year = 9999, month = 12) {
  return _.padStart(year, 4, '0') + _.padStart(month, 2, '0')
}
const toIsoDate = function (y = 9999, M = 12, d = 31) {
  return _.padStart(y, 4, '0') + '-' + _.padStart(M, 2, '0') + '-' + _.padStart(d, 2, '0')
}
const toIsoTime = function (y = 9999, M = 12, d = 31, H, m, s) {
  return _.padStart(y, 4, '0') + '-' + _.padStart(M, 2, '0') + '-' + _.padStart(d, 2, '0') +
    'T' + _.padStart(H, 2, '0') + ':' + _.padStart(m, 2, '0') + ':' + _.padStart(s, 2, '0')
}
const toChsYearMon = function (y, M) {
  if (y == null) return '(未指定)'
  return y + '年' + M + '月'
}
const toChsDate = function (y = 9999, M = 12, d = 31) {
  if (y == null) return '(未指定)'
  return y + '年' + M + '月' + d + '日'
}
const isoToDefault = function (isoTimeStr) {
  if (isoTimeStr === null) {
    return ''
  }
  return isoTimeStr.replace('T', ' ')
}
export default {isoToDefault, toIsoTime, toChsYearMon, toChsDate}
