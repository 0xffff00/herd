package party.threebody.herd.webapp.domain

import party.threebody.skean.data.Column
import party.threebody.skean.data.LastUpdateTime
import party.threebody.skean.data.PrimaryKey

import javax.persistence.Table
import java.time.LocalDateTime

@Table(name="hd_media_path")
class Media {
    @PrimaryKey String hash
    @Column String type
    @Column String subtype
    @Column String desc
    @Column Integer size
    @LastUpdateTime LocalDateTime syncTime

    //DTO fields
    List<MediaPath> paths
    String path0Path;

    void setTypeAndSubtype(MediaTypeAndSubType tast){
        type=tast.type
        subtype=tast.subtype
    }

}
@Table(name="hd_media")
class MediaPath {
    @PrimaryKey String hash
    @PrimaryKey String path
    @Column String type
    @Column String repoName
    @LastUpdateTime LocalDateTime syncTime

    MediaPath(){}

    MediaPath(String hash, String path, String type, String repoName, LocalDateTime syncTime) {
        this.hash = hash
        this.path = path
        this.type = type
        this.repoName = repoName
        this.syncTime = syncTime
    }
}

class MediaTypeAndSubType {
    String type
    String subtype
}