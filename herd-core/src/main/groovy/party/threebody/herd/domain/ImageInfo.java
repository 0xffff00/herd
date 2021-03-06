package party.threebody.herd.domain;


import party.threebody.skean.data.Column;
import party.threebody.skean.data.PrimaryKey;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * image basic & EXIF meta info
 */
@Table(name = "hd_me_file_image")
public class ImageInfo {
    private @PrimaryKey String hash;
    private  @Column String type;
    private @Column @NotNull int height;
    private @Column @NotNull int width;
    private @Column @NotNull int exifBitDepth;
    private @Column String exifMake;
    private @Column String exifModel;
    private @Column LocalDateTime exifDateTime;
    private @Column String exifColorSpace;
    private @Column String exifExposureTime;
    private @Column String exifWhiteBalance;
    private @Column String exifAperture;

    //----DTO props-----
    protected Integer fileSize;

    public void fillHeight(int height) {
        if (this.height == 0 && height > 0) {
            this.height = height;
        }
    }

    public void fillWidth(int width) {
        if (this.width == 0 && width > 0) {
            this.width = width;
        }
    }

    public void fillExifBitDepth(Integer exifBitDepth) {
        if (this.exifBitDepth == 0 && exifBitDepth != null) {
            this.exifBitDepth = exifBitDepth;
        }
    }

    public void fillExifBitDepth(int exifBitDepth) {
        if (this.exifBitDepth == 0 && exifBitDepth > 0) {
            this.exifBitDepth = exifBitDepth;
        }
    }

    public void fillExifMake(String exifMake) {
        if (this.exifMake == null && exifMake != null) {
            this.exifMake = exifMake;
        }

    }

    public void fillExifModel(String exifModel) {
        if (this.exifModel == null && exifModel != null) {
            this.exifModel = exifModel;
        }
    }

    public void fillExifDateTime(Date exifDateTime) {
        if (this.exifDateTime == null && exifDateTime != null) {
            this.exifDateTime = LocalDateTime.ofInstant(exifDateTime.toInstant(), ZoneId.systemDefault());
        }
    }

    public void fillExifDateTime(LocalDateTime exifDateTime) {
        if (this.exifDateTime == null && exifDateTime != null) {
            this.exifDateTime = exifDateTime;
        }
    }

    public void fillExifColorSpace(String exifColorSpace) {
        if (this.exifColorSpace == null && exifColorSpace != null) {
            this.exifColorSpace = exifColorSpace;
        }
    }

    public void fillExifExposureTime(String exifExposureTime) {
        if (this.exifExposureTime == null && exifExposureTime != null) {
            this.exifExposureTime = exifExposureTime;
        }
    }

    public void fillExifWhiteBalance(String exifWhiteBalance) {
        if (this.exifWhiteBalance == null && exifWhiteBalance != null) {
            this.exifWhiteBalance = exifWhiteBalance;
        }
    }

    public void fillExifAperture(String exifAperture) {
        if (this.exifAperture == null && exifAperture != null) {
            this.exifAperture = exifAperture;
        }
    }

    //equals & hashs

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageInfo that = (ImageInfo) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }


    //getters & setters

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getExifBitDepth() {
        return exifBitDepth;
    }

    public void setExifBitDepth(int exifBitDepth) {
        this.exifBitDepth = exifBitDepth;
    }

    public String getExifMake() {
        return exifMake;
    }

    public void setExifMake(String exifMake) {
        this.exifMake = exifMake;
    }

    public String getExifModel() {
        return exifModel;
    }

    public void setExifModel(String exifModel) {
        this.exifModel = exifModel;
    }

    public LocalDateTime getExifDateTime() {
        return exifDateTime;
    }

    public void setExifDateTime(LocalDateTime exifDateTime) {
        this.exifDateTime = exifDateTime;
    }

    public String getExifColorSpace() {
        return exifColorSpace;
    }

    public void setExifColorSpace(String exifColorSpace) {
        this.exifColorSpace = exifColorSpace;
    }

    public String getExifExposureTime() {
        return exifExposureTime;
    }

    public void setExifExposureTime(String exifExposureTime) {
        this.exifExposureTime = exifExposureTime;
    }

    public String getExifWhiteBalance() {
        return exifWhiteBalance;
    }

    public void setExifWhiteBalance(String exifWhiteBalance) {
        this.exifWhiteBalance = exifWhiteBalance;
    }

    public String getExifAperture() {
        return exifAperture;
    }

    public void setExifAperture(String exifAperture) {
        this.exifAperture = exifAperture;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }
}