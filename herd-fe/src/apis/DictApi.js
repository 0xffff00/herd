import RestApi from '../utils/RestApi'
import appConf from '../../config/app-conf'

const CTX = appConf.apis.dict.url

const words = new RestApi(CTX + '/words/', '{text}')
const basicRelations = new RestApi(CTX + '/relations/basic/', 'src={src};attr={attr};no={no}')
const x1Relations = new RestApi(CTX + '/relations/x1/', 'src={src};attr={attr};no={no}')
export default {
  words, basicRelations, x1Relations
}

