package com.example.molecularsearch.dto;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;

@AllArgsConstructor
public class ByteMultpartFile implements MultipartFile {

    private final byte[] byteImage;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return null;
    }

    @Override
    public String getContentType() {
        return MediaType.IMAGE_PNG_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return byteImage == null || byteImage.length == 0;
    }

    @Override
    public long getSize() {
        return byteImage.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return byteImage;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(byteImage);
    }

    @Override
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(byteImage);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        MultipartFile.super.transferTo(dest);
    }
}
