<template>
  <div>
    <h1>{{model.name}}管理</h1>
    <div class="pane criteria-pane">
      <slot name="criteria-pane"></slot>
    </div>
    <div class="pane control-pane">
      <slot name="control-pane">

        <ButtonGroup shape="circle">
          <Button type="ghost" icon="ios-plus-empty" @click="data.editor.item=null,ui.editing=true">新增</Button>
          <Button type="ghost" icon="ios-minus-empty" disabled></Button>
        </ButtonGroup>
        <span class="rt">
          <Button type="primary" shape="circle" icon="ios-search" @click="readItems()">搜索</Button>
        </span>
      </slot>
    </div>
    <div class="pane result-pane">
      <slot name="result-pane">

        <Table border :loading="ui.loading" :columns="computedModel.columns" :data="data.result.items"></Table>
        <div class="pager rt">
          <Page :page-size-opts="[3,5,10, 20 ,30, 50, 100]"
                :page-size="querier.pageSize" :current.sync="querier.pageNum" :total="data.result.totalCount"
                size="small" show-total show-elevator show-sizer></Page>
        </div>
      </slot>
    </div>


    <slot name="editorDialog">
      <Modal v-model="ui.editing" :loading="ui.saving" :title="editorTitle" :ok-text="editorOkText"
             @on-ok="saveItem()" @on-cancel="">
        <Form :model="data.editor.item" :label-width="80">
          <template v-for="col in computedModel.columns">
            <FormItem :label="col.title">
              <template v-if="col.key && col.editable">
                <Input v-model="data.editor.item[col.key]" :placeholder="'请输入'+col.key"></Input>
              </template>
              <template v-else>
                {{data.editor.item[col.key]}}
              </template>

            </FormItem>
          </template>
        </Form>
      </Modal>


    </slot>

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
        const self = this
        const columnDefault = {
          title: '',
          required: false,
          editable: true,
          sortable: false,
          type: 'string',
          render: (item, column, items) => column ? item[column.name] : 'NA11'
        }
        let columns = this.model.columns.map(x => {
          let y = {}
          y.key = x.key
          y.title = x.title || columnDefault.title
          y.render = x.render
          y.required = x.required || columnDefault.required
          y.editable = x.editable || columnDefault.editable
          y.sortable = x.sortable || columnDefault.sortable

          if (x['sk-template'] === 'action') {
            y.editable = false
            y.render = (h, params) => {
              return h('div', {}, [
                rPopDel(self)(h, params),
                rBtnEdit(self)(h, params)
              ])
            }
            console.log(y)
          }

//          if (y.type === 'sk-template' && y.template === 'sk.man2.state') {
//            const stateMap = {'A': '活动', 'S': '停止'}
//            y.render = (item, column) => stateMap[item[column.name]]
//          }
          return y
        })
        return {
          columns
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
      editorOkText () {
        return this.ui.saving ? '保存中...' : '保存'
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

  const rBtnEdit = vue => (h, params) => h('Button', {
    props: {
      type: 'ghost',
      shape: 'circle',
      icon: 'edit'
    },
    style: {
      marginRight: '5px'
    },
    on: {
      click: () => {
        vue.data.editor.item = params.row
        vue.ui.editing = true
      }
    }
  }, '编辑')

  const rPopDel = vue => (h, params) => h(
    'Poptip',
    {
      props: {
        confirm: true,
        title: '您确认删除这条内容吗？'
      },
      on: {
        'on-ok': () => {
          vue.data.deleter.item = params.row
          vue.deleteItem()
        },
        'on-cancel': () => {}
      }
    },
    [
      h('Button', {
        props: {
          type: 'error',
          shape: 'circle',
          icon: 'trash-a'
        },
        style: {
          marginRight: '5px'
        }
      }, '删除')
    ]
  )
</script>

<style scoped>
  .pane {
    margin: 5px auto;
  }

  .rt {
    float: right;
  }

  .pager {
    margin: 5px auto;
  }

</style>
