<template>
  <div id="word-editor">
    <h1>{{herdObjText}} - 编辑中
      <a :href="linkV(text)">
        <Button type="info" shape="circle" size="small">预览</Button>
      </a>
      <ButtonGroup shape="circle" size="small">
        <Button type="ghost" icon="ios-plus-empty" @click="">保存</Button>
        <Button type="error" icon="trash-a" disabled>删除</Button>
      </ButtonGroup>
    </h1>
    <h2>相关话题</h2>
    <div id="subtopic-and-supertopic">

      <h3>直接父话题</h3>
      <Tag closable v-for="r in supertopicRS0" :key="r.no" @on-close="delBR(r,'父话题')">
        <a :href="link(r.src)">{{r.src}}</a></Tag>
      <Input name="SUBT-b" class="adder" size="small" placeholder="关联新父话题" @on-enter="addBR"/>
      <h3>所有父话题</h3>
      <Tag v-for="e in supertopicESR" :key="e"><a :href="link(e)">{{e}}</a></Tag>
    </div>

    <h2>其他属性和引用</h2>
    <div id="x1-rels">
      <h3>属性</h3>
      <ul>
        <li v-for="(rels,attrVO) in attributeRMGv21">
          <span class="x1rmg">
          <Button shape="circle" type="error" icon="close" class="btn-del" @click="delX1Rs(rels)"
                  size="small"></Button>
          <span class="attrName">{{attrVO}}</span>
            {{rels[0] ? pred2str(rels[0].pred) : '有'}}
          <Tag v-for="r in rels" closable @on-close="delX1R(r)">
            <a :href="link(r.dst)">{{r.dst}}</a>
          </Tag>
            <Input class="adder" size="small" placeholder="关联新属性值" @on-enter="addX1RtoLast(rels,$event,attrVO)"/>
            </span>

        </li>

        <li>
          <span class="x1rmg x1rmg-add">
            <Icon type="plus-round" class="icon-add"></Icon>
          <Input v-model="editor.x1RelToAdd.attr" placeholder="属性名" size="small" style="width:140px;"/>
          (<Input v-model="editor.x1RelToAdd.attrx" placeholder="补充属性名" size="small" style="width:80px;"/>)

          <Select title="谓词" v-model="editor.x1RelToAdd.pred" size="small" style="width:60px">
            <Option v-for="(txt,val) in predMap" :value="val" :key="val">{{txt}}</Option>
          </Select>
          <Input v-model="editor.x1RelToAdd.dst" placeholder="属性值，若填多个以空格隔开" size="small"
                 @on-enter="addX1R" style="width:280px;"/>
        </span>

        </li>

      </ul>


      <!--<h3>引用</h3>-->
      <!--<ul>-->
      <!--<li v-for="r in word.referenceRS0">-->
      <!--<a :href="link(r.src)">{{r.src}}</a> 的 {{r.attr}} {{pred2str(r.pred)}} {{r.dst}}-->
      <!--</li>-->
      <!--<li>-->
      <!--<span class="x1rmg x1rmg-add">-->
      <!--<Icon type="plus-round" class="icon-add"></Icon>-->
      <!--<Input v-model="editor.x1RelToAdd2.src" placeholder="主语，若填多个以空格隔开" size="small"-->
      <!--@on-enter="addX1R2" style="width:280px;"/>的-->
      <!--<Input v-model="editor.x1RelToAdd2.attr" placeholder="属性名" size="small" style="width:140px;"/>-->
      <!--(<Input v-model="editor.x1RelToAdd2.attrx" placeholder="补充属性名" size="small" style="width:80px;"/>)-->

      <!--<Select title="谓词" v-model="editor.x1RelToAdd2.pred" size="small" style="width:60px">-->
      <!--<Option v-for="(txt,val) in predMap" :value="val" :key="val">{{txt}}</Option>-->
      <!--</Select>-->
      <!--{{herdObjText}}-->

      <!--</span>-->

      <!--</li>-->
      <!--</ul>-->
    </div>

  </div>

</template>

<script>
  import _ from 'lodash'
  import DAGVisitor from '../utils/DAGVisitor'
  import Objects from '../utils/Objects'
  import DictApi from '../apis/DictApi'
  import MsgBox from '../components/MsgBox'

  /**
   * TODO simply copy from WordEditor, need refactor to high re-usage
   *
   * only uses topic and x1rels
   *
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
    name: 'img-word-edit',
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
          x1RelToAdd: {pred: 'IS'},
          x1RelToAdd2: {pred: 'IS'}
        },
        predMap: predMap
      }
    },
    props: {
      text: {type: String}
    },
    computed: {
      herdObjText () {
        if (this.text && this.text.startsWith('HD-IMG')) {
          let hash = this.text.slice(7, 15)
          return hash
        }
        return this.text
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
      },
      attributeRMGv21 () {
        let rmg = Objects.sortObject(_.mapValues(
          _.groupBy(this.word.attributeRS0, this.rel2attr),
          rels => _.sortBy(rels, ['attr', 'attrx', 'no'])
        ))
        let reservedAttr = ['拍摄者', '被拍摄者', '拍摄地点']
        reservedAttr.forEach(attr => {
          if (!rmg[attr]) rmg[attr] = []
        })
        return rmg
      }
    },
    watch: {
      text (v) {
        this.word.text = v
        this.loadItem()
      }
    },
    created () {
      this.word.text = this.text
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
        DictApi.basicRelations.httpPost(rel)(this.notify(actionName))
      },
      /**
       * delete a basic relation
       */
      delBR (rel, attrName) {
        DictApi.basicRelations.httpDelete(rel)(this.notify('移除' + attrName))
      },

      // TODO need re-use refactor
      addX1RtoLast (siblingES, event, attr) {
        let lastRel = siblingES.length ? siblingES[siblingES.length - 1] : {attr, pred: 'HAS'}
        let v = event.target.value
        let w = this.word.text
        let rel = {src: w, attr: lastRel.attr, attrx: lastRel.attrx, pred: lastRel.pred, dst: v}
        DictApi.x1Relations.httpPost(rel)(this.notify('添加属性'))
      },

      addX1R () {
        this.editor.x1RelToAdd.src = this.word.text
        DictApi.x1Relations.batchCreate(this.editor.x1RelToAdd)(this.notify('添加属性'))
      },
      addX1R2 () {
        this.editor.x1RelToAdd2.dst = this.word.text
        DictApi.x1Relations.batchCreate(this.editor.x1RelToAdd2)(this.notify('添加引用'))
      },
      delX1R (rel) {
        DictApi.x1Relations.httpDelete(rel)(this.notify('移除属性'))
      },
      // delete X1R by example's (src,attr,attrx)
      delX1Rs (exampleRels) {
        let r0 = exampleRels[0]
        let r1 = {src: r0.src, attr: r0.attr, attrx: r0.attrx}
        if (!r1.src || !r1.attr) {
          this.$notify.error({title: '无法批量删除', message: '条件缺失:' + JSON.stringify(r1), duration: 0})
          return
        }
        DictApi.x1Relations.httpDeleteSome(r1)(this.notify('移除属性'))
      },

      // ------------ misc ---------------
      /**
       * @param word
       * @return {String}
       */
      link (w) {
        return `../words/${w}/edit`
      },
      linkV (w) {
        return `../words/${w}/view`
      },
      loadItem () {
        const self = this
        let w = this.word.text
        if (!w) {
          return
        }
        DictApi.words.httpGet(this.word)(resp2 => {
          if (!resp2.ok) {
            MsgBox.open(self, '加载')
            return
          }
          self.$Message.success({content: '加载成功'})
          self.word = resp2.data
          self.ui.loading = false
        })
      },
      // notify after action, reload if OK
      notify (actionName) {
        const self = this
        return resp2 => {
          MsgBox.open(self, actionName)(resp2)
          if (resp2.ok) self.loadItem()
        }
      },
      rel2id: rel2id,
      rel2attr: rel2attr,
      rel2str: rel2str,
      pred2str: (x) => x !== null && predMap[x] || '='
    }
  }
</script>
<style>
  #word-editor a {
    text-decoration: none;
    color: inherit;
  }

  #word-editor .adder {
    width: 10em;
  }

  #word-editor .adder input {
    border-style: dashed;
    border-color: green;
    height: 22px;
  }

  .attrName {
    font-weight: bold;
    color: #641;
  }

  .btn-del {
    color: rgb(192, 0, 0);
    background: rgba(192, 0, 0, .1);
    border-color: rgba(192, 0, 0, .2);
  }

  .btn-add {
    color: rgba(0, 96, 0, .5);;
    border-color: rgba(0, 96, 0, .5);
  }

  #word-editor .ivu-tag-closable {
    border-style: solid;
    border-color: #aa0000;
    color: #c70000;
    background: transparent;
  }

  #word-editor .ivu-tag-closable .ivu-tag-text {
    color: #c70000;
  }

  #word-editor .ivu-tag-closable .ivu-icon {
    color: red;
  }

  #word-editor #x1-rels li {
    margin: 6px;
  }

  #word-editor #x1-rels li .x1rmg {
    border: none 1px #c70000;
    border-radius: 6px;
    margin: 3px 7px;
    padding: 5px 5px 7px;
  }

  #word-editor #x1-rels li .x1rmg.x1rmg-add {
    border: dashed 1px green;
  }

  #word-editor #x1-rels li .x1rmg.x1rmg-add input {
    border: solid 1px rgba(0, 72, 0, .1);
  }

  #word-editor .icon-add {
    color: green;
    margin: 0 7px;
  }
</style>
