import RestApi from '../utils/RestApi'
import appConf from '../../config/sk2/app-conf'

const CTX = appConf.apis.dict.url

const words = new RestApi(CTX + '/words/', '{text}')
const basicRelations = new RestApi(CTX + '/relations/basic/', 'src={src};attr={attr};no={no}')
const x1Relations = new RestApi(CTX + '/relations/x1/', 'src={src};attr={attr};no={no}')
x1Relations.batchCreate = new RestApi(CTX + '/relations2/x1/?batch=1', '').httpPost
export default {
  words, basicRelations, x1Relations
}

