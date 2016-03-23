package ck.panda.domain.entity;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class Document extends EmailTemplate implements Serializable{

    /**
     * Serial version uuid
     */
    private static final long serialVersionUID = 1L;

    /**
     *  File data in byte format.
     */
    private byte[] fileData;

    /**
     * Get file data
     *
     * @return file data
     */
    public byte[] getFileData() {
        return fileData;
    }
    /**
     * Set  the file data.
     *
     * @param fileData for file.
     */
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Passing file data with file name.
     *
     * @param fileData of the file
     * @param fileName of the file
     */
    public Document( byte[] fileData, String fileName) {
        super(fileName);
        this.fileData = fileData;
    }

    /**
     * Get file name
     *
     * @param metadata of the template.
     */
    public Document(EmailTemplate metadata) {
        super(metadata.getFileName());
    }

    /**
     * Set the file metadata.
     *
     *
     * @return file name of the template.
     */
    public EmailTemplate getMetadata() {
        return new EmailTemplate(getFileName());
    }

}
