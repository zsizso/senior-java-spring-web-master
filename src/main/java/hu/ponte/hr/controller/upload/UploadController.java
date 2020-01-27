package hu.ponte.hr.controller.upload;

import hu.ponte.hr.services.IimageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequestMapping("api/file")
public class UploadController {
    @Autowired
    IimageStore iimageStore;

    @RequestMapping(value = "post", method = RequestMethod.POST)
    @ResponseBody
    public String handleFormUpload(@RequestParam("file") MultipartFile file) {
        try {
            iimageStore.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "ok";
    }
}
