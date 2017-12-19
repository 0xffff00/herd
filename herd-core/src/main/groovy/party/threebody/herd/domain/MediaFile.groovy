package party.threebody.herd.domain

import party.threebody.skean.data.Column
import party.threebody.skean.data.LastUpdateTime
import party.threebody.skean.data.PrimaryKey

import javax.persistence.Table
import javax.validation.constraints.NotNull
import java.time.LocalDateTime

@Table(name = "hd_me_file")
class MediaFile {
    @PrimaryKey String path
    @Column String hash
    @Column Integer size    // size in byte
    @Column String mimeType
    @Column String desc
    @LastUpdateTime LocalDateTime syncTime
}



@Table(name = "hd_me_file_video")
class VideoInfo {
    @PrimaryKey String hash
}


@Table(name = "hd_me_file_audio")
class AudioInfo {
    @PrimaryKey String hash
}