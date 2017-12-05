<template>
  <div id="word-viewer">
    <h1>{{word.text}} - 详情</h1>
    <h2>描述</h2>
    <div>
      {{word.desc}}
    </div>
    <h2>别名({{word.aliasRS0.length}})</h2>
    <div id="alias">
      <Tag v-for="r in word.aliasRS0" :key="r.no" @on-close="delBR(r,'别名')">
        <a :href="link(r.dst)">{{r.dst}}</a>
      </Tag>
    </div>

    <h2>定义与实例</h2>
    <div id="def-and-inst">
      <h3>所有超类({{definitionESA.length}})</h3>
      <Tag v-for="e in definitionESA" :key="e"><a :href="link(e)">{{e}}</a></Tag>

      <h3>所有实例({{instanceESA.length}})</h3>
      <Tag v-for="e in instanceESA" :key="e"><a :href="link(e)">{{e}}</a></Tag>
    </div>

    <h2>集合</h2>
    <div id="subset-and-superset">
      <h3>所有子集({{subsetESR.length}})</h3>
      <Tag v-for="e in subsetESR" :key="e"><a :href="link(e)">{{e}}</a></Tag>
      <h3>所有超集({{supersetESR.length}})</h3>
      <Tag v-for="e in supersetESR" :key="e"><a :href="link(e)">{{e}}</a></Tag>
    </div>

    <h2>相关话题</h2>
    <div id="subtopic-and-supertopic">
      <h3>所有子话题</h3>
      <Tag v-for="e in subtopicESR" :key="e"><a :href="link(e)">{{e}}</a></Tag>
      <h3>所有父话题</h3>
      <Tag v-for="e in supertopicESR" :key="e"><a :href="link(e)">{{e}}</a></Tag>
    </div>

    <h2>其他属性和引用</h2>
    <div id="x1-rels">
      <h3>属性</h3>
      <ul>
        <li v-for="(rels,attrVO) in attributeRMG">
          <span class="attrName">{{attrVO}}</span> {{pred2str(rels[0].pred)}}
          <Tag v-for="r in rels">
            <a :href="link(r.dst)">{{r.dst}}</a>
          </Tag>
        </li>
      </ul>

      <h3>引用</h3>
      <ul>
        <li v-for="r in word.referenceRS0">
          <a :href="link(r.src)">{{r.src}}</a> 的 {{r.attr}} {{pred2str(r.pred)}} {{r.dst}}
        </li>
      </ul>
    </div>

  </div>

</template>

<script>
  import WordEdit from '@/views/WordEdit'
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
   * @param theRSR RSR
   * @param forward
   * @returns {Array}
   */
  function fetchESR (me, theRSR, forward) {
    let visitor = forward
      ? new DAGVisitor(theRSR, r => r.src, r => r.dst)
      : new DAGVisitor(theRSR, r => r.dst, r => r.src)
    visitor.visitFrom(me)
    let res = new Set(visitor.getVerticesVisited())
    res.delete(me)
    return Array.from(res) // Set => arr
  }

  // utils
  const rel2id = rel => rel.src + '-' + rel.attr + '-' + rel.no
  const rel2attr = rel => rel.attr + (rel.attrx ? '(' + rel.attrx + ')' : '')
  const rel2str = (rel) => {
    if (!rel) return ''
    return rel.key + '[' + rel.attr + '] -->' + rel.val
  }
  const getMaxNo = (rels) => _.max(rels, r => r.no)
  const predMap = {'IS': '是', 'ARE': '仅有', 'HAS': '有'}

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
        ui: {
          inputVisible: false,
          loading: false
        },
        editor: {
          x1RelToAdd: {pred: 'IS'}
        },
        predMap: predMap
      }
    },
    computed: {
      me: function () {
        return this.word.text
      },

      // --------- 6 RS0 ---------
      subsetRS0: function () {
        let self = this
        return self.word.subsetRSR.filter(r => r.src === self.word.text)
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

      // --------- 6 ESR/ESA ---------
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
        return fetchESR(this.word.text, this.word.supertopicRSR, false)
      },

      // ----- temp ------
      attributeRMG: function () {
        return Objects.sortObject(_.mapValues(
          _.groupBy(this.word.attributeRS0, this.rel2attr),
          rels => _.sortBy(rels, ['attr', 'attrx', 'no'])
        ))
      }

    },

    created () {
      let params = this.$route.params
      this.word.text = params.text
      this.loadItem()
    },
    methods: {
      /**
       * add a basic relation
       * @param event
       * @param forward forward(src->dst) or backward(dst->src)
       * @param attr
       * @param theES existed ES0
       * @param attrName
       */
      addBR (event) {
        let actionName = event.target.placeholder
        let inputName = event.target.name
        let attr = inputName.slice(0, 4)
        let forward = inputName.slice(5, 6) === 'f'
        let v = event.target.value
        let w = this.word.text
        let rel = forward
          ? {src: w, dst: v, attr: attr}
          : {src: v, dst: w, attr: attr}
        event.target.value = null
        DictApi.basicRelations.httpPost(rel, this.notifyOkay(actionName), this.notifyFail(actionName))
      },
      /**
       * delete a basic relation
       */
      delBR (rel, attrName) {
        DictApi.basicRelations.httpDelete(rel, this.notifyOkay('移除' + attrName), this.notifyFail('移除' + attrName))
      },

      addX1RtoLast (siblingES, event) {
        let lastRel = siblingES[siblingES.length - 1]
        let v = event.target.value
        let w = this.word.text
        let rel = {src: w, attr: lastRel.attr, attrx: lastRel.attrx, pred: lastRel.pred, dst: v}
        DictApi.x1Relations.httpPost(rel, this.notifyOkay('添加属性'), this.notifyFail('添加属性'))
      },

      addX1R () {
        this.editor.x1RelToAdd.src = this.word.text
        DictApi.x1Relations.httpPost(this.editor.x1RelToAdd, this.notifyOkay('添加属性'), this.notifyFail('添加属性'))
      },
      delX1R (rel) {
        DictApi.x1Relations.httpDelete(rel, this.notifyOkay('移除属性'), this.notifyFail('移除属性'))
      },
      // delete X1R by example's (src,attr,attrx)
      delX1Rs (exampleRels) {
        let r0 = exampleRels[0]
        let r1 = {src: r0.src, attr: r0.attr, attrx: r0.attrx}
        if (!r1.src || !r1.attr) {
          this.$notify.error({title: '无法批量删除', message: '条件缺失:' + JSON.stringify(r1), duration: 0})
          return
        }
        DictApi.x1Relations.httpDeleteSome(r1, this.notifyOkay('移除属性'), this.notifyFail('移除属性'))
      },

      // ------------ misc ---------------
      /**
       * @param word
       * @return {String}
       */
      link (w) {
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
      },
      rel2id: rel2id,
      rel2attr: rel2attr,
      rel2str: rel2str,
      pred2str: (x) => predMap[x] || '='
    }
  }
</script>
<style scoped>
  #word-edit a {
    text-decoration: none;
    color: inherit;
  }

  .adder {
    width: 10em;
  }

  .attrName {
    font-weight: bold;
    color: #641;
  }

  .btn-del {
    color: rgb(192, 0, 0);
    border-color: rgba(192, 0, 0, .5);
  }

  .btn-add {
    color: rgba(0, 96, 0, .5);;
    border-color: rgba(0, 96, 0, .5);
  }
</style>
