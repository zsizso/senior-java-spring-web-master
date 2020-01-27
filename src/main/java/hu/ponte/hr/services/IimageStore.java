package hu.ponte.hr.services;


import hu.ponte.hr.controller.ImageMeta;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IimageStore {
   public String save(final MultipartFile file) throws Exception;
   public List<ImageMeta> listImages() throws Exception;
   public void getImage(final String id, final HttpServletResponse response) throws Exception;
}
