package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.customexception.SizeTooBigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
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
    private static final Integer MAXSIZE = 2000000;
    private Map<String, ImageMeta> imageMetaMap = new HashMap<>();
    private final String DIRECTORY = "uploaded";

    @Autowired
    SignService signService;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public String save(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path imageDirPath = null;
        try {
            imageDirPath = getImageDirPath();
            if (imageDirPath == null) {
                throw new FileNotFoundException("uploade directory does not exist");
            }
            if (file.getSize() > MAXSIZE) {
                throw new SizeTooBigException("Uploaded image size is too big");
            }
            if (!Files.exists(imageDirPath)) {
                Files.createDirectories(imageDirPath);
            }
            String filePathString = imageDirPath.toString() + "\\" + file.getOriginalFilename();
            File image = new File(filePathString);
            byte[] fileBytes = file.getBytes();
            file.transferTo(image);
            Optional<String> ext = Optional.ofNullable(file.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            final ImageMeta imageMeta = ImageMeta.builder()
                    .id("" + file.getSize())
                    .name(file.getOriginalFilename())
                    .mimeType("image/" + ext.get())
                    .size(file.getSize())
                    .digitalSign(signService.getSigned(fileBytes)).build();
            storeMetaDatas(imageMeta);
        } catch (IOException | SizeTooBigException e) {
            e.printStackTrace();
            return "error";
        }
        return "ok";
    }

    @Override
    public List<ImageMeta> listImages() throws Exception {
        return imageMetaMap.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public void getImage(String id, HttpServletResponse response) throws Exception {
        Path imageDirPath = Paths.get(getImageDirPath().toString() + "\\" + imageMetaMap.get(id).getName());
        if (imageDirPath == null) {
            throw new FileNotFoundException("uploade directory does not exist");
        }
        response.setContentType(imageMetaMap.get(id).getMimeType());
        response.getOutputStream().write(Files.readAllBytes(imageDirPath));
    }

    private void storeMetaDatas(ImageMeta imageMeta) {
        imageMetaMap.put(imageMeta.getId(), imageMeta);
    }

    private Path getImageDirPath() {
        try {
            String absolutePath = resourceLoader.getResource(DIRECTORY).getFile().getAbsolutePath();
            return Paths.get(absolutePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
