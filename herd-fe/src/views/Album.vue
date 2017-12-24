<template>
  <div class="album2">
    <div class="sidebar">
      <Button size="small" @on-click="">无日期({{countByNullDate}})
      </Button>
      <TimeGrid :countByDate="countByDateObjArr" :cssDateTdHeight="15" :cssDateTdPadding="1"
                @clickDate="clickDateOfTimeGrid">
      </TimeGrid>
    </div>
    <div>


    </div>

    <!--{{imageInfosByY}}-->
    <div class="gallery">
      <div class="img-cate-big" v-for="rowByY in imageInfosByY" :id="'img-'+rowByY.y">
        <h2>{{rowByY.y}}({{rowByY.cnt}}/{{countMapByY[rowByY.y]}})</h2>
        <div class="img-cate-big" v-for="rowByYM in rowByY.byYM" :id="'img-'+rowByYM.ym">
          <h3>{{rowByYM.ym}}({{rowByYM.cnt}}/{{countMapByYM[rowByYM.ym]}})</h3>
          <div class="img-cate-mid" v-for="rowByYMD in rowByYM.byYMD" :id="'img-'+rowByYMD.ymd">
            <h4>{{rowByYMD.ymd}}({{rowByYMD.imgs.length}}/{{countMapByYMD[rowByYMD.ymd]}})</h4>
            <span class="img-hull" v-for="img in rowByYMD.imgs">
               <div class="img-mask">
                 <div class="opt">
                   <!--<Checkbox size="large"></Checkbox>-->
                 </div>
                 <div class="info">
                   HASH值：{{describeHash(img.hash)}}<br>
                   拍摄时间：{{describeExifDateTime(img.exifDateTime)}} <br>
                   相机：{{img.exifMake}} {{img.exifModel}}<br>
                   尺寸：{{describeImageSize(img.width, img.height)}}<br>
                   文件：{{img.type}}格式， {{describeFileSize(img.fileSize)}}<br>
                 </div>
               </div>
                <a @click="openViewerModal(img)">
                  <img :src="getUrlByHash(img.hash,'1Kq5')"/>
                </a>
            </span>
            <span class="img-hull img-hull-more"
                  v-if="countUnloadedImagesOfYMD(rowByYMD)>0">
              <a @click="loadMoreImageOfYMD(rowByYMD)">
              <span style="font-size:22px;">{{countUnloadedImagesOfYMD(rowByYMD)}}</span>
              <i class="fa fa-angle-double-right fa-2x" aria-hidden="true"></i></a>
            </span>
          </div>
        </div>
        <span class="img-hull img-hull-more">
              <a @click="loadMoreImageOfLast()">
              <span style="font-size:22px;">More</span>
              <i class="fa fa-angle-double-right fa-2x" aria-hidden="true"></i></a>
            </span>
      </div>
    </div>
    <Modal
      v-model="viewerModal.visible"
      :styles="{top: '0',margin:'10px',width:'auto'}">
      <div class="viewer-modal">
        <div class="main">
          <img :src="viewerModalImageUrl"/>
        </div>
        <div class="opt">
          <div v-if="viewerModal.imageInfo" class="info">
            HASH值：{{describeHash(viewerModal.imageInfo.hash)}}<br>
            拍摄时间：{{describeExifDateTime(viewerModal.imageInfo.exifDateTime)}} <br>
            相机：{{viewerModal.imageInfo.exifMake}} {{viewerModal.imageInfo.exifModel}}<br>
            尺寸：{{describeImageSize(viewerModal.imageInfo.width, viewerModal.imageInfo.height)}}<br>
            文件：{{viewerModal.imageInfo.type}}格式， {{describeFileSize(viewerModal.imageInfo.fileSize)}}<br>
          </div>
          <HerdWordEditor :text="viewerModal.imageDictId">
          </HerdWordEditor>
        </div>
        <div style="clear:both;"></div>
      </div>
      <div slot="header">
        <a :href="viewerModal.mediaFiles && viewerModal.mediaFiles.length && viewerModal.mediaFiles[0].path">
        {{viewerModal.mediaFiles && viewerModal.mediaFiles.length && viewerModal.mediaFiles[0].path}}</a>
      </div>
      <div slot="footer">
      </div>
    </Modal>

  </div>

</template>
<script>
  import _ from 'lodash'
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import Objects from '../utils/Objects'
  import TimeGrid from '../components/TimeGrid.vue'
  import moment from 'moment'
  import MsgBox from '../components/MsgBox'
  import HerdWordEditor from '../components/HerdWordEditor.vue'

  /**
   * code conventions:
   * Y - '2014'
   * YM - '2014-02'
   * YMD -'2014-02-04'
   */
  export default {
    name: 'Album',
    data () {
      return {
        // like [["2017-03-01",1],["2017-03-05",25]], ordered
        countByDateArr2d: [],
        countByNullDate: 0,
        // like [{hash,width,exifDateTime,fileSize..}..], ordered by exifDateTime asc
        imageInfos: [],
        // like imageInfo, but exifDateTime is null, ordered by hash
        imageInfosNoDT: [],

        maxImagesInWindow: 600,
        viewerModal: {
          visible: false,
          title: 'viewer',
          imageInfo: null,
          imageDictId: null,
          mediaFiles: null
        }
      }
    },
    computed: {
      // like ["2017-03-01""2017-03-05"], ordered
      involvedDates () {
        return this.countByDateArr2d.map(row => row[0])
      },
      involvedDateMax () {
        return this.involvedDates.length ? this.involvedDates[0] : null
      },
      involvedDateMin () {
        let len = this.involvedDates.length
        return len ? this.involvedDates[len - 1] : null
      },
      // like {"2017-03-01":1,"2017-03-05":25}
      countMapByYMD () {
        let res = {}
        this.countByDateArr2d.forEach(e => {
          res[e[0]] = e[1]
        })
        return res
      },
      // like {"2017-03":26,"2017-08":3}
      countMapByYM () {
        return _.mapValues(Objects.groupBy(this.countMapByYMD, (ymd, cnt) => DT.toYM(ymd)), _.sum)
      },
      // like {"2017":211,"2018":144}
      countMapByY () {
        return _.mapValues(Objects.groupBy(this.countMapByYM, (ym, cnt) => DT.toY(ym)), _.sum)
      },
      // like [{date:"2017-03-01",cnt:1},...,{date:"2017-03-05",cnt:25}]
      // but not same length as countByDateArr2d. it's filled all continuous dates even if cnt=0.
      countByDateObjArr () {
        let d = moment(this.involvedDateMin)
        let dmax = moment(this.involvedDateMax)
        let n = dmax.diff(d, 'd')
        let map = this.countMapByYMD
        let res = []
        for (let i = 0; i < n; d.add(1, 'd'), i++) {
          let str = d.format('YYYY-MM-DD')
          res.push({date: str, cnt: map[str] || 0})
        }
        return res
      },

      imageInfosByY () {
        const self = this
        const src = self.imageInfos
        let r = iis2byY(src)
        return r
      },
      viewerModalImageUrl () {
        if (!this.viewerModal.imageInfo) return null
        let hash = this.viewerModal.imageInfo.hash
        return this.getUrlByHash(hash, '1Kq5')
      }
    },
    created () {
      this.loadCounts()
      let h = window.location.hash
      if (h && h.length >= 9 && h.startsWith('#img-')) {
        let d = h.slice(5)
        this.loadImages({exifDateTime_GE: d, f: 0, l: 40})
      }
    },
    methods: {
      // load countByNullDate,countByDateArr2d
      loadCounts () {
        const self = this
        herdApi.imageInfos.countByDate(res => {
          if (!res.data || !res.data.length) return
          let len = res.data.length
          // console.log(res.data.slice(0, 218))
          if (res.data[len - 1][0] === null) {
            self.countByNullDate = res.data[len - 1][1]
            len--
          }
          self.countByDateArr2d = res.data.slice(0, len)
        })
      },
      countUnloadedImagesOfYMD (rowByYMD) {
        let cntLoaded = rowByYMD.imgs.length
        let cntAll = this.countMapByYMD[rowByYMD.ymd]
        return cntAll - cntLoaded
      },
      loadMoreImageOfYMD (rowByYMD) {
        let cntLoaded = rowByYMD.imgs.length
        let cntAll = this.countMapByYMD[rowByYMD.ymd]
        if (cntLoaded >= cntAll) {
          return
        }
        this.loadImages({
          exifDateTime_GE: rowByYMD.ymd,
          exifDateTime_LT: moment(rowByYMD.ymd).add(1, 'd').format('YYYY-MM-DD'),
          f: cntLoaded,
          l: 20
        })
      },
      loadMoreImageOfNullDate () {
        let cntLoaded = this.imageInfosNoDT.length
        this.loadImages({exifDateTime: null, f: cntLoaded, l: 20})
      },
      loadMoreImageOfLast () {
        let arr = this.imageInfos
        let x = this.imageInfos.length
        let lastD = x ? arr[x - 1].exifDateTime : '1000-01-01'
        this.loadImages({exifDateTime_GE: lastD, f: 0, l: 20})
      },
      loadImages (params) {
        const self = this
        Object.assign(params, {o: 'exifDateTime'})
        // console.log(params)
        herdApi.imageInfos.httpGetSome(params)(resp2 => {
          if (!resp2.ok) {
            MsgBox.open(self, '加载图片')(resp2)
            return
          }
          let imageInfosToPut = resp2.data
          if (!imageInfosToPut || !imageInfosToPut.length) {
            self.$Notice.warning({title: '没有图片了'})
            return
          }
          self.$Message.success({content: `加载了${imageInfosToPut.length}张图片`})
          imageInfosToPut.forEach(img => self.putImageInfosInOrder(img))
        })
      },
      putImageInfosInOrder (imageInfo) {
        const self = this
        let dt = imageInfo.exifDateTime
        if (dt === null) { // hasn't exifDateTime
          let arr = self.imageInfosNoDT
          let x = _.sortedIndex(arr, imageInfo, 'hash')
          if (!arr[x] || arr[x].hash !== imageInfo.hash) {
            arr.splice(x, 0, imageInfo)
          }
        } else {  // has exifDateTime
          let arr = self.imageInfos
          let x = _.sortedIndexBy(arr, imageInfo, 'exifDateTime')
          // console.log('x1', x, imageInfo.exifDateTime)
          let existed = false
          for (; x < arr.length && arr[x].exifDateTime === imageInfo.exifDateTime; x++) {
            if (arr[x].hash === imageInfo.hash) {
              existed = true
              break
            }
          }
          if (!existed) {  // insert to tail of same exifDateTime items
            arr.splice(x, 0, imageInfo)
          }
        }
      },
      clickDateOfTimeGrid (cell) {
        this.loadImages({exifDateTime_GE: cell.date, l: 20})
      },
      openViewerModal (imageInfo) {
        const self = this
        if (!imageInfo) return
        self.viewerModal.imageInfo = imageInfo
        self.viewerModal.title = imageInfo.hash
        self.viewerModal.visible = true
        self.viewerModal.imageDictId = 'HD-IMG-' + imageInfo.hash

        herdApi.mediaFiles.httpGetSome({hash: imageInfo.hash})(resp2 => {
          self.viewerModal.mediaFiles = resp2.data
        })
      },
      getUrlByHash: herdApi.getUrlByHash,
      describeImageSize: TextUtils.describeImageSize,
      describeFileSize: TextUtils.describeFileSize,
      describeExifDateTime: Dates.isoToDefault,
      describeHash: hash => hash.slice(0, 10)
    },
    components: {TimeGrid, HerdWordEditor}
  }

  const DT = {
    toY (exifDateTime = '') {
      return exifDateTime.slice(0, 4)
    },
    toM (exifDateTime = '') {
      return exifDateTime.slice(5, 7)
    },
    toD (exifDateTime = '') {
      return exifDateTime.slice(8, 10)
    },
    toYM (exifDateTime = '') {
      return exifDateTime.slice(0, 7)
    },
    toYMD (exifDateTime = '') {
      return exifDateTime.slice(0, 10)
    }
  }

  /*
   compute byY from imageInfos
   iis means imageInfos
   byY=[y:'2019',cnt:233,byYM:byYM]
   byYM=[m:11,ym:'2019-11',cnt:32,byYMD:byYMD]
   byYMD=[d:23,ymd:'2019-11-23',imgs:[imageInfo...]]
    */
  const iis2byYMD = iis => {
    let ymd2iisMap = _.groupBy(iis, ii => DT.toYMD(ii.exifDateTime))
    return Object.keys(ymd2iisMap).map(ymd => ({
      ymd: ymd, imgs: ymd2iisMap[ymd]
    }))
  }
  const iisbyYM = iis => {
    let ym2iisMap = _.groupBy(iis, ii => DT.toYM(ii.exifDateTime))
    return Object.keys(ym2iisMap).map(ym => ({
      ym: ym, cnt: ym2iisMap[ym].length, byYMD: iis2byYMD(ym2iisMap[ym])
    }))
  }
  const iis2byY = iis => {
    let y2iisMap = _.groupBy(iis, ii => DT.toY(ii.exifDateTime))
    return Object.keys(y2iisMap).map(y => ({
      y: y, cnt: y2iisMap[y].length, byYM: iisbyYM(y2iisMap[y])
    }))
  }

</script>
<style scoped>
  .album2 {
    position: relative;
    font-family: Roboto, Dialog;
  }

  .album2 a {
    text-decoration: none;
  }

  .album2 .sidebar {
    /*position: fixed;*/
    /*overflow: scroll;*/
    /*height: 100%;*/
    /*font-size: 10px;*/
  }

  .album2 .gallery {
    margin-left: 150px;
  }

  .album2 .gallery .img-cate-big {
    display: block;
  }

  .album2 .gallery .img-cate-mid {
    display: inline-block;
  }

  .album2 .gallery .img-cate-mid > h4 {
    font-size: 12px;
    color: rgba(51, 51, 51, 0.66);
    margin: 4px;
  }

  .album2 .gallery .img-hull {
    height: 200px;
    margin: 0 3px;
    overflow: hidden;
    resize: both;
    display: inline-block;
  }

  .album2 .gallery .img-hull img {
    display: inline;
    height: 100%;
    object-fit: contain;
  }

  .album2 .gallery .img-hull-more {

    width: 100px;
    border-radius: 5px;
    border: solid 1px rgb(144, 127, 255);
    color: rgb(144, 127, 255);
    text-align: center;
  }

  .album2 .gallery .img-hull-more a {
    padding-top: 80px;
    display: inline-block;
  }


</style>
<style>
  .img-hull .img-mask {
    z-index: 1;
    width: 50px;
    height: 50px;
    position: absolute;
    color: transparent;
    background: linear-gradient(135deg, black, transparent, transparent);
    border: red 1px;
    display: none;
  }

  .img-hull:HOVER .img-mask {
    display: inline-block;
  }

  .img-hull .img-mask:HOVER {
    width: 150px;
    height: 180px;
    background: rgba(0, 0, 0, 0.6);
    border-bottom-right-radius: 7px;
    border: rgba(0, 0, 0, 0.3) 3px solid;
    color: yellow;
    cursor: default;
  }

  .img-hull > .img-mask > .opt {
    float: left;
  }

  .img-hull > .img-mask .info {
    display: none;
    /*width: 50px;*/
    /*height: 50px;*/
  }

  .img-hull > .img-mask:HOVER .info {
    display: inline-block;
    width: 150px;
    height: 150px;
  }

  .viewer-modal .main {
    float: left;
    margin-right: 5px;
  }

  .viewer-modal .main img {
    /*height: 100%;*/
    height: 800px;
    /*width: 100%;*/
    object-fit: contain;
  }

  .viewer-modal .opt {
    float: left;
    width: 20%;
  }

  .ivu-modal-body {
    padding: 2px;
  }

  .ivu-modal-close {
    display: none;
  }
</style>
