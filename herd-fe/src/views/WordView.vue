<template>
  <div id="word-viewer">
    <h1>{{word.text}}
      <a :href="link(word.text)">
        <Button type="info" shape="circle" size="small">编辑</Button>
      </a>
    </h1>
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
  import WordEdit from './WordEdit.vue'
  import _ from 'lodash'

  export default {
    name: 'word-edit',
    extends: WordEdit
  }
</script>
<style scoped>
  #word-edit a {
    text-decoration: none;
    color: inherit;
  }
  #word-viewer a {
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
