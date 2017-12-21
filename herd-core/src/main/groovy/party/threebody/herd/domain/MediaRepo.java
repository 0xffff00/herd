package party.threebody.herd.domain;


import party.threebody.skean.data.Column;
import party.threebody.skean.data.LastUpdateTime;
import party.threebody.skean.data.PrimaryKey;

import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 媒体库，一般代表文件系统中的一个目录
 */
@Table(name = "hd_me_repo")
public class MediaRepo {
    @PrimaryKey private String name;
    @Column private String path;
    @Column private String url;
    @Column private String type;
    @Column private String desc;
    @LastUpdateTime private LocalDateTime saveTime;
    @Column private String state;

    private transient int fileCnt,imgCnt;

    public int getFileCnt() {
        return fileCnt;
    }

    public void setFileCnt(int fileCnt) {
        this.fileCnt = fileCnt;
    }

    public int getImgCnt() {
        return imgCnt;
    }

    public void setImgCnt(int imgCnt) {
        this.imgCnt = imgCnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocalDateTime getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(LocalDateTime saveTime) {
        this.saveTime = saveTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
