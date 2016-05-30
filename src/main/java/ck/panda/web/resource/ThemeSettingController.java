package ck.panda.web.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.ThemeSetting;
import ck.panda.service.ThemeSettingService;
import ck.panda.util.error.exception.CustomGenericException;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Theme Settings controller. */
@RestController
@RequestMapping("/api/themesettings")
@Api(value = "ThemeSettings", description = "Operations with templates", produces = "application/json")
public class ThemeSettingController extends CRUDController<ThemeSetting> implements ApiController {

    /**
     * Theme setting service reference.
     */
    @Autowired
    private ThemeSettingService themeSettingService;

    /** Background image directory. */
    @Value("${background.logo.dir}")
    private String backgroundLogoDir;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Theme setting.", response = ThemeSetting.class)
    @Override
    public ThemeSetting create(@RequestBody ThemeSetting theme) throws Exception {
        return themeSettingService.save(theme);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Theme setting.", response = ThemeSetting.class)
    @Override
    public ThemeSetting read(@PathVariable(PATH_ID) Long id) throws Exception {
        return themeSettingService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Theme setting.", response = ThemeSetting.class)
    @Override
    public ThemeSetting update(@RequestBody ThemeSetting theme, @PathVariable(PATH_ID) Long id) throws Exception {
        return themeSettingService.update(theme);
    }

    @RequestMapping(value = "/list")
    public ThemeSetting listThemeSetting() throws Exception {
        return themeSettingService.findByThemeAndIsActive(true);
    }

    @RequestMapping(value = "/listAll")
    public List<ThemeSetting> listAllThemeSetting() throws Exception {
        return themeSettingService.findByIsActive(true);
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
            @RequestParam(value="backgroundImageFile") MultipartFile[] backgroundImageFile,
            @RequestParam(value="logoImageFile") MultipartFile[] logoImageFile,
            @RequestParam(value="headers") String headers,
            @RequestParam(value = "footers") String footers,
            @RequestParam(value="welcomeContent") String welcomeContent,
            @RequestParam(value = "footerContent") String footerContent)
                    throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        int i = 0;
        BufferedImage image = null;
        ThemeSetting theme = themeSettingService.findByThemeAndIsActive(true);
        if (theme == null) {
            theme = new ThemeSetting();
        }
        if (backgroundImageFile != null) {
            for (MultipartFile file : backgroundImageFile) {
                i++;
                if ((backgroundImageFile.length == 1) || (backgroundImageFile.length > 1) && i == 2) {
                    String fileName = file.getOriginalFilename();
                    File newFile = new File(backgroundLogoDir + "/" + "theme_background.jpg");
                    image = ImageIO.read(file.getInputStream());
                    int height = image.getHeight();
                    int width = image.getWidth();
                    if (width >= 700 && height >= 400) {
                        theme.setBackgroundImgFile(fileName);
                        theme.setBackgroundImgPath(backgroundLogoDir);
                        try {
                            inputStream = file.getInputStream();
                            if (!newFile.exists()) {
                                newFile.createNewFile();
                            }
                            outputStream = new FileOutputStream(newFile);
                            int read = 0;
                            byte[] bytes = new byte[1024];

                            while ((read = inputStream.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, read);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        newFile.getAbsolutePath();
                    } else {
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                "Background image resolution should be larger than "+ 700 + "x" + 400);
                    }
                }
            }
        }

        if (logoImageFile != null) {
            for (MultipartFile file : logoImageFile) {
                i++;
                if ((logoImageFile.length == 1) || (logoImageFile.length > 1) && i == 2) {
                    String fileName = file.getOriginalFilename();
                    File newFile = new File(backgroundLogoDir + "/" + "theme_logo.jpg");
                    image = ImageIO.read(file.getInputStream());
                    int height = image.getHeight();
                    int width = image.getWidth();
                    if (width <= 180 && height <= 55) {
                        theme.setLogoImgPath(backgroundLogoDir);
                        theme.setLogoImgFile(fileName);
                        try {
                            inputStream = file.getInputStream();
                            if (!newFile.exists()) {
                                newFile.createNewFile();
                            }
                            outputStream = new FileOutputStream(newFile);
                            int read = 0;
                            byte[] bytes = new byte[1024];

                            while ((read = inputStream.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, read);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        newFile.getAbsolutePath();
                    } else {
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                "Logo image resolution should be smaller than "+ 180 + "x" + 55);
                    }
                }
            }
        }
        if (welcomeContent != null) {
            theme.setWelcomeContent(welcomeContent);
        }
        if (footerContent != null) {
            theme.setFooterContent(footerContent);
        }
        theme.setIsActive(true);
        themeSettingService.saveThemeCustomisation(theme, headers, footers);
        return null;
    }

}
