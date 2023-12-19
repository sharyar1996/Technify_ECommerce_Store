package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.services.FileService;
//import com.sharyar.Electrify.ElectronicsShop.validations.imageValidation.ImageNameValid;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImplement implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImplement.class);
    @Value("${product.profile.image.path}")
    private String productImagePath;


    @Override
    public List<String> uploadFile(String pathName
            , List<MultipartFile> multiPartFiles) throws IOException  {

        Iterator<MultipartFile> it = multiPartFiles.iterator();
        List<String> pathStrings = new ArrayList<>();

        File folder = new File(pathName);
        if( !folder.exists())
        {
            folder.mkdirs();
        }
        int i=0;
        while(it.hasNext())
        {
            Path path = Path.of(pathName + i + ".jpg");
            i++;
            logger.info("path created by java : {}" , path.toUri().toString());
            logger.info("folder name = {}" , folder.getName());
            logger.info("folder parent name = {}" , folder.getParentFile().getName());

//              String originalFileName = it.next().getOriginalFilename();
//              logger.info("originalFileName = {}" , originalFileName);
            try{
                byte[] bytes = it.next().getBytes();
                logger.info("bytes: {}" , bytes.length );
                try {
                    String pathString = Files.write(path , bytes).toString();
                    pathStrings.add(pathString);
                    logger.info("To be Path name = {}" , pathString);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if( i == 4)
            {
                // because we only allow upto 4 images for each product.
                break;
            }

        }
        if(pathStrings.isEmpty())
        {
            logger.info("PathStrings is empty");
            return null;
        }
        logger.info("PathStrings is not empty");

        return  pathStrings;
    }




    @Override
    public InputStream getResource(String path, String name)  {

        String fullPath = path + name;

        //Not using 'try-with-resources' here because then fileInputStream will
        // be closed automatically after this function finished
        // so this fis stream object will be closed when it reaches
        // the UserController's serveImage() method which will then be useless
        try{
            FileInputStream fis = new FileInputStream(fullPath);
            return fis;
        }
        catch (FileNotFoundException e)
        {
            logger.error("FileNotFoundException occured in getResource() of FileService");
            throw new ResourceNotFoundException("image File not found");
        }

    }

    public <T> void deleteFilesFromServer(T entity) throws java.io.IOException {
        Product product = null;
        Category category = null;
        product = (Product) entity;
        logger.info("{}  = {}" , entity.getClass() , Product.class);
        logger.info(productImagePath + product.getProductId());
        List<String> pathList = product.getProductImages();
        if(pathList == null)
        {
            return;
        }
        int numOfImages = pathList.size();
        String[] photos = new String[numOfImages];
        pathList.toArray(photos);
        try
        {
            for (int i = 0; i < numOfImages; i++)
            {
                Path path = Path.of(photos[i]);

                boolean deleted = Files.deleteIfExists(path);
                if (deleted) {
                    logger.info("Product " + product.getName() + "\\'s " + i + 1 + "st   image " +
                            "have been deleted");
                } else {
                    logger.info("Product " + product.getName() + "\\'s  images " +
                            " CANNOT BE DELETED!");
                }
            }
            //NOW DELETING THE EMPTY FOLDER
            Path emptyFolderPath = Path.of(productImagePath + product.getProductId());
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(emptyFolderPath);)
            {
                if(!stream.iterator().hasNext())
                {
                    logger.info("emptyFolderPath is empty!");
                    Files.delete(emptyFolderPath);
                }
                else
                {
                    logger.info("emptyFolderPath is not empty!");
                }
            }
            catch (DirectoryNotEmptyException e)
            {
                throw new RuntimeException(e.getMessage());
            }

        }
        catch (IOException e) {
            logger.info("IOException during deleting product files from server");
            throw new RuntimeException(e.getMessage());
        }
    }

}
