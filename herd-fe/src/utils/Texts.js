const describeImageSize = function (imageHeight, imageWidth) {
  let mp = imageHeight * imageWidth / 1024 / 1024
  return imageWidth + 'x' + imageHeight + ', ' + mp.toPrecision(2) + 'MP'
}
const describeFileSize = function (byteLength) {
  let KB = byteLength / 1024
  let MB = KB / 1024
  let GB = MB / 1024
  if (GB > 1) return GB.toPrecision(3) + 'GB'
  if (MB > 1) return MB.toPrecision(3) + 'MB'
  if (KB > 1) return KB.toPrecision(3) + 'KB'
  return byteLength + 'KB'
}
export default {describeFileSize, describeImageSize}
