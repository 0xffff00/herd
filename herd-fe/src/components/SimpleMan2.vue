<template>

  <div>
    <h1>{{itemName}}  Simple Man</h1>
    <slot name="criteriaPane"></slot>


    <slot name="dataTable"></slot>

    <!--<el-table :data="result.items" border>-->
    <!---->
    <!--</el-table>-->
  </div>
</template>

<script>
  export default {
    name: 'simple-man2',
    data: function () {
      return {
        gui: {
          loading: 0,
          editing: 0
        }
      }
    },
    computed: {
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
      }
    },
    props: {
      itemName: {type: String, default: '条目'},
      crudApi: {type: Object},
      result: {type: Object},
      querier: {type: Object},
      editor: {type: Object},
      ui: {type: Object, default: {loadTick: 0}}

    },
    methods: {
      readItems () {
        let self = this
        self.gui.loading = 1
        self.crudApi.readListAndCount(
          self.translatedQuerierParams,
          d => {
            self.result.items = d.items
            self.result.totalCount = parseInt(d.totalCount)
            self.$message({type: 'success', message: '加载成功 x ' + self.ui.loadTick})
            self.gui.loading = 0
          },
          d => {
            self.$notify.error({
              title: '加载失败',
              duration: 0,
              message: d && d.message
            })
            self.gui.loading = 0
          }
        )
      },
      deleteItem (item) {

//        this.$confirm('此操作将永久删除该条目, 是否继续?', '提示', {
//          confirmButtonText: '确定',
//          cancelButtonText: '取消',
//          type: 'warning'
//        }).then(() => {
//          this.$http
//            .delete(buildSoloUrl(URL0, item))
//            .then(
//              resp => {
//                this.$notify.success({
//                  title: '删除成功'
//                })
//                this.editor.ui.saving = false
//                this.editor.ui.visible = false
//                this.loadItems()
//              },
//              resp => {
//                this.$notify.error({
//                  title: '删除失败',
//                  duration: 0,
//                  message: resp.body && resp.body.message
//                })
//                this.editor.ui.saving = false
//                this.editor.ui.visible = false
//                this.loadItems()
//              })
//        }).catch(() => {
//          this.$message({
//            type: 'info',
//            message: '已取消删除'
//          })
//        })

      },
      saveItem () {
        this.editor.ui.saving = true
        let itemNew = _.pick(this.editor.item, afCols)
        let itemOld = this.editor.itemOld
        if (this.editor.isNew) {
          Vue.http
            .post(URL0, itemNew)
            .then(
              resp => {
                this.$notify.info({
                  title: '创建成功',
                  message: itemNew.text
                })
                this.editor.ui.saving = false
                this.editor.ui.visible = false
                this.loadItems()
              },
              resp => {
                this.$notify.error({
                  title: '创建失败',
                  duration: 0,
                  message: resp.body && resp.body.message
                })
                this.editor.ui.saving = false
                this.editor.ui.visible = false
                this.loadItems()
              })
        } else {
          this.$http
            .put(buildSoloUrl(URL0, itemOld), itemNew)
            .then(
              resp => {
                this.$notify.success({
                  title: '更新成功'
                })
                this.editor.ui.saving = false
                this.editor.ui.visible = false
                this.loadItems()
              },
              resp => {
                this.$notify.error({
                  title: '更新失败',
                  duration: 0,
                  message: resp.body && resp.body.message
                })
                this.editor.ui.saving = false
                this.editor.ui.visible = false
                this.loadItems()
              })

        }
      },
      openEditorDialog () {
        this.ui.currItem = item || this.editor.itemDefault
        this.editor.itemOld = _.cloneDeep(this.editor.item)
        this.gui.editing = 1

      }
      ,
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
      }

    }
  }
</script>
<style scoped>


</style>
