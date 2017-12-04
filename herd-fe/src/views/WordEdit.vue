<template>
  <div id="word-manager">
    <h1>{{word.text}} - 详情</h1>
    <h2>别名</h2>
    <div id="alias-rels">
      <Tag v-for="r in aliasRS0" :key="rel2id(r)" :closable="true" @on-close="delAR(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input class="adder" size="small" placeholder="添加新别名" v-model="editor.newAliasText" @on-enter="addAR"/>
    </div>
    <h2>定义与实例</h2>
    <div id="dual-rels-inst">
      <h3>直接类型</h3>
      <Tag v-for="r in definitionRS0" :key="rel2id(r)" :closable="true" @on-close="delDR(r)">
        <a :href="url$w(r.key)">{{r.key}}</a>
      </Tag>
      <Input name="definition" class="adder" size="small" placeholder="关联新类型" @on-enter="addDR"/>
      <h3>所有超类</h3>
      <Tag v-for="w in definitionESA" :key="w"><a :href="url$w(w)">{{w}}</a></Tag>

      <h3>直接实例</h3>
      <Tag v-for="r in instanceRS0" :key="rel2id(r)" :closable="true" @on-close="delDR(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input name="instance" class="adder" size="small" placeholder="关联新实例" @on-enter="addDR"/>
      <h3>所有实例</h3>
      <Tag v-for="w in instanceESA" :key="w"><a :href="url$w(w)">{{w}}</a></Tag>
    </div>

    <h2>集合</h2>
    <div id="dual-rels-subs">
      <h3>直接子集</h3>
      <Tag v-for="r in subsetRS0" :key="rel2id(r)" :closable="true" @on-close="delDR(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input name="subset" class="adder" size="small" placeholder="关联新子集" @on-enter="addDR"/>

      <h3>所有子集</h3>
      <Tag v-for="w in subsetESR" :key="w"><a :href="url$w(w)">{{w}}</a></Tag>
      <h3>直接超集</h3>
      <Tag v-for="r in supersetRS0" :key="rel2id(r)" :closable="true" @on-close="delDR(r)">
        <a :href="url$w(r.key)">{{r.key}}</a>
      </Tag>
      <Input name="superset" class="adder" size="small" placeholder="关联新超集" @on-enter="addDR"/>
      <h3>所有超集</h3>
      <Tag v-for="w in supersetESR" :key="w"><a :href="url$w(w)">{{w}}</a></Tag>
    </div>

    <h2>相关话题</h2>
    <div id="dual-rels-gech">
      <h3>直接子话题</h3>
      <Tag v-for="r in subtopicRS0" :key="rel2id(r)" :closable="true" @on-close="delDR(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input name="subtopic" class="adder" size="small" placeholder="关联新子话题" @on-enter="addDR"/>
      <h3>所有子话题</h3>
      <Tag v-for="w in subtopicESR" :key="w"><a :href="url$w(w)">{{w}}</a></Tag>
      <h3>直接父话题</h3>
      <Tag v-for="r in supertopicRS0" :key="rel2id(r)" :closable="true" @on-close="delDR(r)">
        <a :href="url$w(r.key)">{{r.key}}</a></Tag>
      <Input name="supertopic" class="adder" size="small" placeholder="关联新父话题"
             @on-enter="addDR"/>
      <h3>所有父话题</h3>
      <Tag v-for="w in supertopicESR" :key="w"><a :href="url$w(w)">{{w}}</a></Tag>
    </div>


    <!--  <h2>属性和应用</h2>
      <div id="generic-rels">
        <h3>属性</h3>
        <ul>
          <li v-for="(rels,attr) in attributeRMG">
            <el-button type="danger" size="small" @click="delG1R(attr)"><i class="fa fa-times"></i></el-button>
            <span class="attrName">{{attr}}</span> {{translate_pred(rels[0].pred)}}
            <Tag v-for="r in rels" :closable="true" @on-close="delG1Rv(r)">
              <a :href="url$w(r.val)">{{r.val}}</a>
            </Tag>
            <Input class="adder" size="small" placeholder="关联新属性值" @on-enter="addG1Rv(rels,$event)"/>

          </li>
          <li>
            <el-form :inline="true" :model="editor.adder.ge1Rel">
              <el-form-item>
                <Input v-model="editor.adder.ge1Rel.attr" placeholder="属性名" size="small"
                       style="width:140px;"></Input>
              </el-form-item>
              <el-form-item>
                <el-select v-model="editor.adder.ge1Rel.pred" placeholder="谓词" size="small" style="width:80px;">
                  <el-option label="是" value="IS"></el-option>
                  <el-option label="有" value="HAS"></el-option>
                  <el-option label="仅有" value="ARE"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <Input v-model="editor.adder.ge1Rel.vals" placeholder="属性值，若填多个以空格隔开" size="small"
                       @on-enter="addG1R" style="width:280px;"></Input>
              </el-form-item>
              <el-form-item>
                <el-button type="success" size="small" @click="addG1R"><i class="fa fa-plus"></i></el-button>
              </el-form-item>
            </el-form>
          </li>
        </ul>
        <h3>属性（值）</h3>
        <ul>
          <li v-for="(rels,attr) in rmg_attr2">
            <el-button type="danger" size="small" @click="delG2R(attr)"><i class="fa fa-times"></i></el-button>
            <span class="attrName">{{attr}}</span> {{translate_pred(rels[0].pred)}}
            <Tag v-for="r in rels" :closable="true" @on-close="delG2Rv(r)">
              <span class="valnum">{{r.valnum}}</span><span class="valstr">{{r.valstr}}</span> {{r.valmu}}
            </Tag>
            <Input class="adder" size="small" placeholder="关联新属性值"
                   @on-enter="addG2Rv(rels,$event)"></Input>

          </li>
          <li>
            <el-form :inline="true" :model="editor.adder.ge2Rel">
              <el-form-item>
                <Input v-model="editor.adder.ge2Rel.attr" placeholder="属性名" size="small"
                       style="width:140px;"></Input>
              </el-form-item>
              <el-form-item>
                <el-select v-model="editor.adder.ge2Rel.pred" placeholder="谓词" size="small" style="width:60px;">
                  <el-option label="是" value="IS"></el-option>
                  <el-option label="有" value="HAS"></el-option>
                  <el-option label="仅有" value="ARE"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <Input v-model="editor.adder.ge2Rel.vals" placeholder="属性值" size="small"
                       @on-enter="addG2R" style="width:230px;"></Input>
              </el-form-item>
              <el-form-item>
                <Input v-model="editor.adder.ge2Rel.valmu" placeholder="量词" size="small"
                       @on-enter="addG2R" style="width:60px;"></Input>
              </el-form-item>
              <el-form-item>
                <el-button type="success" size="small" @click="addG2R"><i class="fa fa-plus"></i></el-button>
              </el-form-item>
            </el-form>
          </li>

        </ul>


        <h3>引用</h3>
        <ul>
          <li v-for="rel in ge1Rels_refer">
            <a :href="url$w(rel.key)">{{rel.key}}</a> 的 {{rel.attr}} {{translate_pred(rel.pred)
            }} {{rel.val}}
          </li>
        </ul>
      </div>-->

  </div>

</template>

<script>
  import _ from 'lodash'
  import DAGVisitor from '../utils/DAGVisitor'
  import Objects from '../utils/Objects'
  import DictApi from '../apis/DictApi'

  /**
   * ---Coding Naming Conventions---
   * ES0, EntSet0: Entity Set shallow
   * ESR, EntSetR: Entity Set recursive
   * RS0, RelSet0: Relation Set shallow
   * RSR, RSR: Relation Set recursive
   * RMG: rel map grouped
   */

  /**
   * RSR -> ESR
   * @param me
   * @param edges RSR
   * @param forward
   * @returns {Set}
   */
  function fetchESR (me, edges, forward) {
    let visitor = forward ? new DAGVisitor(me, edges, e => e.src, e => e.dst) : new DAGVisitor(me, edges, e => e.dst, e => e.src)
    visitor.visitFrom(me)
    let res = new Set(visitor.getVerticesVisited())
    res.delete(me)
    return res
  }

  // utils
  const rel2id = rel => rel.src + '-' + rel.attr + '-' + rel.no
  const rel2attr = rel => rel.attr + (rel.attrx ? '(' + rel.attrx + ')' : '')

  const rel2str = (rel) => {
    if (!rel) return ''
    return rel.key + '[' + rel.attr + '] -->' + rel.val
  }

  function pred2str (pred) {
    switch (pred) {
      case 'IS':
        return '是'
      case 'ARE':
        return '仅有'
      case 'HAS':
        return '有'
    }
    return '='
  }

  export default {
    name: 'word-edit',
    data () {
      return {
        word: {
          text: null,
          desc: null,
          state: null,
          updateTime: null,
          aliasRS0: [],
          subsetRSR: [],
          supersetRSR: [],
          instanceRS0: [],
          instanceESA: [],
          definitionRS0: [],
          definitionESA: [],
          subtopicRSR: [],
          supertopicRSR: [],
          attributeRS0: [],
          referenceRS0: []
        },
        editor: {
          newAliasText: '',
          adder: {
            ge1Rel: {pred: 'HAS'},
            ge2Rel: {pred: 'HAS'}
          }
        },
        ui: {
          inputVisible: false,
          loading: false
        }
      }
    },
    computed: {
      me: function () {
        return this.word.text
      },

      // --------- 6 RS0 ---------
      subsetRS0: function () {
        let self = this
        return self.word.subsetRS.filter(r => r.src === self.word.text)
      },
      supersetRS0: function () {
        let self = this
        return self.word.supersetRSR.filter(r => r.dst === self.word.text)
      },
      instanceRS0: function () {
        return this.word.instanceRS0
      },
      definitionRS0: function () {
        return this.word.definitionRS0
      },
      subtopicRS0: function () {
        let self = this
        return self.word.subtopicRSR.filter(r => r.src === self.word.text)
      },
      supertopicRS0: function () {
        let self = this
        return self.word.supertopicRSR.filter(r => r.dst === self.word.text)
      },

      // --------- 6 NSR ---------
      subsetESR: function () {
        return fetchESR(this.word.text, this.word.subsetRSR, true)
      },
      supersetESR: function () {
        return fetchESR(this.word.text, this.word.supersetRSR, false)
      },
      instanceESA: function () {
        return this.word.instanceESA
      },
      definitionESA: function () {
        return this.word.definitionESA
      },
      subtopicESR: function () {
        return fetchESR(this.word.text, this.word.subtopicRSR, true)
      },
      supertopicESR: function () {
        return fetchESR(this.word.text, this.word.supersetRSR, false)
      },

      attributeRMG: function () {
        return Objects.sortObject(_.mapValues(
          _.groupBy(this.word.attributeES0, rel2attr),
          rels => _.sortBy(rels, ['attr', 'attrx', 'no'])
        ))
      }

    },

    created () {
      let params = this.$route.params
      this.word.text = params.text
      console.log(this.word)
      this.loadItem()
    },
    methods: {
      // ------------ aliasRels ---------------
      /**
       * add alias rel
       */
      addAR () {
        const self = this
        let v = self.editor.newAliasText
        if (!v) return
        let rel = {
          src: this.word.text, attr: '', dst: v
        }
        DictApi.aliasRels.httpPost(rel, self.notifyOkay('添加别名'), self.notifyFail('添加别名'))
      },
      /**
       * del alias rel
       */
      delAR (rel) {
        const self = this
        DictApi.aliasRels.httpDelete(rel, self.notifyOkay('移除关联'), self.notifyFail('移除关联'))
      },
      // ------------ dualRels ---------------
      /**
       * add dual rel
       * @param evt
       */
      addDR (evt) {
        const self = this
        let _vv = evt.target.value
        if (!_vv) return
        let _me = this.me
        let name = evt.target.name
        let rel = {}
        switch (name) {
          case 'subset':
            rel = {key: _me, attr: 'SUBS', val: _vv}
            break
          case 'superset':
            rel = {key: _vv, attr: 'SUBS', val: _me}
            break
          case 'instance':
            rel = {key: _me, attr: 'INST', val: _vv}
            break
          case 'definition':
            rel = {key: _vv, attr: 'INST', val: _me}
            break
          case 'subtopic':
            rel = {key: _me, attr: 'GECH', val: _vv}
            break
          case 'supertopic':
            rel = {key: _vv, attr: 'GECH', val: _me}
            break
        }
        let OldVnoArr = this.word.dualRels.filter(r => r.key === rel.key && r.attr === rel.attr).map(r => r.vno)
        rel.vno = OldVnoArr.length ? _.max(OldVnoArr.map(x => +x)) + 1 : 0
        DictApi.dualRels.httpPost(rel, self.notifyOkay('添加' + name), self.notifyFail('添加' + name))
      },
      /**
       * del dualRel
       * @param rel
       */
      delDR (rel) {
        const self = this
        DictApi.dualRels.httpDelete(rel, self.notifyOkay('移除关联'), self.notifyFail('移除关联'))
      },

      // ------------ gel1Rels ---------------
      /**
       * append ge1Rel val
       * @param relsOld
       * @param evt
       */
      addG1Rv (relsOld, evt) {
        const self = this
        let valNew = evt.target.value.trim()
        if (valNew === '') return
        let vno = _.max(relsOld.map(r => +r.vno)) + 1
        let pred = relsOld[0].pred
        let key = relsOld[0].key
        let attr = relsOld[0].attr
        let rel = {key: key, pred: pred, attr: attr, attrx: null, vno: vno, val: valNew}
        DictApi.ge1Rels.httpPost(rel, () => {
          self.notifyOkay('关联新属性值')
          evt.target.value = ''
          this.loadItem()
        }, self.notifyFail('关联新属性值'))
      },
      /**
       * del ge1Rel val
       * @param rel
       */
      delG1Rv (rel) {
        const self = this
        DictApi.ge1Rels.httpDelete(rel, self.notifyOkay('移除属性值'), self.notifyFail('移除属性值'))
      },
      /**
       *  add ge1Rel
       */
      addG1R () {
        const self = this
        let rel = this.editor.adder.ge1Rel
        rel.key = self.word.text
        rel.vno = -1
        rel.val = rel.vals
        DictApi.ge1Rels.httpPost(rel, self.notifyOkay('添加新属性'), self.notifyFail('添加新属性'))
      },
      /**
       * del ge1Rel vals by attr
       * @param attr
       */
      delG1R (attr) {
        const self = this
        let rel = {key: self.word.text, attr: attr}
        DictApi.ge1Rels.httpDelete(rel, self.notifyOkay('移除属性'), self.notifyFail('移除属性'))
      },
      // ------------ gel2Rels ---------------
      /**
       * append ge2Rel val
       * @param relsOld
       * @param evt
       */
      addG2Rv (relsOld, evt) {
        const self = this
        let valNew = evt.target.value.trim()
        if (valNew === '') return
        let vno = _.max(relsOld.map(r => +r.vno)) + 1
        let pred = relsOld[0].pred
        let key = relsOld[0].key
        let attr = relsOld[0].attr

        let rel = {key: key, pred: pred, attr: attr, attrx: null, vno: vno}
        if (!_.isNaN(+valNew)) {
          rel.valnum = valNew
          rel.valstr = null
        } else {
          rel.valstr = valNew
          rel.valnum = null
        }
        DictApi.ge2Rels.httpPost(rel, () => {
          self.notifyOkay('关联新属性II值')
          evt.target.value = ''
          this.loadItem()
        }, self.notifyFail('关联新属性II值'))
      },
      /**
       * del ge2Rel val
       */
      delG2Rv (rel) {
        const self = this
        DictApi.ge2Rels.httpDelete(rel, self.notifyOkay('移除属性II值'), self.notifyFail('移除属性II值'))
      },
      /**
       * add ge2Rel
       */
      addG2R () {
        const self = this
        let rel = this.editor.adder.ge2Rel
        rel.key = self.word.text
        rel.vno = 0
        if (!_.isNaN(+rel.vals)) {
          rel.valnum = rel.vals
          rel.valstr = null
        } else {
          rel.valstr = rel.vals
          rel.valnum = null
        }
        DictApi.ge2Rels.httpPost(rel, self.notifyOkay('添加属性II'), self.notifyFail('添加属性II'))
      },
      /**
       * del ge2Rel vals by attr
       * @param attr
       */
      delG2R (attr) {
        const self = this
        let rel = {key: self.word.text, attr: attr}
        DictApi.ge2Rels.httpDelete(rel, self.notifyOkay('移除属性II'), self.notifyFail('移除属性II'))
      },

      // ------------ misc ---------------
      /**
       * TODO get page url of word detail
       * @param word
       * @return {*}
       */
      url$w (w) {
        return `../${w}/edit`
      },

      loadItem () {
        const self = this
        let w = this.word.text
        if (!w) {
          return
        }
        DictApi.words.httpGet(this.word, (d) => {
          self.word = d
          self.ui.loading = false
        }, self.notifyFail('加载词汇'))
      },

      notifyOkay (what) {
        const self = this
        return d => {
          let msg = d.message ? d.message : (d.totalAffected ? d.totalAffected + '个条目已' + what : '')
          self.$notify.success({title: what + '成功', message: msg})
          self.loadItem()
        }
      },
      notifyFail (what) {
        const self = this
        return d => {
          self.$notify.error({title: what + '失败', message: d.message, duration: 0})
        }
      }

    }
  }
</script>
<style scoped>
  #word-edit a {
    text-decoration: none;
    color: inherit;
  }

  .haha {
    color: red;
    background: yellow;
  }

  .adder {
    width: 10em;
  }

  .attrName {
    font-weight: bold;
    color: #641;
  }

  .valnum {
    color: red;
  }

  .valstr {
    color: #797;
  }

  .el-input.adder {
    width: 9em;
  }
</style>
