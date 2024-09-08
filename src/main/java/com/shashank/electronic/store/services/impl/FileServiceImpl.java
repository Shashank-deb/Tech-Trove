package com.shashank.electronic.store.services.impl;

import com.shashank.electronic.store.exceptions.BadApiRequestException;
import com.shashank.electronic.store.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadImage(MultipartFile file, String path) throws IOException {
        //abc.png
        String originalFilename = file.getOriginalFilename();
        logger.info("File Name: {} " + originalFilename);
        String fileName = UUID.randomUUID().toString();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileNameWithExtension = fileName + extension;
        String fullPathWithFileName = path + fileNameWithExtension;
        logger.info("Full Image Path: {} " + fullPathWithFileName);
        if (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".mp4") || extension.equalsIgnoreCase(".webp")) {
            //Save the File when extension is correct
            logger.info("File Name with extension: {} " + fileNameWithExtension);
            File folder = new File(path);
            if (!folder.exists()) {
//                create the folder
                folder.mkdirs();
            }
            //uploading the file
            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));
            return fileNameWithExtension;

        } else {
            throw new BadApiRequestException("Invalid file type  " + extension + " not allowed ");
        }
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String fullPath = path + File.separator + name;
        InputStream inputStream = new FileInputStream(fullPath);
        return inputStream;
    }
}
