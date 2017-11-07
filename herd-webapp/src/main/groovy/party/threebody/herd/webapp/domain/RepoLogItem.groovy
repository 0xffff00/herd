package party.threebody.herd.webapp.domain

import party.threebody.skean.data.Column
import party.threebody.skean.data.PrimaryKey

import javax.persistence.Table
import java.time.LocalDateTime

@Table(name = "hd_repo_log")
class RepoLogItem {
    @PrimaryKey LocalDateTime actionTime
    @Column String actionType
    @PrimaryKey String entityKey
    @Column String entityVal
    @Column String result
    @Column String desc

    RepoLogItem() {
    }

    RepoLogItem(LocalDateTime actionTime, String actionType, String entityKey, String entityVal, String result, String desc) {
        this.actionTime = actionTime
        this.actionType = actionType
        this.entityKey = entityKey
        this.entityVal = entityVal
        this.result = result
        this.desc = desc
    }


}
