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

    <Modal v-model="sync.running || sync.ui.informing" :title="sync.ui.title"
           @on-cancel="sync.ui.informing=false">
      <Progress :percent="syncPercent" status="active"></Progress>

      <b>{{sync.current}}/{{sync.totalSteps}}</b>
      <div style="height: 4em;">{{sync.currentMessage}}</div>
      <div slot="footer">
      </div>
    </Modal>
  </div>
</template>
<script>
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import TableMan from '../components/TableMan'
  import { translateResp } from '../utils/RestUtils'

  class HeartbeatMonitor {

    /**
     * @param model an obj like {running,current,totalSteps,currentMessage}, may be vue's data
     * @param statusGetApi a GET must respond Skean JobStatus
     * @param status2GetApi a GET must respond Skean JobStatus
     */
    constructor (model, statusGetApi, status2GetApi = null) {
      this.model=model
      this.statusGetApi = statusGetApi
      this.status2GetApi = status2GetApi || statusGetApi
    }

    beat(){
      statusGetApi(d => {
        this.model.running = d.data.running
        this.model.current = d.data.current
        this.model.totalSteps = d.data.totalSteps
        this.model.currentMessage = d.data.currentMessage
        if (!self.sync.running) {
          this.stop()
          this.model.ui.informing = true
          this.model.ui.title = '同步结束'
          this.model.current = this.model.totalSteps
        }
      }, d => {
        this.notifyFail('')
        this.stop()
      })
    }
    start (func, interval = 500, timeoutSec = 60 * 15) {
      console.info(`starting HeartbeatMonitor[id=${this.id}]...`)
      this.id = setInterval(this.beat, interval)
      setTimeout(() => {
        clearInterval(this.id)
      }, timeoutSec * 1000)
    }

    stop () {
      console.info(`stopping HeartbeatMonitor[id=${this.id}]...`)
      clearInterval(this.id)
    }
  }

  export default {
    name: 'repo-man',
    data () {
      return {
        sync: {
          ui: {
            title: null,
            informing: false,
            heartbeatMonitor: null
          },
          running: false,
          current: 0,
          totalSteps: 0,
          currentMessage: null
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
      'ui.editor.title': function () {
        let n = this.itemName
        return (this.ui.currItem) ? '新增' + n : '修改' + n
      },
      'syncPercent': function () {
        return Math.floor(this.sync.current / this.sync.totalSteps * 100)
      }
    },
    methods: {
      startBatchSyncByRepo (repo) {
        let self = this
        herdApi.jobs.batchSync.start({repoName: repo.name}, () => {}, this.notifyFail('s'))
        self.sync.current = 0
        self.sync.running = true
        self.sync.ui.title = '正在同步...'
        let hbm = self.sync.heartbeatMonitor = new HeartbeatMonitor(
          herdApi.jobs.batchSync.status, herdApi.jobs.batchSync.statusAll)
        hbm.start(() => {
          herdApi.jobs.batchSync.status(d => {
            self.sync.running = d.data.running
            self.sync.current = d.data.current
            self.sync.totalSteps = d.data.totalSteps
            self.sync.currentMessage = d.data.currentMessage
            if (!self.sync.running) {
              hbm.stop()
              self.sync.ui.informing = true
              self.sync.ui.title = '同步结束'
              self.sync.current = self.sync.totalSteps
            }
          }, d => {
            this.notifyFail('')
            hbm.stop()
          })
        })
      },
      truncateRepo (repo) {
        const self = this
        herdApi.jobs.mediaRepos.truncate(
          {repoName: repo.name},
          d => {
            console.log(d)
            self.$Notice.success({title: `清空${repo.name}成功`, message: `删除${d.data}条记录`})
          },
          self.notifyFail(`清空${repo.name}`))
      },
      notifyOkAf (actionName) {
        const self = this
        return d => {
          console.log(d)
          let msg = ''
          if (d.data && d.data.counts) {
            msg = d.data.summary
          } else if (d.totalAffected) {
            msg = d.totalAffected + '个条目已' + actionName
          }
          self.$Notice.success({title: actionName + '成功', desc: msg})
        }
      },
      notifyFail (actionName) {
        const self = this
        return d => {
          let resp2 = translateResp(actionName, d)
          self.$Notice.error({title: resp2.title, desc: resp2.body, duration: 0})
        }
      }
    },
    components: {TableMan}
  }
</script>
<style scoped>

</style>
