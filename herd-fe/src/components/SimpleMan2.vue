<template>
  <div>
    <h1>{{model.name}}  Simple Man</h1>
    <slot name="criteriaPane"></slot>

    <slot name="controlPane">
      <el-button type="success" @click="data.editor.item=null,ui.editing=true">
        <i class="fa fa-plus"></i>新增
      </el-button>
    </slot>
    <slot name="dataTable">
      <table class="sk-data-table">
        <thead>
        <tr>
          <th v-for="col in computedModel.columns" :col="col.name">{{col.title}}</th>
        </tr>
        </thead>
        <tbody>
        <h3 v-if="data.result.items.length==0">没有数据</h3>
        <tr v-for="item in data.result.items">

          <td v-for="col in computedModel.columns">
            <template v-if="col.type==='sk-template'">

              <template v-if="col.template==='sk.man2.action'">
                <template v-for=" action in col.actions">

                  <template v-if="action==='edit'">
                    <el-button @click="data.editor.item=item,ui.editing=true" size="small">编辑</el-button>
                  </template>
                  <template v-else-if="action==='delete'">
                    <el-button @click="data.deleter.item=item" size="small">删除</el-button>
                  </template>
                  <template v-else>(未知action)</template>
                </template>
              </template>

              <template v-else-if="col.template==='sk.man2.state'">
                [{{col.render(item, col, data.result.items)}}]
              </template>

              <template v-else>(未知模版)</template>
            </template>

            <template v-else>
              {{col.render(item, col, data.result.items)}}
            </template>
          </td>
        </tr>
        </tbody>

      </table>

      <el-pagination
        :current-page.sync="querier.pageNum" :page-size.sync="querier.pageSize" :total="data.result.totalCount"
        layout="total, sizes, prev, pager, next, jumper" :page-sizes="[3,5,10, 20 ,30, 50, 100]">
      </el-pagination>
    </slot>

    <slot name="editorDialog">
      <el-dialog id="ddd" v-loading="true" target="#ddd" element-loading-text="拼命加载中"
                 :title="editorTitle" :visible.sync="ui.editing">
        <el-form :model="data.editor.item">
          <template v-for="col in computedModel.columns">
            <el-form-item :label="col.title" :label-width="ui.editorLabelWidth"
                          :required="col.required">
              <template v-if="col.name && col.editable">
                <el-input v-model="data.editor.item[col.name]"></el-input>
              </template>
              <template v-else>
                {{data.editor.item[col.name]}}
              </template>
            </el-form-item>
          </template>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="ui.editing=false">取消</el-button>
          <el-button type="primary" @click="saveItem" :loading="ui.saving">{{ui.saving ? '保存中...' : '保存'}}
          </el-button>
        </div>
        1s
        as
        sd

      </el-dialog>
    </slot>
    <!--<el-table :data="result.items" border>-->
    <!---->
    <!--</el-table>-->
  </div>
</template>

<script>
  import _ from 'lodash'

  export default {
    name: 'simple-man2',
    data () {
      return {}
    },
    computed: {
      computedModel () {
        const columnDefault = {
          title: '',
          required: false,
          editable: true,
          sortable: true,
          type: 'string',
          render: (item, column, items) => column ? item[column.name] : 'NA11',
          template: {
            'sk.man2.action': {
              actions: ['edit', 'delete']
            }
          }
        }
        let columns = this.model.columns.map(x => {
          let y = {}
          y.name = x.name
          y.title = x.title || columnDefault.title
          y.render = x.render || columnDefault.render
          y.required = x.required || columnDefault.required
          y.editable = x.editable || columnDefault.editable
          y.sortable = x.sortable || columnDefault.sortable
          y.type = x.type || columnDefault.type
          y.template = x.template

          if (y.type === 'sk-template' && y.template === 'sk.man2.action') {
            y.actions = x.actions || columnDefault.template['sk.man2.action'].actions
            y.editable = false
          }
          if (y.type === 'sk-template' && y.template === 'sk.man2.state') {
            const stateMap = {'A': '活动', 'S': '停止'}
            y.render = (item, column) => stateMap[item[column.name]]
          }
          return y
        })
        return {
          columns: columns
        }
      },
      translatedQuerierParams () {
        let q = this.querier
        let rp = {}
        if (q.pageSize) {
          rp.p = q.pageNum
          rp.l = q.pageSize
        }
        if (q.orderBy) {
          rp.o = q.orderBy
        }
        q.criteria.forEach(e => {
          let tail = e.operator ? '_' + e.operator : ''
          rp[e.name + tail] = e.value
        })
        return rp
      },
      editorTitle () {
        return this.model.name + ' - ' + (this.isEditingForCreate ? '新增' : '修改')
      },
      isEditingForCreate () {
        return this.data.editor.itemOld === null
      }
    },
    props: {
      'model.name': {type: String, default: '条目'},
      'model.api': {type: Object},
      data: {type: Object},
      querier: {type: Object},
      ui: {type: Object, default: {loadTick: 0}},
      model: {type: Object}

    },
    created () {
      this.readItems()
    },
    methods: {
      readItems () {
        let self = this
        self.ui.loading = true
        self.model.api.readListAndCount(
          self.translatedQuerierParams,
          d => {
            self.data.result.items = d.items
            self.data.result.totalCount = parseInt(d.totalCount)
            // self.$message({type: 'success', message: '加载成功 x ' + self.ui.loadTick})
            self.ui.loading = false
          },
          self.notifyFail('加载')
        )
      },
      deleteItem () {
        const self = this
        self.ui.deleting = true
        self.model.api.deleteOne(self.data.deleter.item, self.notifyOkay('删除'), self.notifyFail('删除'))
      },
      saveItem () {
        const self = this
        this.ui.saving = true
        const isNew = self.data.editor.itemOld === null
        console.log(self.data.editor)
        if (isNew) {
          self.model.api.createOne(self.data.editor.item, self.notifyOkay('创建'), self.notifyFail('创建'))
        } else {
          const changes = changesOfItem(self.data.editor.itemOld, self.data.editor.item, self.model.columns)
          self.model.api.updateOne(self.data.editor.itemOld, changes, self.notifyOkay('更新'), self.notifyFail('更新'))
        }
      },

      initEditor () {
        let item = this.data.editor.item
        this.data.editor.itemOld = _.cloneDeep(item)
        console.log(this.data.editor.itemOld, item)
        if (item === null) {
          this.data.editor.item = _.cloneDeep(this.data.editor.itemDefault)
        }
      },
      openConfirmDialog (actionDesc, nextAction) {
        this.$confirm('即将' + actionDesc + ', 是否继续?', '警告', {
          confirmButtonText: '是',
          cancelButtonText: '否',
          type: 'warning'
        }).then(nextAction)
          .catch(() => {
            this.$message({type: 'info', message: '已取消' + actionDesc})
          })
      },
      openDeleteConfirmDialog () {
        const self = this
        self.openConfirmDialog('删除该条目', () => { self.deleteItem() })
      },

      notifyOkay (what) {
        const self = this
        return d => {
          let msg = d ? (d.message || d.totalAffected) : ''
          self.$notify.success({title: what + '成功', message: msg})
          self.ui.editing = false
          self.ui.saving = false
          self.ui.deleting = false
          self.readItems()
        }
      },
      notifyFail (what) {
        const self = this
        return d => {
          self.$notify.error({title: what + '失败', message: d.message, duration: 0})
          self.ui.saving = false
          self.ui.deleting = false
          self.ui.loading = false
        }
      }
    },
    watch: {
      'ui.loadTick': function (v) {
        this.readItems()
      },
      'querier.pageNum': function (v) {
        this.readItems()
      },
      'querier.pageSize': function (v) {
        this.readItems()
      },
      'ui.editTick': function () {
        this.openEditorDialog()
      },
      'ui.editing': function (v) {
        if (v === true) {
          this.initEditor()
        }
      },
      'data.deleter.item': function (v) {
        console.log('ddd:', v)
        if (v === null) {
          return
        }
        this.openDeleteConfirmDialog()
      }
    }
  }

  function changesOfItem (newItem, oldItem, itemCols) {
    let keysPicked = Object.keys(newItem)
      .filter(k => itemCols.find(col => col.name === k && col.editable) !== null)
      .filter(k => oldItem[k] !== newItem[k])
    return _.pick(newItem, keysPicked)
  }

</script>
<style scoped>
  table.sk-data-table {
    width: 100%;
    background: #FFF;
    margin: 1em 0;
    border: 1px solid rgba(34, 36, 38, .15);
    box-shadow: none;
    border-radius: .28571429rem;
    text-align: left;
    color: rgba(0, 0, 0, .87);
    border-collapse: separate;
    border-spacing: 0;
  }

  table.sk-data-table th {
    cursor: auto;
    background: #F9FAFB;
    text-align: inherit;
    color: rgba(0, 0, 0, .87);
    padding: 5px 2px;
    vertical-align: inherit;
    font-weight: 700;
    text-transform: none;
    border: 1px solid rgba(34, 36, 38, .1);
  }

  table.sk-data-table td {
    color: rgba(0, 0, 0, .87);
    padding: 5px 2px;
    vertical-align: inherit;
    text-transform: none;
    border: 1px solid rgba(34, 36, 38, .1);
  }

</style>
