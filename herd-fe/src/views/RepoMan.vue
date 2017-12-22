<template>
  <div>
    <TableMan :data="data" :ui.sync="ui" :model="model"
              :querier="querier">
      <div slot="criteria-pane">
        <div>

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
    <AsyncProgressBox name="同步媒体库" :start-tick="batchSync.startTick" :apis="batchSync.apis"
                      :params="batchSync.params">

    </AsyncProgressBox>
  </div>
</template>
<script>
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import TableMan from '../components/TableMan'
  import AsyncProgressBox from '../components/AsyncProgressBox.vue'

  export default {
    name: 'repo-man',
    data () {
      return {
        batchSync: {
          startTick: 0,
          params: null,
          apis: herdApi.jobs.batchSync

        },
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
          pageSize: 10,
          criteria: []
        },
        model: {
          api: herdApi.mediaRepos,
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
              key: 'path',
              title: '绝对路径',
              sortable: true
            },
            {
              key: 'state',
              title: '状态',
              sk2template: 'state'
            },
            {
              key: 'fileCnt',
              title: '文件数',
              sortable: false,
              editable: false
            },
            {
              key: 'imgCnt',
              title: '图像数',
              sortable: false,
              editable: false
            },
            {
              title: '操作',
              key: 'action',
              sk2template: 'action',
              sk2actions: [
                'edit',
                'delete',
                {actionName: '同步', actionFunc: this.startBatchSyncByRepo},
                {actionName: '清空', actionFunc: this.truncateRepo}
              ]
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
    },
    methods: {
      startBatchSyncByRepo (repo) {
        let self = this
        self.batchSync.params = {repoName: repo.name}
        self.batchSync.startTick++
      },
      truncateRepo (repo) {
        const self = this
        herdApi.jobs.mediaRepos.truncate({repoName: repo.name})(resp2 => {
          console.log(resp2)
          if (resp2.ok) {
            self.$Notice.success({title: `清空${repo.name}成功`, message: `删除${resp2.data}条记录`})
          } else {
            self.msgBox.show(resp2)
          }
        })
      }
    },
    components: {TableMan, AsyncProgressBox}
  }
</script>
<style scoped>

</style>
