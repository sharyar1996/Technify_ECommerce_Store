package com.sharyar.Electrify.ElectronicsShop.services;

import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {
    List<String> uploadFile(String pathName , List<MultipartFile> multiPartFiles) throws IOException;
    InputStream getResource(String path , String name);
    <T> void deleteFilesFromServer(T entity) throws java.io.IOException;
}
