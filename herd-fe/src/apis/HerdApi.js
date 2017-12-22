import Api from '../components/SkeanRestApi'
import appConf from '../../config/sk2/app-conf'

const CTX = appConf.apis.herd.url

const getUrlByHash = function (hash, cacheCategory) {
  return CTX + '/file/' + hash + '.jpg?cache=' + cacheCategory
}

const mediaRepos = new Api(CTX + '/media-repos/', '{name}')
const mediaFiles = new Api(CTX + '/media-files/', '{path}')
const imageInfos = new Api(CTX + '/image-infos/', '{hash}')

// const listRepos = ajaxList(CTX + '/repos')
// const listMedias = ajaxList(CTX + '/medias')
// const listImageMedias = ajaxList(CTX + '/image-infos')
imageInfos.countByYear = new Api(CTX + '/image-infos/countByYear').httpGet(null)
imageInfos.countByMonth = new Api(CTX + '/image-infos/countByMonth').httpGet(null)
imageInfos.countByDate = new Api(CTX + '/image-infos/countByDate').httpGet(null)

const jobs = {
  batchSync: {
    start: params => new Api(CTX + '/jobs/batch-sync', '?repoName={repoName}').httpPut(params, null),
    status: new Api(CTX + '/jobs/batch-sync/status').httpGet(null),
    statusAll: new Api(CTX + '/jobs/batch-sync/status/all').httpGet(null)
  },
  thumbnail: {
    start: params => new Api(CTX + '/jobs/batch-sync', '?repoName={repoName}').httpGet(params),
    status: new Api(CTX + '/jobs/image/thumbnails/status').httpGet(null),
  },
  mediaRepos: {
    truncate: params => new Api(CTX + '/jobs/media-repos', '?repoName={repoName}').httpDelete(params)
  }
}

export default {
  getUrlByHash,
  mediaFiles,
  imageInfos,
  mediaRepos,
  jobs
}
