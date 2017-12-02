<template>
  <div id="word-manager">
    <h1>{{word.text}} - 详情</h1>
    <h2>别名</h2>
    <div id="alias-rels">
      <Tag :key="r" v-for="r in word.aliasRels" :closable="true"
           @on-close="del$ar(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input class="adder" size="small" placeholder="添加新别名" v-model="editor.newAliasText" @on-enter="add$ar"/>
    </div>
    <h2>定义与实例</h2>
    <div id="dual-rels-inst">
      <h3>直接类型</h3>
      <Tag :key="r" v-for="r in rs0_definitions" :closable="true"
           @on-close="del$dr(r)">
        <a :href="url$w(r.key)">{{r.key}}</a>
      </Tag>
      <Input name="definition" class="adder" size="small" placeholder="关联新类型" @on-enter="add$dr"/>
      <h3>所有超类</h3>
      <Tag :key="w" v-for="w in nsr_definitions" type="success"><a :href="url$w(w)">{{w}}</a></Tag>

      <h3>直接实例</h3>
      <Tag :key="r" v-for="r in rs0_instances" :closable="true"
           @on-close="del$dr(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input name="instance" class="adder" size="small" placeholder="关联新实例" @on-enter="add$dr"/>
      <h3>所有实例</h3>
      <Tag :key="w" v-for="w in nsr_instances" type="success"><a :href="url$w(w)">{{w}}</a></Tag>
    </div>

    <h2>集合</h2>
    <div id="dual-rels-subs">
      <h3>直接子集</h3>
      <Tag :key="r" v-for="r in rs0_subsets" :closable="true"
           @on-close="del$dr(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input name="subset" class="adder" size="small" placeholder="关联新子集" @on-enter="add$dr"/>

      <h3>所有子集</h3>
      <Tag :key="w" v-for="w in nsr_subsets" type="success"><a :href="url$w(w)">{{w}}</a></Tag>
      <h3>直接超集</h3>
      <Tag :key="r" v-for="r in rs0_supersets" :closable="true"
           @on-close="del$dr(r)">
        <a :href="url$w(r.key)">{{r.key}}</a>
      </Tag>
      <Input name="superset" class="adder" size="small" placeholder="关联新超集" @on-enter="add$dr"/>
      <h3>所有超集</h3>
      <Tag :key="w" v-for="w in nsr_supersets" type="success"><a :href="url$w(w)">{{w}}</a></Tag>
    </div>

    <h2>相关话题</h2>
    <div id="dual-rels-gech">
      <h3>直接子话题</h3>
      <Tag :key="r" v-for="r in rs0_subtopics" :closable="true"
           @on-close="del$dr(r)">
        <a :href="url$w(r.val)">{{r.val}}</a>
      </Tag>
      <Input name="subtopic" class="adder" size="small" placeholder="关联新子话题" @on-enter="add$dr"/>
      <h3>所有子话题</h3>
      <Tag :key="w" v-for="w in nsr_subtopics" type="success"><a :href="url$w(w)">{{w}}</a></Tag>
      <h3>直接父话题</h3>
      <Tag :key="r" v-for="r in rs0_supertopics" :closable="true"
           @on-close="del$dr(r)">
        <a :href="url$w(r.key)">{{r.key}}</a></Tag>
      <Input name="supertopic" class="adder" size="small" placeholder="关联新父话题"
             @on-enter="add$dr"/>
      <h3>所有父话题</h3>
      <Tag :key="w" v-for="w in nsr_supertopics" type="success"><a :href="url$w(w)">{{w}}</a></Tag>
    </div>


    <h2>属性和应用</h2>
    <div id="generic-rels">
      <h3>属性</h3>
      <ul>
        <li v-for="(rels,attr) in rmg_attr1">
          <el-button type="danger" size="small" @click="del$gr1(attr)"><i class="fa fa-times"></i></el-button>
          <span class="attrName">{{attr}}</span> {{translate_pred(rels[0].pred)}}
          <Tag :key="r" v-for="r in rels" :closable="true"
               @on-close="del$gr1v(r)">
            <a :href="url$w(r.val)">{{r.val}}</a>
          </Tag>
          <Input class="adder" size="small" placeholder="关联新属性值" @on-enter="add$gr1v(rels,$event)"/>

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
                     @on-enter="add$gr1" style="width:280px;"></Input>
            </el-form-item>
            <el-form-item>
              <el-button type="success" size="small" @click="add$gr1"><i class="fa fa-plus"></i></el-button>
            </el-form-item>
          </el-form>
        </li>
      </ul>
      <h3>属性（值）</h3>
      <ul>
        <li v-for="(rels,attr) in rmg_attr2">
          <el-button type="danger" size="small" @click="del$gr2(attr)"><i class="fa fa-times"></i></el-button>
          <span class="attrName">{{attr}}</span> {{translate_pred(rels[0].pred)}}
          <Tag :key="r" v-for="r in rels" :closable="true" type="gray"
               @on-close="del$gr2v(r)">
            <span class="valnum">{{r.valnum}}</span><span class="valstr">{{r.valstr}}</span> {{r.valmu}}
          </Tag>
          <Input class="adder" size="small" placeholder="关联新属性值"
                 @on-enter="add$gr2v(rels,$event)"></Input>

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
                     @on-enter="add$gr2" style="width:230px;"></Input>
            </el-form-item>
            <el-form-item>
              <Input v-model="editor.adder.ge2Rel.valmu" placeholder="量词" size="small"
                     @on-enter="add$gr2" style="width:60px;"></Input>
            </el-form-item>
            <el-form-item>
              <el-button type="success" size="small" @click="add$gr2"><i class="fa fa-plus"></i></el-button>
            </el-form-item>
          </el-form>
        </li>

      </ul>


      <h3>引用</h3>
      <ul>
        <li v-for="rel in grels_refer">
          <a :href="url$w(rel.key)">{{rel.key}}</a> 的 {{rel.attr}} {{translate_pred(rel.pred)
          }} {{rel.val}}
        </li>
      </ul>
    </div>

  </div>

</template>

<script>
  import _ from 'lodash'
  import DAGVisitor from '../utils/DAGVisitor'
  import Objects from '../utils/Objects'
  import DictApi from '../apis/DictApi'

  export default {
    name: 'word-edit',
    data () {
      return {
        word: {
          text: null,
          aliasRels: [],
          dualRels: [],
          ge1Rels: [],
          ge2Rels: []
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
      drels_subs: function () {
        return this.word.dualRels.filter(r => r.attr === 'SUBS')
      },
      drels_inst: function () {
        return this.word.dualRels.filter(r => r.attr === 'INST')
      },
      drels_gech: function () {
        return this.word.dualRels.filter(r => r.attr === 'GECH')
      },
      grels_attr1: function () {
        return this.word.ge1Rels.filter(r => r.key === this.me)
      },
      grels_attr2: function () {
        return this.word.ge2Rels.filter(r => r.key === this.me)
      },
      grels_refer: function () {
        return this.word.ge1Rels.filter(r => r.val === this.me)
      },

      /* rs0: arr of rels shallow
                ns0: arr of nodes shallow; nsr: arr of nodes recursive */
      // --------- subsets & supersets ---------
      rs0_subsets: function () {
        return this.drels_subs.filter(e => e.key === this.me)
      },
      nsr_subsets: function () {
        return DAGVisitor.newTraversal(this.drels_subs, e => e.key, e => e.val, this.me)
          .filter(n => n !== this.me)
      },
      rs0_supersets: function () {
        return this.drels_subs.filter(e => e.val === this.me)
      },
      nsr_supersets: function () {
        return DAGVisitor.newTraversal(this.drels_subs, e => e.val, e => e.key, this.me)
          .filter(n => n !== this.me)
      },
      // -------- subtopics & supertopics ---------
      rs0_subtopics: function () {
        return this.drels_gech.filter(e => e.key === this.me)
      },
      ns0_subtopics: function () {
        return this.drels_gech.filter(e => e.key === this.me).map(e => e.val)
      },
      nsr_subtopics: function () {
        return DAGVisitor.newTraversal(this.drels_gech, e => e.key, e => e.val, this.me)
          .filter(n => n !== this.me)
      },
      rs0_supertopics: function () {
        return this.drels_gech.filter(e => e.val === this.me)
      },
      ns0_supertopics: function () {
        return this.drels_gech.filter(e => e.val === this.me).map(e => e.key)
      },
      nsr_supertopics: function () {
        return DAGVisitor.newTraversal(this.drels_gech, e => e.val, e => e.key, this.me)
          .filter(n => n !== this.me)
      },

      // -------- definitions & instances ---------
      rs0_definitions: function () {
        return this.drels_inst.filter(e => e.val === this.me)
      },
      ns0_definitions: function () {
        return this.drels_inst.filter(e => e.val === this.me).map(e => e.key)
      },
      nsr_definitions: function () {
        let arrs = this.ns0_definitions.map(def0 =>
          DAGVisitor.newTraversal(this.drels_subs, e => e.val, e => e.key, def0))
        return arrs.reduce((S, A) => _.union(S, A), [])   // lodash's array union
      },
      rs0_instances: function () {
        return this.drels_inst.filter(e => e.key === this.me)
      },
      ns0_instances: function () {
        return this.drels_inst.filter(e => e.key === this.me).map(e => e.val)
      },
      nsr_instances: function () {
        let subsets = _.union(Array.from(this.nsr_subsets), [this.me])
        let arrs = subsets.map(subdef =>
          this.drels_inst.filter(e => e.key === subdef).map(e => e.val))
        return arrs.reduce((S, A) => _.union(S, A), [])
      },

      ns0_attr1: function () {
        return this.grels_attr1.filter(e => e.key === this.me).map(e => e.val)
      },

      /* rmg: rel map grouped */
      rmg_attr1: function () {
        return Objects.sortObject(_.mapValues(
          _.groupBy(this.grels_attr1, this.rel_attr_mapper),
          rels => _.sortBy(rels, ['attr', 'attrx', 'vno'])
        ))
      },
      rmg_attr2: function () {
        return Objects.sortObject(_.mapValues(
          _.groupBy(this.grels_attr2, this.rel_attr_mapper),
          rels => _.sortBy(rels, ['attr', 'attrx', 'vno'])
        ))
      },

      ns0_attributes2: function () {
        return this.word.ge2Rels.filter(e => e.key === this.me).map(e => e.val)
      },
      ns0releferences: function () {
        return this.word.ge1Rels.filter(e => e.val === this.me).map(e => e.key)
      }
    },
    created () {
      let params = this.$route.params
      this.word.text = params.text
      console.log(this.word)
      this.loadItem()
    },
    methods: {
      /**
       * add alias rel
       */
      add$ar () {
        const self = this
        let v = self.editor.newAliasText
        if (!v) return
        let rel = {key: this.word.text, val: v}
        DictApi.aliasRels.httpPost(rel, self.notifyOkay('添加别名'), self.notifyFail('添加别名'))
      },
      // ------------ dualRels ---------------
      /**
       * add dual rel
       * @param evt
       */
      add$dr (evt) {
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
      del$dr (rel) {
        const self = this
        DictApi.dualRels.httpDelete(rel, self.notifyOkay('移除关联'), self.notifyFail('移除关联'))
      },

      // ------------ gel1Rels ---------------
      /**
       * append ge1Rel val
       * @param relsOld
       * @param evt
       */
      add$gr1v (relsOld, evt) {
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
      del$gr1v (rel) {
        const self = this
        DictApi.ge1Rels.httpDelete(rel, self.notifyOkay('移除属性值'), self.notifyFail('移除属性值'))
      },
      /**
       *  add ge1Rel
       */
      add$gr1 () {
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
      del$gr1 (attr) {
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
      add$gr2v (relsOld, evt) {
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
       * @param rel
       */
      del$gr2v (rel) {
        const self = this
        DictApi.ge2Rels.httpDelete(rel, self.notifyOkay('移除属性II值'), self.notifyFail('移除属性II值'))
      },
      /**
       * add ge2Rel
       */
      add$gr2 () {
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
      del$gr2 (attr) {
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
      url$w (word) {
        return 1
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
        }
      },
      notifyFail (what) {
        const self = this
        return d => {
          self.$notify.error({title: what + '失败', message: d.message, duration: 0})
        }
      },

      // -------- utils --------
      rel_attr_mapper (rel1) {
        return rel1.attr + (rel1.attrx ? '(' + rel1.attrx + ')' : '')
      },
      rel_to_str (rel) {
        if (!rel) return ''
        return rel.key + '[' + rel.attr + '] -->' + rel.val
      },
      translate_pred (pred) {
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

  .Tag {
    margin: 3px;
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
