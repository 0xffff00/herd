<template>
  <simpleMan2 :data="data" :ui.sync="ui" :model="model"
              :querier="querier">
    <div slot="criteriaPane">
      <div>ssx
        <!--<el-form :inline="true" :model="querier">-->
        <!--<el-form-item label="词语">-->
        <!--<el-input v-model="querier.criteria[0].value" placeholder="词语"></el-input>-->
        <!--<el-select v-model="querier.criteria[0].operator">-->
        <!--<el-option label="部分匹配" value="contains"></el-option>-->
        <!--<el-option label="左匹配" value="startsWith"></el-option>-->
        <!--<el-option label="全匹配" value=""></el-option>-->

        <!--</el-select>-->
        <!--</el-form-item>-->

        <!--<el-form-item>-->
        <!--<el-button type="primary" @click="submitQuerierForm"><i class="fa fa-search"></i>查询</el-button>-->
        <!--</el-form-item>-->
        <!--</el-form>-->
        <el-button type="primary" @click="ui.loadTick++"><i class="fa fa-search"></i>查询</el-button>
      </div>
      <div>
        <!--<el-tag v-for="crit in querier.criteria" v-if="crit.value" :key="crit.name" :closable="true"-->
        <!--@close="removeCriterion(crit)" type="primary">-->
        <!--{{crit.name}} {{crit.operator}} {{crit.value}}-->
        <!--</el-tag>-->

      </div>
    </div>



  </simpleMan2>
</template>
<script>
  import herdService from '../services/HerdService'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import simpleMan2 from '../components/SimpleMan2'

  export default {
    name: 'repo-man',
    data () {
      return {
        data: {
          result: {
            items: [],
            totalCount: 0
          },
          editor: {
            item: {},
            itemOld: null,
            itemDefault: {}
          },
          deleter: {
            item: null,
            items: []
          }
        },
        querier: {
          orderBy: null,
          pageNum: 1,
          pageSize: 3,
          criteria: []
        },
        model: {
          api: herdService.repoRestApi,
          name: '仓库',
          columnDefault: {
            required: false,
            editable: true,
            sortable: true,
            type: 'string'
          },
          columns: [
            {
              name: 'name',
              title: '名称',
              required: true
            },
            {
              name: 'absPath',
              title: '绝对路径'
            },
            {
              name: 'state',
              title: '状态',
              type: 'sk-template',
              template: 'sk.man2.state'
            },
            {
              type: 'sk-template',
              template: 'sk.man2.action'
            }
          ]
        },
        ui: {
          loadTick: 0,
          saving: false,
          loading: false,
          editing: false,
          editorLabelWidth: '140px',
          editorTitle: ''
        }

      }
    },
    computed: {
      'ui.editor.title': function () {
        let n = this.itemName
        return (this.ui.currItem) ? '新增' + n : '修改' + n
      }
    },
    methods: {},
    components: {simpleMan2}
  }
</script>
<style scoped>

</style>
