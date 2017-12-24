import Api from '../components/SkeanRestApi'
import appConf from '../../config/sk2/app-conf'

const CTX = appConf.apis.dict.url

const words = new Api(CTX + '/words/', '{text}')
const basicRelations = new Api(CTX + '/relations/basic/', 'src={src};attr={attr};no={no}')
const x1Relations = new Api(CTX + '/relations/x1/', 'src={src};attr={attr};no={no}')
x1Relations.batchCreate = params => new Api(CTX + '/relations/x1/?batch=true').httpPost(params)
export default {
  words,
  basicRelations,
  x1Relations
}

