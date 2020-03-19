package com.lyf.forum.controller;

import com.lyf.forum.dto.FileDTO;
import com.lyf.forum.provider.COSProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Controller
public class FileController {
    final Logger logger= LoggerFactory.getLogger(getClass());
    @Autowired
    private COSProvider cosProvider;
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(@RequestParam("editormd-image-file") MultipartFile multipartFile) throws IOException {
//        MultipartHttpServletRequest multipartHttpServletRequest= (MultipartHttpServletRequest) request;
//        MultipartFile multipartFile=multipartHttpServletRequest.getFile("editormd-image-file");
        File file=File.createTempFile("prefix","suffix");
        if (multipartFile != null) {
            multipartFile.transferTo(file);
        }
        logger.info("uploading image");
        String url=cosProvider.COSUpload(file,multipartFile.getOriginalFilename());

        FileDTO fileDTO = new FileDTO();
        fileDTO.setMessage("success");
        fileDTO.setSuccess(1);
        fileDTO.setUrl(url);
        logger.info("url={}",url);
        return fileDTO;
    }
}
