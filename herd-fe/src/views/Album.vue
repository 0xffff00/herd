<template>
  <div class="album2">
    <div class="sidebar">
      <TimeGrid :countByDate="countByDateObjArr" :cssDateTdHeight="15" :cssDateTdPadding="1">
      </TimeGrid>
    </div>


    <!--  <div class="gallery">
        <div class="img-cate-big" v-for="imgBC in imgBigCates" :id="'img-bc-'+imgBC.name">
          <h3>{{imgBC.name}}
            &lt;!&ndash;<span>({{imgBC.cnt}}) a-a {{countLoadedInBigCate(imgBC)}}</span>&ndash;&gt;
          </h3>

          <div class="img-cate-mid" v-for="imgMC in imgBC.imgMidCates" :id="'img-mc-'+imgMC.dateStr">
            <h4>{{imgMC.name}}</h4>
            <span class="img-hull" v-for="(im,i) in imgMC.imageMedias">
              <el-tooltip placement="top" :offset="30" :open-delay="500">
                 <div slot="content">
                   HASH值：{{describeHash(im.hash)}}<br>
                   拍摄时间：{{describeExifDateTime(im.exifDateTime)}} <br>
                   相机：{{im.exifMake}} {{im.exifModel}}<br>
                   尺寸：{{describeImageSize(im.width, im.height)}}<br>
                   文件：{{im.type}}格式， {{describeFileSize(im.fileSize)}}<br>
                 </div>
                <img :src="getUrlByHash(im.hash,'1Kq5')"/>
              </el-tooltip>

              </span>
          </div>
          <span class="img-hull img-hull-more" v-if="!isAllLoadedInBigCate(imgBC)">
                <a @click="loadMoreImagesOfBigCate(imgBC)">
                  &lt;!&ndash;:href="'#img-bc-'+imgBC.name"&ndash;&gt;
                  <span style="font-size:24px;">{{imgBC.cnt - countLoadedInBigCate(imgBC)}}</span>
                  <i class="fa fa-angle-double-right fa-2x" aria-hidden="true"></i></a>
              </span>
        </div>
      </div>-->
  </div>

</template>
<script>
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'
  import TimeGrid from '../components/TimeGrid.vue'
  import moment from 'moment'

  /*
  Big Cate : catorgories by month
  Mid Cate: catorgories by date
  imgBigCate:{year,month,cnt,imgMidCates}
  imgMidCate:{dateStr,imageMedias}
  imageMedias is ordered by time desc
   */
  export default {
    name: 'Album',
    data () {
      return {
        // like [["2017-03-01",1],["2017-03-05",25]], ordered
        countByDateArr2d: []
      }
    },
    mounted () {
      this.load()
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
      }
    },
    methods: {
      load () {
        const self = this
        herdApi.imageInfos.countByDate(res => {
          if (!res.data || !res.data.length) return
          let len = res.data.length
          // console.log(res.data.slice(0, 218))
          if (res.data[len - 1][0] === null) len--
          self.countByDateArr2d = res.data.slice(0, len)
        })
        // this.loadImages('9999-12-31', 0, 20)
      },
      loadMoreImagesOfBigCate (imgBigCate) {
        let offset = this.countLoadedInBigCate(imgBigCate)
        if (imgBigCate.year) {
          let d = Dates.toIsoTime(imgBigCate.year, imgBigCate.month + 1, 1, 0, 0, 0)
          this.loadImages({exifDateTime_LT: d, f: offset, l: 20})
        } else {
          this.loadImages({exifDateTime: null, f: offset, l: 20})
        }
      },
      loadImages (params) {
        Object.assign(params, {o: '-exifDateTime'})
        // console.log(params)
        herdApi.listImageMedias(params, res => {
          res.forEach(imageMedia => this._addImageMediaToCates(imageMedia))
        })
      },
      getUrlByHash: herdApi.getUrlByHash,
      describeImageSize: TextUtils.describeImageSize,
      describeFileSize: TextUtils.describeFileSize,
      describeExifDateTime: Dates.isoToDefault,
      describeHash: hash => hash.slice(0, 10)
    },
    components: {TimeGrid}
  }

  const ExifDateTimeUtils = {
    readYear (exifDateTime = '') {
      return parseInt(exifDateTime.slice(0, 4))
    },
    readMonth (exifDateTime = '') {
      return parseInt(exifDateTime.slice(5, 7))
    },
    readDay (exifDateTime = '') {
      return parseInt(exifDateTime.slice(8, 10))
    },
    readDateStr (exifDateTime = '') {
      return exifDateTime.slice(0, 10)
    }
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
