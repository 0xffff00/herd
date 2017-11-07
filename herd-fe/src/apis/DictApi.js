import RestApi from '../utils/RestApi'

const CTX = 'http://localhost:8080'

const words = new RestApi(CTX + '/words/', '{text}')
const aliasRels = new RestApi(CTX + '/alias-rels/', 'key={key};attr={attr};vno={vno}')
const dualRels = new RestApi(CTX + '/dual-rels/', 'key={key};attr={attr};vno={vno}')
const ge1Rels = new RestApi(CTX + '/ge1-rels/', 'key={key};attr={attr};vno={vno}')
const ge2Rels = new RestApi(CTX + '/ge2-rels/', 'key={key};attr={attr};vno={vno}')
export default {
  words, aliasRels, dualRels, ge1Rels, ge2Rels
}

