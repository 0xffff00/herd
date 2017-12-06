<template>
  <div class="album2">
    <div class="sidebar">
      <ul>
        <li v-for="(bc,i) in imgBigCates">
          <a @click="loadMoreImagesOfBigCate(bc)" :href="'#img-bc-'+bc.name">
            {{bc.name}}({{bc.cnt}}) </a>
        </li>
      </ul>
    </div>

    <div class="gallery">
      <div class="img-cate-big" v-for="imgBC in imgBigCates" :id="'img-bc-'+imgBC.name">
        <h3>{{imgBC.name}}
          <!--<span>({{imgBC.cnt}}) a-a {{countLoadedInBigCate(imgBC)}}</span>-->
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
                <!--:href="'#img-bc-'+imgBC.name"-->
                <span style="font-size:24px;">{{imgBC.cnt - countLoadedInBigCate(imgBC)}}</span>
                <i class="fa fa-angle-double-right fa-2x" aria-hidden="true"></i></a>
            </span>
      </div>
    </div>
  </div>

</template>
<script>
  import herdApi from '../apis/HerdApi'
  import Dates from '../utils/Dates'
  import TextUtils from '../utils/Texts'
  import Arrays from '../utils/Arrays'

  /*
  Big Cate : catorgories by month
  Mid Cate: catorgories by date
  imgBigCate:{year,month,cnt,imgMidCates}
  imgMidCate:{dateStr,imageMedias}
  imageMedias is ordered by time desc
   */
  export default {
    name: 'HerdAlbum2b',
    data () {
      return {
        imgBigCates: []
      }
    },
    mounted () {
      this.load()
    },

    methods: {
      getUrlByHash: herdApi.getUrlByHash,
      load () {
        console.log('loading')
        // load timeline bar
        herdApi.countImageMediasByMonth(null, res => {
          this.imgBigCates = res.map(e => ({
            year: e[0],
            month: e[1],
            cnt: e[2],
            imgMidCates: [],
            name: Dates.toChsYearMon(e[0], e[1])
          }))
        })
        // load gallery
        this.loadImages('9999-12-31', 0, 20)
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
        console.log(params)
        herdApi.listImageMedias(params, res => {
          res.forEach(imageMedia => this._addImageMediaToCates(imageMedia))
        })
      },
      _addImageMediaToCates (imageMedia) { // insert in time desc order
        let dt = imageMedia.exifDateTime
        if (dt === null) {  // hasn't exifDateTime
          let imgBC = this.imgBigCates.find(bc => (bc.year === null))
          if (!imgBC.imgMidCates.length) {
            imgBC.imgMidCates.push({dateStr: null, imageMedias: [], name: 'NA'})
          }
          let imgMC = imgBC.imgMidCates[0]
          imgMC.imageMedias.push(imageMedia)
        } else {  // has exifDateTime
          let y = ExifDateTimeUtils.readYear(dt)
          let m = ExifDateTimeUtils.readMonth(dt)
          let d = ExifDateTimeUtils.readDay(dt)
          let chsDateStr = Dates.toChsDate(y, m, d)
          let dateStr = ExifDateTimeUtils.readDateStr(dt)
          let imgBC = this.imgBigCates.find(bc => (bc.year === y && bc.month === m))
          // find or create imgMidCate by date
          let imgMC = Arrays.insertToOrderedArray(
            imgBC.imgMidCates,
            {dateStr: dateStr, imageMedias: [], name: chsDateStr},
            (x, y) => x.dateStr <= y.dateStr,
            (x, y) => x.dateStr === y.dateStr
          )
          // insert imageMedia if not existed
          Arrays.insertToOrderedArray(
            imgMC.imageMedias,
            imageMedia,
            (x, y) => x.exifDateTime <= y.exifDateTime,
            (x, y) => x.hash === y.hash
          )
        }
      },
      countLoadedInBigCate (imgBigCate) {
        return imgBigCate.imgMidCates.map(mc => mc.imageMedias.length).reduce((s, a) => s + a, 0)
      },
      isAllLoadedInBigCate (imgBigCate) {
        return imgBigCate.cnt === this.countLoadedInBigCate(imgBigCate)
      },
      getLasthImageMediaInBigCate (imgBigCate) {
        let x = imgBigCate.imgMidCates.length
        if (x === 0) return null
        let imgMC = imgBigCate.imgMidCates[x - 1]
        x = imgMC.imageMedias.length
        if (x === 0) return null
        return imgMC.imageMedias[x - 1]
      },
      describeImageSize: TextUtils.describeImageSize,
      describeFileSize: TextUtils.describeFileSize,
      describeExifDateTime: Dates.isoToDefault,
      describeHash: hash => hash.slice(0, 10)
    }
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
    font-family : Roboto, Dialog;
  }

  .album2 a {
    text-decoration: none;
  }

  .album2 .sidebar {
    position: fixed;

    font-size: 10px;
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
