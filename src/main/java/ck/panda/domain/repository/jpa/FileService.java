package ck.panda.domain.repository.jpa;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Document;

/**
 *
 * For each document a folder is created. The folder contains the document
 * and a properties files with the meta data of the document.
 *
 */
@Service
public class FileService {

    /** Logger attribute of the file. */
    private static final Logger LOG = Logger.getLogger(FileService.class);

    /** Directory for file location .*/
    public static final String DIRECTORY = "tmp";

    /**
     * Init method for creating directory.
     */
    @PostConstruct
    public void init() {
        createDirectory(DIRECTORY);
    }

    /**
     * Inserts a document to the archive by creating a folder with the UUID
     * of the document. In the folder the document is saved and a properties file
     * with the meta data of the document.
     *
     */

    public void insert(Document document) {
        try {
            createDirectory(document);
            saveFileData(document);
        } catch (IOException e) {
            String message = "Error while inserting document";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * To save the file document.
     *
     * @param document to be uploaded.
     * @throws IOException if exception occurs.
     */
    private void saveFileData(Document document) throws IOException {
        String path = getDirectoryPath(document);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(new File(path), document.getFileName())));
        stream.write(document.getFileData());
        stream.close();
    }

    /**
     * For creating separate file directory .
     *
     * @param document to be stored
     * @return path of the created directory.
     */
    private String createDirectory(Document document) {
        String path = getDirectoryPath(document);
        createDirectory(path);
        return path;
    }

    /**
     * Get the directory path.
     *
     * @param document to be stored.
     * @return directory path.
     */
    private String getDirectoryPath(Document document) {
       return getDirectoryPath(document.getFileName());
    }

    /**
     * Get directory path of the file.
     *
     * @param filename of the file.
     * @return directory path.
     */
    private String getDirectoryPath(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIRECTORY).append(File.separator).append(fileName);
        String path = sb.toString();
        return path;
    }

    /**
     * Create directory using the path.
     *
     * @param path to show created directory path.
     */
    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
    }
}
