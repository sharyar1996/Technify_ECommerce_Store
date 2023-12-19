package com.sharyar.Electrify.ElectronicsShop.helpers;

import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

public class Helper  {

    private static Logger logger = LoggerFactory.getLogger(Helper.class);
    // U = entity
    // V = Dto
    public static <U,V> PageableResponse<V> getPageableResponse(Page<U> page , Class<V> type)
    {
        logger.info("Helper method ran");
        List<U> entityList = page.getContent();

        List<V> dtoList = entityList.stream().map(
                entity ->{
                      V dto = new ModelMapper().map(entity , type);
                      logger.info("type = {}" , type);
                      return  dto;
                }
        ).collect(Collectors.toList());

        PageableResponse<V> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(dtoList);
        pageableResponse.setPageNumber(page.getNumber()+1);
        pageableResponse.setTotalPages(page.getTotalPages());
        pageableResponse.setPageSize(page.getSize());
        pageableResponse.setTotalElements(page.getTotalElements());
        pageableResponse.setLastPage(page.isLast());

        return pageableResponse;
    }
    public static Pageable getPageable(int pageNumber,
                                int pageSize, String sortBy , String sortDir)
    {
        Sort sort = null;
        try{
            sort = (sortDir.equalsIgnoreCase("desc") ) ?
                    (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        }
        catch (IllegalArgumentException e)
        {
            logger.info("Sort by seems to be null{}", e.getMessage());
        }
        if( sort == null)
        {
            return PageRequest.of(pageNumber,pageSize);
        }

        return PageRequest.of(pageNumber,pageSize,sort);
    }

    public static boolean validateMultipartFiles(@org.jetbrains.annotations.NotNull List<MultipartFile> files)
    {
        boolean isImage = false;
        for(MultipartFile file : files)
        {
            isImage = false;
            String value = file.getContentType();
             logger.info("value in Helper's isValid() method : {}" , value );

            String[] suffix = {"image/jpg" , "image/jpeg" , "image/png" ,"image/webp" , "image/gif" };
            int suffixLen = suffix.length;
//

            for( int i=0; i< suffixLen ; i++)
            {
                if(value.equalsIgnoreCase(suffix[i]))
                {
                    isImage = true;
                    break;
                }
            }
            if(!isImage)
            {
                return  false;
            }
        }

        return isImage;
    }


}
