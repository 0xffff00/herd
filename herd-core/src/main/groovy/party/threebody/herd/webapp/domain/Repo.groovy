package party.threebody.herd.webapp.domain

import party.threebody.skean.data.Column
import party.threebody.skean.data.LastUpdateTime
import party.threebody.skean.data.PrimaryKey

import javax.persistence.Table
import java.time.LocalDateTime

@Table(name = "hd_repo")
class Repo {
    @PrimaryKey String name
    @Column String absPath
    @Column String url
    @Column String type
    @Column String desc
    @LastUpdateTime LocalDateTime saveTime
    @Column String state
}

