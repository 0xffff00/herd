<template>
  <simpleMan2 :itemName="itemName" :crudApi="crudApi" :result="result" :ui="ui"
              :querier="querier" :editor="editor">
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
    <div id="optHeadBar">
      <el-button type="success" @click="ui.editTick++"><i class="fa fa-plus"></i>新增</el-button>

    </div>


    <div slot="dataTable">
      <el-table :data="result.items" border>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="absPath" label="绝对路径"></el-table-column>
        <el-table-column prop="url" label="url"></el-table-column>
        <el-table-column prop="saveTime" label="保存时间"></el-table-column>
        <el-table-column prop="state" label="状态"></el-table-column>
        <el-table-column label="">
          <template scope="scope">
            <el-button @click="ui.currItem=scope.row,ui.editTick++" size="small">
              <i class="fa fa-pencil"></i></el-button>
            <el-button @click="ui.currItem=scope.row,ui.deleteTick++" size="small" type="danger">
              <i class="fa fa-trash-o"></i></el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page.sync="querier.pageNum" :page-size.sync="querier.pageSize" :total="result.totalCount"
        layout="total, sizes, prev, pager, next, jumper" :page-sizes="[3,5,10, 20 ,30, 50, 100]">
      </el-pagination>

    </div>


    <el-dialog :title="ui.editor.title" :visible.sync="ui.editor.visible"
               show-close>
      <el-form :model="editor.item">
        <el-form-item label="名称" :label-width="ui.editor.labelWidth" required>
          <el-input v-model="editor.item.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="绝对路径" :label-width="ui.editor.labelWidth">
          <el-input v-model="editor.item.absPath"></el-input>
        </el-form-item>
        <el-form-item label="状态" :label-width="ui.editor.labelWidth">
          <el-input v-model="editor.item.state"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editor.ui.visible = false">取消</el-button>
        <el-button type="primary" @click="saveItem" :loading="ui.editor.saving">{{editor.ui.saving ? '保存中...' : '保存'}}
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="同步新的词条" :visible.sync="tempTextImporter.ui.visible" show-close>
      <el-tag v-for="text in tempTextImporter.texts" key="text">
        {{text}}
      </el-tag>
      <div slot="footer" class="dialog-footer">
        <el-button @click="tempTextImporter.ui.visible = false">取消</el-button>
        <el-button type="primary" @click="importTempTexts" :loading="tempTextImporter.ui.saving">
          {{tempTextImporter.ui.saving ? '保存中...' : '保存'}}
        </el-button>
      </div>
    </el-dialog>


  </simpleMan2>
</template>
<script>
  import herdService from '../services/HerdService'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/TextUtils'
  import Arrays from '../utils/Arrays'
  import simpleMan2 from '../components/SimpleMan2'

  export default {
    name: 'repo-man',
    data () {
      return {
        itemName: '仓库',
        crudApi: herdService.repoCrudApi,
        result: {
          items: [],
          totalCount: 0
        },
        querier: {
          orderBy: null,
          pageNum: 1,
          pageSize: 3,
          criteria: []
        },
        editor: {
          item: {},
          itemOld: {}
        },
        ui: {
          loadTick: 0,
          editTick: 0,
          saving: 1,
          editor: {
            visible: 1,
            labelWidth: 80
          }
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
      pageSizeChanged (size) {
        this.querier.pageSize = size
        this.loadItems()
      },
      pageCurrentChanged (current) {
        this.querier.pageIndex = current
        this.loadItems()
      },
      sortChanged (e) {
        if (e.prop) {
          this.querier.orderBy = (e.order === 'descending' ? '-' : '') + e.prop
        } else {
          delete this.querier.orderBy
        }

        this.loadItems()
      }
    },
    components: {simpleMan2}
  }
</script>
<style scoped>

</style>
