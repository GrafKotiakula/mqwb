package knu.csc.ttp.qualificationwork.mqwb.entities.image;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractEntityController;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.jpa.ImageService;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/data/image")
public class ImageController extends AbstractEntityController<Image, ImageService, ImageValidator> {
    protected GrantedAuthority getAllAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();
    protected GrantedAuthority getByIdAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();
    protected GrantedAuthority createAuthority = Role.Authority.CREATE.getAuthority();
    protected GrantedAuthority updateAuthority = Role.Authority.UPDATE.getAuthority();
    protected GrantedAuthority deleteAuthority = Role.Authority.DELETE.getAuthority();

    @Autowired
    public ImageController(ApplicationContext context) {
        super(context);
    }

    public Image validateAndCreate(String alternate, MultipartFile file, String parameter) {
        if(file == null || file.isEmpty()) {
            return null;
        } else {
            Image image = new Image();
            image.setAlternate(alternate);
            image.setName(file.getOriginalFilename());
            byte[] imageBytes = service.getBytes(file);

            validator.validate(image);
            validator.validateFileBytes(imageBytes, parameter);

            return service.create(image, imageBytes);
        }
    }

    @GetMapping("/all")
    public Page<Image> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page) {
        checkAuthority(getAllAuthority);
        return service.findAll(page);
    }

    @GetMapping("/{id}")
    public Image getById(@PathVariable("id") UUID id) {
        checkAuthority(getByIdAuthority);
        return service.findByIdOrThrow(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadById(@PathVariable("id") UUID id) {
        checkAuthority(getByIdAuthority);

        Image image = service.findByIdOrThrow(id);
        ByteArrayResource resource = new ByteArrayResource(service.readFile(image));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment;filename=\"%s\"", image.getName()))
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<Image> create(@RequestParam("alternate") String alternate,
                                        @RequestParam("image") MultipartFile file) {
        checkAuthority(createAuthority);

        Image image = validateAndCreate(alternate, file, "image");

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(image.getId())
                .toUri();
        return ResponseEntity.created(uri).body(image);
    }

    @PutMapping("/id")
    public Image update(@PathVariable("id") UUID id, @RequestBody JsonNode json) {
        checkAuthority(updateAuthority);

        Image image = service.findByIdOrThrow(id);
        try {
            image = mapper.readerForUpdating(image).readValue(json);
        } catch (IOException ex) {
            throw LoggerUtils.logException(logger, defaultLogLvl, BadRequestException.cannotProcessJson(ex));
        }
        validator.validate(image);

        return service.update(image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        checkAuthority(deleteAuthority);
        service.findById(id).ifPresent(service::delete);
        return ResponseEntity.noContent().build();
    }
}
