package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.customexception.SizeTooBigException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageStore implements IimageStore {
    private static final String DIRECTORY = "/tesztkep/";
    private static final Integer MAXSIZE = 2000000;
    private Map<String, ImageMeta> imageMetaMap = new HashMap<>();

    @Override
    public String save(MultipartFile file) {
        String result = "ok";
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path path = Paths.get(DIRECTORY + fileName);
        try {
            if (file.getSize() > MAXSIZE) {
                throw new SizeTooBigException("Uploaded image size is too big");
            }
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            Optional<String> ext = Optional.ofNullable(file.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            final ImageMeta imageMeta = ImageMeta.builder()
                    .id("" + file.getSize())
                    .name(file.getOriginalFilename())
                    .mimeType("image/" + ext.get())
                    .size(file.getSize())
                    .digitalSign("").build();
            storeMetaDatas(imageMeta, imageMetaMap);
        } catch (IOException | SizeTooBigException e) {
            e.printStackTrace();
            result = "error";
        }
        return result;
    }

    @Override
    public List<ImageMeta> listImages() throws Exception {
        return imageMetaMap.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public void getImage(String id, HttpServletResponse response) throws Exception {
       response.setContentType(imageMetaMap.get(id).getMimeType());
       response.getOutputStream().write(Files.readAllBytes(Paths.get(DIRECTORY + imageMetaMap.get(id).getName())));
    }

    private void storeMetaDatas(ImageMeta imageMeta, Map imageMetaMap) {
        imageMetaMap.put(imageMeta.getId(), imageMetaMap);
    }
}
