<template>
  <div class="album2">
    <div class="sidebar">
      <TimeGrid :countByDate="countByDateObjArr" :cssDateTdHeight="15" :cssDateTdPadding="1">
      </TimeGrid>
    </div>

    <!--{{imageInfosByY}}-->
    <div class="gallery">
      <div class="img-cate-big" v-for="rowByY in imageInfosByY" :id="'img-'+rowByY.y">
        <h2>{{rowByY.y}}({{rowByY.cnt}})</h2>
        <div class="img-cate-big" v-for="rowByYM in rowByY.byYM" :id="'img-'+rowByYM.ym">
          <h3>{{rowByYM.ym}}({{rowByYM.cnt}})</h3>
          <div class="img-cate-mid" v-for="rowByYMD in rowByYM.byYMD" :id="'img-'+rowByYMD.ymd">
            <h4>{{rowByYMD.ymd}}({{rowByYMD.imgs.length}})</h4>
            <span class="img-hull" v-for="img in rowByYMD.imgs">
               <Tooltip placement="top" content="Tooltip 文字提示" :delay="400">
                  <div slot="content">
                   HASH值：{{describeHash(img.hash)}}<br>
                   拍摄时间：{{describeExifDateTime(img.exifDateTime)}} <br>
                   相机：{{img.exifMake}} {{img.exifModel}}<br>
                   尺寸：{{describeImageSize(img.width, img.height)}}<br>
                   文件：{{img.type}}格式， {{describeFileSize(img.fileSize)}}<br>
                 </div>

              </Tooltip>
              <img :src="getUrlByHash(img.hash,'1Kq5')"/>
            </span>
          </div>
          <!--<span class="img-hull img-hull-more" v-if="!isAllLoadedInBigCate(imgBC)">-->
            <!--<a @click="loadMoreImagesOfBigCate(imgBC)">-->
            <!--<span style="font-size:24px;">{{imgBC.cnt - countLoadedInBigCate(imgBC)}}</span>-->
            <!--<i class="fa fa-angle-double-right fa-2x" aria-hidden="true"></i></a>-->
          <!--</span>-->
        </div>

      </div>
    </div>


  </div>

</template>
<script>
  import _ from 'lodash'
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import TimeGrid from '../components/TimeGrid.vue'
  import moment from 'moment'
  import MsgBox from '../components/MsgBox'

  /*
  Big Cate : catorgories by month
  Mid Cate: catorgories by date
  imgBigCate:{year,month,cnt,imgMidCates}
  imgMidCate:{dateStr,imageMedias}
  imageMedias is ordered by time desc

  imagesByMonth: {ym:'2012-12',}
  imagesByDate {}
   */
  export default {
    name: 'Album',
    data () {
      return {
        // like [["2017-03-01",1],["2017-03-05",25]], ordered
        countByDateArr2d: [],
        // like [{hash,width,exifDateTime,fileSize..}..], ordered by exifDateTime asc
        imageInfos: [],
        // like imageInfo, but exifDateTime is null, ordered by hash
        imageInfosNoDT: [],

        maxImagesInWindow: 600
      }
    },
    created () {
      this.loadTimeGrid()
      this.loadImages({exifDateTime_GE: '2016', f: 0, l: 200})
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
      // like [{"2017-03-01":1},{"2017-03-05":25}]
      countByDateMap () {
        let res = {}
        this.countByDateArr2d.forEach(e => {
          res[e[0]] = e[1]
        })
        return res
      },
      // like [{date:"2017-03-01",cnt:1},...,{date:"2017-03-05",cnt:25}]
      // but not same length as countByDateArr2d. it's filled all continuous dates even if cnt=0.
      countByDateObjArr () {
        let d = moment(this.involvedDateMin)
        let dmax = moment(this.involvedDateMax)
        let n = dmax.diff(d, 'd')
        let map = this.countByDateMap
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
        console.log('r232323=', r)
        return r
      }
    },
    methods: {
      loadTimeGrid () {
        const self = this
        herdApi.imageInfos.countByDate(res => {
          if (!res.data || !res.data.length) return
          let len = res.data.length
          // console.log(res.data.slice(0, 218))
          if (res.data[len - 1][0] === null) len--
          self.countByDateArr2d = res.data.slice(0, len)
        })
        // this.loadImages({exifDateTime_LT: d, f: offset, l: 20})
      },
//      loadMoreImagesOfBigCate (imgBigCate) {
//        let offset = this.countLoadedInBigCate(imgBigCate)
//        if (imgBigCate.year) {
//          let d = Dates.toIsoTime(imgBigCate.year, imgBigCate.month + 1, 1, 0, 0, 0)
//          this.loadImages({exifDateTime_LT: d, f: offset, l: 20})
//        } else {
//          this.loadImages({exifDateTime: null, f: offset, l: 20})
//        }
//      },
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
          let x = _.sortedIndex(arr, imageInfo, 'exifDateTime')
          if (!arr[x] || arr[x].hash !== imageInfo.hash) {
            arr.splice(x, 0, imageInfo)
          }
        }
      },
      getUrlByHash: herdApi.getUrlByHash,
      describeImageSize: TextUtils.describeImageSize,
      describeFileSize: TextUtils.describeFileSize,
      describeExifDateTime: Dates.isoToDefault,
      describeHash: hash => hash.slice(0, 10)
    },
    components: {TimeGrid}
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
