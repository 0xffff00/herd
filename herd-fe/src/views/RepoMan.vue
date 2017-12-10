<template>
  <TableMan :data="data" :ui.sync="ui" :model="model"
            :querier="querier">
    <div slot="criteria-pane">
      <div>
        <p>
        <Button type="warning" @click="actOnRepos('sync')">同步仓库</Button>
        <Button type="warning" @click="actOnRepos('sync.path')">同步Path</Button>
        <Button type="warning" @click="actOnRepos('sync.info.brief')">同步简要信息</Button>
        <Button type="warning" @click="actOnRepos('sync.info.senior')">同步高级信息</Button>
        </p>
        <p>
          <Button type="error" @click="actOnRepos('clear')">清空仓库</Button>
          <Button type="error" @click="actOnRepos('clear.path')">清空Path</Button>
          <Button type="error" @click="actOnRepos('clear.info.brief')">清空简要信息</Button>
          <Button type="error" @click="actOnRepos('clear.info.senior')">清空高级信息</Button>

        </p>
        <p>
          <Button type="warning" @click="actOnRepos('convert2jpg.1Kq5')">jpg批量压缩</Button>
        </p>
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
      </div>
      <div>
        <!--<el-tag v-for="crit in querier.criteria" v-if="crit.value" :key="crit.name" :closable="true"-->
        <!--@close="removeCriterion(crit)" type="primary">-->
        <!--{{crit.name}} {{crit.operator}} {{crit.value}}-->
        <!--</el-tag>-->

      </div>
    </div>


  </TableMan>
</template>
<script>
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import TableMan from '../components/TableMan'

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
          api: herdApi.repoRestApi,
          name: '仓库',
          columnDefault: {
            required: false,
            editable: true,
            sortable: true,
            type: 'string'
          },
          columns: [
            {
              key: 'name',
              title: '名称',
              sortable: true
            },
            {
              key: 'absPath',
              title: '绝对路径',
              sortable: true
            },
            {
              key: 'state',
              title: '状态',
              'sk-template': 'sk.man2.state'
            },
            {
              title: '操作',
              key: 'action',
              'sk-template': 'action'
            }
          ]
        },
        ui: {
          loadTick: 0,
          saving: false,
          loading: false,
          editing: false,
          editorLabelWidth: '140px',
          editorTitle: '',
          deleteConfirming: false
        }

      }
    },
    computed: {
      'ui.editor.title': function () {
        let n = this.itemName
        return (this.ui.currItem) ? '新增' + n : '修改' + n
      }
    },
    methods: {
      actOnRepos (action) {
        herdApi.ajaxActOnRepo(action)(null, this.notifyOkay(action), this.notifyFail(action))
      },
      notifyOkay (actionName) {
        const self = this
        return d => {
          let msg = d.message ? d.message : (d.totalAffected ? d.totalAffected + '个条目已' + actionName : '')
          self.$notify.success({title: actionName + '成功', message: JSON.stringify(msg)})
        }
      },
      notifyFail (actionName) {
        const self = this
        return d => {
          let msg = d.message || ''
          if (d.debugInfo) {
            msg += d.debugInfo.message
          }
          self.$notify.error({title: actionName + '失败', message: msg, duration: 0})
        }
      }
    },
    components: {TableMan}
  }
</script>
<style scoped>

</style>
